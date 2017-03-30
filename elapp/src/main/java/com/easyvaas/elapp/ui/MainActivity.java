package com.easyvaas.elapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.ui.live.HomeLiveFragment;
import com.easyvaas.elapp.ui.market.HomeMarketFragment;
import com.easyvaas.elapp.ui.news.HomeNewsFragment;
import com.easyvaas.elapp.ui.user.UserProfileFragment;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

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
    public SharedPreferences mSp;
    private RelativeLayout rl_mask;


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
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        mRgNavigation = (RadioGroup) findViewById(R.id.rg_navigation);
        mRbNews = (RadioButton) findViewById(R.id.rb_news);
        mRbLive = (RadioButton) findViewById(R.id.rb_live);
        mRbMarket = (RadioButton) findViewById(R.id.rb_market);
        mRbUser = (RadioButton) findViewById(R.id.rb_user);
        rl_mask = (RelativeLayout) findViewById(R.id.rl_mask);
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
//        Intent intent = new Intent(this, AppService.class);
//        stopService(intent);
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


    //蒙版的实现
    public void startShowMask(int maginBottom) {
//        if (mSp.getBoolean("isShowed", false)) {
//            return;
//        }
        rl_mask.setVisibility(View.VISIBLE);
        addFirstMask(maginBottom);
    }

    //添加第一个蒙版
    private void addFirstMask(int margin) {
        LinearLayout mLinearLayout = (LinearLayout)
                LayoutInflater.from(this).inflate(R.layout.layout_mask_first, rl_mask, false);
        mLinearLayout.findViewById(R.id.iv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSecondMask();
            }
        });

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLinearLayout.getLayoutParams();
        params.topMargin = margin - (int) ViewUtil.dp2Px(this, 170);
        mLinearLayout.setLayoutParams(params);
        rl_mask.addView(mLinearLayout);
    }

    //第二个蒙版，咨询
    private void addSecondMask() {
        rl_mask.removeAllViews();
        float mScreenWidth = ViewUtil.getScreenWidth(this);

        int mMashMarginLeft = (int) mScreenWidth / 16;

        RelativeLayout mRelativeLayout = (RelativeLayout)
                LayoutInflater.from(this).inflate(R.layout.layout_mask_second, rl_mask, false);
        mRelativeLayout.setPadding(mMashMarginLeft, 0, 0, 0);
        mRelativeLayout.findViewById(R.id.iv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addThirdMask();

                rl_mask.removeAllViews();
                mRbLive.setChecked(true);
                addFourMask();
            }
        });
        rl_mask.addView(mRelativeLayout);
    }

    private void addThirdMask() {
        rl_mask.removeAllViews();
        RelativeLayout mRelativeLayout = (RelativeLayout)
                LayoutInflater.from(this).inflate(R.layout.layout_mask_third, rl_mask, false);
        rl_mask.addView(mRelativeLayout);
        //
        FrameLayout mFramelayout = (FrameLayout)
                LayoutInflater.from(this).inflate(R.layout.iv_mask_bottom, rl_mask, false);

        int mMaskMarginRight = (int) (ViewUtil.getScreenWidth(this) * 5 / 16);
        int padding = (int) ViewUtil.dp2Px(this, 1);
        mFramelayout.setPadding(0, padding, mMaskMarginRight, padding);
        rl_mask.addView(mFramelayout);
        mRelativeLayout.findViewById(R.id.iv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_mask.removeAllViews();
                rl_mask.setVisibility(View.GONE);
                if (mHomeLiveFragment != null && mHomeLiveFragment.getmViewPager() != null) {
                    mHomeLiveFragment.getmViewPager().setCurrentItem(0);
                }
                mRbNews.setChecked(true);
                mSp.edit().putBoolean("isShowed", true).apply();
            }
        });
    }

    //第三个蒙版。直播
    private void addFourMask() {
        RelativeLayout mRelativeLayout = (RelativeLayout)
                LayoutInflater.from(this).inflate(R.layout.layout_mask_five, rl_mask, false);
        mRelativeLayout.setPadding(0, (int) ViewUtil.dp2Px(this, 50), 0, 0);

        ImageView iv = (ImageView) mRelativeLayout.findViewById(R.id.iv_top);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        params.width = (int) ViewUtil.getScreenWidth(this) / 3 - (int) ViewUtil.dp2Px(this, 24);
        iv.setLayoutParams(params);
        rl_mask.addView(mRelativeLayout);
        mRelativeLayout.findViewById(R.id.iv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_mask.removeAllViews();
                mRgNavigation.check(R.id.rb_live);
                if (mHomeLiveFragment != null && mHomeLiveFragment.getmViewPager() != null) {
                    mHomeLiveFragment.getmViewPager().setCurrentItem(1);
                }
                addFiveMask();
            }
        });
    }

    //第四个蒙版，图文
    public void addFiveMask() {

        rl_mask.removeAllViews();
        RelativeLayout mRelativeLayout = (RelativeLayout)
                LayoutInflater.from(this).inflate(R.layout.layout_mask_four, rl_mask, false);
        mRelativeLayout.setPadding(0, (int) ViewUtil.dp2Px(this, 50), 0, 0);

        ImageView iv = (ImageView) mRelativeLayout.findViewById(R.id.iv_top);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        params.width = (int) ViewUtil.getScreenWidth(this) / 3 - (int) ViewUtil.dp2Px(this, 24);
        iv.setLayoutParams(params);
        rl_mask.addView(mRelativeLayout);

        mRelativeLayout.findViewById(R.id.iv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_mask.removeAllViews();
                mRbMarket.setChecked(true);
                addThirdMask();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        RealmHelper.getInstance().deleteGlobalAllRecord();
    }
}
