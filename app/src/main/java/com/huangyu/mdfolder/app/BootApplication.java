package com.huangyu.mdfolder.app;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.library.util.SPUtils;

/**
 * Created by huangyu on 2017-5-23.
 */
public class BootApplication extends BaseApplication {

    private SPUtils spUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        spUtils = new SPUtils(Constants.APP_NAME);
    }

    public SPUtils getSPUtils() {
        return spUtils;
    }

}
