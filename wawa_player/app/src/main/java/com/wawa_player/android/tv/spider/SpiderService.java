package com.wawa_player.android.tv.spider;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderNull;
import com.github.catvod.net.OkHttp;

import org.json.JSONObject;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 运行在独立进程 {@code :spider} 的爬虫服务。
 * <p>
 * 职责：
 * <ol>
 *   <li>承载 Jar / JS / Python 三套爬虫，与主进程内存与崩溃隔离。</li>
 *   <li>任务下发、超时中断、暂停/停止控制。</li>
 *   <li>按策略限制执行时间、并发数、内存水位、响应体大小与网络范围。</li>
 *   <li>通过 {@link ISpiderCallback} 回传日志与状态。</li>
 * </ol>
 */
public class SpiderService extends Service {

    private static final String TAG = "SpiderService";

    private final RemoteCallbackList<ISpiderCallback> callbacks = new RemoteCallbackList<>();
    private final java.util.Set<String> paused = ConcurrentHashMap.newKeySet();
    private final AtomicInteger aliveTasks = new AtomicInteger();
    private final AtomicLong totalTasks = new AtomicLong();
    private final AtomicLong failedTasks = new AtomicLong();
    private final long startAt = SystemClock.elapsedRealtime();

    private volatile SpiderPolicy policy = new SpiderPolicy();
    private final SpiderNetGuard netGuard = new SpiderNetGuard();

    private ExecutorService executor;
    private ExecutorService pump;

    private final ThreadFactory factory = r -> {
        Thread t = new Thread(r, "spider-task");
        t.setUncaughtExceptionHandler((th, e) -> log(Log.ERROR, TAG, "uncaught in task: " + e));
        return t;
    };

