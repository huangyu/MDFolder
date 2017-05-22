package com.huangyu.library.http;

import android.text.TextUtils;

import com.huangyu.library.util.LogUtils;
import com.huangyu.library.util.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by huangyu on 2017-4-14.
 */
public class RewriteCacheControlInterceptor implements Interceptor {

    private long mCacheStaleSec;

    public RewriteCacheControlInterceptor(long cacheStaleSec) {
        this.mCacheStaleSec = cacheStaleSec;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String cacheControl = request.cacheControl().toString();
        if (!NetworkUtils.isConnected()) {
            request = request.newBuilder()
                    .cacheControl(TextUtils.isEmpty(cacheControl) ? CacheControl.FORCE_NETWORK : CacheControl.FORCE_CACHE)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        LogUtils.logd("api返回数据", originalResponse.body().toString());
        if (NetworkUtils.isConnected()) {
            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + mCacheStaleSec)
                    .removeHeader("Pragma")
                    .build();
        }
    }

}
