package com.easyvaas.elapp.ui.user.newuser.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.user.newuser.fragment.UserPublishCheatsFragment;
import com.easyvaas.elapp.ui.user.newuser.fragment.UserPublishLivingFragment;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import butterknife.BindView;

/**
 * Date    2017/4/21
 * Author  xiaomao
 * 个人中心---我的余额
 */
public class UserBalanceActivity extends MyBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.balance_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.balance_money)
    TextView mBalanceTv;
    @BindView(R.id.balance_tab_layout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.balance_tab_viewpager)
    ViewPager mTabViewpager;

    private String[] mTitles;
    private Fragment[] mFragments;

    @Override
    protected int getLayout() {
        return R.layout.activity_user_balance;
    }

    @Override
    protected String getTitleText() {
        return "我的余额";
    }

    @Override
    protected void initViewAndData() {
        mToobarTitleView.setTitleTextRight("充值", R.color.tab_text_color_selected, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mTitles = new String[]{"消费", "充值"};
        mFragments = new Fragment[]{
                UserPublishLivingFragment.newInstance(),
                UserPublishCheatsFragment.newInstance()
        };
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.base_purplish));
        mTabViewpager.setAdapter(new BalancePagerAdapter(getSupportFragmentManager()));
        mTabLayout.setViewPager(mTabViewpager);
        updateTabStyles(0);
        mTabViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        onRefresh();
    }

    @Override
    public void onRefresh() {

    }

    private class BalancePagerAdapter extends FragmentPagerAdapter {

        public BalancePagerAdapter(FragmentManager fm) {
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
