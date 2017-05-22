package com.huangyu.library.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huangyu.library.mvp.BasePresenter;
import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.rx.RxManager;

import butterknife.ButterKnife;

import static com.huangyu.library.util.GenericUtils.getT;

/**
 * Created by huangyu on 2017-4-10.
 */
public abstract class BaseFragment<V extends IBaseView, P extends BasePresenter<V>> extends Fragment {

    private View mRootView;
    protected P mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
        ButterKnife.bind(this, mRootView);

        // 因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        mPresenter = getT(this, 1);
        if (mPresenter != null) {
            mPresenter.mContext = getContext();
            V view = initAttachView();
            mPresenter.attachView(view);
            mPresenter.create();
        }
        initView(savedInstanceState);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        RxManager.getInstance().clear();
        super.onDestroy();
    }

    /**
     * 获取R.layout.xxx
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    /**
     * 初始化MVP的View，可返回null
     *
     * @return
     */
    protected abstract V initAttachView();

    /**
     * 初始化界面
     *
     * @param savedInstanceState
     */
    protected abstract void initView(Bundle savedInstanceState);

}
