package com.wawa_player.android.tv.api.loader;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderNull;
import com.wawa_player.android.tv.api.config.LiveConfig;
import com.wawa_player.android.tv.api.config.VodConfig;
import com.wawa_player.android.tv.bean.Live;
import com.wawa_player.android.tv.bean.Site;
import com.wawa_player.android.tv.spider.RemoteSpider;
import com.wawa_player.android.tv.spider.SpiderClient;
import com.wawa_player.android.tv.spider.SpiderJson;
import com.wawa_player.android.tv.spider.SpiderProtocol;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 爬虫加载门面。
 * <p>
 * 改造后不再直接持有 Jar/JS/Py 三套 Loader，全部下发到 :spider 独立进程执行，
 * 这里只保留上层（Site / Live / Proxy / ParseJob）已经依赖的方法签名，
 * 因此上层代码零改动。
 */
public class BaseLoader {

    private BaseLoader() {
    }

    public static BaseLoader get() {
        return Loader.INSTANCE;
    }

    /** 清理远端全部 spider。 */
    public void clear() {
        SpiderClient.get().stopAll();
    }

    public Spider getSpider(String key, String api, String ext, String jar) {
        if (api == null || api.isEmpty()) return new SpiderNull();
        return new RemoteSpider(key, api, ext, jar);
    }

    public Spider getSpider(String key) {
        Site site = VodConfig.get().getSite(key);
        Live live = LiveConfig.get().getLive(key);
        if (!site.isEmpty()) return site.spider();
        if (!live.isEmpty()) return live.spider();
        return new SpiderNull();
    }

    public void setRecent(String key, String api, String jar) {
        SpiderClient.get().setRecent(key, api, jar);
    }

    public void parseJar(String jar, boolean recent) {
        SpiderClient.get().parseJar(jar, recent);
    }

    /** 本地 HTTP 代理，响应体经 ParcelFileDescriptor 管道回传。 */
    public Object[] proxy(Map<String, String> params) throws Exception {
        Bundle b = SpiderClient.get().proxy(toBundle(params));
        if (b == null) return null;
        int code = b.getInt(SpiderProtocol.Result.CODE, SpiderProtocol.Code.ERR_UNKNOWN);
        if (code != SpiderProtocol.Code.OK) return null;
        ParcelFileDescriptor pfd = b.getParcelable(SpiderProtocol.Result.PFD);
        InputStream in = pfd != null ? new ParcelFileDescriptor.AutoCloseInputStream(pfd) : null;
        return new Object[]{
                b.getInt(SpiderProtocol.Result.STATUS, 200),
                b.getString(SpiderProtocol.Result.MIME, "application/octet-stream"),
                in,
                toMap(b.getBundle(SpiderProtocol.Result.HEADERS))
        };
    }

    public JSONObject jsonExt(String key, LinkedHashMap<String, String> jxs, String url) throws Throwable {
        String json = SpiderClient.get().jsonExt(key, SpiderJson.fromMap(jxs), url);
        return json == null ? null : new JSONObject(json);
    }

    public JSONObject jsonExtMix(String flag, String key, String name, LinkedHashMap<String, HashMap<String, String>> jxs, String url) throws Throwable {
        String json = SpiderClient.get().jsonExtMix(flag, key, name, SpiderJson.fromNestedMap(jxs), url);
        return json == null ? null : new JSONObject(json);
    }

    private static Bundle toBundle(Map<String, String> map) {
        Bundle b = new Bundle();
        if (map == null) return b;
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) b.putString(e.getKey(), e.getValue());
        }
        return b;
    }

    private static Map<String, String> toMap(Bundle bundle) {
        Map<String, String> map = new HashMap<>();
        if (bundle == null) return map;
        for (String k : bundle.keySet()) {
            Object v = bundle.get(k);
            if (v != null) map.put(k, String.valueOf(v));
        }
        return map;
    }

    private static class Loader {
        static volatile BaseLoader INSTANCE = new BaseLoader();
    }
}
