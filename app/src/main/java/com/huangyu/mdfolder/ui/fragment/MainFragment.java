package com.huangyu.mdfolder.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huangyu.library.ui.BaseFragment;
import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.mvp.presenter.MainPresenter;
import com.huangyu.mdfolder.mvp.view.IMainView;
import com.huangyu.mdfolder.ui.adapter.FileListAdapter;
import com.huangyu.mdfolder.ui.widget.TabView;

import java.io.File;

import butterknife.Bind;

/**
 * Created by huangyu on 2017-5-23.
 */
public class MainFragment extends BaseFragment<IMainView, MainPresenter> implements IMainView {

    @Bind(R.id.cl_main)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.tab_view)
    TabView mTabView;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private FileListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected IMainView initAttachView() {
        return this;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mAdapter = new FileListAdapter(getContext());
        mAdapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                File file = mAdapter.getItem(position);
                if (file.isDirectory()) {
                    mPresenter.enterFolder(file);
                } else {
                    mPresenter.openFile(getContext(), file);
                }
            }
        });
        mAdapter.setOnItemLongClick(new CommonRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                // TODO
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });

        mAdapter.setData(mPresenter.getRootFileList());
    }

    @Override
    public void addTab(String path) {
        mTabView.addTab(path, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag(R.id.tag);
                if (tag != null && tag instanceof Integer) {
                    int index = (Integer) tag;
                    mPresenter.enterCertainFolder(index);
                }
            }
        });
    }

    @Override
    public boolean removeTab() {
        return mTabView.removeTab();
    }

    @Override
    public void refreshData() {
        mAdapter.clearData();
        mAdapter.setData(mPresenter.getCurrentFileList());
    }

    public boolean onBackPressed() {
        return mPresenter.backFolder();
    }

}
