package com.huangyu.mdfolder.utils;

import com.huangyu.mdfolder.R;

import java.util.Locale;

/**
 * Created by huangyu on 2017-6-16.
 */
public class ThemeUtils {

    private boolean isLightMode;
    private boolean isShowMode;
    private Locale localeMode;

    public ThemeUtils() {
        isChanged();
    }

    public boolean isChanged() {
        boolean isLightTheme = SPUtils.isLightMode();
        boolean isShow = SPUtils.isShowHiddenFiles();
        Locale locale = LanguageUtils.getLanguage();
        boolean changed = isLightMode != isLightTheme || isShowMode != isShow || locale.getLanguage().equals(localeMode.getLanguage());
        isLightMode = isLightTheme;
        isShowMode = isShow;
        localeMode = locale;
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

