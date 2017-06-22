package com.huangyu.mdfolder.utils;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.huangyu.library.app.BaseApplication;

import java.util.Locale;

/**
 * Created by huangyu on 2017-6-22.
 */
public final class LanguageUtils {

    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale SIMPLIFIED_CHINESE = Locale.SIMPLIFIED_CHINESE;

    private LanguageUtils() {

    }

    public static Locale getLocale() {
        if (Build.VERSION.SDK_INT < 24) {
            return BaseApplication.getInstance().getApplicationContext().getResources().getConfiguration().locale;
        } else {
            return BaseApplication.getInstance().getApplicationContext().getResources().getConfiguration().getLocales().get(0);
        }
    }

    public static void changeLanguage(Locale language) {
        Locale.setDefault(language);
        final Resources resources = BaseApplication.getInstance().getApplicationContext().getResources();
        final Configuration config = resources.getConfiguration();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(language);
        } else {
            config.locale = language;
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    /**
     * 是否是设置值
     *
     * @return 是否是设置值
     */
    public static boolean isSetValue() {
        String currentValue;
        Locale currentLocale = getLocale();
        if(currentLocale.getLanguage().equals(SIMPLIFIED_CHINESE.getLanguage())) {
            currentValue = "1";
        }
        else if(currentLocale.getLanguage().equals(ENGLISH.getLanguage())) {
            currentValue = "2";
        }
        else {
            currentValue = "0";
        }
        String localeValue = SPUtils.getLocaleValue();
        return currentValue.equals(localeValue);
    }

}