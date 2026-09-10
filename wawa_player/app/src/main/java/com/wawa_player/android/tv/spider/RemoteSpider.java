package com.wawa_player.android.tv.spider;

import android.content.Context;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.github.catvod.crawler.Spider;
import com.wawa_player.android.tv.BuildConfig;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主进程侧的 spider 代理：看起来就是一个普通的 {@link Spider}，
 * 实际每次调用都通过 AIDL 下发到 :spider 进程执行。
 * <p>
 * 好处是 {@code Site} / {@code Live} 等上层代码完全不用改。
 */
public class RemoteSpider extends Spider {

    private static final String TAG = "RemoteSpider";

    private final String key;
    private final String api;
    private final String ext;
    private final String jar;

    public RemoteSpider(String key, String api, String ext, String jar) {
        this.key = key;
        this.api = api;
        this.ext = ext;
        this.jar = jar;
        this.siteKey = key;
    }

    /* ---------------- 生命周期 ---------------- */

    @Override
    public void init(Context context, String extend) throws Exception {
        Bundle args = new Bundle();
        args.putString(SpiderProtocol.Arg.EXTEND, extend);
        call(SpiderProtocol.Method.INIT, args);
    }

    @Override
    public void destroy() {
        call(SpiderProtocol.Method.DESTROY, new Bundle());
    }

    /* ---------------- 内容抓取 ---------------- */

    @Override
    public String homeContent(boolean filter) throws Exception {
        Bundle args = new Bundle();
        args.putBoolean(SpiderProtocol.Arg.FILTER, filter);
        return string(call(SpiderProtocol.Method.HOME_CONTENT, args), "");
    }

    @Override
    public String homeVideoContent() throws Exception {
        return string(call(SpiderProtocol.Method.HOME_VIDEO_CONTENT, new Bundle()), "");
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        Bundle args = new Bundle();
        args.putString(SpiderProtocol.Arg.TID, tid);
        args.putString(SpiderProtocol.Arg.PG, pg);
        args.putBoolean(SpiderProtocol.Arg.FILTER, filter);
        if (extend != null) args.putBundle(SpiderProtocol.Arg.EXTEND_MAP, toBundle(extend));
        return string(call(SpiderProtocol.Method.CATEGORY_CONTENT, args), "");
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Bundle args = new Bundle();
        if (ids != null) args.putStringArrayList(SpiderProtocol.Arg.IDS, new java.util.ArrayList<>(ids));
        return string(call(SpiderProtocol.Method.DETAIL_CONTENT, args), "");
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        Bundle args = new Bundle();
        args.putString(SpiderProtocol.Arg.KEY, key);
        args.putBoolean(SpiderProtocol.Arg.QUICK, quick);
        return string(call(SpiderProtocol.Method.SEARCH_CONTENT, args), "");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        Bundle args = new Bundle();
        args.putString(SpiderProtocol.Arg.KEY, key);
        args.putBoolean(SpiderProtocol.Arg.QUICK, quick);
        args.putString(SpiderProtocol.Arg.PG, pg);
        return string(call(SpiderProtocol.Method.SEARCH_CONTENT_PG, args), "");
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        Bundle args = new Bundle();
        args.putString(SpiderProtocol.Arg.FLAG, flag);
        args.putString(SpiderProtocol.Arg.ID, id);
        if (vipFlags != null) args.putStringArrayList(SpiderProtocol.Arg.VIP_FLAGS, new java.util.ArrayList<>(vipFlags));
        return string(call(SpiderProtocol.Method.PLAYER_CONTENT, args), "");
    }

    @Override
    public String liveContent(String url) throws Exception {
        Bundle args = new Bundle();
        args.putString(SpiderProtocol.Arg.URL, url);
        return string(call(SpiderProtocol.Method.LIVE_CONTENT, args), "");
    }

    @Override
    public boolean manualVideoCheck() throws Exception {
        return call(SpiderProtocol.Method.MANUAL_VIDEO_CHECK, new Bundle())
                .getBoolean(SpiderProtocol.Result.FLAG, false);
    }

    @Override
    public boolean isVideoFormat(String url) throws Exception {
        Bundle args = new Bundle();
        args.putString(SpiderProtocol.Arg.URL, url);
        return call(SpiderProtocol.Method.IS_VIDEO_FORMAT, args)
                .getBoolean(SpiderProtocol.Result.FLAG, false);
    }

    @Override
    public String action(String action) throws Exception {
        Bundle args = new Bundle();
        args.putString(SpiderProtocol.Arg.ACTION, action);
        return string(call(SpiderProtocol.Method.ACTION, args), null);
    }

    /* ---------------- 代理 ---------------- */

    @Override
    public Object[] proxy(Map<String, String> params) throws Exception {
        Bundle args = new Bundle();
        if (params != null) args.putBundle(SpiderProtocol.Arg.PARAMS, toBundle(params));
        Bundle result = call(SpiderProtocol.Method.PROXY, args);
        if (result.getInt(SpiderProtocol.Result.CODE, SpiderProtocol.Code.ERR_UNKNOWN) != SpiderProtocol.Code.OK) {
            return null;
        }
        ParcelFileDescriptor pfd = result.getParcelable(SpiderProtocol.Result.PFD);
        InputStream body = pfd != null ? new ParcelFileDescriptor.AutoCloseInputStream(pfd) : null;
        return new Object[]{
                result.getInt(SpiderProtocol.Result.STATUS, 200),
                result.getString(SpiderProtocol.Result.MIME, "application/octet-stream"),
                body,
                toMap(result.getBundle(SpiderProtocol.Result.HEADERS))
        };
    }

    /* ---------------- 内部 ---------------- */

    private Bundle call(String method, Bundle args) {
        Bundle result = SpiderClient.get().call(key, api, ext, jar, method, args);
        if (result == null) result = new Bundle();
        int code = result.getInt(SpiderProtocol.Result.CODE, SpiderProtocol.Code.ERR_UNKNOWN);
        if (code != SpiderProtocol.Code.OK) {
            Log.w(TAG, key + "." + method + " -> " + code + " " + result.getString(SpiderProtocol.Result.ERROR));
        } else if (BuildConfig.DEBUG) {
            Log.d(TAG, key + "." + method + " cost=" + result.getLong(SpiderProtocol.Result.COST_MS) + "ms");
        }
        return result;
    }

    private static String string(Bundle result, String fallback) {
        String v = result.getString(SpiderProtocol.Result.VALUE);
        return v != null ? v : fallback;
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
}
