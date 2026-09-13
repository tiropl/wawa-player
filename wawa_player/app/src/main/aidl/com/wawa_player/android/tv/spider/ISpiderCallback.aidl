// ISpiderCallback.aidl
// 由 spider 独立进程回传给主进程的日志与运行状态。
package com.wawa_player.android.tv.spider;

interface ISpiderCallback {

    /**
     * 日志回传。
     *
     * @param level   android.util.Log 级别（2=VERBOSE 3=DEBUG 4=INFO 5=WARN 6=ERROR）
     * @param tag     日志标签，通常是站点 key
     * @param message 日志内容
     */
    void onLog(int level, String tag, String message);

    /**
     * 运行状态变化回传。
     *
     * @param key    站点 key
     * @param state  见 SpiderState：1=IDLE 2=RUNNING 3=PAUSED 4=STOPPED 5=TIMEOUT 6=CRASHED 7=OOM
     * @param detail 补充说明（如异常信息、耗时）
     */
    void onStateChanged(String key, int state, String detail);
}
