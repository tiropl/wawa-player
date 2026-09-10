package com.wawa_player.android.tv.spider;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * spider 进程的资源与行为限制。
 * <p>
 * 说明哪些能硬性限制、哪些只能尽力而为：
 * <ul>
 *   <li>执行时间 —— 可硬性限制（Future + 超时中断）。</li>
 *   <li>网络范围 —— 可硬性限制（OkHttp 拦截器，拒绝非法主机）。</li>
 *   <li>响应体大小 —— 可硬性限制（管道拷贝时截断）。</li>
 *   <li>内存 —— Android 无法给进程设硬上限，只能按水位检测并拒绝新任务 + 主动清理。</li>
 *   <li>CPU —— Android 无法直接配额，只能降低线程优先级 + 并发数上限间接控制。</li>
 * </ul>
 */
public class SpiderPolicy {

    private long timeoutMs = SpiderProtocol.DEFAULT_TIMEOUT_MS;
    private int maxMemoryMb = SpiderProtocol.DEFAULT_MAX_MEMORY_MB;
    private int maxBodyBytes = SpiderProtocol.DEFAULT_MAX_BODY_BYTES;
    private int maxTasks = SpiderProtocol.DEFAULT_MAX_TASKS;
    private List<String> allowHosts = Collections.emptyList();
    private List<String> denyHosts = Collections.emptyList();

    public static SpiderPolicy from(Bundle bundle) {
        SpiderPolicy p = new SpiderPolicy();
        if (bundle == null) return p;
        p.timeoutMs = bundle.getLong(SpiderProtocol.Policy.TIMEOUT_MS, p.timeoutMs);
        p.maxMemoryMb = bundle.getInt(SpiderProtocol.Policy.MAX_MEMORY_MB, p.maxMemoryMb);
        p.maxBodyBytes = bundle.getInt(SpiderProtocol.Policy.MAX_BODY_BYTES, p.maxBodyBytes);
        p.maxTasks = bundle.getInt(SpiderProtocol.Policy.MAX_TASKS, p.maxTasks);
        p.allowHosts = toList(bundle.getStringArrayList(SpiderProtocol.Policy.ALLOW_HOSTS));
        p.denyHosts = toList(bundle.getStringArrayList(SpiderProtocol.Policy.DENY_HOSTS));
        if (p.maxTasks < 1) p.maxTasks = 1;
        if (p.timeoutMs < 1000) p.timeoutMs = 1000;
        return p;
    }

    private static List<String> toList(ArrayList<String> src) {
        if (src == null || src.isEmpty()) return Collections.emptyList();
        return Collections.unmodifiableList(new ArrayList<>(src));
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putLong(SpiderProtocol.Policy.TIMEOUT_MS, timeoutMs);
        b.putInt(SpiderProtocol.Policy.MAX_MEMORY_MB, maxMemoryMb);
        b.putInt(SpiderProtocol.Policy.MAX_BODY_BYTES, maxBodyBytes);
        b.putInt(SpiderProtocol.Policy.MAX_TASKS, maxTasks);
        b.putStringArrayList(SpiderProtocol.Policy.ALLOW_HOSTS, new ArrayList<>(allowHosts));
        b.putStringArrayList(SpiderProtocol.Policy.DENY_HOSTS, new ArrayList<>(denyHosts));
        return b;
    }

    /** 主机是否被允许访问。黑名单优先，白名单非空时只放行白名单内的主机及子域。 */
    public boolean isHostAllowed(String host) {
        if (host == null || host.isEmpty()) return false;
        String h = host.toLowerCase();
        for (String d : denyHosts) {
            if (matches(h, d)) return false;
        }
        if (allowHosts.isEmpty()) return true;
        for (String a : allowHosts) {
            if (matches(h, a)) return true;
        }
        return false;
    }

    private static boolean matches(String host, String pattern) {
        String p = pattern.toLowerCase();
        if (p.startsWith("*.")) return host.endsWith(p.substring(1));
        return host.equals(p) || host.endsWith("." + p);
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public int getMaxMemoryMb() {
        return maxMemoryMb;
    }

    public int getMaxBodyBytes() {
        return maxBodyBytes;
    }

    public int getMaxTasks() {
        return maxTasks;
    }
}
