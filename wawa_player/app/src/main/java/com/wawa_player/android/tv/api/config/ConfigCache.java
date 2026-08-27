package com.wawa_player.android.tv.api.config;

import android.text.TextUtils;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.Decoder;
import com.wawa_player.android.tv.bean.Config;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.UrlUtil;
import com.github.catvod.utils.Prefers;
import com.github.catvod.utils.Util;

/** 线路配置缓存管理，负责缓存有效期判断、命中读取与降级兜底。 */
public class ConfigCache {

    private static final String KEY_PREFIX = "config_cache_time_";

    private final long timeout;
    private final TagProvider tagProvider;
    private boolean cacheUsed;

    public interface TagProvider {
        String getTag();
    }

    public ConfigCache(long timeout, TagProvider tagProvider) {
        this.timeout = timeout;
        this.tagProvider = tagProvider;
    }

    /** 本次 fetch 是否命中缓存（fetch 返回后查询）。 */
    public boolean cacheUsed() {
        return cacheUsed;
    }

    /** 网络成功后调用，记录缓存时间戳。 */
    public void recordTime(Config config) {
        Prefers.put(cacheTimeKey(config), System.currentTimeMillis());
    }

    /**
     * 获取配置 JSON：缓存有效直接返回；否则请求网络；网络失败时若有缓存则降级返回。
     * 返回后通过 {@link #cacheUsed()} 判断来源。
     */
    public String fetch(Config config) throws Throwable {
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
            json = Decoder.getJson(UrlUtil.convert(config.getUrl()), tagProvider.getTag(), timeout);
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

    private static String cacheTimeKey(Config config) {
        return KEY_PREFIX + config.getType() + "_" + Util.md5(config.getUrl());
    }
}
