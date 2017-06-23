package com.huangyu.mdfolder.utils;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.mdfolder.R;

/**
 * Created by huangyu on 2017-6-16.
 */
public class ThemeUtils {

    private boolean isLightMode;
    private boolean isShowMode;
    private String localeMode;

    public ThemeUtils() {
        setMode();
        changeLocale(SPUtils.getLocaleValue());
    }

    public boolean isChanged() {
        boolean isLightTheme = SPUtils.isLightMode();
        boolean isShow = SPUtils.isShowHiddenFiles();
        String localeValue = SPUtils.getLocaleValue();
        return isLightMode != isLightTheme || isShowMode != isShow || !localeValue.equals(localeMode);
    }

    private void setMode() {
        isLightMode = SPUtils.isLightMode();
        isShowMode = SPUtils.isShowHiddenFiles();
        localeMode = SPUtils.getLocaleValue();
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

