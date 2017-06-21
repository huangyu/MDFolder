package com.huangyu.mdfolder.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

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

    @Bind(R.id.tv_number)
    TextView tvNumber;

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
        final ArrayList<String> imageList = getIntent().getStringArrayListExtra(getString(R.string.intent_image_list));
        int position = getIntent().getIntExtra(getString(R.string.intentimage_position), 0);
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvNumber.setText(position + 1 + "/" + imageList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvNumber.setText(position + 1 + "/" + imageList.size());
    }

}
