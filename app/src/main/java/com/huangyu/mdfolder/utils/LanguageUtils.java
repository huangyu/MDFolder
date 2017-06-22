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

    public static final Locale AUTO = Locale.getDefault();
    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale SIMPLIFIED_CHINESE = Locale.SIMPLIFIED_CHINESE;

    private LanguageUtils() {

    }

    public static Locale getLanguage() {
        return BaseApplication.getInstance().getApplicationContext().getResources().getConfiguration().locale;
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

}