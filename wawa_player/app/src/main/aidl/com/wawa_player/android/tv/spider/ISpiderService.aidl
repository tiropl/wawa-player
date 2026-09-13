// ISpiderService.aidl
// 主进程 <-> spider 独立进程（:spider）的通信契约。
//
// 统一用 Bundle 传递参数与结果：AIDL 对 Map 泛型支持有限，Bundle 是官方
// 推荐的跨进程键值容器，且天然支持嵌套与 ParcelFileDescriptor。
package com.wawa_player.android.tv.spider;

import android.os.Bundle;
import com.wawa_player.android.tv.spider.ISpiderCallback;

interface ISpiderService {

    /**
     * 启动（必要时创建）一个 spider 实例。
     *
     * @param key  站点 key
     * @param api  爬虫标识（.py / .js / csp_Xxx）
     * @param ext  扩展参数（JSON 字符串）
     * @param jar  csp 类型的 jar 地址，可为空
     * @return 见 SpiderResult.code
     */
    int start(String key, String api, String ext, String jar);

    /**
     * 暂停：拒绝新的任务下发，已收到停止标志的在途任务会尽快退出。
     * 注意：无法中断已进入第三方 native/JS/Python 代码且不可打断的调用，
     * 这里做到的是「拒新 + 标记取消 + 超时兜底」。
     */
    int pause(String key);

    /** 恢复被暂停的 spider。 */
    int resume(String key);

    /** 停止并销毁单个 spider。 */
    int stop(String key);

    /** 停止并销毁全部 spider。 */
    int stopAll();

    /** 预加载 jar（csp 解析用）。 */
    int parseJar(String jar, boolean recent);

    /** 标记最近使用的站点，影响 proxy 的兜底路由。 */
    int setRecent(String key, String api, String jar);

    /**
     * JSON 解析扩展点。
     * jxsJson 是参数表的 JSON 字符串，返回 JSON 字符串，失败返回 null。
     * 走 JSON 字符串是因为 Bundle 不好表达嵌套 Map。
     */
    String jsonExt(String key, String jxsJson, String url);

    /** 同 jsonExt，多一层 Mix 参数。 */
    String jsonExtMix(String flag, String key, String name, String jxsJson, String url);

    /**
     * 通用任务下发：调用 spider 的某个方法。
     *
     * @param method 方法名，见 SpiderMethod 常量
     * @param args   方法参数，见 SpiderArgs 常量
     * @return 结果 Bundle，见 SpiderResult 常量
     */
    Bundle call(String key, String api, String ext, String jar, String method, in Bundle args);

    /**
     * 本地 HTTP 代理入口。内部按 siteKey / do=js / do=py / jar 路由，
     * 返回结构与 call(PROXY) 一致。
     */
    Bundle proxy(in Bundle params);

    /**
     * 下发资源与行为限制策略。
     * key 见 SpiderPolicy：timeoutMs / maxMemoryMb / maxBodyBytes /
     * allowHosts / denyHosts / maxTasks。
     */
    int setPolicy(in Bundle policy);

    /**
     * 查询运行状态。
     * 返回 Bundle：KEY_PID / KEY_ALIVE_TASKS / KEY_SPIDERS(String[]) /
     * KEY_MEMORY_MB / KEY_POLICY(Bundle) / KEY_UPTIME_MS
     */
    Bundle status();

    /** 注册日志与状态回调（支持多客户端）。 */
    void registerCallback(ISpiderCallback callback);

    /** 注销回调。 */
    void unregisterCallback(ISpiderCallback callback);
}