    private final ThreadFactory pumpFactory = r -> {
        Thread t = new Thread(r, "spider-pump");
        return t;
    };

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newCachedThreadPool(factory);
        pump = Executors.newCachedThreadPool(pumpFactory);
        // 只在 spider 进程注入网络护栏，主进程 guard 恒为 null
        OkHttp.setGuard(netGuard);
        netGuard.update(policy);
        log(Log.INFO, TAG, "spider process started, pid=" + Process.myPid());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        log(Log.INFO, TAG, "spider process destroying");
        shutdown(executor);
        shutdown(pump);
        try {
            SpiderHost.get().clear();
        } catch (Throwable ignored) {
        }
        callbacks.kill();
        super.onDestroy();
    }

    private static void shutdown(ExecutorService e) {
        if (e == null) return;
        e.shutdownNow();
        try {
            e.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Binder 实现                                                         */
    /* ------------------------------------------------------------------ */

    private final ISpiderService.Stub binder = new ISpiderService.Stub() {

        @Override
        public int start(String key, String api, String ext, String jar) {
            if (key == null) return SpiderProtocol.Code.ERR_BAD_ARGS;
            paused.remove(key);
            try {
                Spider spider = SpiderHost.get().getSpider(key, api, ext, jar);
                if (spider instanceof SpiderNull) {
                    log(Log.WARN, key, "unsupported api: " + api);
                    return SpiderProtocol.Code.ERR_NO_SPIDER;
                }
                SpiderHost.get().setRecent(key, api, jar);
                state(key, SpiderProtocol.State.IDLE, "started");
                return SpiderProtocol.Code.OK;
            } catch (Throwable e) {
                log(Log.ERROR, key, "start failed: " + e);
                state(key, SpiderProtocol.State.CRASHED, String.valueOf(e));
                return SpiderProtocol.Code.ERR_UNKNOWN;
            }
        }

        @Override
        public int pause(String key) {
            if (key == null) return SpiderProtocol.Code.ERR_BAD_ARGS;
            paused.add(key);
            state(key, SpiderProtocol.State.PAUSED, "paused");
            return SpiderProtocol.Code.OK;
        }

        @Override
        public int resume(String key) {
            if (key == null) return SpiderProtocol.Code.ERR_BAD_ARGS;
            paused.remove(key);
            state(key, SpiderProtocol.State.IDLE, "resumed");
            return SpiderProtocol.Code.OK;
        }

        @Override
        public int stop(String key) {
            if (key == null) return SpiderProtocol.Code.ERR_BAD_ARGS;
            paused.remove(key);
            try {
                SpiderHost.get().destroy(key);
                state(key, SpiderProtocol.State.STOPPED, "stopped");
                return SpiderProtocol.Code.OK;
            } catch (Throwable e) {
                log(Log.ERROR, key, "stop failed: " + e);
                return SpiderProtocol.Code.ERR_UNKNOWN;
            }
        }

        @Override
        public int stopAll() {
            paused.clear();
            SpiderHost.get().clear();
            state("*", SpiderProtocol.State.STOPPED, "stopAll");
            return SpiderProtocol.Code.OK;
        }

        @Override
        public int parseJar(String jar, boolean recent) {
            try {
                SpiderHost.get().parseJar(jar, recent);
                return SpiderProtocol.Code.OK;
            } catch (Throwable e) {
                log(Log.ERROR, TAG, "parseJar failed: " + e);
                return SpiderProtocol.Code.ERR_UNKNOWN;
            }
        }

        @Override
        public int setRecent(String key, String api, String jar) {
            try {
                SpiderHost.get().setRecent(key, api, jar);
                return SpiderProtocol.Code.OK;
            } catch (Throwable e) {
                log(Log.ERROR, TAG, "setRecent failed: " + e);
                return SpiderProtocol.Code.ERR_UNKNOWN;
            }
        }

        @Override
        public String jsonExt(String key, String jxsJson, String url) {
            try {
                LinkedHashMap<String, String> jxs = SpiderJson.toLinkedMap(jxsJson);
                JSONObject result = SpiderHost.get().jsonExt(key, jxs, url);
                return result != null ? result.toString() : null;
            } catch (Throwable e) {
                log(Log.ERROR, TAG, "jsonExt failed: " + e);
                return null;
            }
        }

        @Override
        public String jsonExtMix(String flag, String key, String name, String jxsJson, String url) {
            try {
                LinkedHashMap<String, HashMap<String, String>> jxs = SpiderJson.toNestedMap(jxsJson);
                JSONObject result = SpiderHost.get().jsonExtMix(flag, key, name, jxs, url);
                return result != null ? result.toString() : null;
            } catch (Throwable e) {
                log(Log.ERROR, TAG, "jsonExtMix failed: " + e);
                return null;
            }
        }

        @Override
        public Bundle call(String key, String api, String ext, String jar, String method, Bundle args) {
            long t0 = SystemClock.elapsedRealtime();
            Bundle out = new Bundle();
            if (key == null || method == null) return error(out, SpiderProtocol.Code.ERR_BAD_ARGS, "key/method required");
            final Bundle taskArgs = args == null ? Bundle.EMPTY : args;
            if (paused.contains(key)) return error(out, SpiderProtocol.Code.ERR_PAUSED, "spider paused");
            if (!checkMemory(out)) return out;

            SpiderPolicy p = policy;
            if (aliveTasks.get() >= p.getMaxTasks()) {
                return error(out, SpiderProtocol.Code.ERR_BUSY, "busy, alive=" + aliveTasks.get());
            }

            aliveTasks.incrementAndGet();
            totalTasks.incrementAndGet();
            state(key, SpiderProtocol.State.RUNNING, method);
            Future<Bundle> future = null;
            try {
                future = executor.submit(() -> runTask(key, api, ext, jar, method, taskArgs));
                Bundle result = future.get(p.getTimeoutMs(), TimeUnit.MILLISECONDS);
                return result != null ? result : error(out, SpiderProtocol.Code.ERR_UNKNOWN, "null result");
            } catch (TimeoutException e) {
                if (future != null) future.cancel(true);
                failedTasks.incrementAndGet();
                state(key, SpiderProtocol.State.TIMEOUT, method + " timeout " + p.getTimeoutMs() + "ms");
                return error(out, SpiderProtocol.Code.ERR_TIMEOUT, "timeout after " + p.getTimeoutMs() + "ms");
            } catch (ExecutionException e) {
                failedTasks.incrementAndGet();
                Throwable cause = e.getCause();
                state(key, SpiderProtocol.State.CRASHED, String.valueOf(cause));
                return error(out, SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(cause));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return error(out, SpiderProtocol.Code.ERR_INTERRUPTED, "interrupted");
            } catch (Throwable e) {
                failedTasks.incrementAndGet();
                return error(out, SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(e));
            } finally {
                aliveTasks.decrementAndGet();
                out.putLong(SpiderProtocol.Result.COST_MS, SystemClock.elapsedRealtime() - t0);
                state(key, SpiderProtocol.State.IDLE, method + " cost=" + (SystemClock.elapsedRealtime() - t0) + "ms");
            }
        }

        @Override
        public Bundle proxy(Bundle params) {
            Bundle out = new Bundle();
            if (params == null) return error(out, SpiderProtocol.Code.ERR_BAD_ARGS, "params required");
            if (!checkMemory(out)) return out;
            SpiderPolicy p = policy;
            if (aliveTasks.get() >= p.getMaxTasks()) {
                return error(out, SpiderProtocol.Code.ERR_BUSY, "busy, alive=" + aliveTasks.get());
            }
            long t0 = SystemClock.elapsedRealtime();
            aliveTasks.incrementAndGet();
            totalTasks.incrementAndGet();
            Future<Bundle> future = null;
            try {
                future = executor.submit(() -> runProxy(params));
                Bundle result = future.get(p.getTimeoutMs(), TimeUnit.MILLISECONDS);
                return result != null ? result : error(out, SpiderProtocol.Code.ERR_UNKNOWN, "null result");
            } catch (TimeoutException e) {
                if (future != null) future.cancel(true);
                failedTasks.incrementAndGet();
                state("*", SpiderProtocol.State.TIMEOUT, "proxy timeout " + p.getTimeoutMs() + "ms");
                return error(out, SpiderProtocol.Code.ERR_TIMEOUT, "proxy timeout after " + p.getTimeoutMs() + "ms");
            } catch (ExecutionException e) {
                failedTasks.incrementAndGet();
                return error(out, SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(e.getCause()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return error(out, SpiderProtocol.Code.ERR_INTERRUPTED, "interrupted");
            } catch (Throwable e) {
                failedTasks.incrementAndGet();
                return error(out, SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(e));
            } finally {
                aliveTasks.decrementAndGet();
                state("*", SpiderProtocol.State.IDLE, "proxy cost=" + (SystemClock.elapsedRealtime() - t0) + "ms");
            }
        }

        @Override
        public int setPolicy(Bundle bundle) {
            policy = SpiderPolicy.from(bundle);
            netGuard.update(policy);
            log(Log.INFO, TAG, "policy updated: " + policy.toBundle());
            return SpiderProtocol.Code.OK;
        }

        @Override
        public Bundle status() {
            Bundle b = new Bundle();
            b.putInt(SpiderProtocol.Status.PID, Process.myPid());
            b.putInt(SpiderProtocol.Status.ALIVE_TASKS, aliveTasks.get());
            b.putInt(SpiderProtocol.Status.MEMORY_MB, memoryMb());
            b.putLong(SpiderProtocol.Status.UPTIME_MS, SystemClock.elapsedRealtime() - startAt);
            b.putLong(SpiderProtocol.Status.TOTAL_TASKS, totalTasks.get());
            b.putLong(SpiderProtocol.Status.FAILED_TASKS, failedTasks.get());
            b.putBundle(SpiderProtocol.Status.POLICY, policy.toBundle());
            b.putStringArray(SpiderProtocol.Status.SPIDERS, new String[0]);
            return b;
        }

        @Override
        public void registerCallback(ISpiderCallback callback) {
            if (callback != null) callbacks.register(callback);
        }

        @Override
        public void unregisterCallback(ISpiderCallback callback) {
            if (callback != null) callbacks.unregister(callback);
        }
    };

    /* ------------------------------------------------------------------ */
    /* 任务执行                                                            */
    /* ------------------------------------------------------------------ */

    private Bundle runTask(String key, String api, String ext, String jar, String method, Bundle args) {
        Bundle out = new Bundle();
        long t0 = SystemClock.elapsedRealtime();
        Spider spider;
        try {
            spider = SpiderHost.get().getSpider(key, api, ext, jar);
        } catch (Throwable e) {
            return error(out, SpiderProtocol.Code.ERR_UNKNOWN, "getSpider failed: " + e);
        }
        if (spider == null || spider instanceof SpiderNull) {
            return error(out, SpiderProtocol.Code.ERR_NO_SPIDER, "no spider for " + api);
        }
        try {
            switch (method) {
                case SpiderProtocol.Method.INIT:
                    spider.init(this, args.getString(SpiderProtocol.Arg.EXTEND));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.VOID);
                    break;
                case SpiderProtocol.Method.HOME_CONTENT:
                    out.putString(SpiderProtocol.Result.VALUE, spider.homeContent(args.getBoolean(SpiderProtocol.Arg.FILTER)));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.STRING);
                    break;
                case SpiderProtocol.Method.HOME_VIDEO_CONTENT:
                    out.putString(SpiderProtocol.Result.VALUE, spider.homeVideoContent());
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.STRING);
                    break;
                case SpiderProtocol.Method.CATEGORY_CONTENT:
                    out.putString(SpiderProtocol.Result.VALUE, spider.categoryContent(
                            args.getString(SpiderProtocol.Arg.TID),
                            args.getString(SpiderProtocol.Arg.PG),
                            args.getBoolean(SpiderProtocol.Arg.FILTER),
                            toMap(args.getBundle(SpiderProtocol.Arg.EXTEND_MAP))));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.STRING);
                    break;
                case SpiderProtocol.Method.DETAIL_CONTENT:
                    out.putString(SpiderProtocol.Result.VALUE, spider.detailContent(args.getStringArrayList(SpiderProtocol.Arg.IDS)));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.STRING);
                    break;
                case SpiderProtocol.Method.SEARCH_CONTENT:
                    out.putString(SpiderProtocol.Result.VALUE, spider.searchContent(
                            args.getString(SpiderProtocol.Arg.KEY),
                            args.getBoolean(SpiderProtocol.Arg.QUICK)));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.STRING);
                    break;
                case SpiderProtocol.Method.SEARCH_CONTENT_PG:
                    out.putString(SpiderProtocol.Result.VALUE, spider.searchContent(
                            args.getString(SpiderProtocol.Arg.KEY),
                            args.getBoolean(SpiderProtocol.Arg.QUICK),
                            args.getString(SpiderProtocol.Arg.PG)));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.STRING);
                    break;
                case SpiderProtocol.Method.PLAYER_CONTENT:
                    out.putString(SpiderProtocol.Result.VALUE, spider.playerContent(
                            args.getString(SpiderProtocol.Arg.FLAG),
                            args.getString(SpiderProtocol.Arg.ID),
                            args.getStringArrayList(SpiderProtocol.Arg.VIP_FLAGS)));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.STRING);
                    break;
                case SpiderProtocol.Method.LIVE_CONTENT:
                    out.putString(SpiderProtocol.Result.VALUE, spider.liveContent(args.getString(SpiderProtocol.Arg.URL)));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.STRING);
                    break;
                case SpiderProtocol.Method.MANUAL_VIDEO_CHECK:
                    out.putBoolean(SpiderProtocol.Result.FLAG, spider.manualVideoCheck());
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.BOOLEAN);
                    break;
                case SpiderProtocol.Method.IS_VIDEO_FORMAT:
                    out.putBoolean(SpiderProtocol.Result.FLAG, spider.isVideoFormat(args.getString(SpiderProtocol.Arg.URL)));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.BOOLEAN);
                    break;
                case SpiderProtocol.Method.ACTION:
                    out.putString(SpiderProtocol.Result.VALUE, spider.action(args.getString(SpiderProtocol.Arg.ACTION)));
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.STRING);
                    break;
                case SpiderProtocol.Method.PROXY:
                    fillProxy(out, spider, args);
                    break;
                case SpiderProtocol.Method.DESTROY:
                    spider.destroy();
                    out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.VOID);
                    break;
                default:
                    return error(out, SpiderProtocol.Code.ERR_BAD_ARGS, "unknown method: " + method);
            }
            out.putInt(SpiderProtocol.Result.CODE, SpiderProtocol.Code.OK);
        } catch (Throwable e) {
            log(Log.ERROR, key, method + " failed: " + e);
            return error(out, SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(e));
        }
        out.putLong(SpiderProtocol.Result.COST_MS, SystemClock.elapsedRealtime() - t0);
        return out;
    }

    /** 本地 HTTP 代理入口，路由规则与改造前的 BaseLoader.proxy 一致。 */
    private Bundle runProxy(Bundle params) {
        Bundle out = new Bundle();
        long t0 = SystemClock.elapsedRealtime();
        try {
            fillProxyResult(out, SpiderHost.get().proxy(toMap(params)));
            if (!out.containsKey(SpiderProtocol.Result.CODE)) {
                out.putInt(SpiderProtocol.Result.CODE, SpiderProtocol.Code.OK);
            }
        } catch (Throwable e) {
            log(Log.ERROR, TAG, "proxy failed: " + e);
            return error(out, SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(e));
        }
        out.putLong(SpiderProtocol.Result.COST_MS, SystemClock.elapsedRealtime() - t0);
        return out;
    }

    /**
     * proxy 的响应体用管道跨进程传输：Binder 单次事务上限约 1MB，
     * 视频流远超，必须走 ParcelFileDescriptor。
     */
    private void fillProxy(Bundle out, Spider spider, Bundle args) throws Exception {
        Map<String, String> params = toMap(args.getBundle(SpiderProtocol.Arg.PARAMS));
        fillProxyResult(out, spider.proxy(params));
    }

    private void fillProxyResult(Bundle out, Object[] rs) throws Exception {
        if (rs == null || rs.length < 3) {
            out.putInt(SpiderProtocol.Result.CODE, SpiderProtocol.Code.ERR_UNKNOWN);
            out.putString(SpiderProtocol.Result.ERROR, "invalid proxy response");
            return;
        }
        int status = rs[0] instanceof Integer ? (Integer) rs[0] : 200;
        String mime = rs[1] instanceof String ? (String) rs[1] : "application/octet-stream";
        InputStream body = rs[2] instanceof InputStream ? (InputStream) rs[2] : null;
        Map<String, String> headers = rs.length > 3 && rs[3] instanceof Map ? castMap(rs[3]) : null;

        out.putInt(SpiderProtocol.Result.CODE, SpiderProtocol.Code.OK);
        out.putString(SpiderProtocol.Result.KIND, SpiderProtocol.Kind.PROXY);
        out.putInt(SpiderProtocol.Result.STATUS, status);
        out.putString(SpiderProtocol.Result.MIME, mime);
        if (headers != null) out.putBundle(SpiderProtocol.Result.HEADERS, toBundle(headers));
        if (body == null) return;

        ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
        out.putParcelable(SpiderProtocol.Result.PFD, pipe[0]);
        pump.submit(() -> pumpBody(body, pipe[1], policy.getMaxBodyBytes()));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> castMap(Object o) {
        return (Map<String, String>) o;
    }

    private void pumpBody(InputStream src, ParcelFileDescriptor writeEnd, int maxBytes) {
        try {
            // 注意：只 flush 不 close，fd 的关闭交给 ParcelFileDescriptor，避免双重关闭
            OutputStream os = new FileOutputStream(writeEnd.getFileDescriptor());
            byte[] buf = new byte[16 * 1024];
            int n;
            long total = 0;
            while ((n = src.read(buf)) != -1) {
                total += n;
                if (maxBytes > 0 && total > maxBytes) {
                    log(Log.WARN, TAG, "proxy body truncated at " + maxBytes + " bytes");
                    break;
                }
                os.write(buf, 0, n);
            }
            os.flush();
        } catch (IOException e) {
            // 客户端提前关闭读端会走到这里，属正常
            log(Log.DEBUG, TAG, "pump closed: " + e.getMessage());
        } catch (Throwable e) {
            log(Log.ERROR, TAG, "pump failed: " + e);
        } finally {
            closeQuietly(src);
            closeQuietly(writeEnd);
        }
    }

    private static void closeQuietly(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable ignored) {
        }
    }

    /* ------------------------------------------------------------------ */
    /* 工具                                                                */
    /* ------------------------------------------------------------------ */

    private boolean checkMemory(Bundle out) {
        int mb = memoryMb();
        if (mb <= policy.getMaxMemoryMb()) return true;
        log(Log.WARN, TAG, "memory " + mb + "MB over limit " + policy.getMaxMemoryMb() + "MB, clearing");
        SpiderHost.get().clear();
        int after = memoryMb();
        if (after > policy.getMaxMemoryMb()) {
            state("*", SpiderProtocol.State.OOM, after + "MB");
            error(out, SpiderProtocol.Code.ERR_MEMORY, after + "MB > " + policy.getMaxMemoryMb() + "MB");
            return false;
        }
        return true;
    }

    private static int memoryMb() {
        try {
            Debug.MemoryInfo mi = new Debug.MemoryInfo();
            Debug.getMemoryInfo(mi);
            return mi.getTotalPss() / 1024;
        } catch (Throwable e) {
            return 0;
        }
    }

    private static Bundle error(Bundle out, int code, String message) {
        out.putInt(SpiderProtocol.Result.CODE, code);
        out.putString(SpiderProtocol.Result.ERROR, message);
        return out;
    }

    private static HashMap<String, String> toMap(Bundle bundle) {
        HashMap<String, String> map = new HashMap<>();
        if (bundle == null) return map;
        for (String k : bundle.keySet()) {
            Object v = bundle.get(k);
            if (v != null) map.put(k, String.valueOf(v));
        }
        return map;
    }

    private static Bundle toBundle(Map<String, String> map) {
        Bundle b = new Bundle();
        if (map == null) return b;
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) b.putString(e.getKey(), e.getValue());
        }
        return b;
    }

    private void log(int level, String tag, String message) {
        Log.println(level, "spider:" + tag, message);
        int n = callbacks.beginBroadcast();
        try {
            for (int i = 0; i < n; i++) {
                try {
                    callbacks.getBroadcastItem(i).onLog(level, tag, message);
                } catch (RemoteException | RuntimeException ignored) {
                }
            }
        } finally {
            callbacks.finishBroadcast();
        }
    }

    private void state(String key, int state, String detail) {
        Log.d("spider:" + TAG, "state " + key + " -> " + SpiderProtocol.stateName(state) + " (" + detail + ")");
        int n = callbacks.beginBroadcast();
        try {
            for (int i = 0; i < n; i++) {
                try {
                    callbacks.getBroadcastItem(i).onStateChanged(key, state, detail);
                } catch (RemoteException | RuntimeException ignored) {
                }
            }
        } finally {
            callbacks.finishBroadcast();
        }
    }
}
