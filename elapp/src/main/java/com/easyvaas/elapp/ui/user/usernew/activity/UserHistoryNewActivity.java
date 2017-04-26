package com.easyvaas.elapp.ui.user.usernew.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserHistoryReadNewFragment;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserHistoryWatchFragment;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import butterknife.BindView;

/**
 * Date   2017/4/20
 * Editor  Misuzu
 * 用户历史记录
 */

public class UserHistoryNewActivity extends MyBaseActivity {

    @BindView(R.id.user_history_tablayout)
    SlidingTabLayout mUserHistoryTablayout;
    @BindView(R.id.user_history_viewpager)
    ViewPager mUserHistoryViewpager;
    private String[] titles;
    private Fragment[] mFragments;

    @Override
    protected int getLayout() {
        return R.layout.activity_user_history_new_layout;
    }

    @Override
    protected String getTitleText() {
        return getString(R.string.user_history);
    }

    @Override
    protected void initViewAndData() {
        titles = new String[]{
                getString(R.string.user_history_watch),
                getString(R.string.user_history_read)};
        mFragments = new Fragment[]{
                UserHistoryWatchFragment.newInstance(),
                UserHistoryReadNewFragment.newInstance()};

        mUserHistoryViewpager.setAdapter(new HistoryPageAdapter(getSupportFragmentManager()));
        mUserHistoryTablayout.setViewPager(mUserHistoryViewpager);
    }


    private class HistoryPageAdapter extends FragmentPagerAdapter {

        private HistoryPageAdapter(FragmentManager fm) {
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

}
