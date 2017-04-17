package com.easyvaas.elapp.ui.base.mybase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Date   2017/3/28
 * Editor  Misuzu
 * 普通fragment基类
 */

public abstract class MyBaseFragment extends Fragment {

    protected Activity mActivity;
    protected Context mContext;
    protected Unbinder mUnbinder;
    protected CompositeSubscription mCompositeSubscription;


    @Override
    public void onAttach(Context context) {
        mActivity = (Activity) context;
        mContext = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
              return inflater.inflate(getLayout(),null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this,view);
        initViewAndData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        unSubsribe();
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
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
        mCompositeSubscription = null;
    }


    protected abstract int getLayout();
    protected abstract void initViewAndData();
}
