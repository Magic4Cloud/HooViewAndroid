package com.easyvaas.elapp.net.mynet;

import android.util.Log;

import rx.Subscriber;

/**
 * Date   2017/4/6
 * Editor  Misuzu
 */

public abstract class NetSubscribe<T> extends Subscriber<NetResponse<T>> {


    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        OnFailue(e.toString());
    }

    @Override
    public void onNext(NetResponse<T> response) {
        if (response.getRetval().equals("ok"))
        {
            OnSuccess(response.getRetinfo());
            Log.d("Misuzu","return --->"+response.getRetinfo().toString());
        }else
        {
            OnFailue(response.getReterr());
        }
    }

    public abstract void OnSuccess(T t);
    public abstract void OnFailue(String msg);
}
