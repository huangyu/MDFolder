package com.huangyu.mdfolder.ui.activity;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.huangyu.library.mvp.IBaseView;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.ui.fragment.SettingsFragment;

import butterknife.Bind;

public class SettingsActivity extends ThematicActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.content_main;
    }

    @Override
    protected IBaseView initAttachView() {
        return null;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mToolbar.setTitle(getString(R.string.menu_settings));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        replaceFragment();
    }

    private void replaceFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rl_content, new SettingsFragment())
                .commit();
    }

}
