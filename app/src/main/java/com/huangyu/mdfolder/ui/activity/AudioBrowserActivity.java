package com.huangyu.mdfolder.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.ui.adapter.AudioPagerAdapter;
import com.huangyu.mdfolder.utils.AlertUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huangyu on 2017-6-23.
 */
public class AudioBrowserActivity extends ThematicActivity implements EasyVideoCallback {

    @Bind(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.tv_number)
    TextView mTvNumber;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private ProgressDialog mProgressDialog;
    private AudioPagerAdapter mAdapter;

    private ArrayList<FileItem> mFileList;
    private FileListModel mFileListModel;
    private int mCurrentPosition;

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
        mFileListModel = new FileListModel();
        mFileList = (ArrayList<FileItem>) getIntent().getSerializableExtra(getString(R.string.intent_audio_list));
        mCurrentPosition = getIntent().getIntExtra(getString(R.string.intent_audio_position), 0);
        mAdapter = new AudioPagerAdapter(this, mFileList, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetLastPositionPlayer();
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
        } else {
            mAppBarLayout.getContext().setTheme(R.style.AppTheme_AppBarOverlay_Dark);
            mToolbar.setPopupTheme(R.style.AppTheme_PopupOverlay_Dark);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void resetLastPositionPlayer() {
        View view = mViewPager.findViewWithTag("audio" + mCurrentPosition);
        if (view != null) {
            EasyVideoPlayer easyVideoPlayer = ButterKnife.findById(view, R.id.audio_player);
            easyVideoPlayer.pause();
            easyVideoPlayer.seekTo(0);
            SeekBar seekBar = ButterKnife.findById(view, com.afollestad.easyvideoplayer.R.id.seeker);
            seekBar.setProgress(0);
            TextView tvLabelPosition = ButterKnife.findById(view, com.afollestad.easyvideoplayer.R.id.position);
            tvLabelPosition.setText("00:00");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_control_type, menu);
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
                        finish();
                        return;
                    }

                    Subscription subscription = Observable.just(mFileListModel.getAudioList("", getContentResolver()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<ArrayList<FileItem>>() {
                                @Override
                                public void onStart() {
                                    AlertUtils.showSnack(getWindow().getDecorView(), getString(R.string.tips_delete_successfully));
                                    showProgressDialog(getString(R.string.tips_loading));
                                }

                                @Override
                                public void onCompleted() {
                                    if (mCurrentPosition >= mFileList.size() - 1) {
                                        mCurrentPosition = mFileList.size() - 1;
                                    } else {
                                        mCurrentPosition++;
                                    }
                                    mTvNumber.setText(mCurrentPosition + 1 + "/" + mFileList.size());
                                    mViewPager.setCurrentItem(mCurrentPosition);
                                    mRxManager.post("onDeleteAndRefresh", "");
                                    hideProgressDialog();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    AlertUtils.showSnack(getWindow().getDecorView(), getString(R.string.tips_error));
                                    onCompleted();
                                }

                                @Override
                                public void onNext(ArrayList<FileItem> fileItemList) {
                                    mFileList = fileItemList;
                                    mAdapter = new AudioPagerAdapter(AudioBrowserActivity.this, fileItemList, AudioBrowserActivity.this);
                                    mViewPager.setAdapter(mAdapter);
                                }
                            });
                    mRxManager.add(subscription);
                } else {
                    AlertUtils.showSnack(getWindow().getDecorView(), getString(R.string.tips_delete_in_error));
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
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        AlertUtils.showSnack(mViewPager, getString(R.string.tips_not_support));
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }

}
