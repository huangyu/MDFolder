package com.huangyu.mdfolder.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huangyu.library.BuildConfig;
import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.ui.BaseFragment;
import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.util.LogToFileUtils;
import com.huangyu.library.util.LogUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.mvp.model.FileModel;
import com.huangyu.mdfolder.ui.activity.FileListActivity;
import com.huangyu.mdfolder.ui.activity.ImageBrowserActivity;
import com.huangyu.mdfolder.ui.adapter.AlbumFolderAdapter;
import com.huangyu.mdfolder.ui.adapter.AlbumImageAdapter;
import com.huangyu.mdfolder.ui.widget.TabView;
import com.huangyu.mdfolder.utils.AlertUtils;
import com.huangyu.mdfolder.utils.SPUtils;
import com.yanzhenjie.album.widget.recyclerview.AlbumVerticalGirdDecoration;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by huangyu on 2017-5-23.
 */
public class AlbumFolderFragment extends BaseFragment {

    @Bind(R.id.cl_main)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.tab_view)
    TabView mTabView;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.ll_empty)
    LinearLayout mLlEmpty;

    @Bind(R.id.iv_center)
    ImageView mIvCenter;

    private AlbumFolderAdapter mAdapter;
    private AlbumImageAdapter mImageAdapter;
    private FileListModel mFileListModel;
    private String mSearchStr;
    private int mSortType;
    private int mOrderType;
    private FileModel mFileModel;
    private FileItem mCurrentAlbum;
    private boolean isInAlbum;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album;
    }

    @Override
    protected IBaseView initAttachView() {
        return null;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mFileListModel = new FileListModel();
        mFileModel = new FileModel();

        if (savedInstanceState == null) {
            isInAlbum = true;
        } else {
            isInAlbum = savedInstanceState.getBoolean("isInAlbum");
        }

        mIvCenter.setColorFilter(getResources().getColor(R.color.colorDarkGray));

        mImageAdapter = new AlbumImageAdapter(getContext());
        mImageAdapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FileItem file = mImageAdapter.getItem(position);
                if (file == null) {
                    return;
                }
                if (SPUtils.isBuildInMode()) {
                    Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
                    intent.putExtra(getString(R.string.intent_image_list), mImageAdapter.getDataList());
                    intent.putExtra(getString(R.string.intent_image_position), position);
                    getActivity().startActivity(intent);
                } else {
                    if (!mFileModel.openFile(getContext(), new File(file.getPath()))) {
                        AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_can_not_access_file));
                    }
                }
            }
        });
        mImageAdapter.setOnItemLongClick(new CommonRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mAdapter = new AlbumFolderAdapter(getContext());
        mAdapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FileItem albumFolder = mAdapter.getItem(position);
                mCurrentAlbum = albumFolder;
                loadImage();
            }
        });

        mAdapter.setOnItemLongClick(new CommonRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.grid_decoration, null);
        mRecyclerView.addItemDecoration(new AlbumVerticalGirdDecoration(drawable));
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (((FileListActivity) getActivity()).isLightMode()) {
            mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        } else {
            mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isInAlbum) {
                    loadAlbum();
                } else {
                    loadImage();
                }
            }
        });

        addTab();

        mRxManager.on("onSortType", new Action1<Integer>() {
            @Override
            public void call(Integer sortType) {
                mSortType = sortType;
                if (isInAlbum) {
                    loadAlbum();
                } else {
                    loadImage();
                }
            }
        });

        mRxManager.on("onOrderType", new Action1<Integer>() {
            @Override
            public void call(Integer orderType) {
                mOrderType = orderType;
                if (isInAlbum) {
                    loadAlbum();
                } else {
                    loadImage();
                }
            }
        });

        mRxManager.on("onSearch", new Action1<String>() {
            @Override
            public void call(String text) {
                mSearchStr = text;
                loadAlbum();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInAlbum) {
            loadAlbum();
        } else {
            loadImage();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isInAlbum", isInAlbum);
    }

    public void startRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void stopRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void addTab() {
        mTabView.addTab(getString(R.string.str_image), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAlbum();
            }
        });
    }

    public void showTabs() {
        if (mTabView.isHide()) {
            mTabView.showTabs();
        }
    }

    public void showError(String error) {
        if (BuildConfig.DEBUG) {
            LogUtils.logd(error);
            LogToFileUtils.saveCrashInfoFile(error);
        }
        AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_error));
    }

    private void refreshAlbum(ArrayList<FileItem> albumFolderList) {
        mAdapter.clearData(true);
        mAdapter.setData(albumFolderList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void refreshImage(ArrayList<FileItem> albumFolderList) {
        mImageAdapter.clearData(true);
        mImageAdapter.setData(albumFolderList);
        mRecyclerView.setAdapter(mImageAdapter);
    }

    public boolean onBackPressed() {
        if (isInAlbum) {
            return false;
        } else {
            loadAlbum();
            return true;
        }
    }

    private void loadAlbum() {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(mFileListModel.getPhotoAlbum(mSearchStr, getContext().getContentResolver()));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        showTabs();
                        startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> albumFolderList) {
                        refreshAlbum(albumFolderList);
                        isInAlbum = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    private void loadImage() {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(getCurrentImageList(mCurrentAlbum));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        showTabs();
                        startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> albumFolderList) {
                        refreshImage(albumFolderList);
                        isInAlbum = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    private ArrayList<FileItem> getCurrentImageList(FileItem albumFolder) {
        ArrayList<FileItem> fileItemList;

        fileItemList = albumFolder.getImages();

        if (fileItemList == null) {
            return null;
        }

        switch (mSortType) {
            case Constants.SortType.TYPE:
                fileItemList = mFileListModel.orderByType(fileItemList);
                break;
            case Constants.SortType.TIME:
                fileItemList = mFileListModel.orderByTime(fileItemList);
                break;
            case Constants.SortType.ALPHABET:
                fileItemList = mFileListModel.orderByAlphabet(fileItemList);
                break;
            case Constants.SortType.SIZE:
                fileItemList = mFileListModel.orderBySize(fileItemList);
                break;
        }

        switch (mOrderType) {
            case Constants.OrderType.DESC:
                break;
            case Constants.OrderType.ASC:
                mFileListModel.orderByOrder(fileItemList);
                break;
        }

        return fileItemList;
    }

}
