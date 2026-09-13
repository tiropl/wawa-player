package com.wawa_player.android.tv;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;

import com.wawa_player.android.tv.utils.Notify;
import com.fongmi.hook.Hook;
import com.github.catvod.Init;
import com.google.gson.Gson;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private static volatile App instance;

    private final Handler handler;
    private final Gson gson;
    private final long time;

    private Activity activity;
    private Hook hook;

    public App() {
        instance = this;
        gson = new Gson();
        time = System.currentTimeMillis();
        handler = HandlerCompat.createAsync(Looper.getMainLooper());
    }

    public static App get() {
        return instance;
    }

    public static Gson gson() {
        return get().gson;
    }

    public static long time() {
        return get().time;
    }

    public static Activity activity() {
        return get().activity;
    }

    public static void post(Runnable runnable) {
        get().handler.post(runnable);
    }

    public static void post(Runnable runnable, long delayMillis) {
        get().handler.removeCallbacks(runnable);
        if (delayMillis >= 0) get().handler.postDelayed(runnable, delayMillis);
    }

    public static void removeCallbacks(Runnable runnable) {
        get().handler.removeCallbacks(runnable);
    }

    public static void removeCallbacks(Runnable... runnable) {
        for (Runnable r : runnable) get().handler.removeCallbacks(r);
    }

    public static void restart() {
        Intent intent = get().getPackageManager().getLaunchIntentForPackage(get().getPackageName());
        if (intent == null) return;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        get().startActivity(intent);
        // 进程内重启时，旧 Activity 的 onDestroy 会清空 VodConfig/LiveConfig/Server/OkHttp/Source 等单例，
        // 与新 Activity 的加载流程存在竞态（首页加载失败或加载动画卡住）。startActivity 已把启动意图
        // 交给系统，直接结束当前进程，让系统以全新进程冷启动，规避全部竞态。
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void setHook(Hook hook) {
        this.hook = hook;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Init.set(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Application 在每个进程都会跑一次。:spider 沙箱进程只需要
        // attachBaseContext 里的 Init.set()，通知渠道 / 生命周期回调 /
        // 数据库预热对它无意义，跳过以免重复开销。
        if (isSpiderProcess()) return;
        Notify.createChannel();
        registerActivityLifecycleCallbacks(this);
        // 一次性迁移：把旧版本自动写入的弹幕开关重置为关闭，配合默认关闭弹幕
        com.wawa_player.android.tv.setting.DanmakuSetting.migrate();
        // Pre-warm database on background thread to avoid blocking main thread
        com.wawa_player.android.tv.db.AppDatabase.warmUp();
    }

    /** 当前是否运行在 :spider 沙箱进程。 */
    public static boolean isSpiderProcess() {
        App app = instance;
        if (app == null) return false;
        String name = currentProcessName(app);
        return name != null && name.endsWith(com.wawa_player.android.tv.spider.SpiderProtocol.PROCESS_NAME);
    }

    private static String currentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return null;
        java.util.List<android.app.ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list == null) return null;
        for (android.app.ActivityManager.RunningAppProcessInfo info : list) {
            if (info.pid == pid) return info.processName;
        }
        return null;
    }

    @Override
    public PackageManager getPackageManager() {
        return hook != null ? hook : getBaseContext().getPackageManager();
    }

    @Override
    public String getPackageName() {
        return hook != null ? hook.getPackageName() : getBaseContext().getPackageName();
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (activity != activity()) this.activity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (activity == activity()) this.activity = null;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }
}