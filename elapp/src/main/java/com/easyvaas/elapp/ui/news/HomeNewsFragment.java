package com.easyvaas.elapp.ui.news;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.easyvaas.elapp.event.NewsListScrollEvent;
import com.easyvaas.elapp.ui.base.BaseFragment;
import com.easyvaas.elapp.ui.search.GlobalSearchActivity;
import com.easyvaas.elapp.utils.Logger;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

public class HomeNewsFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "HomeNewsFragment";
    private FrameLayout mFrTitle;
    private ImageView mIvSearch;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_home_news, null);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        AppService.startRefreshExponent(getContext().getApplicationContext());
    }

    private void initView(View view) {
        mFrTitle = (FrameLayout) view.findViewById(R.id.fr_title);
        mIvSearch = (ImageView) view.findViewById(R.id.iv_title_search);
        mIvSearch.setOnClickListener(this);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        String[] tabTitles = getResources().getStringArray(R.array.news_tab);
        mViewPager.setAdapter(new MyAdapter(getChildFragmentManager(), tabTitles));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                showTitleView(false);
                if (position == 0) {
                    mIvSearch.setVisibility(View.VISIBLE);
                } else {
                    mIvSearch.setVisibility(View.GONE);
                }
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_search:
                GlobalSearchActivity.start(getContext());
                break;
        }
    }

    private class MyAdapter extends FragmentPagerAdapter {
        String[] tabTitles;
        Map<Integer, Fragment> fragments = new HashMap();

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
            if (position == 0) {
                fragment = new ImportantNewsListFragment();
            } else if (position == 1) {
                fragments.put(position, fragment = new LastestNewsListFragment());
            } else {
                fragments.put(position, fragment = new MyStockNewsListFragment());
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewsListScrollEvent event) {
        Logger.d(TAG, "onMessageEvent: " + event.isShowTitle + "   msearch  " + mIvSearch.isShown());
        showTitleView(event.isShowTitle);
    }

    private void showTitleView(boolean showTitle) {
        if (showTitle) {
            mFrTitle.setBackgroundColor(getContext().getResources().getColor(R.color.base_white));
            ColorStateList colorStateList = getResources().getColorStateList(R.color.selector_news_title_tab_show);
            mTabLayout.setSelectedTabIndicatorColor(getContext().getResources().getColor(R.color.base_purplish));
            mIvSearch.setBackgroundColor(getContext().getResources().getColor(R.color.black_alpha_percent_100));
            mIvSearch.setImageResource(R.drawable.market_search);
            mTabLayout.setTabTextColors(colorStateList);
        } else {
            mFrTitle.setBackgroundColor(getContext().getResources().getColor(R.color.black_alpha_percent_100));
            ColorStateList colorStateList = getResources().getColorStateList(R.color.selector_news_title_tab_normal);
            mTabLayout.setSelectedTabIndicatorColor(getContext().getResources().getColor(R.color.base_white));
            mTabLayout.setTabTextColors(colorStateList);
            mIvSearch.setBackground(getResources().getDrawable(R.drawable.shape_news_search_bg));
            mIvSearch.setImageResource(R.drawable.btn_news_search_n);
        }
    }

}
