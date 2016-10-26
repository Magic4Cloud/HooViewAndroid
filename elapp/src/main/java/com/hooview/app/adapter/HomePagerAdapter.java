package com.hooview.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hooview.app.fragment.HomeMainTabFragment;
import com.hooview.app.fragment.HomeMessageTabFragment;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/19
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private static final int PAGER_COUNT = 2;

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private HomeMainTabFragment mMainFragment = new HomeMainTabFragment();

    private HomeMessageTabFragment mMessageFragment = new HomeMessageTabFragment();

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return mMainFragment;
        } else {
            return mMessageFragment;
        }
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    public HomeMainTabFragment getMainFragment() {
        return mMainFragment;
    }
}
