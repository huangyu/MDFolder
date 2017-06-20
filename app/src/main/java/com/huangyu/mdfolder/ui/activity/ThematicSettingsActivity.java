package com.huangyu.mdfolder.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

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

}
