package com.huangyu.library.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by huangyu on 2017-4-14.
 */
public class HeaderIntercepter implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request build = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build();
        return chain.proceed(build);
    }

}
