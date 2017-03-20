package com.easyvaas.elapp.ui.news;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.news.ChannelModel;
import com.easyvaas.elapp.ui.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class HooviewEyesNewsActivity extends BaseActivity {
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    private TextView mTvTitle;
    private static final String EXTRA_CHANNEL = "extra_channel";
    private ChannelModel mChannelModel;

    public static void start(Context context, ChannelModel channelsModel) {
        Intent starter = new Intent(context, HooviewEyesNewsActivity.class);
        starter.putExtra(EXTRA_CHANNEL, channelsModel);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hooview_eyes);
//        mChannelModel = Preferences.getInstance(getApplicationContext()).getJsonObject(Preferences.KEY_HOOOVIEW_EYE_TABS, ImportantNewsModel.HuoyanEntity.ChannelsEntity.class);
        mChannelModel = (ChannelModel) getIntent().getSerializableExtra(EXTRA_CHANNEL);
        if (mChannelModel == null) {
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.hooview_gold_eye);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private Map<Integer, Fragment> fragments = new HashMap<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            fragments.put(position,fragment = HooViewNewsListFragment.newInstance(mChannelModel.getId() + "", mChannelModel.getPrograms().get(position).getId() + ""));
//            fragments.put(position, fragment = new HooViewNewsListFragment(mChannelModel.getId() + "", mChannelModel.getPrograms().get(position).getId() + ""));
            return fragment;
        }

        @Override
        public int getCount() {
            return mChannelModel.getPrograms().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mChannelModel.getPrograms().get(position).getName();
        }

    }
}
