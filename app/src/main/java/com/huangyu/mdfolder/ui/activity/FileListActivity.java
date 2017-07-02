package com.huangyu.mdfolder.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.huangyu.library.app.ActivityManager;
import com.huangyu.library.mvp.IBaseView;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.ui.fragment.AlbumFolderFragment;
import com.huangyu.mdfolder.ui.fragment.FileListFragment;
import com.huangyu.mdfolder.utils.AlertUtils;
import com.huangyu.mdfolder.utils.SDCardUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.huangyu.mdfolder.app.Constants.PERMISSION_ACCESS_FILES;

public class FileListActivity extends ThematicActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    @Bind(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.rl_file)
    RelativeLayout mRlFile;

    @Bind(R.id.rl_album)
    RelativeLayout mRLAlbum;

    private SearchView mSearchView;
    private FileListFragment mFileListFragment;
    private AlbumFolderFragment mAlbumFolderFragment;
    private boolean isSearchViewShow;
    private long mCurrentTime;
    private int selectedPosition = -1;

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
        mFileListFragment = new FileListFragment();
        mAlbumFolderFragment = new AlbumFolderFragment();

        setSupportActionBar(mToolbar);

        if (isLightMode()) {
            mAppBarLayout.getContext().setTheme(R.style.AppTheme_AppBarOverlay);
            mToolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
        } else {
            mAppBarLayout.getContext().setTheme(R.style.AppTheme_AppBarOverlay_Dark);
            mToolbar.setPopupTheme(R.style.AppTheme_PopupOverlay_Dark);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        String outerSdcardPath = SDCardUtils.getStoragePath(this, true);
        if (!TextUtils.isEmpty(outerSdcardPath)) {
            MenuItem item = mNavigationView.getMenu().add(R.id.nav_group_folder, R.id.nav_outer_storage, 2, getString(R.string.menu_outer_storage));
            item.setIcon(R.mipmap.ic_sd);
            item.setTitle(getString(R.string.menu_inner_storage) + " " + SDCardUtils.getSDCardSizeInfo(SDCardUtils.getStoragePath(this, true)));
        }
        mNavigationView.getMenu().setGroupCheckable(R.id.nav_group_folder, true, true);

        mNavigationView.getMenu().getItem(0).setTitle(getString(R.string.menu_inner_storage) + " " + SDCardUtils.getSDCardSizeInfo(SDCardUtils.getStoragePath(this, false)));

        requirePermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isChanged()) {
            replaceFragment();
            if (selectedPosition != 4) {
                mRlFile.setVisibility(View.VISIBLE);
                mRLAlbum.setVisibility(View.GONE);
            }
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (selectedPosition) {
                        case 2:
                            mRxManager.post("toRoot", "");
                            mNavigationView.setCheckedItem(R.id.nav_root);
                            break;
                        case 1:
                            mRxManager.post("toStorage", true);
                            mNavigationView.setCheckedItem(R.id.nav_outer_storage);
                            break;
                        case 5:
                            mRxManager.post("toMusic", "");
                            mNavigationView.setCheckedItem(R.id.nav_music);
                            break;
                        case 4:
//                        mRxManager.post("toPhoto", "");
                            mRlFile.setVisibility(View.GONE);
                            mRLAlbum.setVisibility(View.VISIBLE);
                            mNavigationView.setCheckedItem(R.id.nav_photo);
                            break;
                        case 6:
                            mRxManager.post("toVideo", "");
                            mNavigationView.setCheckedItem(R.id.nav_video);
                            break;
                        case 7:
                            mRxManager.post("toDocument", "");
                            mNavigationView.setCheckedItem(R.id.nav_document);
                            break;
                        case 3:
                            mRxManager.post("toDownload", "");
                            mNavigationView.setCheckedItem(R.id.nav_download);
                            break;
                        case 8:
                            mRxManager.post("toApk", "");
                            mNavigationView.setCheckedItem(R.id.nav_apk);
                            break;
                        case 9:
                            mRxManager.post("toZip", "");
                            mNavigationView.setCheckedItem(R.id.nav_zip);
                            break;
                        default:
                            mRxManager.post("toStorage", false);
                            mNavigationView.setCheckedItem(R.id.nav_inner_storage);
                            break;
                    }
                }
            }, 200);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
