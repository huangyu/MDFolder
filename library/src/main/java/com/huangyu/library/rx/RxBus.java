package com.huangyu.library.rx;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Rx EventBus管理类
 * Created by huangyu on 2017-4-10.
 */
public class RxBus {

    private static volatile RxBus INSTANCE;
    private final Map<Object, Object> mStickyEventMap;
    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();

    public static RxBus getInstance() {
        if (INSTANCE == null) {
            synchronized (RxBus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RxBus();
                }
            }
        }
        return INSTANCE;
    }

    private RxBus() {
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    /**
     * 订阅事件源
     *
     * @param mObservable
     * @param mAction1
     * @returno
     */
    public RxBus onEvent(Observable<?> mObservable, Action1<Object> mAction1) {
        mObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(mAction1, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        return getInstance();
    }

    /**
     * 注册事件源
     *
     * @param tag
     * @return
     */
    public <T> Observable<T> register(@NonNull Object tag) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(tag, subjectList);
        }
        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        return subject;
    }

    public void unregister(@NonNull Object tag) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            subjectMapper.remove(tag);
        }
    }

    /**
     * 取消监听
     *
     * @param tag
     * @param observable
     * @return
     */
    public RxBus unregister(@NonNull Object tag,
                            @NonNull Observable<?> observable) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            subjects.remove(observable);
            if (isEmpty(subjects)) {
                subjectMapper.remove(tag);
            }
        }
        return getInstance();
    }

    public void post(@NonNull Object content) {
        post(content.getClass().getName(), content);
    }

    /**
     * 触发事件
     *
     * @param content
     */
    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (!isEmpty(subjectList)) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
    }

    public static boolean isEmpty(Collection<Subject> collection) {
        return null == collection || collection.isEmpty();
    }


    /***************************Stciky 相关******************************/

    /**
     * 发送一个新Sticky事件
     */
    public void postSticky(@NonNull Object tag, @NonNull Object content) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(tag, content);
        }
        post(tag, content);
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Observable<T> registerSticky(final Object tag) {
        synchronized (mStickyEventMap) {
            Observable<T> observable = register(tag);
            final Object event = mStickyEventMap.get(tag);
            if (event != null) {
                return observable.mergeWith(observable.create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        subscriber.onNext((T) event);
                    }
                }));
            } else {
                return observable;
            }
        }
    }


    /**
     * 移除指定eventType的Sticky事件
     */
    public void removeStickyEvent(final Object tag) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.remove(tag);
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }

}
