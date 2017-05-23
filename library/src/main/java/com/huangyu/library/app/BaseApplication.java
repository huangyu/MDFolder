package com.huangyu.library.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.huangyu.library.util.LogUtils;

/**
 * 应用基类
 * Created by huangyu on 2017-4-10.
 */
public class BaseApplication extends MultiDexApplication {

    private static BaseApplication mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;

        CrashHandler.getInstance().init(this);
        LogUtils.init(isApkInDebug(this));
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

}
