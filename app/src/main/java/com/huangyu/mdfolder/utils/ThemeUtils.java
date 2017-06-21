package com.huangyu.mdfolder.utils;

import com.huangyu.mdfolder.R;

/**
 * Created by huangyu on 2017-6-16.
 */
public class ThemeUtils {

    private boolean isLightMode;
    private boolean isShowMode;

    public ThemeUtils() {
        isChanged();
    }

    public boolean isChanged() {
        boolean isLightTheme = SPUtils.isLightMode();
        boolean isShow = SPUtils.isShowHiddenFiles();
        boolean changed = isLightMode != isLightTheme || isShowMode != isShow;
        isLightMode = isLightTheme;
        isShowMode = isShow;
        return changed;
    }

    public boolean isLightMode() {
        return isLightMode;
    }

    public int getCurrentTheme() {
        if (isLightMode) {
            return R.style.MainTheme;
        } else {
            return R.style.MainThemeDark;
        }
    }

    public int getCurrentSettingsTheme() {
        if (isLightMode) {
            return R.style.SettingsTheme;
        } else {
            return R.style.SettingsThemeDark;
        }
    }

}

