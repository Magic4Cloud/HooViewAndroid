package com.easyvaas.elapp.ui.market;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.easyvaas.elapp.adapter.BaseFragmentPagerAdapter;
import com.easyvaas.elapp.ui.base.BaseTabViewPagerFragment;
import com.hooview.app.R;

/**
 * Created by guojun on 2017/1/2 14:31.
 */

public class MySelectedStockMainFragment extends BaseTabViewPagerFragment {

    @Override
    public void initView(View view) {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(new MyAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.my_stock_tab)));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class MyAdapter extends BaseFragmentPagerAdapter {
        private String[] tabTitles;

        public MyAdapter(FragmentManager fm, String[] tabTitles) {
            super(fm);
            this.tabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
//            fragments.put(position, fragment = MySelectedStockListFragment.newInstance(position + 1 + ""));
            fragments.put(position, fragment = MySelectedStockAllFragment.newInstance(position));

            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitles == null ? 0 : tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
