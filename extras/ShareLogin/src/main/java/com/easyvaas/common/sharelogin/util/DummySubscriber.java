package com.easyvaas.common.sharelogin.util;

import rx.Subscriber;

public class DummySubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T t) {

    }
}
