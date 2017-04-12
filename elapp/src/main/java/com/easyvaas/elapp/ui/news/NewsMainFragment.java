package com.easyvaas.elapp.ui.news;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.ui.base.mybase.MyBaseFragment;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import butterknife.BindView;

/**
 * Date   2017/4/7
 * Editor  Misuzu
 * 资讯主页Fragment
 */

public class NewsMainFragment extends MyBaseFragment {

    @BindView(R.id.news_logo)
    ImageView mNewsLogo;
    @BindView(R.id.news_tablayout)
    SlidingTabLayout mNewsTablayout;
    @BindView(R.id.news_search)
    ImageView mNewsSearch;
    @BindView(R.id.news_tab_viewpager)
    ViewPager mNewsTabViewpager;

    private String[] titles;
    private Fragment[] mFragments;

    @Override
    protected int getLayout() {
        return R.layout.fragment_news_main_layout;
    }

    @Override
    protected void initViewAndData() {
        titles = new String[]{
                getString(R.string.news_import),
                getString(R.string.news_speed),
                getString(R.string.news_slecte),
                getString(R.string.news_specil)};
        mFragments = new Fragment[]{
                TopNewsFragment.newInstance(),
                LastestNewsListFragment.newInstance(),
                MyStockNewsListFragment.newInstance(),
                TopNewsFragment.newInstance(),
        };
        mNewsTabViewpager.setAdapter(new MainPageAdapter(getChildFragmentManager()));
        mNewsTablayout.setViewPager(mNewsTabViewpager);
        mNewsTabViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    public static NewsMainFragment newInstance() {

        return new NewsMainFragment();
    }

    private class MainPageAdapter extends FragmentPagerAdapter
    {

        private MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    /**
     * 动态改变Tab文字大小
     */
    private void updateTabStyles(int currentPosition) {
        for (int i = 0; i < titles.length; i++) {
            TextView v = mNewsTablayout.getTitleView(i);
            if (v != null) {
                v.setTextSize(TypedValue.COMPLEX_UNIT_SP,i == currentPosition ? 16: 14);
            }
        }
    }
}
