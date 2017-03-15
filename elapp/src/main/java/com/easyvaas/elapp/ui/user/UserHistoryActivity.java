package com.easyvaas.elapp.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.event.HistoryDeleteEvent;
import com.easyvaas.elapp.event.HistoryReadDeleteEvent;
import com.easyvaas.elapp.ui.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoliuya on 2017/2/27.
 */

public class UserHistoryActivity extends BaseActivity implements View.OnClickListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyAdapter myAdapter;
    private ImageView mBackIv;
    private TextView mTitleTv;
    private TextView mCompleteTv;
    private boolean mHistoryTag;

    public static void start(Context context) {
        Intent starter = new Intent(context, UserHistoryActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mBackIv = (ImageView) findViewById(R.id.iv_back);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mCompleteTv = (TextView) findViewById(R.id.tv_complete);
        mTabLayout.setupWithViewPager(mViewPager);
        myAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myAdapter);
        mCompleteTv.setVisibility(View.VISIBLE);
        mTitleTv.setText(R.string.user_my_read_history);
        mCompleteTv.setText(R.string.clear_history);
        initListener();

    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mCompleteTv.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            if(position==0){
                mHistoryTag = false;
            }else{
                mHistoryTag = true;
            }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:{
                finish();
                break;
            }
            case R.id.tv_complete:{
                //清空历史记录
                if(!mHistoryTag){
                    EventBus.getDefault().post(new HistoryDeleteEvent());
                }else{
                    EventBus.getDefault().post(new HistoryReadDeleteEvent());
                }
            }
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private String[] mTabsTitle;
        List<Fragment> fragments;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mTabsTitle = getResources().getStringArray(R.array.history_tabs);
            fragments = new ArrayList<>();
            fragments.add(new WatchHistoryFragment());
            fragments.add(new ReadHistoryFragment());
            //            fragments.add(new ImageTextLiveListFragment());
            //            fragments.add(new GoodsVideoFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }

}

