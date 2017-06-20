package com.huangyu.mdfolder.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

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
            recreateActivity();
        }
    }

    /**
     * Delaying activity recreate by 1 millisecond. If the recreate is not delayed and is done
     * immediately in onResume() you will get RuntimeException: Performing pause of activity that is not resumed
     */
    public void recreateActivity() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        }, 1);
    }

    protected boolean isChanged() {
        return mThemeUtils.isChanged();
    }

    public boolean isLightMode() {
        return mThemeUtils.isLightMode();
    }

}