//        super.onSaveInstanceState(outState);
    }

    private void replaceFragment() {
        mFileListFragment = new FileListFragment();
        mAlbumFolderFragment = new AlbumFolderFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rl_file, mFileListFragment)
                .replace(R.id.rl_album, mAlbumFolderFragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (mFileListFragment != null && mFileListFragment.isVisible() && mFileListFragment.onBackPressed()) {
            return;
        }

        if (mAlbumFolderFragment != null && mAlbumFolderFragment.isVisible() && mAlbumFolderFragment.onBackPressed()) {
            return;
        }

        if (mSearchView != null && mSearchView.isShown() && isSearchViewShow) {
            resetSearch();
            return;
        }

        if (selectedPosition != 0) {
            if (selectedPosition != 4) {
                mRlFile.setVisibility(View.VISIBLE);
                mRLAlbum.setVisibility(View.GONE);
            } else {
                mRlFile.setVisibility(View.GONE);
                mRLAlbum.setVisibility(View.VISIBLE);
            }
            mRxManager.post("toStorage", false);
            selectedPosition = 0;
            mNavigationView.setCheckedItem(R.id.nav_inner_storage);
            return;
        }

        if (isDoubleCheck()) {
            ActivityManager.getInstance().finishAllActivity();
        } else {
            mCurrentTime = System.currentTimeMillis();
            View view = ButterKnife.findById(this, R.id.cl_main);
            if (view != null) {
                AlertUtils.showSnack(view, getString(R.string.tips_leave));
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_type:
                item.setChecked(!item.isChecked());
                mRxManager.post("onSortType", Constants.SortType.TYPE);
                break;
            case R.id.action_sort_by_time:
                item.setChecked(!item.isChecked());
                mRxManager.post("onSortType", Constants.SortType.TIME);
                break;
            case R.id.action_sort_by_alphabet:
                item.setChecked(!item.isChecked());
                mRxManager.post("onSortType", Constants.SortType.ALPHABET);
                break;
            case R.id.action_sort_by_size:
                item.setChecked(!item.isChecked());
                mRxManager.post("onSortType", Constants.SortType.SIZE);
                break;
            case R.id.action_sort_ascending:
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    mRxManager.post("onOrderType", Constants.OrderType.DESC);
                } else {
                    mRxManager.post("onOrderType", Constants.OrderType.ASC);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initSearchView(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                mSearchView.setIconified(false);
                mRxManager.post("onSearch", text);
                return true;
            }

            public boolean onQueryTextChange(String text) {
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
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                resetSearch();
                mRxManager.post("onSearch", "");
                return true;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        if (item.getItemId() != R.id.nav_photo) {
            mRlFile.setVisibility(View.VISIBLE);
            mRLAlbum.setVisibility(View.GONE);
        }
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (item.getItemId()) {
                    case R.id.nav_root:
                        selectedPosition = 2;
                        mRxManager.post("toRoot", "");
                        break;
                    case R.id.nav_inner_storage:
                        selectedPosition = 0;
                        mRxManager.post("toStorage", false);
                        break;
                    case R.id.nav_outer_storage:
                        selectedPosition = 1;
                        mRxManager.post("toStorage", true);
                        break;
                    case R.id.nav_music:
                        selectedPosition = 5;
                        mRxManager.post("toMusic", "");
                        break;
                    case R.id.nav_photo:
                        selectedPosition = 4;
//                        mRxManager.post("toPhoto", "");
                        mRlFile.setVisibility(View.GONE);
                        mRLAlbum.setVisibility(View.VISIBLE);
                        break;
                    case R.id.nav_video:
                        selectedPosition = 6;
                        mRxManager.post("toVideo", "");
                        break;
                    case R.id.nav_document:
                        selectedPosition = 7;
                        mRxManager.post("toDocument", "");
                        break;
                    case R.id.nav_download:
                        selectedPosition = 3;
                        mRxManager.post("toDownload", "");
                        break;
                    case R.id.nav_apk:
                        selectedPosition = 8;
                        mRxManager.post("toApk", "");
                        break;
                    case R.id.nav_zip:
                        selectedPosition = 9;
                        mRxManager.post("toZip", "");
                        break;
                    case R.id.nav_settings:
                        startActivity(SettingsActivity.class);
                        break;
                }
                resetSearch();
            }
        }, 200);
        return true;
    }

    private void resetSearch() {
        supportInvalidateOptionsMenu();
        mSearchView.onActionViewCollapsed();
        isSearchViewShow = false;
    }

    @AfterPermissionGranted(PERMISSION_ACCESS_FILES)
    private void requirePermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            replaceFragment();
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRxManager.post("toStorage", false);
                }
            }, 200);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.tips_require_file_permissions), PERMISSION_ACCESS_FILES, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        replaceFragment();
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRxManager.post("toStorage", false);
            }
        }, 200);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        AlertUtils.showSnack(mRlFile, getString(R.string.tips_no_permissions), getString(R.string.act_exit), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance().finishAllActivity();
            }
        });

    }

}
