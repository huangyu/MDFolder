package com.huangyu.mdfolder.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.provider.DocumentFile;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huangyu.library.app.ActivityManager;
import com.huangyu.library.mvp.IBaseView;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.AppApplication;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.ui.fragment.AlbumFolderFragment;
import com.huangyu.mdfolder.ui.fragment.FileListFragment;
import com.huangyu.mdfolder.utils.AlertUtils;
import com.huangyu.mdfolder.utils.SDCardUtils;
import com.huangyu.mdfolder.utils.SPUtils;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.huangyu.mdfolder.app.Constants.PERMISSION_ACCESS_FILES;
import static com.huangyu.mdfolder.app.Constants.STORAGE_REQUEST_CODE;
import static com.huangyu.mdfolder.app.Constants.UNINSTALL_REQUEST_CODE;
import static com.huangyu.mdfolder.utils.SDCardUtils.getStoragePath;

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
    RelativeLayout mRlAlbum;

    private SearchView mSearchView;
    private FileListFragment mFileListFragment;
    private AlbumFolderFragment mAlbumFolderFragment;
    private boolean isSearchViewShow;
    private long mCurrentTime;
    private int selectedPosition = 0;

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
        mToolbar.setTitleTextAppearance(this, R.style.ToolBarTitle);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        String outerSdcardPath = getStoragePath(this, true);
        if (!TextUtils.isEmpty(outerSdcardPath)) {
            MenuItem item = mNavigationView.getMenu().add(R.id.nav_group_folder, R.id.nav_outer_storage, 2, getString(R.string.menu_outer_storage));
            item.setIcon(R.mipmap.ic_sd);
            item.setTitle(getString(R.string.menu_outer_storage) + " " + SDCardUtils.getSDCardSizeInfo(getStoragePath(this, true)));
        }
        mNavigationView.getMenu().setGroupCheckable(R.id.nav_group_folder, true, true);

        mNavigationView.getMenu().getItem(0).setTitle(getString(R.string.menu_inner_storage) + " " + SDCardUtils.getSDCardSizeInfo(getStoragePath(this, false)));

        View header = mNavigationView.getHeaderView(0);
        LinearLayout mLlHeader = ButterKnife.findById(header, R.id.ll_header);
        String themeMode = getThemeMode();
        switch (themeMode) {
            case "1":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case "2":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryIndigo));
                break;
            case "3":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryCyan));
                break;
            case "4":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryTeal));
                break;
            case "5":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryGreen));
                break;
            case "6":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryRed));
                break;
            case "7":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryPurple));
                break;
            case "8":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOrange));
                break;
            case "9":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryYellow));
                break;
            case "10":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryPink));
                break;
            case "11":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryBrown));
                break;
            case "12":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryGrey));
                break;
            case "13":
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryBlack));
                break;
            default:
                mLlHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryBlue));
                break;
        }

        requirePermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isChanged()) {
            replaceFragment();
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
            selectedPosition = 0;
            mRlFile.setVisibility(View.VISIBLE);
            mRlAlbum.setVisibility(View.GONE);
            mRxManager.post("toStorage", false);
            mNavigationView.setCheckedItem(R.id.nav_inner_storage);
            return;
        }

        if (isDoubleCheck()) {
            ActivityManager.getInstance().finishAllActivity();
        } else {
            mCurrentTime = System.currentTimeMillis();
            View view = ButterKnife.findById(this, R.id.cl_main);
            if (view != null) {
                AlertUtils.showToast(FileListActivity.this, getString(R.string.tips_leave));
            }
        }
    }

    private boolean isDoubleCheck() {
        return Math.abs(mCurrentTime - System.currentTimeMillis()) < 1500L;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initSearchView(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initSearchView(menu);

        int sortType = SPUtils.getSortType();
        switch (sortType) {
            case Constants.SortType.TYPE:
                menu.getItem(1).setChecked(true);
                break;
            case Constants.SortType.ALPHABET:
                menu.getItem(2).setChecked(true);
                break;
            case Constants.SortType.TIME:
                menu.getItem(3).setChecked(true);
                break;
            case Constants.SortType.SIZE:
                menu.getItem(4).setChecked(true);
                break;
            case Constants.SortType.REMARK:
                menu.getItem(5).setChecked(true);
                break;
        }

        int orderType = SPUtils.getOrderType();
        switch (orderType) {
            case Constants.OrderType.ASC:
                menu.getItem(6).setChecked(false);
                break;
            case Constants.OrderType.DESC:
                menu.getItem(6).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_type:
                item.setChecked(!item.isChecked());
                SPUtils.setSortType(Constants.SortType.TYPE);
                mRxManager.post("onSortType", Constants.SortType.TYPE);
                break;
            case R.id.action_sort_by_time:
                item.setChecked(!item.isChecked());
                SPUtils.setSortType(Constants.SortType.TIME);
                mRxManager.post("onSortType", Constants.SortType.TIME);
                break;
            case R.id.action_sort_by_alphabet:
                item.setChecked(!item.isChecked());
                SPUtils.setSortType(Constants.SortType.ALPHABET);
                mRxManager.post("onSortType", Constants.SortType.ALPHABET);
                break;
            case R.id.action_sort_by_size:
                item.setChecked(!item.isChecked());
                SPUtils.setSortType(Constants.SortType.SIZE);
                mRxManager.post("onSortType", Constants.SortType.SIZE);
                break;
            case R.id.action_sort_by_remark:
                item.setChecked(!item.isChecked());
                SPUtils.setSortType(Constants.SortType.REMARK);
                mRxManager.post("onSortType", Constants.SortType.REMARK);
                break;
            case R.id.action_order_by_ascending:
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    SPUtils.setOrderType(Constants.OrderType.DESC);
                    mRxManager.post("onOrderType", Constants.OrderType.DESC);
                } else {
                    SPUtils.setOrderType(Constants.OrderType.ASC);
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
            mRlAlbum.setVisibility(View.GONE);
        }
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (item.getItemId()) {
                    case R.id.nav_inner_storage:
                        selectedPosition = 0;
                        mRxManager.post("toStorage", false);
                        break;
                    case R.id.nav_outer_storage:
                        checkSdCardPermission();
                        break;
                    case R.id.nav_root:
                        selectedPosition = 2;
                        mRxManager.post("toRoot", "");
                        break;
                    case R.id.nav_download:
                        selectedPosition = 3;
                        mRxManager.post("toDownload", "");
                        break;
                    case R.id.nav_photo:
                        selectedPosition = 4;
                        mRlFile.setVisibility(View.GONE);
                        mRlAlbum.setVisibility(View.VISIBLE);
                        break;
                    case R.id.nav_music:
                        selectedPosition = 5;
                        mRxManager.post("toMusic", "");
                        break;
                    case R.id.nav_video:
                        selectedPosition = 6;
                        mRxManager.post("toVideo", "");
                        break;
                    case R.id.nav_document:
                        selectedPosition = 7;
                        mRxManager.post("toDocument", "");
                        break;
                    case R.id.nav_apk:
                        selectedPosition = 8;
                        mRxManager.post("toApk", "");
                        break;
                    case R.id.nav_zip:
                        selectedPosition = 9;
                        mRxManager.post("toZip", "");
                        break;
                    case R.id.nav_apps:
                        selectedPosition = 10;
                        mRxManager.post("toApps", "");
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
                    selectedPosition = 0;
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
                selectedPosition = 0;
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

    private void checkSdCardPermission() {
        String sdcardPath = getStoragePath(this, true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && sdcardPath != null) {
            File sdcard = new File(sdcardPath);
            List<UriPermission> permissions = getContentResolver().getPersistedUriPermissions();
            DocumentFile documentFile;
            boolean needPermissions = true;
            for (UriPermission permission : permissions) {
                if (permission.isWritePermission()) {
                    documentFile = DocumentFile.fromTreeUri(this, permission.getUri());
                    if (documentFile != null) {
                        if (documentFile.lastModified() == sdcard.lastModified()) {
                            needPermissions = false;
                            break;
                        }
                    }
                }
            }

            if (needPermissions) {
                AlertUtils.showNormalAlert(this, getString(R.string.tips_select_sdcard), getString(R.string.act_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), STORAGE_REQUEST_CODE);
                    }
                });
            } else {
                selectedPosition = 1;
                mRxManager.post("toStorage", true);
            }
        } else {
            // 无法使用此功能
            AlertUtils.showToast(FileListActivity.this, getString(R.string.tips_cannot_access_sdcard));
            selectedPosition = 1;
            mRxManager.post("toStorage", true);
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (resultCode == RESULT_OK && data != null && mAlbumFolderFragment != null) {
            mAlbumFolderFragment.onActivityReenter(resultCode, data);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STORAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                String externalPath = SDCardUtils.getStoragePath(this, true);
                if (externalPath == null) {
                    // 无法使用此功能
                    AlertUtils.showToast(FileListActivity.this, getString(R.string.tips_cannot_access_sdcard));
                    selectedPosition = 1;
                    mRxManager.post("toStorage", true);
                    return;
                }

                File sdcard = new File(externalPath);
                boolean needPermissions = true;
                DocumentFile documentFile = DocumentFile.fromTreeUri(this, data.getData());
                if (documentFile != null) {
                    if (documentFile.lastModified() == sdcard.lastModified()) {
                        needPermissions = false;
                    }
                }

                if (needPermissions) {
                    startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), STORAGE_REQUEST_CODE);
                } else {
                    Uri uri = data.getData();
                    getContentResolver().takePersistableUriPermission(data.getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    ((AppApplication) AppApplication.getInstance()).getSPUtils().put("uri", uri.toString());
                    selectedPosition = 1;
                    mRxManager.post("toStorage", true);
                }
            } else {
                // 无法使用此功能
                AlertUtils.showToast(FileListActivity.this, getString(R.string.tips_cannot_access_sdcard));
                selectedPosition = 1;
                mRxManager.post("toStorage", true);
            }
        } else if (requestCode == UNINSTALL_REQUEST_CODE) {
            mRxManager.post("onFinishAction", "");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
