package com.easyvaas.elapp.ui.base.mybase;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.easyvaas.elapp.view.base.ToolBarTitleView;
import com.hooview.app.R;

import butterknife.BindView;
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

    @BindView(R.id.toobar_title_view)
    ToolBarTitleView mToobarTitleView;

    protected Activity mContext;
    protected Unbinder mUnbinder;
    protected CompositeSubscription mCompositeSubscription;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        mUnbinder = ButterKnife.bind(this);
        mContext = this;
        initToolBar();
        initViewAndData();
    }

    /**
     * 初始化ToolBar 可重写修改属性
     * 传null 代表没有标题栏
     */
    protected void initToolBar() {

        if (getTitleText() != null)
            mToobarTitleView.setTitleText(getTitleText());
        else
            mToobarTitleView.setVisibility(View.GONE);

        mToobarTitleView.setTitleBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        unSubsribe();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_title_base_layout);
        LinearLayout baseLayout = (LinearLayout) findViewById(R.id.base_layout);
        View.inflate(this, layoutResID, baseLayout);
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
    protected void unSubsribe() {
        if (mCompositeSubscription != null)
            mCompositeSubscription.unsubscribe();
    }


    protected abstract int getLayout(); // 获取布局ID

    protected abstract String getTitleText(); // 获取标题title

    protected abstract void initViewAndData(); // 初始化布局和数据
}
