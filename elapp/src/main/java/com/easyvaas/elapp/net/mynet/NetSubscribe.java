package com.easyvaas.elapp.net.mynet;

import android.util.Log;
import android.widget.Toast;

import com.easyvaas.elapp.app.EVApplication;
import com.hooview.app.R;

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
        Log.e("Misuzu","error --->"+e.toString());
        Toast.makeText(EVApplication.getApp(),EVApplication.getApp().getString(R.string.network_error),Toast.LENGTH_SHORT).show();
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
