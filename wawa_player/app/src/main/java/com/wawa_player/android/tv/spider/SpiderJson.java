package com.wawa_player.android.tv.spider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 跨进程传递嵌套 Map 用的 JSON 编解码。
 * <p>
 * Bundle 可以放 Bundle，但表达 {@code Map<String, Map<String,String>>}
 * 很别扭，统一序列化成 JSON 字符串最清晰。
 */
public final class SpiderJson {

    private SpiderJson() {
    }

    public static LinkedHashMap<String, String> toLinkedMap(String json) throws Exception {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (json == null || json.isEmpty()) return map;
        JSONObject o = new JSONObject(json);
        Iterator<String> it = o.keys();
        while (it.hasNext()) {
            String k = it.next();
            map.put(k, o.optString(k));
        }
        return map;
    }

    public static LinkedHashMap<String, HashMap<String, String>> toNestedMap(String json) throws Exception {
        LinkedHashMap<String, HashMap<String, String>> map = new LinkedHashMap<>();
        if (json == null || json.isEmpty()) return map;
        JSONObject o = new JSONObject(json);
        Iterator<String> it = o.keys();
        while (it.hasNext()) {
            String k = it.next();
            JSONObject inner = o.optJSONObject(k);
            HashMap<String, String> m = new HashMap<>();
            if (inner != null) {
                Iterator<String> it2 = inner.keys();
                while (it2.hasNext()) {
                    String k2 = it2.next();
                    m.put(k2, inner.optString(k2));
                }
            }
            map.put(k, m);
        }
        return map;
    }

    public static String fromMap(Map<String, String> map) {
        if (map == null) return "";
        try {
            return new JSONObject(map).toString();
        } catch (Throwable e) {
            return "";
        }
    }

    public static String fromNestedMap(Map<String, ? extends Map<String, String>> map) {
        if (map == null) return "";
        JSONObject o = new JSONObject();
        for (Map.Entry<String, ? extends Map<String, String>> e : map.entrySet()) {
            if (e.getKey() == null) continue;
            try {
                o.put(e.getKey(), e.getValue() == null ? null : new JSONObject(e.getValue()));
            } catch (Throwable ignored) {
            }
        }
        return o.toString();
    }
}
