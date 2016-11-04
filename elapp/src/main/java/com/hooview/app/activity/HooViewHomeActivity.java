package com.hooview.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hooview.app.R;
import com.hooview.app.adapter.HomePagerAdapter;
import com.hooview.app.app.EVApplication;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiUtil;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.UpdateManager;

import butterknife.ButterKnife;

public class HooViewHomeActivity extends BaseActivity{

    private RadioGroup mRadiogroup;//多选
    private ViewPager mViewpager;
    private RadioButton mButtonHome;//底部标签（左）
    private RadioButton mButtonMessage;//底部标签(右)


    //双击退出的返回计时的时间
    private long mLastPressBack;

    //数据存储的
    private Preferences mPref;

    protected boolean mIsActionBarColorStatusBar = false;
    private HomePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsActionBarColorStatusBar = true;
        super.onCreate(savedInstanceState);
        EVApplication.getApp().setHaveLaunchedHome(true);

        setContentView(R.layout.activity_hoo_view_home);
        ButterKnife.bind(this);
        init();
        initListener();
    }

    //初始化操作
    private void init() {
        mButtonHome = (RadioButton) findViewById(R.id.button_home);
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mRadiogroup = (RadioGroup) findViewById(R.id.home_radiogroup);
        mButtonMessage = (RadioButton) findViewById(R.id.button_message);

        //默认选择主页面的tab
        mButtonHome.setSelected(true);
        mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mPagerAdapter);

        //初始化获取存储的数据的SharedPreference
        mPref = Preferences.getInstance(this);
        if (!mPref.getBoolean(Preferences.KEY_IS_HAVE_SHOW_UPDATE_DIALOG, false)
                && UpdateManager.getInstance(this).isHaveUpdate()) {
            UpdateManager.getInstance(this).checkUpdateAfterSplash();
            mPref.putBoolean(Preferences.KEY_IS_HAVE_SHOW_UPDATE_DIALOG, false);
        }

    }

    //初始化监听
    private void initListener() {
        mRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.button_home) {
                    mViewpager.setCurrentItem(0, false);
                    mButtonHome.setSelected(true);
                    mButtonMessage.setSelected(false);

                } else {
                    mViewpager.setCurrentItem(1, false);
                    mButtonMessage.setSelected(true);
                    mButtonHome.setSelected(false);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApiUtil.checkUnreadMessage(this);
    }

    //跳转方法
    public static void launch(Context context) {
        context.startActivity(new Intent(context, HooViewHomeActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (mLastPressBack + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            SingleToast.show(this, R.string.msg_click_again_to_exit);
            mLastPressBack = System.currentTimeMillis();
        }
    }
}
