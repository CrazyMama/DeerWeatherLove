package com.lu.deerweatherlove.common.utils;

import rx.Subscriber;

/**
 * Created by L on 16/12/1.
 *
 * Info: 简化 Rx 的模式,用在只关乎 onNext() 逻辑
 */
public abstract class SimpleSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        ULog.e(e.toString());
    }
}

