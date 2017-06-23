package com.huangyu.mdfolder.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.ui.BaseActivity;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.ui.adapter.VideoPagerAdapter;
import com.huangyu.mdfolder.utils.AlertUtils;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by huangyu on 2017-6-23.
 */
public class VideoBrowserActivity extends BaseActivity implements EasyVideoCallback {

    @Bind(R.id.tv_number)
    TextView tvNumber;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser;
    }

    @Override
    protected IBaseView initAttachView() {
        return null;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        final ArrayList<String> videoList = getIntent().getStringArrayListExtra(getString(R.string.intent_video_list));
        int position = getIntent().getIntExtra(getString(R.string.intent_video_position), 0);
        VideoPagerAdapter adapter = new VideoPagerAdapter(this, videoList, this);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvNumber.setText(position + 1 + "/" + videoList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvNumber.setText(position + 1 + "/" + videoList.size());
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
