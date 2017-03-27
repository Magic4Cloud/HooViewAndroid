package com.easyvaas.elapp.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wanghai on 2017/3/27.
 * 懒加载的 Fragment
 */

public abstract class LazyLoadFragment extends BaseFragment {
    protected boolean isOnceLoad = false;
    protected boolean isPrepared = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isOnceLoad) {
            isOnceLoad = true;
            load();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView(inflater, container, savedInstanceState);
        isPrepared = true;
        load();
        return view;
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    private void load() {
        if (isFirstLoad()) {
            lazyLoadView();
        }
    }

    /**
     * 是否是第一次加载
     */
    public boolean isFirstLoad(){
        return isPrepared && isOnceLoad;
    }

    /** 懒加载所需要加载的数据 **/
    public abstract void lazyLoadView();
}
