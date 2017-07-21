package com.huangyu.mdfolder.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.AppApplication;
import com.huangyu.mdfolder.app.Constants;

/**
 * Created by huangyu on 2017-6-21.
 */
public class SPUtils {

    private SPUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
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
     *
     * @return true/false
     */
    public static boolean isBuildInMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getBoolean("pref_open_mode", true);
    }

    /**
     * 是否是显示全部应用
     *
     * @return true/false
     */
    public static boolean isShowAllApps() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getBoolean("pref_show_apps_mode", false);
    }

    /**
     * 是否是全局搜索
     *
     * @return true/false
     */
    public static boolean isSearchGlobally() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance().getApplicationContext());
        return prefs.getBoolean("pref_search_mode", true);
    }

    /**
     * 设置文件备注
     */
    public static void setFileRemark(String filePath, String remark) {
        com.huangyu.library.util.SPUtils sp = ((AppApplication) AppApplication.getInstance()).getSPUtils();
        sp.put(filePath, remark);
    }

    /**
     * 获取文件备注
     *
     * @return 备注名
     */
    public static String getFileRemark(String filePath) {
        com.huangyu.library.util.SPUtils sp = ((AppApplication) AppApplication.getInstance()).getSPUtils();
        return sp.getString(filePath);
    }

    /**
     * 移除备注
     */
    public static void removeFileRemark(String filePath) {
        com.huangyu.library.util.SPUtils sp = ((AppApplication) AppApplication.getInstance()).getSPUtils();
        sp.remove(filePath);
    }

    /**
     * 获取排序类型
     */
    public static int getSortType() {
        com.huangyu.library.util.SPUtils sp = ((AppApplication) AppApplication.getInstance()).getSPUtils();
        return sp.getInt("sort_type", Constants.SortType.TYPE);
    }

    /**
     * 设置排序类型
     */
    public static void setSortType(int type) {
        com.huangyu.library.util.SPUtils sp = ((AppApplication) AppApplication.getInstance()).getSPUtils();
        sp.put("sort_type", type);
    }

    /**
     * 获取升降序类型
     */
    public static int getOrderType() {
        com.huangyu.library.util.SPUtils sp = ((AppApplication) AppApplication.getInstance()).getSPUtils();
        return sp.getInt("order_type", Constants.OrderType.ASC);
    }

    /**
     * 设置升降序类型
     */
    public static void setOrderType(int type) {
        com.huangyu.library.util.SPUtils sp = ((AppApplication) AppApplication.getInstance()).getSPUtils();
        sp.put("order_type", type);
    }

}
