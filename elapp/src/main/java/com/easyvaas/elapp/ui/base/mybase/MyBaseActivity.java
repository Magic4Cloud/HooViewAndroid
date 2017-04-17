package com.easyvaas.elapp.ui.base.mybase;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Date   2017/3/28
 * Editor  Misuzu
 * 普通Activity基类
 */

public abstract class MyBaseActivity extends AppCompatActivity {

    protected Activity mContext;
    protected Unbinder mUnbinder;
    protected CompositeSubscription mCompositeSubscription;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        mUnbinder = ButterKnife.bind(this);
        mContext = this;
        initViewAndData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        unSubsribe();
    }

    /**
     * 添加订阅到集合
     */
    protected void addSubscribe(Subscription subscription) {

        if (mCompositeSubscription == null)
            mCompositeSubscription = new CompositeSubscription();
        mCompositeSubscription.add(subscription);
    }

    /**
     * 解除所有订阅
     */
    protected void unSubsribe()
    {
        if (mCompositeSubscription != null)
            mCompositeSubscription.unsubscribe();
    }


    protected abstract int getLayout();
    protected abstract void initViewAndData();
}
