package com.huangyu.mdfolder.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.RelativeLayout;

import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.ui.BaseActivity;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.ui.fragment.MainFragment;
import com.huangyu.mdfolder.utils.AlertUtils;

import java.util.List;

import butterknife.Bind;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.huangyu.mdfolder.app.Constants.PERMISSION_ACCESS_FILES;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.rl_content)
    RelativeLayout mRelativeLayout;

    private SearchView mSearchView;
    private MainFragment mMainFragment;
    private boolean isSearchViewShow;
    private long mCurrentTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected IBaseView initAttachView() {
        return null;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        // 请求运行时权限
        requirePermissions();
    }

    private void replaceFragment() {
        mMainFragment = new MainFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rl_content, mMainFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (mMainFragment != null && mMainFragment.onBackPressed()) {
            return;
        }

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
            AlertUtils.showSnack(mRelativeLayout, getString(R.string.tips_leave));
        }
    }

    private boolean isDoubleCheck() {
        return Math.abs(mCurrentTime - System.currentTimeMillis()) < 1000L;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @AfterPermissionGranted(PERMISSION_ACCESS_FILES)
    private void requirePermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            replaceFragment();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.tips_require_file_permissions), PERMISSION_ACCESS_FILES, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        replaceFragment();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        AlertUtils.showSnack(mRelativeLayout, getString(R.string.tips_no_permissions), getString(R.string.act_exit), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
