package com.huangyu.mdfolder.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.mdfolder.R;

/**
 * Created by huangyu on 2017-6-21.
 */
public class SPUtils {

    private SPUtils() {
    }

    /**
     * 是否是白天主题
     */
    public static boolean isLightMode() {
        String themeMode = getThemeMode();
        String[] themesArray = BaseApplication.getInstance().getApplicationContext().getResources().getStringArray(R.array.array_themes_value);
        if (themeMode.equals(themesArray[1])) {
            return false;
        }
        return true;
    }


    /**
     * 设置主题
     *
     * @return true/false
     */
    public static String getThemeMode() {
        String[] themesArray = BaseApplication.getInstance().getApplicationContext().getResources().getStringArray(R.array.array_themes_value);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getString("pref_themes", themesArray[0]);
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

    /**
     * 获取存储的语言类型
     *
     * @return 语言类型
     */
    public static String getLocaleValue() {
        String[] languageArray = BaseApplication.getInstance().getApplicationContext().getResources().getStringArray(R.array.array_languages_value);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getString("pref_language", languageArray[0]);
    }

    /**
     * 是否是内置模式
     */
    public static boolean isBuildInMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getBoolean("pref_open_mode", true);
    }

    /**
     * 是否是显示全部应用
     */
    public static boolean isShowAllApps() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getBoolean("pref_show_apps_mode", false);
    }

    /**
     * 是否是全局搜索
     */
    public static boolean isSearchGlobally() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getBoolean("pref_search_mode", true);
    }

}
