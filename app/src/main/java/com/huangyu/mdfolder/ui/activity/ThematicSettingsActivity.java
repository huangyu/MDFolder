package com.huangyu.mdfolder.ui.activity;

import android.os.Bundle;

import com.huangyu.mdfolder.utils.ThemeUtils;

/**
 * Created by huangyu on 2017-6-16.
 */
public abstract class ThematicSettingsActivity extends AppCompatPreferenceActivity {

    private ThemeUtils mThemeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeUtils = new ThemeUtils(this);
        setTheme(mThemeUtils.getCurrentBySettings());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mThemeUtils.isChanged()) {
            setTheme(mThemeUtils.getCurrentBySettings());
            recreate();
        }
    }

    public boolean isLightMode() {
        return mThemeUtils.isLightMode();
    }

}
