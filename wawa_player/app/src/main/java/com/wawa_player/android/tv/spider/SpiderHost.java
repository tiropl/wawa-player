package com.wawa_player.android.tv.spider;

import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderNull;
import com.github.catvod.utils.Util;
import com.wawa_player.android.tv.api.loader.JarLoader;
import com.wawa_player.android.tv.api.loader.JsLoader;
import com.wawa_player.android.tv.api.loader.PyLoader;
import com.wawa_player.android.tv.utils.Task;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

/**
 * 只在 :spider 进程内使用的爬虫宿主。
 * <p>
 * 存在的意义：{@code BaseLoader} 已被改造成「主进程 -> 远程」的客户端门面，
 * 如果 spider 进程里再调 BaseLoader 就会自己连自己。因此把原来 BaseLoader
 * 的真实加载逻辑搬到这儿，仅供 {@link SpiderService} 直接调用。
 */
public class SpiderHost {

    private final JarLoader jarLoader;
    private final PyLoader pyLoader;
    private final JsLoader jsLoader;

    private SpiderHost() {
        jarLoader = new JarLoader();
        pyLoader = new PyLoader();
        jsLoader = new JsLoader();
    }

    public static SpiderHost get() {
        return Loader.INSTANCE;
    }

    public static boolean isJs(String api) {
        return api != null && api.contains(".js");
    }

    public static boolean isPy(String api) {
        return api != null && api.contains(".py");
    }

    public static boolean isCsp(String api) {
        return api != null && api.startsWith("csp_");
    }

    public Spider getSpider(String key, String api, String ext, String jar) {
        if (api == null) return new SpiderNull();
        if (isPy(api)) return pyLoader.getSpider(key, api, ext);
        if (isJs(api)) return jsLoader.getSpider(key, api, ext, jar);
        if (isCsp(api)) return jarLoader.getSpider(key, api, ext, jar);
        return new SpiderNull();
    }

    public DexClassLoader dex(String jar) {
        return jarLoader.dex(jar);
    }

    public void parseJar(String jar, boolean recent) {
        if (TextUtils.isEmpty(jar)) return;
        String key = Util.md5(jar);
        jarLoader.parseJar(key, jar);
        if (recent) jarLoader.setRecent(key);
    }

    public void setRecent(String key, String api, String jar) {
        if (isJs(api)) jsLoader.setRecent(key);
        else if (isPy(api)) pyLoader.setRecent(key);
        else if (isCsp(api)) jarLoader.setRecent(Util.md5(jar));
    }

    public Object[] proxy(Map<String, String> params) throws Exception {
        if (params == null) return null;
        if (params.containsKey("siteKey")) {
            Spider spider = getSpider(params.get("siteKey"), params.get("api"), params.get("ext"), params.get("jar"));
            return spider.proxy(params);
        }
        if ("js".equals(params.get("do"))) return jsLoader.proxy(params);
        if ("py".equals(params.get("do"))) return pyLoader.proxy(params);
        return jarLoader.proxy(params);
    }

    public JSONObject jsonExt(String key, LinkedHashMap<String, String> jxs, String url) throws Throwable {
        return jarLoader.jsonExt(key, jxs, url);
    }

    public JSONObject jsonExtMix(String flag, String key, String name, LinkedHashMap<String, HashMap<String, String>> jxs, String url) throws Throwable {
        return jarLoader.jsonExtMix(flag, key, name, jxs, url);
    }

    /** 销毁单个站点的 spider 实例，dex 与 jar 缓存保留以便复用。 */
    public void destroy(String key) {
        try {
            jarLoader.remove(key);
            jsLoader.remove(key);
            pyLoader.remove(key);
        } catch (Throwable ignored) {
        }
    }

    public void clear() {
        Task.execute(() -> {
            jarLoader.clear();
            pyLoader.clear();
            jsLoader.clear();
        });
    }

    private static class Loader {
        static volatile SpiderHost INSTANCE = new SpiderHost();
    }
}
