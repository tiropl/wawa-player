package com.wawa_player.android.tv.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Github {
    // 仓库地址配置
    private static final String REPO = "oyonono/wawa-player";

    public static String getRelease() {
        return "https://api.github.com/repos/" + REPO + "/releases/latest";
    }

    public static String findApk(JSONObject release, String mode, String abi) {
        JSONArray assets = release.optJSONArray("assets");
        if (assets == null) return "";
        String exact = mode + "-" + abi + ".apk";
        String fallback = "";
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.optJSONObject(i);
            if (asset == null) continue;
            String name = asset.optString("name");
            String url = asset.optString("browser_download_url");
            if (url.isEmpty()) continue;
            if (exact.equals(name)) return url;
            if (fallback.isEmpty() && name.endsWith(".apk") && name.contains(abi)) fallback = url;
        }
        return fallback;
    }
}
