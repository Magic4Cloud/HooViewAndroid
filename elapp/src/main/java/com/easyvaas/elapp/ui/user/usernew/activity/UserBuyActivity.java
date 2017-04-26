package com.easyvaas.elapp.ui.user.usernew.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.widget.TextView;

import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserBuyCheatsFragment;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserBuyLivingFragment;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserBuyVideoFragment;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import butterknife.BindView;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * 个人中心---我的发布
 */

public class UserBuyActivity extends MyBaseActivity {

    @BindView(R.id.publish_tablayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.publish_tab_viewpager)
    ViewPager mTabViewpager;

    private String[] mTitles;
    private Fragment[] mFragments;

    @Override
    protected int getLayout() {
        return R.layout.activity_user_buy;
    }

    @Override
    protected String getTitleText() {
        return "我的购买";
    }

    @Override
    protected void initViewAndData() {
        mTitles = new String[]{"视频直播", "精品视频", "已买秘籍"};
        mFragments = new Fragment[]{
                UserBuyLivingFragment.newInstance(),
                UserBuyVideoFragment.newInstance(),
                UserBuyCheatsFragment.newInstance()
        };
        mTabViewpager.setAdapter(new PublishPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setViewPager(mTabViewpager);
        mTabViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class PublishPagerAdapter extends FragmentPagerAdapter {

        public PublishPagerAdapter(FragmentManager fm) {
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
            TextView v = mTabLayout.getTitleView(i);
            if (v != null) {
                v.setTextSize(TypedValue.COMPLEX_UNIT_SP, i == currentPosition ? 16 : 14);
            }
        }
    }
}
