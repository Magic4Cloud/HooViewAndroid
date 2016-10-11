/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

import com.hooview.app.utils.Logger;

import java.util.HashMap;

public class TabManager implements TabHost.OnTabChangeListener {
    private static final String TAG = TabManager.class.getSimpleName();

    private final FragmentActivity mActivity;
    private final TabHost mTabHost;
    private final int mContainerId;
    private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
    TabInfo mLastTab;

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private android.support.v4.app.Fragment fragment;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }

    static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
        mActivity = activity;
        mTabHost = tabHost;
        mTabHost.setOnTabChangedListener(this);
        mContainerId = containerId;
    }

    public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(mActivity));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);
        Logger.d(TAG, "addTab() tabInfo: " + info + " tag: " + info.tag);

        info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        if (info.fragment != null && info.fragment.isDetached()) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.detach(info.fragment);
            ft.commitAllowingStateLoss();
        }

        mTabs.put(tag, info);
        mTabHost.addTab(tabSpec);
    }

    @Override
    public void onTabChanged(String tabId) {
        Logger.d(TAG, "onTabChanged() tabId: " + tabId);
        TabInfo newTab = mTabs.get(tabId);
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        if (mLastTab != newTab) {
            if (mLastTab != null && mLastTab.fragment != null) {
                ft.detach(mLastTab.fragment);
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(mActivity, newTab.clss.getName(), newTab.args);
                    ft.add(mContainerId, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }
        }

        mLastTab = newTab;
        ft.commitAllowingStateLoss();
        mActivity.getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * 获取Tag标签的字符串
     * @param tag
     * @return
     */
    public TabInfo getTabInfoByTag(String tag) {
        return mTabs.get(tag);
    }
}
