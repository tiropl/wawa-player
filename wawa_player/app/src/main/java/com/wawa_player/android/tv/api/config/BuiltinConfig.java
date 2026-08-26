package com.wawa_player.android.tv.api.config;

/**
 * 打包定制用的内置配置地址。
 *
 * <p>默认均为空字符串，此时 App 行为与官方版一致（未配置时首次启动弹出配置窗口）。
 * 需要打包内置了默认点播 / 直播 / 壁纸地址的定制版时，直接在此填入对应地址即可：
 * 首次启动（本地数据库无该类型配置）时会自动加载内置地址，不再弹出配置窗口；
 * 已在使用的用户不受影响，仍以已保存的配置为准。</p>
 */
public class BuiltinConfig {

    // 内置点播（Vod）配置地址，支持仓库（urls 数组）与单配置 JSON
    public static final String VOD_URL = "";
    // 内置直播（Live）配置地址，支持 M3U / TXT / JSON
    public static final String LIVE_URL = "";
    // 内置壁纸（Wall）地址，支持图片 / GIF / 视频
    public static final String WALL_URL = "";
}
