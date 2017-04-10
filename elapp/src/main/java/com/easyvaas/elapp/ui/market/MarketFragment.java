package com.easyvaas.elapp.ui.market;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.ui.base.mybase.MyBaseFragment;
import com.easyvaas.elapp.ui.search.GlobalSearchActivity;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Date    2017/4/10
 * Author  xiaomao
 * 行情fragment主界面
 */
public class MarketFragment extends MyBaseFragment {

    @BindView(R.id.market_logo)
    ImageView mMarketLogo;
    @BindView(R.id.market_tablayout)
    SlidingTabLayout mMarketTabLayout;
    @BindView(R.id.market_search)
    ImageView mMarketSearch;
    @BindView(R.id.market_tab_viewpager)
    ViewPager mMarketTabViewpager;

    private String[] mTitles;
    private Fragment[] mFragments;

    @Override
    protected int getLayout() {
        return R.layout.fragment_market_main_layout;
    }

    @Override
    protected void initViewAndData() {
        mTitles = new String[]{
                getString(R.string.market_stock),
                getString(R.string.market_chinese),
                getString(R.string.market_hk),
                getString(R.string.market_global)};
        mFragments = new Fragment[] {
                MarketOptionalFragment.newInstance(),
                ChineseMarketFragment.newInstance(),
                HKMarketFragment.newInstance(),
                GlobalMarketFragment.newInstance()
        };
        mMarketTabViewpager.setAdapter(new MarketPagerAdapter(getChildFragmentManager()));
        mMarketTabLayout.setViewPager(mMarketTabViewpager);
        mMarketTabViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateTabStyles(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static MarketFragment newInstance() {
        return new MarketFragment();
    }

    @OnClick(R.id.market_search)
    public void onSearch() {
        GlobalSearchActivity.start(getContext());
    }

    private class MarketPagerAdapter extends FragmentPagerAdapter {

        public MarketPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    /**
     * 动态改变Tab文字大小
     */
    private void updateTabStyles(int currentPosition) {
        for (int i = 0; i < mTitles.length; i++) {
            TextView v = mMarketTabLayout.getTitleView(i);
            if (v != null) {
                v.setTextSize(TypedValue.COMPLEX_UNIT_SP, i == currentPosition ? 16 : 14);
            }
        }
    }

}
