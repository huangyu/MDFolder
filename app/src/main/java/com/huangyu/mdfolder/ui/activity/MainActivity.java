package com.huangyu.mdfolder.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.ui.BaseActivity;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.mvp.presenter.MainPresenter;
import com.huangyu.mdfolder.mvp.view.IMainView;
import com.huangyu.mdfolder.utils.AlertUtils;

import butterknife.Bind;

public class MainActivity extends BaseActivity<IBaseView, MainPresenter> implements NavigationView.OnNavigationItemSelectedListener, IMainView {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.cl_main)
    ConstraintLayout mConstraintLayout;

    SearchView mSearchView;

    private boolean isSearchViewShow;
    private long mCurrentTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected IBaseView initAttachView() {
        return this;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mSearchView != null && mSearchView.isShown() && isSearchViewShow) {
                mSearchView.onActionViewCollapsed();
                supportInvalidateOptionsMenu();
                isSearchViewShow = false;
                return;
            }

            if (isDoubleCheck()) {
                finish();
            } else {
                mCurrentTime = System.currentTimeMillis();
                AlertUtils.showSnack(mConstraintLayout, getString(R.string.tips_leave));
            }
        }
    }

    private boolean isDoubleCheck() {
        return Math.abs(mCurrentTime - System.currentTimeMillis()) < 1000L;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        initSearchView(menu);
        return true;
    }

    private void initSearchView(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                mSearchView.setIconified(false);
                return true;
            }

            public boolean onQueryTextChange(String text) {
                // TODO
                return false;
            }
        });
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isSearchViewShow = true;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // TODO
                return true;
            case R.id.action_settings:
                // TODO
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_music:
                // TODO
                break;
            case R.id.nav_photo:
                // TODO
                break;
            case R.id.nav_video:
                // TODO
                break;
            case R.id.nav_apps:
                // TODO
                break;
            case R.id.nav_share:
                // TODO
                break;
            case R.id.nav_help:
                // TODO
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        mSearchView.onActionViewCollapsed();
        supportInvalidateOptionsMenu();
        isSearchViewShow = false;
        return true;
    }

}
