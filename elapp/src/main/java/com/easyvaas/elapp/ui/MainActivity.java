package com.easyvaas.elapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.db.RealmHelper;
import com.hooview.app.R;
import com.easyvaas.elapp.service.AppService;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.ui.live.HomeLiveFragment;
import com.easyvaas.elapp.ui.market.HomeMarketFragment;
import com.easyvaas.elapp.ui.news.HomeNewsFragment;
import com.easyvaas.elapp.ui.user.UserProfileFragment;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    public static final String TAB_NEWS = "news";
    public static final String TAB_LIVE = "live";
    public static final String TAB_MARKET = "market";
    public static final String TAB_USER = "user";
    private static final String EXTRA_KEY_TAB = "extra_key_tab";
    private RadioGroup mRgNavigation;
    private RadioButton mRbNews;
    private RadioButton mRbLive;
    private RadioButton mRbMarket;
    private RadioButton mRbUser;
    private String mCurrentTab;
    private FragmentManager mFragmentManager;
    private UserProfileFragment mUserProfileFragment;
    private HomeLiveFragment mHomeLiveFragment;
    private HomeMarketFragment mHomeMarketFragment;
    private HomeNewsFragment mHomeNewsFragment;
    private Fragment mCurrentFragment;


    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    /**
     * @param context
     * @param tab     TAB_NEWS,TAB_LIVE,TAB_MARKET,TAB_USER
     */
    public static void start(Context context, String tab) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra(EXTRA_KEY_TAB, tab);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initTab();
    }

    private void initView() {
        mRgNavigation = (RadioGroup) findViewById(R.id.rg_navigation);
        mRbNews = (RadioButton) findViewById(R.id.rb_news);
        mRbLive = (RadioButton) findViewById(R.id.rb_live);
        mRbMarket = (RadioButton) findViewById(R.id.rb_market);
        mRbUser = (RadioButton) findViewById(R.id.rb_user);
        mRgNavigation.setOnCheckedChangeListener(this);
        mFragmentManager = getSupportFragmentManager();
    }

    private void initTab() {
        mHomeLiveFragment = new HomeLiveFragment();
        mUserProfileFragment = new UserProfileFragment();
        mHomeMarketFragment = new HomeMarketFragment();
        mHomeNewsFragment = new HomeNewsFragment();
        mCurrentFragment = mUserProfileFragment;
        mFragmentManager.beginTransaction().replace(R.id.fl_content, mUserProfileFragment).commit();
        selectTab(getIntent());
    }

    private void selectTab(Intent intent) {
        String tab = intent.getStringExtra(EXTRA_KEY_TAB);
        if (TextUtils.isEmpty(tab)) {
            mRbNews.setChecked(true);
        } else if (TAB_NEWS.equals(tab)) {
            mRbNews.setChecked(true);
        } else if (TAB_LIVE.equals(tab)) {
            mRbLive.setChecked(true);
        } else if (TAB_MARKET.equals(tab)) {
            mRbMarket.setChecked(true);
        } else if (TAB_USER.equals(tab)) {
            mRbUser.setChecked(true);
        } else {
            mRbNews.setChecked(true);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        selectTab(intent);
        setIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(this, AppService.class);
        stopService(intent);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_news:
                if (mCurrentFragment != mHomeNewsFragment && !mHomeNewsFragment.isAdded()) {
                    mCurrentFragment = mHomeNewsFragment;
                    mFragmentManager.beginTransaction().replace(R.id.fl_content, mHomeNewsFragment).commit();
                }
                break;
            case R.id.rb_live:
                if (mCurrentFragment != mHomeLiveFragment && !mHomeLiveFragment.isAdded()) {
                    mCurrentFragment = mHomeLiveFragment;
                    mFragmentManager.beginTransaction().replace(R.id.fl_content, mHomeLiveFragment).commit();
                }
                break;
            case R.id.rb_market:
                if (mCurrentFragment != mHomeMarketFragment && !mHomeMarketFragment.isAdded()) {
                    mCurrentFragment = mHomeMarketFragment;
                    mFragmentManager.beginTransaction().replace(R.id.fl_content, mHomeMarketFragment).commit();
                }
                break;
            case R.id.rb_user:
                if (mCurrentFragment != mUserProfileFragment && !mUserProfileFragment.isAdded()) {
                    mCurrentFragment = mUserProfileFragment;
                    mFragmentManager.beginTransaction().replace(R.id.fl_content, mUserProfileFragment).commit();
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RealmHelper.getInstance().deleteGlobalAllRecord();
    }
}
