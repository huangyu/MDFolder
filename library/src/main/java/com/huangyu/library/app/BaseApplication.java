package com.huangyu.library.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.huangyu.library.util.LogUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * 应用基类
 * Created by huangyu on 2017-4-10.
 */
public class BaseApplication extends Application {

    private static BaseApplication INSTANCE;
    private RefWatcher mRefWatcher;

    private static Boolean isDebug = null;

    public static boolean isDebug() {
        return isDebug != null && isDebug;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        isApkInDebug(getApplicationContext());
        if (isDebug()) {
//            CrashHandler.getInstance().init(this);
            LogUtils.init(true);
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            mRefWatcher = LeakCanary.install(this);
        } else {
            mRefWatcher = RefWatcher.DISABLED;
        }
    }

    public static BaseApplication getInstance() {
        return INSTANCE;
    }

    public static RefWatcher getRefWatcher() {
        return getInstance().mRefWatcher;
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }

    /**
     * 判断当前应用是否是debug状态
     *
     * @param context
     */
    public static void isApkInDebug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null &&
                    (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }

}
