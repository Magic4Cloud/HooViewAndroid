package com.easyvaas.elapp.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyvaas.elapp.base.BaseFragment;

/**
 * Date    2017/4/12
 * Author  xiaomao
 * 懒加载
 */
public abstract class BaseLazyFragment extends BaseFragment {

    private boolean mIsPrepared = false;
    private boolean mIsVisible = false;
    protected boolean mIsLoad = false;
    protected View mRootView = null;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mRootView == null) {
            return;
        }
        if (isVisibleToUser && mIsPrepared) {
            onVisibleChanged(true);
            mIsVisible = true;
            return;
        }
        if (mIsVisible && mIsPrepared) {
            onVisibleChanged(false);
            mIsVisible = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsPrepared = false;
        mIsVisible = false;
        mIsLoad = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayout(), container, false);
        }
        initView();
        mIsPrepared = true;
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsPrepared = false;
        mIsLoad = false;
    }

    /**
     * 返回布局id
     *
     * @return int
     */
    protected abstract int getLayout();

    /**
     * 初始化view
     */
    protected abstract void initView();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsPrepared && getUserVisibleHint()) {
            onVisibleChanged(true);
            mIsVisible = true;
        }
    }

    /**
     * Fragment可见且View已经初始化完成
     *
     * @param isVisible boolean
     */
    protected void onVisibleChanged(boolean isVisible) {
        // subclass does something
    }

}
