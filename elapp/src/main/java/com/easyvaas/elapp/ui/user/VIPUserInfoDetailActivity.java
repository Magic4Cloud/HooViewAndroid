package com.easyvaas.elapp.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.UserInfoArrayModel;
import com.easyvaas.elapp.bean.user.UserInfoModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.ApiUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.ui.live.BookPlayFragment;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;


public class VIPUserInfoDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "VIPUserInfoDetailActivi";
    public static final String EXTRA_NAME = "extra_name";
    private ImageView mIvBack;
    private ImageView mIvReport;
    private TextView mTvFollow;
    private String mName;
    private UserInfoModel mUserInfoModel;
    protected MyAdapter mMyAdapter;
    protected ViewPager mViewPager;
    protected String mRoomId;
    private boolean isAnchor;
    protected View mRoot;


    public static void start(Context context, String name) {
        Intent starter = new Intent(context, VIPUserInfoDetailActivity.class);
        starter.putExtra(EXTRA_NAME, name);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mName = getIntent().getStringExtra(EXTRA_NAME);
        if (TextUtils.isEmpty(mName)) {
            return;
        }
        setContentView(R.layout.activity_vip_user_detail);
        initView();
        getUserInfos(mName);

    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mIvReport = (ImageView) findViewById(R.id.iv_report);
        mIvReport.setOnClickListener(this);
        mTvFollow = (TextView) findViewById(R.id.tv_follow);
        mTvFollow.setOnClickListener(this);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(mViewPager);
        mRoot = findViewById(R.id.root);
        initFollowStatus();
    }

    private void getUserInfos(String name) {
        List<String> names = new ArrayList<>();
        names.add(name);
        ApiHelper.getInstance().getUserInfosNew(names, new MyRequestCallBack<UserInfoArrayModel>() {
            @Override
            public void onSuccess(UserInfoArrayModel result) {
                if (result != null && result.getUsers() != null && result.getUsers().size() > 0) {
                    mUserInfoModel = result.getUsers().get(0);
                    List<Fragment> list = new ArrayList<>();
                    list.add(VipLiveListFragment.newInstance(mUserInfoModel));
                    list.add(BookPlayFragment.newInstance());
                    list.add(FansListFragment.newInstance(mName));
                    mViewPager.setOffscreenPageLimit(2);
                    mViewPager.setPageMargin((int) ViewUtil.dp2Px(getApplicationContext(), 10));
                    mMyAdapter = new MyAdapter(getSupportFragmentManager(), list, getResources().getStringArray(R.array.vip_center));
                    mViewPager.setAdapter(mMyAdapter);
                    ImageView headerBg = (ImageView) findViewById(R.id.iv_header_bg);
                    ImageView avater = (RoundImageView) findViewById(R.id.riv_avater);
                    Utils.showImage(mUserInfoModel.getLogourl(), R.drawable.account_bitmap_user, avater);
                    Utils.showImageBlur(VIPUserInfoDetailActivity.this, mUserInfoModel.getLogourl(), R.drawable.account_bitmap_user, headerBg);
                    updateUI();
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    private void updateUI() {
        LinearLayout tagsViewContainer = (LinearLayout) findViewById(R.id.ll_tag_container);
        if (mUserInfoModel.getTags() != null) {
            for (int i = 0; i < mUserInfoModel.getTags().size(); i++) {
                UserInfoModel.TagsEntity tagsEntity = mUserInfoModel.getTags().get(i);
                TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_use_tag, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.rightMargin = (int) ViewUtil.dp2Px(this, 10);
                tagsViewContainer.addView(textView, layoutParams);
                textView.setText(tagsEntity.getName());
            }
        }
        TextView tvName = (TextView) findViewById(R.id.tv_name);
        tvName.setText(mUserInfoModel.getNickname());
        TextView textSlogn  = (TextView) findViewById(R.id.tv_company);
        textSlogn.setText(mUserInfoModel.getSignature());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_report:
                break;
            case R.id.tv_follow:
                if (Preferences.getInstance(this).isLogin() && EVApplication.isLogin()) {
                    if (v.isSelected()) {
                        v.setSelected(false);
                        SingleToast.show(getApplicationContext(), getString(R.string.cancel_follow_sccuess));
                    } else {
                        v.setSelected(true);
                        SingleToast.show(getApplicationContext(), getString(R.string.follow_sccuess));
                    }
                    ApiUtil.userFollow(VIPUserInfoDetailActivity.this, mName, v.isSelected(), v);
                } else {
                    LoginActivity.start(this);
                }
                break;
        }
    }

    private void initFollowStatus() {
        if (Preferences.getInstance(this).getBoolean(mName, false)) {
            mTvFollow.setSelected(true);
        } else {
            mTvFollow.setSelected(false);
        }

    }

    public class MyAdapter extends FragmentPagerAdapter {
        private String[] mTabsTitle;
        List<Fragment> fragments;

        public MyAdapter(FragmentManager fm, List<Fragment> list, String[] tabTitles) {
            super(fm);
            this.mTabsTitle = tabTitles;
            this.fragments = list;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return mTabsTitle.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }
}
