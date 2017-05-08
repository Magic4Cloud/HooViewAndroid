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
        Log.d("Misuzu","error --->"+e.toString());
        Log.d("xmzd","onError --->"+e.toString());
//        Toast.makeText(EVApplication.getApp(),EVApplication.getApp().getString(R.string.network_error),Toast.LENGTH_SHORT).show();
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
            Log.e("xmzd","OnFailue --->"+response.getReterr().toString());
        }
    }

    public abstract void OnSuccess(T t);
    public abstract void OnFailue(String msg);
}
