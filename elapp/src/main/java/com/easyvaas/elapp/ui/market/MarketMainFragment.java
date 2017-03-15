package com.easyvaas.elapp.ui.market;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.BaseFragmentPagerAdapter;
import com.easyvaas.elapp.service.AppService;
import com.easyvaas.elapp.ui.base.BaseTabViewPagerFragment;

public class MarketMainFragment extends BaseTabViewPagerFragment {

    @Override
    public void initView(View view) {
        mViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        AppService.startRefreshStock(getContext().getApplicationContext());
    }

    public class MyAdapter extends BaseFragmentPagerAdapter {
        private String[] mTabsTitle;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mTabsTitle = getResources().getStringArray(R.array.market_tabs);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            if (position == 3) {
                fragments.put(position, fragment = new GlobalMarketFragment());
            } else if (position == 0) {
                fragments.put(position, fragment = new ChineseMarketFragment());
            } else if (position == 1) {
                fragments.put(position, fragment = new HKMarketFragment());

            } else if (position == 2) {
                fragments.put(position, fragment = new USAMarketFragment());
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mTabsTitle.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }

}
