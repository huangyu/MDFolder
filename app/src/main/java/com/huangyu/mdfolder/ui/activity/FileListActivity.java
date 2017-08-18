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
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import com.huangyu.mdfolder.ui.fragment.AlbumAndImageFragment;
import com.huangyu.mdfolder.ui.fragment.FileListFragment;
import com.huangyu.mdfolder.utils.AlertUtils;
import com.huangyu.mdfolder.utils.RootUtils;
import com.huangyu.mdfolder.utils.SDCardUtils;
import com.huangyu.mdfolder.utils.SPUtils;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.huangyu.mdfolder.app.Constants.PERMISSION_ACCESS_FILES;
import static com.huangyu.mdfolder.app.Constants.STORAGE_REQUEST_CODE;
import static com.huangyu.mdfolder.app.Constants.UNINSTALL_REQUEST_CODE;
import static com.huangyu.mdfolder.utils.SDCardUtils.getStoragePath;

public class FileListActivity extends ThematicActivity implements EasyPermissions.PermissionCallbacks {

    @Bind(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.rl_file)
    RelativeLayout mRlFile;

    @Bind(R.id.rl_album)
    RelativeLayout mRlAlbum;

    private SearchView mSearchView;
    private FileListFragment mFileListFragment;
    private AlbumAndImageFragment mAlbumAndImageFragment;
    private boolean isSearchViewShow;
    private long mCurrentTime;
    private int selectedPosition = 0;
    private Drawer mDrawer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_list;
    }

    @Override
    protected IBaseView initAttachView() {
        return null;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mFileListFragment = new FileListFragment();
        mAlbumAndImageFragment = new AlbumAndImageFragment();

        setSupportActionBar(mToolbar);

        if (isLightMode()) {
            mAppBarLayout.getContext().setTheme(R.style.AppTheme_AppBarOverlay);
            mToolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
        } else {
            mAppBarLayout.getContext().setTheme(R.style.AppTheme_AppBarOverlay_Dark);
            mToolbar.setPopupTheme(R.style.AppTheme_PopupOverlay_Dark);
        }
        mToolbar.setTitleTextAppearance(this, R.style.ToolBarTitle);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, new LinearLayout(this), false);
        String themeMode = getThemeMode();
        switch (themeMode) {
            case "1":
                header.setBackgroundResource(R.color.colorPrimaryDark);
                break;
            case "2":
                header.setBackgroundResource(R.color.colorPrimaryIndigo);
                break;
            case "3":
                header.setBackgroundResource(R.color.colorPrimaryCyan);
                break;
            case "4":
                header.setBackgroundResource(R.color.colorPrimaryTeal);
                break;
            case "5":
                header.setBackgroundResource(R.color.colorPrimaryGreen);
                break;
            case "6":
                header.setBackgroundResource(R.color.colorPrimaryRed);
                break;
            case "7":
                header.setBackgroundResource(R.color.colorPrimaryPurple);
                break;
            case "8":
                header.setBackgroundResource(R.color.colorPrimaryOrange);
                break;
            case "9":
                header.setBackgroundResource(R.color.colorPrimaryYellow);
                break;
            case "10":
                header.setBackgroundResource(R.color.colorPrimaryPink);
                break;
            case "11":
                header.setBackgroundResource(R.color.colorPrimaryBrown);
                break;
            case "12":
                header.setBackgroundResource(R.color.colorPrimaryGrey);
                break;
            case "13":
                header.setBackgroundResource(R.color.colorPrimaryBlack);
                break;
            default:
                header.setBackgroundResource(R.color.colorPrimaryBlue);
                break;
        }


        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHeader(header)
                .withActionBarDrawerToggle(true)
                .build();

        PrimaryDrawerItem item1;
        PrimaryDrawerItem item3;
        PrimaryDrawerItem item4;
        PrimaryDrawerItem item5;
        PrimaryDrawerItem item6;
        PrimaryDrawerItem item7;
        PrimaryDrawerItem item8;
        PrimaryDrawerItem item9;
        PrimaryDrawerItem item10;
        PrimaryDrawerItem item11;
        PrimaryDrawerItem item12;
        PrimaryDrawerItem item13;

        if (isLightMode()) {
            item1 = new PrimaryDrawerItem().withIdentifier(0L).withIcon(R.mipmap.ic_storage).withName(R.string.menu_inner_storage).withDescription(SDCardUtils.getSDCardSizeInfo(getStoragePath(this, false))).withTextColorRes(R.color.colorPrimaryText).withDescriptionTextColorRes(R.color.colorSecondaryText);
            item3 = new PrimaryDrawerItem().withIdentifier(2L).withIcon(R.mipmap.ic_root).withName(R.string.menu_root).withTextColorRes(R.color.colorPrimaryText);
            item4 = new PrimaryDrawerItem().withIdentifier(3L).withIcon(R.mipmap.ic_recent).withName(R.string.menu_recent).withTextColorRes(R.color.colorPrimaryText);
            item5 = new PrimaryDrawerItem().withIdentifier(4L).withIcon(R.mipmap.ic_download).withName(R.string.menu_download).withTextColorRes(R.color.colorPrimaryText);
            item6 = new PrimaryDrawerItem().withIdentifier(5L).withIcon(R.mipmap.ic_photo).withName(R.string.menu_photo).withTextColorRes(R.color.colorPrimaryText);
            item7 = new PrimaryDrawerItem().withIdentifier(6L).withIcon(R.mipmap.ic_music).withName(R.string.menu_music).withTextColorRes(R.color.colorPrimaryText);
            item8 = new PrimaryDrawerItem().withIdentifier(7L).withIcon(R.mipmap.ic_video).withName(R.string.menu_video).withTextColorRes(R.color.colorPrimaryText);
            item9 = new PrimaryDrawerItem().withIdentifier(8L).withIcon(R.mipmap.ic_document).withName(R.string.menu_document).withTextColorRes(R.color.colorPrimaryText);
            item10 = new PrimaryDrawerItem().withIdentifier(9L).withIcon(R.mipmap.ic_apk).withName(R.string.menu_apk).withTextColorRes(R.color.colorPrimaryText);
            item11 = new PrimaryDrawerItem().withIdentifier(10L).withIcon(R.mipmap.ic_zip).withName(R.string.menu_compress_package).withTextColorRes(R.color.colorPrimaryText);
            item12 = new PrimaryDrawerItem().withIdentifier(11L).withIcon(R.mipmap.ic_apps).withName(R.string.menu_apps).withTextColorRes(R.color.colorPrimaryText);
            item13 = new PrimaryDrawerItem().withIdentifier(12L).withIcon(R.mipmap.ic_settings).withName(R.string.menu_settings).withTextColorRes(R.color.colorPrimaryText);
            mDrawer.getRecyclerView().setBackgroundResource(R.drawable.select_item);
        } else {
            item1 = new PrimaryDrawerItem().withIdentifier(0L).withIcon(R.mipmap.ic_storage).withName(R.string.menu_inner_storage).withDescription(SDCardUtils.getSDCardSizeInfo(getStoragePath(this, false))).withTextColorRes(R.color.colorPrimaryTextWhite).withDescriptionTextColorRes(R.color.colorSecondaryTextWhite);
            item3 = new PrimaryDrawerItem().withIdentifier(2L).withIcon(R.mipmap.ic_root).withName(R.string.menu_root).withTextColorRes(R.color.colorPrimaryTextWhite);
            item4 = new PrimaryDrawerItem().withIdentifier(3L).withIcon(R.mipmap.ic_recent).withName(R.string.menu_recent).withTextColorRes(R.color.colorPrimaryTextWhite);
            item5 = new PrimaryDrawerItem().withIdentifier(4L).withIcon(R.mipmap.ic_download).withName(R.string.menu_download).withTextColorRes(R.color.colorPrimaryTextWhite);
            item6 = new PrimaryDrawerItem().withIdentifier(5L).withIcon(R.mipmap.ic_photo).withName(R.string.menu_photo).withTextColorRes(R.color.colorPrimaryTextWhite);
            item7 = new PrimaryDrawerItem().withIdentifier(6L).withIcon(R.mipmap.ic_music).withName(R.string.menu_music).withTextColorRes(R.color.colorPrimaryTextWhite);
            item8 = new PrimaryDrawerItem().withIdentifier(7L).withIcon(R.mipmap.ic_video).withName(R.string.menu_video).withTextColorRes(R.color.colorPrimaryTextWhite);
            item9 = new PrimaryDrawerItem().withIdentifier(8L).withIcon(R.mipmap.ic_document).withName(R.string.menu_document).withTextColorRes(R.color.colorPrimaryTextWhite);
            item10 = new PrimaryDrawerItem().withIdentifier(9L).withIcon(R.mipmap.ic_apk).withName(R.string.menu_apk).withTextColorRes(R.color.colorPrimaryTextWhite);
            item11 = new PrimaryDrawerItem().withIdentifier(10L).withIcon(R.mipmap.ic_zip).withName(R.string.menu_compress_package).withTextColorRes(R.color.colorPrimaryTextWhite);
            item12 = new PrimaryDrawerItem().withIdentifier(11L).withIcon(R.mipmap.ic_apps).withName(R.string.menu_apps).withTextColorRes(R.color.colorPrimaryTextWhite);
            item13 = new PrimaryDrawerItem().withIdentifier(12L).withIcon(R.mipmap.ic_settings).withName(R.string.menu_settings).withTextColorRes(R.color.colorPrimaryTextWhite);
            mDrawer.getRecyclerView().setBackgroundResource(R.drawable.select_item_dark);
        }

        mDrawer.addItems(
                item1,
                item3,
                item4,
                item5,
                item6,
                item7,
                item8,
                item9,
                item10,
                item11,
                item12,
                new DividerDrawerItem()
        );

        String outerSdcardPath = getStoragePath(this, true);
        if (!TextUtils.isEmpty(outerSdcardPath)) {
            PrimaryDrawerItem item2;
            if (isLightMode()) {
                item2 = new PrimaryDrawerItem().withIdentifier(1L).withIcon(R.mipmap.ic_sd)
                        .withName(R.string.menu_outer_storage).withDescription(SDCardUtils.getSDCardSizeInfo(getStoragePath(this, true)))
                        .withTextColorRes(R.color.colorPrimaryText).withDescriptionTextColorRes(R.color.colorSecondaryText);
            } else {
                item2 = new PrimaryDrawerItem().withIdentifier(1L).withIcon(R.mipmap.ic_sd)
                        .withName(R.string.menu_outer_storage).withDescription(SDCardUtils.getSDCardSizeInfo(getStoragePath(this, true)))
                        .withTextColorRes(R.color.colorPrimaryTextWhite).withDescriptionTextColorRes(R.color.colorSecondaryTextWhite);
            }
            mDrawer.addItemAtPosition(item2, 2);
        }
        mDrawer.addItemAtPosition(item13, mDrawer.getAdapter().getItemCount());

        mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, final int position, final IDrawerItem drawerItem) {
                mDrawer.closeDrawer();
                selectedPosition = Long.valueOf(drawerItem.getIdentifier()).intValue();
                if (selectedPosition != 5) {
                    mRlFile.setVisibility(View.VISIBLE);
                    mRlAlbum.setVisibility(View.GONE);
                }
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (selectedPosition) {
                            case 0:
                                mRxManager.post("toStorage", false);
                                break;
                            case 1:
                                checkSdCardPermission();
                                break;
                            case 2:
                                checkRootPermission();
                                break;
                            case 3:
                                mRxManager.post("toRecent", "");
                                break;
                            case 4:
                                mRxManager.post("toDownload", "");
                                break;
                            case 5:
                                mRlFile.setVisibility(View.GONE);
                                mRlAlbum.setVisibility(View.VISIBLE);
                                break;
                            case 6:
                                mRxManager.post("toMusic", "");
                                break;
                            case 7:
                                mRxManager.post("toVideo", "");
                                break;
                            case 8:
                                mRxManager.post("toDocument", "");
                                break;
                            case 9:
                                mRxManager.post("toApk", "");
                                break;
                            case 10:
                                mRxManager.post("toZip", "");
                                break;
                            case 11:
                                mRxManager.post("toApps", "");
                                break;
                            case 12:
                                startActivity(SettingsActivity.class);
                                break;
                            default:
                                PrimaryDrawerItem primaryDrawerItem = (PrimaryDrawerItem) drawerItem;
                                String path = primaryDrawerItem.getDescription().getText().toString();
                                mRxManager.post("toPath", path);
                                break;
                        }
                        resetSearch();
                    }
                }, 200);
                return true;
            }
        });
        mDrawer.setOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
                final int longClickPosition = Long.valueOf(drawerItem.getIdentifier()).intValue();
                switch (longClickPosition) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        break;
                    default:
                        PrimaryDrawerItem primaryDrawerItem = (PrimaryDrawerItem) drawerItem;
                        String name = primaryDrawerItem.getName().getText().toString();
                        AlertUtils.showNormalAlert(FileListActivity.this, String.format(getString(R.string.tips_delete_bookmark), name), getString(R.string.act_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    SPUtils.removeBookmark(longClickPosition - 100);
                                    AlertUtils.showToast(FileListActivity.this, getString(R.string.tips_delete_bookmark_successfully));
                                    refreshBookMarkList();
                                } catch (Exception e) {
                                    AlertUtils.showToast(FileListActivity.this, getString(R.string.tips_delete_bookmark_in_error));
                                }
                            }
                        });
                        break;
                }
                return true;
            }
        });

        refreshBookMarkList();

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
        mAlbumAndImageFragment = new AlbumAndImageFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rl_file, mFileListFragment)
                .replace(R.id.rl_album, mAlbumAndImageFragment)
                .commitAllowingStateLoss();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mFileListFragment != null && mFileListFragment.isVisible() && mFileListFragment.isActionModeActive()) {
            onBackFolder();
            return true;
        } else if (mAlbumAndImageFragment != null && mAlbumAndImageFragment.isVisible() && mAlbumAndImageFragment.isActionModeActive()) {
            onBackFolder();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onBackPressed() {
        if (onBackFolder()) {
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

    private boolean onBackFolder() {
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
            return true;
        }

        if (mFileListFragment != null && mFileListFragment.isVisible() && mFileListFragment.onBackPressed()) {
            return true;
        }

        if (mAlbumAndImageFragment != null && mAlbumAndImageFragment.isVisible() && mAlbumAndImageFragment.onBackPressed()) {
            return true;
        }

        if (mSearchView != null && mSearchView.isShown() && isSearchViewShow) {
            resetSearch();
            return true;
        }

        if (selectedPosition != 0) {
            selectedPosition = 0;
            mRlFile.setVisibility(View.VISIBLE);
            mRlAlbum.setVisibility(View.GONE);
            mRxManager.post("toStorage", false);
            mDrawer.setSelection(0L);
            return true;
        }
        return false;
    }

    private boolean isDoubleCheck() {
        return Math.abs(mCurrentTime - System.currentTimeMillis()) < 1500L;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (selectedPosition == 3 || selectedPosition == 5 || selectedPosition == 6 || selectedPosition == 7
                || selectedPosition == 8 || selectedPosition == 9 || selectedPosition == 10 || selectedPosition == 11 || selectedPosition == 12) {
            getMenuInflater().inflate(R.menu.menu_main2, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
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
            case R.id.action_bookmark:
                mRxManager.post("onSaveBookmark", "");
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

    private void resetSearch() {
        supportInvalidateOptionsMenu();
        mSearchView.onActionViewCollapsed();
        isSearchViewShow = false;
    }

    public void refreshBookMarkList() {
        int maxNum = SPUtils.getBookmarkNum();
        SparseArray<String> bookmarkMap = new SparseArray<>();
        List<String> bookmarkList = new ArrayList<>();
        String bookmarkName;
        for (int i = 0; i < maxNum; i++) {
            bookmarkName = SPUtils.getBookmarkName(i);
            if (bookmarkName != null) {
                bookmarkMap.put(i, bookmarkName);
                bookmarkList.add(bookmarkName);
            }
        }

        for (int i = 0; i < maxNum; i++) {
            mDrawer.removeItems(100L + i);
        }
        PrimaryDrawerItem primaryDrawerItem;
        if (bookmarkList.size() > 0) {
            IDrawerItem drawerItem = mDrawer.getDrawerItem("divider");
            if (drawerItem == null) {
                DividerDrawerItem dividerDrawerItem = new DividerDrawerItem().withTag("divider");
                mDrawer.addItemAtPosition(dividerDrawerItem, mDrawer.getAdapter().getItemCount() - 1);
            }
            for (int i = 0; i < maxNum; i++) {
                bookmarkName = bookmarkMap.get(i);
                if (bookmarkName != null) {
                    primaryDrawerItem = new PrimaryDrawerItem();
                    if (isLightMode()) {
                        if (bookmarkName.equals(SDCardUtils.getStoragePath(this, false)) || bookmarkName.equals(SDCardUtils.getStoragePath(this, true))) {
                            primaryDrawerItem.withIdentifier(100L + i).withName(bookmarkName).withIcon(R.mipmap.ic_bookmark).withDescription(bookmarkName).withTextColorRes(R.color.colorPrimaryText).withDescriptionTextColorRes(R.color.colorSecondaryText);
                        } else {
                            primaryDrawerItem.withIdentifier(100L + i).withName(bookmarkName.substring(bookmarkName.lastIndexOf("/") + 1)).withIcon(R.mipmap.ic_bookmark).withDescription(bookmarkName).withTextColorRes(R.color.colorPrimaryText).withDescriptionTextColorRes(R.color.colorSecondaryText);
                        }
                    } else {
                        if (bookmarkName.equals(SDCardUtils.getStoragePath(this, false)) || bookmarkName.equals(SDCardUtils.getStoragePath(this, true))) {
                            primaryDrawerItem.withIdentifier(100L + i).withName(bookmarkName).withIcon(R.mipmap.ic_bookmark).withDescription(bookmarkName).withTextColorRes(R.color.colorPrimaryTextWhite).withDescriptionTextColorRes(R.color.colorSecondaryTextWhite);
                        } else {
                            primaryDrawerItem.withIdentifier(100L + i).withName(bookmarkName.substring(bookmarkName.lastIndexOf("/") + 1)).withIcon(R.mipmap.ic_bookmark).withDescription(bookmarkName).withTextColorRes(R.color.colorPrimaryTextWhite).withDescriptionTextColorRes(R.color.colorSecondaryTextWhite);
                        }
                    }
                    mDrawer.addItemAtPosition(primaryDrawerItem, mDrawer.getAdapter().getItemCount() - 2);
                }
            }
        } else {
            IDrawerItem drawerItem = mDrawer.getDrawerItem("divider");
            if (drawerItem != null) {
                mDrawer.removeItem(drawerItem.getIdentifier());
            }
        }
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

    private void checkRootPermission() {
        if (RootUtils.isRoot()) {
            if (RootUtils.requestRootPermission("com.huangyu.mdfolder")) {
                if (RootUtils.mount()) {
                    // TODO
                } else {
                    // TODO
                }
            } else {
                // TODO
            }
        } else {
            AlertUtils.showToast(this, getString(R.string.tips_no_root_permission));
            mRxManager.post("toRoot", "");
        }
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
        if (resultCode == RESULT_OK && data != null && mAlbumAndImageFragment != null) {
            mAlbumAndImageFragment.onActivityReenter(resultCode, data);
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
            if (data != null) {
                int position = data.getIntExtra("position", -1);
                mRxManager.post("onUninstall", position);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
