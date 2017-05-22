package com.huangyu.library.app;

import android.content.Context;

import com.huangyu.library.util.LogToFileUtils;

/**
 * Created by huangyu on 2017-4-10.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
//            ActivityManager.getInstance().AppExitAndRestart(mContext);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
//        //使用Toast来显示异常信息
//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
//                Looper.loop();
//            }
//        }.start();
        // 收集设备参数信息
        LogToFileUtils.collectDeviceInfo(mContext);
        // 保存日志文件
        LogToFileUtils.saveCrashInfoToFile(ex);
        return true;
    }

}

