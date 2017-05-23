package com.huangyu.mdfolder.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.ui.BaseFragment;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.mvp.presenter.MainPresenter;
import com.huangyu.mdfolder.ui.adapter.FileListAdapter;

import butterknife.Bind;

/**
 * Created by huangyu on 2017-5-23.
 */
public class MainFragment extends BaseFragment<IBaseView, MainPresenter> implements IBaseView {

    @Bind(R.id.cl_main)
    ConstraintLayout mConstraintLayout;

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
    protected IBaseView initAttachView() {
        return this;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mAdapter = new FileListAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }

}
