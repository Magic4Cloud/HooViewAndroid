/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;

import com.easyvaas.common.viewpageindicator.TitlePageIndicator;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.R;
import com.easyvaas.elapp.activity.home.HomeTopicListActivity;
import com.easyvaas.elapp.activity.user.SearchListActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.base.BaseFragment;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.UserUtil;

public class TabBaseMainFragment extends BaseFragment {
    protected static final int PAGE_FIRST_INDEX = 0;
    protected static final int PAGE_SECOND_INDEX = 1;
    protected static final int PAGE_THIRD_INDEX = 2;

    private static final int SHOW_TAG_HIDE_TITLE = 2;
    private static final int SHOW_TAG_SHOW_TITLE = 3;

    protected MyFragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private View mLastSelectedTitleView;
    private View mHomePageTitleBkgRl;
    private TextView mTabTitleTv1;
    private TextView mTabTitleTv2;
    private TextView mTabTitleTv3;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.title_first_tv:
                    mViewPager.setCurrentItem(PAGE_FIRST_INDEX);
                    break;
                case R.id.title_second_tv:
                    if (TabBaseMainFragment.this instanceof TabTimelineMainFragment
                            && mViewPager.getCurrentItem() == PAGE_SECOND_INDEX) {
                        getActivity().startActivity(new Intent(getActivity(), HomeTopicListActivity.class));
                    } else {
                        mViewPager.setCurrentItem(PAGE_SECOND_INDEX);
                    }
                    break;
                case R.id.title_third_tv:
                    mViewPager.setCurrentItem(PAGE_THIRD_INDEX);
                    break;
                case R.id.tab_bar_search_btn:
                    startActivity(new Intent(getActivity(), SearchListActivity.class));
                    break;
                case R.id.tab_bar_mine_btn:
                    getActivity().sendBroadcast(new Intent(Constants.ACTION_GO_HOME_MINE));
                    break;
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.ACTION_HIDE_HOME_TITLE_BAR.equals(action)) {
                toggleTitleView(SHOW_TAG_HIDE_TITLE);
            } else if (Constants.ACTION_SHOW_HOME_TITLE_BAR.equals(action)) {
                toggleTitleView(SHOW_TAG_SHOW_TITLE);
            } else if (Constants.ACTION_CHANGE_HOME_TOPIC.equals(action)) {
                String topic = Preferences.getInstance(context)
                        .getString(Preferences.KEY_HOME_CURRENT_TOPIC_NAME);
                if (!TextUtils.isEmpty(topic)) {
                    updateTopicTitle(topic);
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View personView = inflater.inflate(R.layout.fragment_base_main, container, false);

        mTabTitleTv1 = (TextView) personView.findViewById(R.id.title_first_tv);
        mTabTitleTv2 = (TextView) personView.findViewById(R.id.title_second_tv);
        mTabTitleTv3 = (TextView) personView.findViewById(R.id.title_third_tv);


        mTabTitleTv1.setOnClickListener(mOnClickListener);
        mTabTitleTv2.setOnClickListener(mOnClickListener);
        mTabTitleTv3.setOnClickListener(mOnClickListener);
        personView.findViewById(R.id.tab_bar_search_btn).setOnClickListener(mOnClickListener);
        MyUserPhoto myUserPhoto = (MyUserPhoto) personView.findViewById(R.id.tab_bar_mine_btn);
        myUserPhoto.setOnClickListener(mOnClickListener);
        UserUtil.showUserPhoto(getActivity(), EVApplication.getUser().getLogourl(), myUserPhoto);
        mHomePageTitleBkgRl = personView.findViewById(R.id.home_page_title_bkg_rl);
        mHomePageTitleBkgRl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        mLastSelectedTitleView = mTabTitleTv1;

        mPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        TitlePageIndicator indicator = (TitlePageIndicator) personView.findViewById(R.id.person_indicator);
        mViewPager = (ViewPager) personView.findViewById(R.id.person_pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mPagerAdapter);
        indicator.setViewPager(mViewPager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateTitleState(position);
                getContext().sendBroadcast(new Intent(Constants.ACTION_SHOW_HOME_TITLE_BAR));

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_HIDE_HOME_TITLE_BAR);
        intentFilter.addAction(Constants.ACTION_SHOW_HOME_TITLE_BAR);
        intentFilter.addAction(Constants.ACTION_CHANGE_HOME_TOPIC);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);

        return personView;
    }

    protected void updateTabTitle(int tabTitleResId1, int tabTitleResId2, int tabTitleResId3) {
        mTabTitleTv1.setText(tabTitleResId1);
        mTabTitleTv2.setText(tabTitleResId2);
        mTabTitleTv3.setText(tabTitleResId3);
    }

    protected void updateTopicTitle(String topicTitle) {
        mTabTitleTv2.setText(topicTitle);
    }

    protected void addTabs(Fragment tab1, Fragment tab2, Fragment tab3) {
        mPagerAdapter.addFragment(PAGE_FIRST_INDEX, tab1);
        mPagerAdapter.addFragment(PAGE_SECOND_INDEX, tab2);
        mPagerAdapter.addFragment(PAGE_THIRD_INDEX, tab3);
        mPagerAdapter.notifyDataSetChanged();
    }

    protected void setCurrentTab(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTitleState(mViewPager.getCurrentItem());
    }

    @Override
    public void onDestroyView() {
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
        super.onDestroyView();
    }

    protected void updateTitleState(int position) {

        mLastSelectedTitleView.setSelected(false);
        switch (position) {
            case PAGE_FIRST_INDEX:
                mLastSelectedTitleView = mTabTitleTv1;
                break;
            case PAGE_SECOND_INDEX:
                mLastSelectedTitleView = mTabTitleTv2;
                break;
            case PAGE_THIRD_INDEX:
                mLastSelectedTitleView = mTabTitleTv3;
                break;
        }
        mLastSelectedTitleView.setSelected(true);
    }

    private ObjectAnimator mTitleAnimator;

    private void toggleTitleView(int showTag) {
        int duration = 200;
        float titleBarHeight = getResources().getDimension(R.dimen.action_bar_height);
        float titleViewY = mHomePageTitleBkgRl.getTranslationY();
        if (mTitleAnimator != null && mTitleAnimator.isRunning()) {
            mTitleAnimator.cancel();
        }
        switch (showTag) {
            case SHOW_TAG_HIDE_TITLE: {
                mTitleAnimator = ObjectAnimator.ofFloat(mHomePageTitleBkgRl, "translationY",
                        titleViewY, -titleBarHeight);
                mTitleAnimator.setDuration(duration);
                mTitleAnimator.start();
                break;
            }
            case SHOW_TAG_SHOW_TITLE: {
                mTitleAnimator = ObjectAnimator.ofFloat(mHomePageTitleBkgRl, "translationY",
                        titleViewY, 0);
                mTitleAnimator.setDuration(duration);
                mTitleAnimator.start();
                break;
            }
        }
    }
}
