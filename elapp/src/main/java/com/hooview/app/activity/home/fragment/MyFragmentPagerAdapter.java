/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.home.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentList;

    public MyFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragmentList = new ArrayList<Fragment>();
    }

    public void addFragment(int pos, Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public Fragment getItem(int pos) {
        return mFragmentList.get(pos);
    }
}
