package com.wawa_player.android.tv.spider;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络访问范围护栏：按主机白/黑名单拦截请求。
 * <p>
 * 只会在 :spider 进程里被注入到 OkHttp，主进程不受影响。
 */
public class SpiderNetGuard implements Interceptor {

    private volatile SpiderPolicy policy = new SpiderPolicy();

    public void update(SpiderPolicy policy) {
        if (policy != null) this.policy = policy;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String host = request.url().host();
        SpiderPolicy p = policy;
        if (p != null && !p.isHostAllowed(host)) {
            throw new IOException("Host blocked by spider network policy: " + host);
        }
        return chain.proceed(request);
    }
}
