package com.huangyu.mdfolder.ui.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.mvp.model.FileModel;
import com.huangyu.mdfolder.utils.AlertUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by huangyu on 2017-6-20.
 */
public class ImageBrowserActivity extends ThematicActivity {

    @Bind(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.tv_number)
    TextView mTvNumber;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private ProgressDialog mProgressDialog;
    private ImagePagerAdapter mAdapter;

    private ArrayList<FileItem> mFileList;
    private FileListModel mFileListModel;
    private FileModel mFileModel;
    private int mCurrentPosition;
    private int mEnterPosition;
    private int mSortType;
    private int mOrderType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser;
    }

    @Override
    protected IBaseView initAttachView() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initView(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        mFileListModel = new FileListModel();
        mFileModel = new FileModel();
        mFileList = (ArrayList<FileItem>) getIntent().getSerializableExtra(getString(R.string.intent_image_list));
        mCurrentPosition = getIntent().getIntExtra(getString(R.string.intent_image_position), 0);
        mEnterPosition = getIntent().getIntExtra(getString(R.string.intent_image_position), 0);
        mSortType = getIntent().getIntExtra(getString(R.string.intent_image_sort_type), 0);
        mOrderType = getIntent().getIntExtra(getString(R.string.intent_image_order_type), 0);
        mAdapter = new ImagePagerAdapter(this, mFileList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
//        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                mToolbar.setTitle(mFileList.get(position).getName());
                mTvNumber.setText(position + 1 + "/" + mFileList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mToolbar.setTitle(mFileList.get(mCurrentPosition).getName());
        mTvNumber.setText(mCurrentPosition + 1 + "/" + mFileList.size());

        setSupportActionBar(mToolbar);

        if (isLightMode()) {
            mAppBarLayout.getContext().setTheme(R.style.AppTheme_AppBarOverlay);
            mToolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
            mTvNumber.setTextColor(getResources().getColor(R.color.colorPrimaryText));
        } else {
            mAppBarLayout.getContext().setTheme(R.style.AppTheme_AppBarOverlay_Dark);
            mToolbar.setPopupTheme(R.style.AppTheme_PopupOverlay_Dark);
            mTvNumber.setTextColor(getResources().getColor(R.color.colorPrimaryTextWhite));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_control_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                onShowFileInfo(mFileList.get(mCurrentPosition));
                break;
            case R.id.action_delete:
                onDelete(mFileList.get(mCurrentPosition));
                break;
            case R.id.action_share:
                List<File> files = new ArrayList<>();
                File file = new File(mFileList.get(mCurrentPosition).getPath());
                files.add(file);
                mFileModel.shareFile(this, files);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示文件详情
     *
     * @param fileItem 文件
     */
    public void onShowFileInfo(FileItem fileItem) {
        AlertUtils.showFileInfoBottomSheet(this, fileItem, null);
    }

    /**
     * 删除文件
     *
     * @param fileItem 文件
     */
    public void onDelete(final FileItem fileItem) {
        AlertUtils.showNormalAlert(this, getString(R.string.tips_delete_file), getString(R.string.act_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (FileUtils.deleteFile(fileItem.getPath())) {
                    if (mFileList.size() <= 1) {
                        onBackPressed();
                        return;
                    }

                    Subscription subscription = Observable.create(new Observable.OnSubscribe<ArrayList<FileItem>>() {
                        @Override
                        public void call(Subscriber<? super ArrayList<FileItem>> subscriber) {
                            Iterator<FileItem> it = mFileList.iterator();
                            while (it.hasNext()) {
                                FileItem file = it.next();
                                if (file.getPath().equals(fileItem.getPath())) {
                                    it.remove();
                                    break;
                                }
                            }
                            subscriber.onNext(mFileList);
                            subscriber.onCompleted();
                        }
                    })
                            .subscribeOn(Schedulers.io())
                            .map(new Func1<ArrayList<FileItem>, ArrayList<FileItem>>() {
                                @Override
                                public ArrayList<FileItem> call(ArrayList<FileItem> fileList) {
                                    switch (mSortType) {
                                        case Constants.SortType.TYPE:
                                            fileList = mFileListModel.orderByType(fileList);
                                            break;
                                        case Constants.SortType.TIME:
                                            fileList = mFileListModel.orderByTime(fileList);
                                            break;
                                        case Constants.SortType.ALPHABET:
                                            fileList = mFileListModel.orderByAlphabet(fileList);
                                            break;
                                        case Constants.SortType.SIZE:
                                            fileList = mFileListModel.orderBySize(fileList);
                                            break;
                                        case Constants.SortType.REMARK:
                                            fileList = mFileListModel.orderByRemark(fileList);
                                            break;
                                    }

                                    switch (mOrderType) {
                                        case Constants.OrderType.DESC:
                                            break;
                                        case Constants.OrderType.ASC:
                                            mFileListModel.orderByOrder(fileList);
                                            break;
                                    }
                                    return fileList;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<ArrayList<FileItem>>() {
                                @Override
                                public void onStart() {
                                    AlertUtils.showToast(ImageBrowserActivity.this, getString(R.string.tips_delete_successfully));
                                    showProgressDialog(getString(R.string.tips_loading));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    AlertUtils.showToast(ImageBrowserActivity.this, getString(R.string.tips_error));
                                    onCompleted();
                                }

                                @Override
                                public void onNext(ArrayList<FileItem> fileList) {
                                    mFileList = fileList;
                                    mAdapter = new ImagePagerAdapter(ImageBrowserActivity.this, mFileList);
                                    mViewPager.setAdapter(mAdapter);
                                }

                                @Override
                                public void onCompleted() {
                                    if (mCurrentPosition >= mFileList.size() - 1) {
                                        mCurrentPosition = mFileList.size() - 1;
                                    }
                                    mViewPager.setCurrentItem(mCurrentPosition);
                                    mTvNumber.setText((mCurrentPosition + 1) + "/" + mFileList.size());
                                    mRxManager.post("onDeleteAndRefresh", "");
                                    hideProgressDialog();
                                }
                            });
                    mRxManager.add(subscription);
                } else {
                    AlertUtils.showToast(ImageBrowserActivity.this, getString(R.string.tips_delete_in_error));
                }
            }
        });
    }

    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.tips_alert));
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();
        super.onDestroy();
    }

    @Override
    public void finishAfterTransition() {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.intent_exit_position), mCurrentPosition);
        setResult(RESULT_OK, intent);
        if (mEnterPosition != mCurrentPosition) {
            setCallback();
        }
        super.finishAfterTransition();
    }

    @TargetApi(21)
    private void setCallback() {
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                names.clear();
                sharedElements.clear();
                View view = mViewPager.findViewWithTag(getString(R.string.transition_image_name) + mCurrentPosition);
                names.add(view.getTransitionName());
                sharedElements.put(view.getTransitionName(), view);
            }
        });
    }

    @TargetApi(21)
    public void setStartPostTransition(final View sharedView) {
        sharedView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedView.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return false;
                    }
                });
    }

    /**
     * Created by huangyu on 2017-6-20.
     */
    public class ImagePagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<FileItem> mImageList;

        public ImagePagerAdapter(Context context, List<FileItem> imageList) {
            this.mContext = context;
            this.mImageList = imageList;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mImageList == null ? 0 : mImageList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, final int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_image_pager, viewGroup, false);
            PhotoView photoView = ButterKnife.findById(view, R.id.photo_view);
            String suffix = FileUtils.getSuffix(mImageList.get(position).getName());
            if (suffix.equals(".gif")) {
                Glide.with(mContext).load(mImageList.get(position).getPath()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(photoView);
            } else {
                Glide.with(mContext).load(mImageList.get(position).getPath()).into(photoView);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String name = mContext.getString(R.string.transition_image_name) + position;
                photoView.setTransitionName(name);
                photoView.setTag(name);
                if (position == mEnterPosition) {
                    setStartPostTransition(photoView);
                }
            }

            viewGroup.addView(photoView);
            return photoView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

    }

}
