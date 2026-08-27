## 目录

- [快速开始](#快速开始)
- [项目架构](#项目架构)
- [播放器](#播放器)
- [点播功能](#点播功能)
- [直播功能](#直播功能)
- [爬虫引擎](#爬虫引擎)
- [网络功能](#网络功能)
- [DLNA 投放](#dlna-投放)
- [Android Auto](#android-auto)
- [远程控制](#远程控制)
- [配置说明](#配置说明)
- [延伸阅读](#延伸阅读)

---

## 快速开始

```bash
# 克隆主仓库
git clone https://github.com/your-fork/wawa-player.git
cd wawa-player

# 初始化 media3 子模块
git submodule update --init --recursive
```

### 内置配置（打包定制）

需要打包内置了点播 / 直播 / 壁纸默认地址的定制版时，修改 [BuiltinConfig.java](wawa_player/app/src/main/java/com/wawa_player/android/tv/api/config/BuiltinConfig.java) 中的常量即可：

> 点播地址是主配置，可以内嵌直播和壁纸

| 常量 | 说明 | 默认值 |
|------|------|--------|
| `BuiltinConfig.VOD_URL` | 内置点播配置地址（支持仓库 `urls` 数组与单配置 JSON） | 空 |
| `BuiltinConfig.LIVE_URL` | 内置直播配置地址（支持 M3U / TXT / JSON） | 空 |
| `BuiltinConfig.WALL_URL` | 内置壁纸地址（支持图片 / GIF / 视频） | 空 |

填入地址后，App 首次启动（本地数据库无对应类型配置）会自动加载内置地址，不再弹出配置窗口；已在使用的用户不受影响，仍以已保存的配置为准。

---

## 项目架构

| 项目      | 值                            |
|---------|------------------------------|
| package | `com.wawa_player.android.tv` |
| minSdk  | 24（Android 7.0 Nougat）       |
| abi     | `arm64-v8a`、`armeabi-v7a`    |
| flavor  | `tv`（电视版）、`mobile`（手机版）      |

```
wawa_player/
├── app/
    ├── main/        公共业务逻辑
    ├── tv/    电视端
    ├── mobile/      手机端
├── catvod/         爬虫抽象层（Spider 接口、OkHttp 网络栈）
├── quickjs/        QuickJS JavaScript 引擎
├── chaquo/         Chaquopy Python 引擎
```

## 播放器

- **核心**：ExoPlayer（Media3）+ FFmpeg 软解，硬解 / 软解自动降级切换
- **渲染**：SurfaceView / TextureView
- **DRM**：Widevine、PlayReady、ClearKey，支持 `#KODIPROP` 声明
- **弹幕**：DanmakuFlameMaster，与播放时间轴精确同步，支持远程推送
- **字幕**：SRT / SSA / ASS 外挂字幕、系统 CaptioningManager、远程实时注入
- **其他**：倍速、多缩放比例、画中画（PiP）、背景音频、片头 / 片尾自动跳过

---

## 点播功能

- 多站点分类浏览，Filter 筛选（年份 / 地区 / 类型等）
- 多站点**并行搜索**，关键字自动繁转简提升兼容性
- 播放失败自动换源：解析器 → 线路 → 搜索其他站 → 下一站点
- 观看记录（保留 60 天）、收藏、无痕模式
- 电视版使用遥控器操作；手机版支持手势（亮度 / 音量 / 进度）、上下滑切集、屏幕旋转与锁定

---

## 直播功能

- 支持 M3U、TXT（`#genre#` 分组）、JSON 三种直播源格式
- **EPG**：XMLTV 格式（支持 `.gz`），每 6 小时自动刷新
- **追看 / 时移**：`append`、`pltv` 等多种类型
- 频道收藏、隐藏分组密码保护
- 特殊引擎：TVBus、ForceTech

---

## 爬虫引擎

支持三种语言编写爬虫：

- Java JAR（DexClassLoader）
- JavaScript（QuickJS）
- Python（Chaquopy）

通过 `api` 字段指定爬虫，`ext` 字段传入初始化参数。完整 API 规格见 [SPIDER.md](docs/SPIDER.md)。

---

## 网络功能

- **DoH**：DNS over HTTPS，支持 Bootstrap IP
- **代理**：HTTP / HTTPS / SOCKS4 / SOCKS5，依 host 正则规则动态选择
- **Hosts**：DNS 解析覆盖，支持通配符 `*`
- **CORS 注入**：依 host 规则在响应中注入自定义标头
- **广告拦截**：`ads` 黑名单，符合域名直接拦截
- **WebView 嗅探**：Sniffer 以 regex 拦截媒体 URL；支持 UA 伪装

---

## DLNA 投放

- **DMC（投放端）**：手机版，扫描局域网 DLNA 设备并投放媒体
- **DMR（被投放端）**：电视版，作为 DLNA Renderer 接收其他设备投放

使用 JUPnP 3.0.4（UPnP），支持 play / pause / stop / seek / next / repeat 控制，可传递自定义 HTTP 标头（User-Agent、Referer 等）至目标串流。

---

## Android Auto

电视版支持 Android Auto，PlaybackService 实现 MediaLibraryService，可在车机上浏览播放记录与直播频道：

- **点播**：历史记录条目可直接续播，恢复上次进度
- **直播**：依分组浏览频道，可直接选台
- **播放控制**：支持车机端 play / pause / prev / next / stop
- **懒加载**：App 退出后 Auto 仍保持连接，配置自动重新加载

---

## 远程控制

应用启动后绑定本地 HTTP 服务器（NanoHTTPD），端口从 **9978** 起自动检测至 **9998**，可用于播放控制、推送字幕 / 弹幕、多设备同步等。完整端点说明见 [LOCAL.md](docs/LOCAL.md)。

---

## 配置说明

Vod 配置为应用主要入口，通过 URL 或本地路径加载，顶层字段定义：

- 点播站点（`sites`）、解析规则（`parses`）
- 直播来源（`lives`）
- 网络设置（`doh`、`proxy`、`hosts`、`ads`）

Live 配置可内嵌或独立存放。完整字段说明见 [CONFIG.md](docs/CONFIG.md)。

---

## 延伸阅读

| 文件                         | 说明                   |
|----------------------------|----------------------|
| [CONFIG.md](docs/CONFIG.md) | Vod / Live 完整配置字段说明  |
| [SPIDER.md](docs/SPIDER.md) | Spider 所有方法规格与返回格式   |
| [LOCAL.md](docs/LOCAL.md)  | 本地 HTTP API 所有端点完整说明 |
| [LIVE.md](docs/LIVE.md)    | 直播来源格式完整说明           |
