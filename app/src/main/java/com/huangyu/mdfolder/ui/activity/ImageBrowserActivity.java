package com.huangyu.mdfolder.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.ui.BaseActivity;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.ui.adapter.ImagePagerAdapter;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by huangyu on 2017-6-20.
 */
public class ImageBrowserActivity extends BaseActivity {

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_browser;
    }

    @Override
    protected IBaseView initAttachView() {
        return null;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ArrayList<String> imageList = getIntent().getStringArrayListExtra(getString(R.string.intent_image_list));
        int position = getIntent().getIntExtra(getString(R.string.intentimage_position), 0);
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(position);
    }

}
