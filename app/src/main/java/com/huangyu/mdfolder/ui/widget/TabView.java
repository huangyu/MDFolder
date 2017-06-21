package com.huangyu.mdfolder.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.utils.SizeUtils;

/**
 * Created by huangyu on 2017-5-23.
 */
public class TabView extends HorizontalScrollView {

    private LinearLayout mLayout;
    private LayoutInflater mInflater;

    public TabView(Context context) {
        super(context);
        init();
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        this.setHorizontalScrollBarEnabled(false);
        mInflater = LayoutInflater.from(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SizeUtils.dp2px(30) + SizeUtils.sp2px(14));
        mLayout = new LinearLayout(getContext());
        mLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(mLayout, params);
    }

    public void addTab(String title, OnClickListener onClickListener) {
        View view = mInflater.inflate(R.layout.item_tab_text, mLayout, false);
        TextView textView = (TextView) view.findViewById(R.id.tv_name);
        textView.setOnClickListener(onClickListener);
        textView.setText(title);
        textView.setTag(R.id.tab_tag, mLayout.getChildCount());
        if (mLayout.getChildCount() <= 0) {
            view.findViewById(R.id.arrow).setVisibility(View.GONE);
        } else {
            TextView lastTitle = (TextView) mLayout.getChildAt(mLayout.getChildCount() - 1).findViewById(R.id.tv_name);
            lastTitle.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryTextDark));
        }
        mLayout.addView(view, mLayout.getChildCount());

        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothScrollBy(1000, 0);
            }
        }, 5);
    }

    public boolean removeTab() {
        int count = mLayout.getChildCount();
        if (count > 1) {
            mLayout.removeViewAt(count - 1);
            View lastView = mLayout.getChildAt(mLayout.getChildCount() - 1);
            TextView lastTitle = (TextView) lastView.findViewById(R.id.tv_name);
            lastTitle.setTextColor(getResources().getColor(android.R.color.white));
            return true;
        }

        if (mLayout.getChildCount() == 1) {
            View lastView = mLayout.getChildAt(mLayout.getChildCount() - 1);
            lastView.findViewById(R.id.arrow).setVisibility(View.GONE);
        }
        return false;
    }

    public void removeAllTabs() {
        int count = mLayout.getChildCount();
        if (count > 0) {
            mLayout.removeAllViews();
        }
    }

}
