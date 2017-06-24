package com.huangyu.mdfolder.ui.activity;

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
import com.huangyu.mdfolder.ui.adapter.VideoPagerAdapter;
import com.huangyu.mdfolder.utils.AlertUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by huangyu on 2017-6-23.
 */
public class VideoBrowserActivity extends ThematicActivity implements EasyVideoCallback {

    @Bind(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.tv_number)
    TextView mTvNumber;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private ArrayList<FileItem> mFileList;
    private FileListModel mFileListModel;
    private VideoPagerAdapter adapter;
    private int currentPosition;

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
        mFileList = (ArrayList<FileItem>) getIntent().getSerializableExtra(getString(R.string.intent_video_list));
        currentPosition = getIntent().getIntExtra(getString(R.string.intent_video_position), 0);
        adapter = new VideoPagerAdapter(this, mFileList, this);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(currentPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetLastPositionPlayer();
                currentPosition = position;
                mToolbar.setTitle(mFileList.get(position).getName());
                mTvNumber.setText(position + 1 + "/" + mFileList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mToolbar.setTitle(mFileList.get(currentPosition).getName());
        mTvNumber.setText(currentPosition + 1 + "/" + mFileList.size());

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
        View view = mViewPager.findViewWithTag("video" + currentPosition);
        if (view != null) {
            EasyVideoPlayer easyVideoPlayer = ButterKnife.findById(view, R.id.video_player);
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
                onShowFileInfo(mFileList.get(currentPosition));
                break;
            case R.id.action_delete:
                onDelete(mFileList.get(currentPosition));
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
        AlertUtils.showInfoBottomSheet(this, fileItem, null);
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
                    mFileList = mFileListModel.getVideoList("", getContentResolver());
                    AlertUtils.showSnack(getWindow().getDecorView(), getString(R.string.tips_delete_successfully));
                    adapter = new VideoPagerAdapter(VideoBrowserActivity.this, mFileList, VideoBrowserActivity.this);
                    mViewPager.setAdapter(adapter);
                    if (currentPosition >= mFileList.size() - 1) {
                        currentPosition = mFileList.size() - 1;
                    } else {
                        currentPosition++;
                    }
                    mViewPager.setCurrentItem(currentPosition);
                } else {
                    AlertUtils.showSnack(getWindow().getDecorView(), getString(R.string.tips_delete_in_error));
                }
            }
        });
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
