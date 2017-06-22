package com.huangyu.mdfolder.app;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.mdfolder.utils.LanguageUtils;

import java.util.Locale;

/**
 * Created by huangyu on 2017-5-23.
 */
public class AppApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LanguageUtils.changeLanguage(Locale.getDefault());
    }

}
