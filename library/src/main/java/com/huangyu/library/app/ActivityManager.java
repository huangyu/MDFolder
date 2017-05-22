
package com.huangyu.library.app;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

/**
 * Activity管理类
 * Created by huangyu on 2017-4-10.
 */
public class ActivityManager {

    private static Stack<Activity> mActivityStack;
    private volatile static ActivityManager INSTANCE;

    private ActivityManager() {
        mActivityStack = new Stack<>();
    }

    /**
     * 单一实例
     */
    public static ActivityManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ActivityManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActivityManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<Activity>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        try {
            Activity activity = mActivityStack.lastElement();
            return activity;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前Activity的前一个Activity
     */
    public Activity preActivity() {
        int index = mActivityStack.size() - 2;
        if (index < 0) {
            return null;
        }
        Activity activity = mActivityStack.get(index);
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = mActivityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        try {
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 返回到指定的activity
     *
     * @param cls
     */
    public void returnToActivity(Class<?> cls) {
        while (mActivityStack.size() != 0)
            if (mActivityStack.peek().getClass() == cls) {
                break;
            } else {
                finishActivity(mActivityStack.peek());
            }
    }


    /**
     * 是否已经打开指定的activity
     *
     * @param cls
     * @return
     */
    public boolean isOpenActivity(Class<?> cls) {
        if (mActivityStack != null) {
            for (int i = 0, size = mActivityStack.size(); i < size; i++) {
                if (cls == mActivityStack.peek().getClass()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 退出应用程序
     *
     * @param context      上下文
     * @param isBackground 是否开开启后台运行
     */
    public void AppExit(Context context, Boolean isBackground) {
        try {
            finishAllActivity();
            android.app.ActivityManager activityMgr = (android.app.ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
        } finally {
            // 注意，如果您有后台程序运行，请不要支持此句子
            if (!isBackground) {
                System.exit(0);
            }
        }
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExitAndRestart(Context mContext) {
        ActivityManager.getInstance().finishAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

}