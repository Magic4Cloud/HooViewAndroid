package com.easyvaas.elapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;


public abstract class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    protected Map<Integer, Fragment> fragments = new HashMap<>();
    protected FragmentManager mFm;

    public BaseFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFm = fm;
    }

    @Override
    public Fragment instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mFm.beginTransaction().show(fragment).commit();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = fragments.get(position);
        mFm.beginTransaction().hide(fragment).commit();
    }
}
