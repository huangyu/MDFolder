package com.huangyu.mdfolder.ui.activity;

import android.os.Bundle;

import com.huangyu.library.ui.BaseActivity;
import com.huangyu.mdfolder.utils.ThemeUtils;

/**
 * Created by huangyu on 2017-6-16.
 */
public abstract class ThematicActivity extends BaseActivity {

    private ThemeUtils mThemeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeUtils = new ThemeUtils(this);
        setTheme(mThemeUtils.getCurrent());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mThemeUtils.isChanged()) {
            setTheme(mThemeUtils.getCurrent());
            recreate();
        }
    }

    protected boolean isChanged() {
        return mThemeUtils.isChanged();
    }

    public boolean isLightMode() {
        return mThemeUtils.isLightMode();
    }

}
