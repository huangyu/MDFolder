package com.huangyu.library.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.huangyu.library.app.ActivityManager;
import com.huangyu.library.mvp.BasePresenter;
import com.huangyu.library.mvp.IBaseView;
import com.huangyu.library.rx.RxManager;

import butterknife.ButterKnife;

import static com.huangyu.library.util.GenericUtils.getT;

/**
 * BaseActivity
 * Created by huangyu on 2017-4-10.
 */
public abstract class BaseActivity<V extends IBaseView, P extends BasePresenter<V>> extends AppCompatActivity {

    protected P mPresenter;
    protected RxManager mRxManager = new RxManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ActivityManager.getInstance().addActivity(this);
        ButterKnife.bind(this);

        mPresenter = getT(this, 1);
        if (mPresenter != null) {
            mPresenter.mContext = this;
            V view = initAttachView();
            mPresenter.attachView(view);
            mPresenter.create();
        }

        initView(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        if(mRxManager!= null) {
            mRxManager.clear();
        }
        ButterKnife.unbind(this);
        ActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }

    /**
     * 通过Class跳转界面
     *
     * @param cls
     **/
    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     *
     * @param cls
     * @param requestCode
     **/
    protected void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     *
     * @param cls
     * @param bundle
     **/
    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 含有Bundle通过Class跳转界面
     *
     * @param cls
     * @param bundle
     * @param requestCode
     **/
    protected void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
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
