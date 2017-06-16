package com.huangyu.mdfolder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.huangyu.mdfolder.R;

/**
 * Created by huangyu on 2017-6-16.
 */
public class ThemeUtils {

    private Context mContext;
    private boolean isLightMode;

    public ThemeUtils(Activity context) {
        mContext = context;
        isChanged();
    }

    public boolean isChanged() {
        boolean lightTheme = isLightMode(mContext);
        boolean changed = isLightMode != lightTheme;
        isLightMode = lightTheme;
        return changed;
    }

    private boolean isLightMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("pref_theme", true);
    }

    public boolean isLightMode() {
        return isLightMode;
    }

    public int getCurrent() {
        if (isLightMode) {
            return R.style.MainTheme;
        } else {
            return R.style.MainThemeDark;
        }
    }

    public int getCurrentBySettings() {
        if (isLightMode) {
            return R.style.SettingsTheme;
        } else {
            return R.style.SettingsThemeDark;
        }
    }

}

