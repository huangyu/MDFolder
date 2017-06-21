package com.huangyu.mdfolder.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.huangyu.library.app.BaseApplication;

/**
 * Created by huangyu on 2017-6-21.
 */
public class SPUtils {

    private SPUtils() {
    }

    /**
     * 是否是白天模式
     *
     * @return true/false
     */
    public static boolean isLightMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getBoolean("pref_theme", true);
    }

    /**
     * 是否显示隐藏文件
     *
     * @return true/false
     */
    public static boolean isShowHiddenFiles() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getBoolean("pref_show_hidden", false);
    }

}
