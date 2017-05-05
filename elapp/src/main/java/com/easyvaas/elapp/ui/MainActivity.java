package com.easyvaas.elapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.ui.live.HomeLiveFragment;
import com.easyvaas.elapp.ui.live.livenew.LiveMainFragment;
import com.easyvaas.elapp.ui.market.HomeMarketFragment;
import com.easyvaas.elapp.ui.market.marketnew.MarketFragment;
import com.easyvaas.elapp.ui.news.HomeNewsFragment;
import com.easyvaas.elapp.ui.news.NewsMainFragment;
import com.easyvaas.elapp.ui.user.UserProfileFragment;
import com.easyvaas.elapp.ui.user.usernew.UserCenterFragment;
import com.easyvaas.elapp.utils.SingleToast;
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
    private LiveMainFragment mLiveMainFragment;
    private HomeMarketFragment mHomeMarketFragment;
    private HomeNewsFragment mHomeNewsFragment;
    private NewsMainFragment mNewsMainFragment;
    private MarketFragment mMarketFragment;
    private UserCenterFragment mUserCenterFragment;
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
        mLiveMainFragment = LiveMainFragment.newInstance();
        mUserProfileFragment = new UserProfileFragment();
        mHomeMarketFragment = new HomeMarketFragment();
        mHomeNewsFragment = new HomeNewsFragment();
        mNewsMainFragment = NewsMainFragment.newInstance();
        mMarketFragment = MarketFragment.newInstance();
        mUserCenterFragment = UserCenterFragment.newInstance();
        mCurrentFragment = mHomeNewsFragment;
        mRbNews.setChecked(true);
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
               switchFragment(mNewsMainFragment);
                break;
            case R.id.rb_live:
                switchFragment(mLiveMainFragment);
                break;
            case R.id.rb_market:
                switchFragment(mMarketFragment);
                break;
            case R.id.rb_user:
                switchFragment(mUserCenterFragment);
                break;

        }
    }

    /**
     * 切换fragment
     */
    private void switchFragment(Fragment fragment)
    {
        if (!fragment.isAdded())
        {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.fl_content,fragment).commit();
        }else
        {
            if (mCurrentFragment != fragment)
            mFragmentManager.beginTransaction().show(fragment).hide(mCurrentFragment).commit();
        }
        mCurrentFragment = fragment;
    }


    //蒙版的实现
    public void startShowMask(int maginBottom) {
        if (mSp.getBoolean("isShowed", false)) {
            return;
        }
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


    //最后一个
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
                if (mLiveMainFragment != null && mLiveMainFragment.getViewPager() != null) {
                    mLiveMainFragment.getViewPager().setCurrentItem(0);
                }
                mRbNews.setChecked(true);
                mSp.edit().putBoolean("isShowed", true).apply();
            }
        });
    }

    //第三个蒙版。视频直播
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
                if (mLiveMainFragment != null && mLiveMainFragment.getViewPager() != null) {
                    mLiveMainFragment.getViewPager().setCurrentItem(1);
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

    private long firstPressTime= 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){

            if (System.currentTimeMillis()-firstPressTime>2000){
                SingleToast.show(MainActivity.this,getString(R.string.press_to_exit),Toast.LENGTH_SHORT);
                firstPressTime = System.currentTimeMillis();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
