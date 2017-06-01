package com.huangyu.library.rx;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * RxJava管理类
 * Created by huangyu on 2017-4-10.
 */
public class RxManager {

    private RxBus mRxBus = RxBus.getInstance();

    private Map<String, Observable<?>> mObservables = new HashMap<>();
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    /**
     * RxBus注入监听
     *
     * @param eventName
     * @param action1
     */
    public <T> void on(String eventName, Action1<T> action1) {
        Observable<T> mObservable = mRxBus.register(eventName);
        mObservables.put(eventName, mObservable);
        /*订阅管理*/
        mCompositeSubscription.add(mObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }));
    }

    /**
     * RxBus sticky注入监听
     * content
     */
    public <T> void onSticky(String eventName, Action1<T> action1) {
        Observable<T> mObservable = mRxBus.registerSticky(eventName);
        mObservables.put(eventName, mObservable);
        /*订阅管理*/
        mCompositeSubscription.add(mObservable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(action1, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }));
    }

    /**
     * 单纯的Observables 和 Subscribers管理
     *
     * @param m
     */
    public void add(Subscription m) {
        mCompositeSubscription.add(m);
    }

    /**
     * 单个presenter生命周期结束，取消订阅和所有Rxbus观察
     */
    public void clear() {
        mCompositeSubscription.unsubscribe();
        for (Map.Entry<String, Observable<?>> entry : mObservables.entrySet()) {
            mRxBus.unregister(entry.getKey(), entry.getValue());
            mRxBus.removeStickyEvent(entry.getKey());
        }
        mObservables.clear();
    }

    /**
     * 发送Rxbus
     */
    public void post(Object tag, Object content) {
        mRxBus.post(tag, content);
    }

    /**
     * 发送Rxbus StickKey
     */
    public void postStick(Object tag, Object content) {
        if (content == null) {
            throw new IllegalArgumentException("postStickKey的content不允许为null");
        } else {
            mRxBus.postSticky(tag, content);
        }
    }

}
