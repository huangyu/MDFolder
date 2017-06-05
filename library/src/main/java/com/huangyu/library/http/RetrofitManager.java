package com.huangyu.library.http;


import com.huangyu.library.app.BaseApplication;
import com.huangyu.library.app.BaseConstants;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 管理类
 * Created by huangyu on 2017-4-10.
 */
public class RetrofitManager {

    // 读超时长，单位：毫秒
    private static final int READ_TIME_OUT = 8000;
    // 连接时长，单位：毫秒
    private static final int CONNECT_TIME_OUT = 8000;
    private Retrofit mRetrofit;

    /*************************缓存设置*********************/
    /*
    1. noCache 不使用缓存，全部走网络

    2. noStore 不使用缓存，也不存储缓存

    3. onlyIfCached 只使用缓存

    4. maxAge 设置最大失效时间，失效则不使用 需要服务器配合

    5. maxStale 设置最大失效时间，失效则不
    使用 需要服务器配合 感觉这两个类似 还没怎么弄清楚，清楚的同学欢迎留言

    6. minFresh 设置有效时间，依旧如上

    7. FORCE_NETWORK 只走网络

    8. FORCE_CACHE 只走缓存*/

    /**
     * 设缓存有效期为7天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 7;

    /**
     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
     */
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;

    /**
     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
     */
    private static final String CACHE_CONTROL_AGE = "max-age=0";

    private RetrofitManager(String baseUrl) {
        // 开启Log
        final HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // 缓存
        File cacheFile = new File(BaseApplication.getAppContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); // 100Mb

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
//                .addNetworkInterceptor(new RewriteCacheControlInterceptor(CACHE_STALE_SEC))
                .addInterceptor(new HeaderIntercepter())
                .addInterceptor(new GzipInterceptor())
                .addInterceptor(logInterceptor)
                .cache(cache)
                .build();

        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    private static class SingletonHolder {
        private static final RetrofitManager INSTANCE = new RetrofitManager(BaseConstants.API_URL);
    }

    public static Retrofit getInstance() {
        return SingletonHolder.INSTANCE.mRetrofit;
    }

//    /**
//     * 根据网络状况获取缓存的策略
//     */
//    @NonNull
//    public static String getCacheControl() {
//        return NetworkUtils.isConnected() ? CACHE_CONTROL_AGE : CACHE_CONTROL_CACHE;
//    }

}