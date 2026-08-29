package com.wawa_player.android.tv;

import java.util.concurrent.TimeUnit;

public class Constant {

    /** 快进/快退防抖间隔 */
    public static final long INTERVAL_SEEK = TimeUnit.SECONDS.toMillis(10);
    /** 控件自动隐藏延迟 */
    public static final long INTERVAL_HIDE = TimeUnit.SECONDS.toMillis(3);
    /** 点播 API 请求超时（首页、分类、详情、播放地址解析） */
    public static final long TIMEOUT_VOD = TimeUnit.SECONDS.toMillis(10);
    /** 直播频道列表解析超时 */
    public static final long TIMEOUT_LIVE = TimeUnit.SECONDS.toMillis(10);
    /** 单频道 EPG 获取超时 */
    public static final long TIMEOUT_EPG = TimeUnit.SECONDS.toMillis(5);
    /** XML EPG 文件下载解析超时 */
    public static final long TIMEOUT_XML = TimeUnit.SECONDS.toMillis(15);
    /** 播放器渲染超时（未出画面则报错） */
    public static final long TIMEOUT_PLAY = TimeUnit.SECONDS.toMillis(15);
    /** 设备同步/投屏通信超时 */
    public static final long TIMEOUT_SYNC = TimeUnit.SECONDS.toMillis(2);
    /** 搜索请求超时（单站/多站） */
    public static final long TIMEOUT_SEARCH = TimeUnit.SECONDS.toMillis(10);
    /** 解析接口超时（JSON 解析源） */
    public static final long TIMEOUT_PARSE_DEF = TimeUnit.SECONDS.toMillis(15);
    /** 解析接口超时（WebView 解析源） */
    public static final long TIMEOUT_PARSE_WEB = TimeUnit.SECONDS.toMillis(15);
    /** 直播 URL 解析超时 */
    public static final long TIMEOUT_PARSE_LIVE = TimeUnit.SECONDS.toMillis(10);
    /** 配置 URL 加载超时（VOD/Live/线路源） */
    public static final long TIMEOUT_CONFIG = TimeUnit.SECONDS.toMillis(15);
    /** 局域网设备扫描超时 */
    public static final long TIMEOUT_SCAN = TimeUnit.SECONDS.toMillis(1);
    /** 历史记录保留天数 */
    public static final long HISTORY_TIME = TimeUnit.DAYS.toMillis(60);

    /**
     * 片头/片尾可设置的最大时长，按视频总时长分档：
     * < 15 分钟 → 3 分钟，15~29 分钟 → 6 分钟，≥ 30 分钟 → 10 分钟
     */
    public static long getOpEdLimit(long duration) {
        if (duration < TimeUnit.MINUTES.toMillis(15)) return TimeUnit.MINUTES.toMillis(3);
        if (duration < TimeUnit.MINUTES.toMillis(30)) return TimeUnit.MINUTES.toMillis(6);
        return TimeUnit.MINUTES.toMillis(10);
    }
}
