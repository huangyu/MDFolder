package com.huangyu.library.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huangyu.library.app.BaseApplication;
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
    protected RxManager mRxManager = new RxManager();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
        ButterKnife.bind(this, mRootView);

        mPresenter = getT(this, 1);
        if (mPresenter != null) {
            mPresenter.mContext = getContext();
            V view = initAttachView();
            mPresenter.attachView(view);
            mPresenter.create();
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRootView != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        if (mRxManager != null) {
            mRxManager.clear();
        }
        BaseApplication.getRefWatcher().watch(this);
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
