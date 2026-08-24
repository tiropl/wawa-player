package com.wawa_player.android.tv.api.config;

import android.text.TextUtils;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.Decoder;
import com.wawa_player.android.tv.bean.Config;
import com.wawa_player.android.tv.event.ConfigEvent;
import com.wawa_player.android.tv.impl.Callback;
import com.wawa_player.android.tv.server.Server;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.Task;
import com.wawa_player.android.tv.utils.UrlUtil;
import com.github.catvod.bean.Header;
import com.github.catvod.bean.Proxy;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Prefers;
import com.github.catvod.utils.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

abstract class BaseConfig {

    public static final int VOD = 0;
    public static final int LIVE = 1;
    public static final int WALL = 2;

    protected static final long TIMEOUT = 15000;

    private final AtomicInteger taskId = new AtomicInteger(0);

    protected boolean sync;
    protected boolean silent;
    protected volatile Config config;
    private volatile Future<?> future;
    private boolean cacheUsed;

    protected abstract String getTag();

    protected abstract Config defaultConfig();

    protected abstract void load(Config config) throws Throwable;

    protected abstract boolean isLoaded();

    public synchronized void ensureLoaded() {
        try {
            if (isLoaded()) return;
            if (config == null) config = defaultConfig();
            Server.get().start();
            load(config);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void postEvent() {
        ConfigEvent.common();
    }

    public void silent(boolean silent) {
        this.silent = silent;
    }

    public boolean needSync(String url) {
        return sync || config == null || TextUtils.isEmpty(config.getUrl()) || url.equals(config.getUrl());
    }

    public Config getConfig() {
        return config == null ? defaultConfig() : config;
    }

    protected void setHeaders(List<Header> headers) {
        OkHttp.responseInterceptor().addAll(headers);
    }

    protected void setProxy(List<Proxy> proxy) {
        OkHttp.selector().addAll(proxy);
    }

    protected void setHosts(List<String> hosts) {
        OkHttp.dns().addAll(hosts);
    }

    public void load(Callback callback) {
        int id = taskId.incrementAndGet();
        if (future != null && !future.isDone()) future.cancel(true);
        future = Task.submit(() -> loadConfig(id, config, callback));
        callback.start();
    }

    protected void loadConfig(int id, Config config, Callback callback) {
        try {
            Server.get().start();
            OkHttp.cancel(getTag());
            load(config);
            if (taskId.get() != id) return;
            if (config.equals(this.config)) {
                if (cacheUsed) config.save();
                else {
                    config.update();
                    if (Setting.getCacheExpire() > 0)
                        Prefers.put(cacheTimeKey(config), System.currentTimeMillis());
                }
            }
            App.post(() -> Notify.show(config.getNotice()));
            App.post(callback::success);
        } catch (Throwable e) {
            e.printStackTrace();
            if (isCanceled(e)) return;
            if (taskId.get() != id) return;
            if (TextUtils.isEmpty(config.getUrl())) App.post(() -> callback.error(""));
            else App.post(() -> callback.error(Notify.getError(R.string.error_config_get, e)));
        } finally {
            if (taskId.get() == id) postEvent();
            silent = false;
        }
    }

    protected boolean isCanceled(Throwable e) {
        if (isTimeout(e)) return false;
        if ("Canceled".equals(e.getMessage())) return true;
        if (e instanceof InterruptedException) return true;
        if (e instanceof InterruptedIOException) return true;
        return e.getCause() instanceof InterruptedIOException && !isTimeout(e.getCause());
    }

    private boolean isTimeout(Throwable e) {
        return e instanceof SocketTimeoutException || "timeout".equals(e.getMessage());
    }

    private static String cacheTimeKey(Config config) {
        return "config_cache_time_" + config.getType() + "_" + Util.md5(config.getUrl());
    }

    protected String fetchJson(Config config) throws Throwable {
        cacheUsed = false;
        long expire = Setting.getCacheExpire() * 60L * 60L * 1000L;
        String json = config.getJson();
        long cacheTime = Prefers.getLong(cacheTimeKey(config));
        long age = cacheTime > 0 ? System.currentTimeMillis() - cacheTime : -1;
        if (expire > 0 && !TextUtils.isEmpty(json) && age >= 0 && age < expire) {
            cacheUsed = true;
            return json;
        }
        try {
            json = Decoder.getJson(UrlUtil.convert(config.getUrl()), getTag(), TIMEOUT);
            config.json(json);
            return json;
        } catch (Throwable e) {
            if (expire > 0 && !TextUtils.isEmpty(json)) {
                cacheUsed = true;
                App.post(() -> Notify.show(R.string.setting_cache_fallback));
                return json;
            }
            throw e;
        }
    }

    protected JsonArray fetchArray(JsonObject object, String key) {
        if (!object.has(key)) return new JsonArray();
        JsonElement element = object.get(key);
        if (element.isJsonObject()) return new JsonArray();
        if (element.isJsonPrimitive()) element = fetch(element.getAsString());
        JsonArray result = new JsonArray();
        for (JsonElement item : element.getAsJsonArray()) {
            if (item.isJsonPrimitive()) result.addAll(fetch(item.getAsString()));
            else if (item.isJsonObject()) result.add(item);
        }
        return result;
    }

    private JsonArray fetch(String url) {
        try {
            JsonElement parsed = Json.parse(OkHttp.string(UrlUtil.convert(url)));
            return parsed.isJsonArray() ? parsed.getAsJsonArray() : new JsonArray();
        } catch (Exception e) {
            return new JsonArray();
        }
    }
}
