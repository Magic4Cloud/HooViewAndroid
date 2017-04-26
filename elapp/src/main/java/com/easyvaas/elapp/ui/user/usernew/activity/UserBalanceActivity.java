package com.easyvaas.elapp.ui.user.usernew.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.gift.GiftManager;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.pay.MyAssetEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.pay.CashInActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserPublishCheatsFragment;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserPublishLivingFragment;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.NumberUtil;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import butterknife.BindView;

/**
 * Date    2017/4/21
 * Author  xiaomao
 * 个人中心---我的余额
 */
public class UserBalanceActivity extends MyBaseActivity {

    @BindView(R.id.balance_money)
    TextView mBalanceTv;
    @BindView(R.id.balance_income_view)
    LinearLayout mBalanceView;
    @BindView(R.id.balance_income)
    TextView mBalanceIncomeTv;
    @BindView(R.id.balance_tab_layout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.balance_tab_viewpager)
    ViewPager mTabViewpager;

    private String[] mTitles;
    private Fragment[] mFragments;
    private Bundle mBundle;

    public static void start(Context context, Bundle bundle) {
        if (Preferences.getInstance(context).isLogin() && EVApplication.isLogin()) {
            Intent starter = new Intent(context, UserBalanceActivity.class);
            starter.putExtras(bundle);
            context.startActivity(starter);
        } else {
            LoginActivity.start(context);
        }
    }

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
        mBundle = getIntent().getExtras();
        mToobarTitleView.setTitleTextRight("充值", R.color.tab_text_color_selected, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CashInActivity.start(UserBalanceActivity.this);
            }
        });
        // 大V身份
        boolean isVip = mBundle.getBoolean(Constants.EXTRA_KEY_IS_VIP, false);
        if (isVip) {
            mBalanceView.setVisibility(View.VISIBLE);
        } else {
            mBalanceView.setVisibility(View.GONE);
        }

        mTitles = new String[]{"消费", "充值"};
        mFragments = new Fragment[]{
                UserPublishLivingFragment.newInstance(),
                UserPublishCheatsFragment.newInstance()
        };
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
        loadAsset();
    }

    /**
     * 加载用户资产
     */
    private void loadAsset() {
        ApiHelper.getInstance().getAssetInfo(new MyRequestCallBack<MyAssetEntity>() {
            @Override
            public void onSuccess(MyAssetEntity result) {
                if (result != null){
                    mBalanceTv.setText(NumberUtil.format(result.getEcoin()));
                    mBalanceIncomeTv.setText(NumberUtil.format(result.getRiceroll()));
                    Preferences.getInstance(UserBalanceActivity.this).putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, result.getEcoin());
                    GiftManager.setECoinCount(UserBalanceActivity.this, result.getEcoin());
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });
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
