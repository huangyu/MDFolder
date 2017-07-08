package com.huangyu.mdfolder.utils;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.mdfolder.R;

/**
 * Created by huangyu on 2017-6-16.
 */
public class ThemeUtils {

    private String themeMode;
    private boolean isShowMode;
    private String localeMode;

    public ThemeUtils() {
        setMode();
        changeLocale(SPUtils.getLocaleValue());
    }

    private void setMode() {
        themeMode = SPUtils.getThemeMode();
        isShowMode = SPUtils.isShowHiddenFiles();
        localeMode = SPUtils.getLocaleValue();
    }

    public boolean isChanged() {
        String themeValue = SPUtils.getThemeMode();
        boolean isShow = SPUtils.isShowHiddenFiles();
        String localeValue = SPUtils.getLocaleValue();
        return !themeValue.equals(themeMode) || isShowMode != isShow || !localeValue.equals(localeMode);
    }

    public boolean isLightMode() {
        String[] themesArray = BaseApplication.getInstance().getApplicationContext().getResources().getStringArray(R.array.array_themes_value);
        if (themeMode.equals(themesArray[1])) {
            return false;
        }
        return true;
    }

    public String getThemeMode() {
        return themeMode;
    }

    public int getCurrentTheme() {
        switch (themeMode) {
            case "1":
                return R.style.AppThemeDark;
            case "2":
                return R.style.AppThemeIndigo;
            case "3":
                return R.style.AppThemeCyan;
            case "4":
                return R.style.AppThemeTeal;
            case "5":
                return R.style.AppThemeGreen;
            case "6":
                return R.style.AppThemeRed;
            case "7":
                return R.style.AppThemePurple;
            case "8":
                return R.style.AppThemeOrange;
            case "9":
                return R.style.AppThemeYellow;
            case "10":
                return R.style.AppThemePink;
            case "11":
                return R.style.AppThemeBrown;
            case "12":
                return R.style.AppThemeGrey;
            case "13":
                return R.style.AppThemeBlack;
            default:
                return R.style.AppThemeBlue;
        }
    }

    private void changeLocale(String localeValue) {
        String[] languageArray = BaseApplication.getInstance().getApplicationContext().getResources().getStringArray(R.array.array_languages_value);
        String simplifiedChineseValue = languageArray[0];
        String englishValue = languageArray[1];

        if (localeValue.equals(simplifiedChineseValue)) {
            LanguageUtils.changeLanguage(LanguageUtils.SIMPLIFIED_CHINESE);
        } else if (localeValue.equals(englishValue)) {
            LanguageUtils.changeLanguage(LanguageUtils.ENGLISH);
        }
    }

}

