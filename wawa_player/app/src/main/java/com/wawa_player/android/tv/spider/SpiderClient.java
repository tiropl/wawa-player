package com.wawa_player.android.tv.spider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.wawa_player.android.tv.App;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 主进程侧的 spider 管家。
 * <p>
 * 负责：绑定 :spider 服务、进程死亡重连、客户端侧超时兜底、
 * 卡死后杀死并回收进程、对外提供简洁同步 API。
 */
public class SpiderClient {

    private static final String TAG = "SpiderClient";

    /** 客户端侧兜底超时：服务端自杀式卡死时，这里兜住不让主进程被拖死。 */
    private static final long CALL_TIMEOUT_MS = 30_000L;
    private static final long BIND_TIMEOUT_MS = 5_000L;

    private final Context context;
    private final Object lock = new Object();
    private final ExecutorService caller = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "spider-caller");
        t.setDaemon(true);
        return t;
    });

    private volatile ISpiderService service;
    private volatile int pid = -1;
    private volatile boolean binding;

    private SpiderClient() {
        this.context = App.get();
    }

    public static SpiderClient get() {
        return Loader.INSTANCE;
    }

    private final IBinder.DeathRecipient death = () -> {
        Log.w(TAG, "spider process died");
        synchronized (lock) {
            service = null;
            binding = false;
            pid = -1;
            lock.notifyAll();
        }
    };

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            ISpiderService s = ISpiderService.Stub.asInterface(binder);
            int p = -1;
            try {
                binder.linkToDeath(death, 0);
                p = s.status().getInt(SpiderProtocol.Status.PID, -1);
            } catch (RemoteException e) {
                Log.w(TAG, "linkToDeath failed", e);
            }
            synchronized (lock) {
                service = s;
                pid = p;
                binding = false;
                lock.notifyAll();
            }
            Log.i(TAG, "spider service connected, pid=" + p);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            synchronized (lock) {
                service = null;
                binding = false;
                pid = -1;
                lock.notifyAll();
            }
        }
    };

    /* ------------------------------------------------------------------ */
    /* 绑定                                                                */
    /* ------------------------------------------------------------------ */

    private ISpiderService awaitService() {
        ISpiderService s = service;
        if (s != null) return s;
        synchronized (lock) {
            if (service != null) return service;
            if (!binding) {
                binding = true;
                try {
                    context.bindService(new Intent(context, SpiderService.class),
                            conn, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
                } catch (Throwable e) {
                    Log.e(TAG, "bindService failed", e);
                    binding = false;
                    return null;
                }
            }
            long deadline = SystemClock.elapsedRealtime() + BIND_TIMEOUT_MS;
            while (service == null && SystemClock.elapsedRealtime() < deadline) {
                long wait = deadline - SystemClock.elapsedRealtime();
                if (wait <= 0) break;
                try {
                    lock.wait(wait);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return service;
        }
    }

    /* ------------------------------------------------------------------ */
    /* 对外 API                                                            */
    /* ------------------------------------------------------------------ */

    public boolean isAlive() {
        return service != null;
    }

    /**
     * 下发一次调用。永不抛异常，失败以 code 返回。
     * 客户端超时或远端死亡时会自动触发进程回收。
     */
    public Bundle call(String key, String api, String ext, String jar, String method, Bundle args) {
        ISpiderService s = awaitService();
        if (s == null) return err(SpiderProtocol.Code.ERR_DEAD, "spider service unavailable");
        try {
            Future<Bundle> future = caller.submit(() -> s.call(key, api, ext, jar, method, args));
            return future.get(CALL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Log.w(TAG, "call timeout: " + method);
            recycle();
            return err(SpiderProtocol.Code.ERR_TIMEOUT, "client timeout " + CALL_TIMEOUT_MS + "ms");
        } catch (java.util.concurrent.ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DeadObjectException) {
                Log.w(TAG, "dead object, recycling");
                recycle();
                return err(SpiderProtocol.Code.ERR_DEAD, "spider process dead");
            }
            Log.w(TAG, "call failed: " + cause);
            return err(SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(cause));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return err(SpiderProtocol.Code.ERR_INTERRUPTED, "interrupted");
        } catch (Throwable e) {
            return err(SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(e));
        }
    }

    /** 本地 HTTP 代理，返回结构与 call(PROXY) 一致。 */
    public Bundle proxy(Bundle params) {
        ISpiderService s = awaitService();
        if (s == null) return err(SpiderProtocol.Code.ERR_DEAD, "spider service unavailable");
        try {
            Future<Bundle> f = caller.submit(() -> s.proxy(params));
            return f.get(CALL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Log.w(TAG, "proxy timeout");
            recycle();
            return err(SpiderProtocol.Code.ERR_TIMEOUT, "client timeout");
        } catch (java.util.concurrent.ExecutionException e) {
            if (e.getCause() instanceof DeadObjectException) {
                recycle();
                return err(SpiderProtocol.Code.ERR_DEAD, "spider process dead");
            }
            return err(SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(e.getCause()));
        } catch (Throwable e) {
            return err(SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(e));
        }
    }

    public int parseJar(String jar, boolean recent) {
        return simple(() -> awaitServiceSafe().parseJar(jar, recent));
    }

    public int setRecent(String key, String api, String jar) {
        return simple(() -> awaitServiceSafe().setRecent(key, api, jar));
    }

    /** 返回 JSON 字符串，失败返回 null。 */
    public String jsonExt(String key, String jxsJson, String url) {
        ISpiderService s = awaitService();
        if (s == null) return null;
        try {
            return caller.submit(() -> s.jsonExt(key, jxsJson, url)).get(CALL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            Log.w(TAG, "jsonExt failed: " + e);
            return null;
        }
    }

    public String jsonExtMix(String flag, String key, String name, String jxsJson, String url) {
        ISpiderService s = awaitService();
        if (s == null) return null;
        try {
            return caller.submit(() -> s.jsonExtMix(flag, key, name, jxsJson, url)).get(CALL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            Log.w(TAG, "jsonExtMix failed: " + e);
            return null;
        }
    }

    public int start(String key, String api, String ext, String jar) {
        return simple(() -> awaitServiceSafe().start(key, api, ext, jar));
    }

    public int pause(String key) {
        return simple(() -> awaitServiceSafe().pause(key));
    }

    public int resume(String key) {
        return simple(() -> awaitServiceSafe().resume(key));
    }

    public int stop(String key) {
        return simple(() -> awaitServiceSafe().stop(key));
    }

    public int stopAll() {
        return simple(() -> awaitServiceSafe().stopAll());
    }

    public int setPolicy(Bundle policy) {
        return simple(() -> awaitServiceSafe().setPolicy(policy));
    }

    public Bundle status() {
        ISpiderService s = awaitService();
        if (s == null) return err(SpiderProtocol.Code.ERR_DEAD, "spider service unavailable");
        try {
            return caller.submit(s::status).get(CALL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            return err(SpiderProtocol.Code.ERR_UNKNOWN, String.valueOf(e));
        }
    }

    public void registerCallback(ISpiderCallback callback) {
        ISpiderService s = awaitService();
        if (s == null || callback == null) return;
        try {
            s.registerCallback(callback);
        } catch (Throwable e) {
            Log.w(TAG, "registerCallback failed", e);
        }
    }

    public void unregisterCallback(ISpiderCallback callback) {
        ISpiderService s = service;
        if (s == null || callback == null) return;
        try {
            s.unregisterCallback(callback);
        } catch (Throwable ignored) {
        }
    }

    /**
     * 回收 spider 进程：解绑 -> 杀进程 -> 下次调用时自动重连。
     * 用于超时、卡死、状态异常等场景。
     */
    public void recycle() {
        int p = pid;
        synchronized (lock) {
            ISpiderService s = service;
            if (s != null) {
                try {
                    s.asBinder().unlinkToDeath(death, 0);
                } catch (Throwable ignored) {
                }
            }
            service = null;
            binding = false;
            pid = -1;
            try {
                context.unbindService(conn);
            } catch (Throwable ignored) {
            }
        }
        if (p <= 0) {
            // 没缓存到 pid，退化为按进程名杀
            android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            try {
                if (am != null) am.killBackgroundProcesses(context.getPackageName() + SpiderProtocol.PROCESS_NAME);
            } catch (Throwable ignored) {
            }
            return;
        }
        try {
            Process.killProcess(p);
            Log.w(TAG, "killed spider process " + p);
        } catch (Throwable e) {
            Log.w(TAG, "killProcess failed", e);
        }
    }

    /* ------------------------------------------------------------------ */
    /* 内部                                                                */
    /* ------------------------------------------------------------------ */

    private ISpiderService awaitServiceSafe() throws IllegalStateException {
        ISpiderService s = awaitService();
        if (s == null) throw new IllegalStateException("spider service unavailable");
        return s;
    }

    private int simple(java.util.concurrent.Callable<Integer> action) {
        try {
            return caller.submit(action).get(CALL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Log.w(TAG, "control call timeout");
            recycle();
            return SpiderProtocol.Code.ERR_TIMEOUT;
        } catch (Throwable e) {
            Log.w(TAG, "control call failed: " + e);
            return SpiderProtocol.Code.ERR_UNKNOWN;
        }
    }

    private static Bundle err(int code, String message) {
        Bundle b = new Bundle();
        b.putInt(SpiderProtocol.Result.CODE, code);
        b.putString(SpiderProtocol.Result.ERROR, message);
        return b;
    }

    private static class Loader {
        static volatile SpiderClient INSTANCE = new SpiderClient();
    }
}
