package com.easyvaas.elapp.ui.user.usernew.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.user.UserPageInfo;
import com.easyvaas.elapp.bean.user.UserPageInfo.TagBean;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.ui.user.usernew.fragment.VipUserArticleFragment;
import com.easyvaas.elapp.ui.user.usernew.fragment.VipUserCheatsFragment;
import com.easyvaas.elapp.ui.user.usernew.fragment.VipUserCommentFragment;
import com.easyvaas.elapp.ui.user.usernew.fragment.VipUserLivingFragment;
import com.easyvaas.elapp.utils.SingleToast;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/21
 * Editor  Misuzu
 * 大V主页
 */
public class UserVipPageActivity extends MyBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.vip_avator)
    ImageView mVipAvator;
    @BindView(R.id.vip_number)
    TextView mVipNumber;
    @BindView(R.id.vip_info)
    TextView mVipInfo;
    @BindView(R.id.vip_name)
    TextView mVipName;
    @BindView(R.id.vip_fans_text)
    TextView mVipFansText;
    @BindView(R.id.vip_fans_counts)
    TextView mVipFansCounts;
    @BindView(R.id.vip_focus_button)
    TextView mVipFocusButton;
    @BindView(R.id.vip_tags)
    TextView mVipTags;
    @BindView(R.id.vip_introduce)
    TextView mVipIntroduce;
    @BindView(R.id.vip_tab_layout)
    SlidingTabLayout mVipTabLayout;
    @BindView(R.id.vip_tab_viewpager)
    ViewPager mVipTabViewpager;
    @BindView(R.id.vip_appbar_layout)
    AppBarLayout mVipAppbarLayout;
    @BindView(R.id.vip_collapsing_layout)
    CollapsingToolbarLayout mVipCollapsingLayout;
    @BindView(R.id.user_swipe_refresh_layout)
    SwipeRefreshLayout mUserSwipeRefreshLayout;
    @BindView(R.id.vip_CoordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    private String[] titles;
    private Fragment[] mFragments;
    private UserPageInfo mUserPageInfo;
    private String userId;
    private String sessionId;
    private String personId;

    @Override
    protected int getLayout() {
        return R.layout.activity_vip_page_layout;
    }

    @Override
    protected String getTitleText() {
        return "";
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        mToobarTitleView.setTitleRightImg(R.drawable.btn_report_n, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleToast.show(UserVipPageActivity.this, R.string.msg_report_success);
            }
        });
    }

    @Override
    protected void initViewAndData() {
        userId = getIntent().getStringExtra(AppConstants.USER_ID);
        sessionId = getIntent().getStringExtra(AppConstants.SESSION_ID);
        personId = getIntent().getStringExtra(AppConstants.PERSON_ID);
        if (userId.equals(personId)) //自己看自己主页 隐藏关注按钮
            mVipFocusButton.setVisibility(View.GONE);
        initTabView();
        initAppBar();
    }


    private void initTabView() {
        titles = new String[]{
                getString(R.string.user_living),
                getString(R.string.user_secret),
                getString(R.string.user_article),
                getString(R.string.comment)};
        mFragments = new Fragment[]{
                VipUserLivingFragment.newInstance(personId),
                VipUserCheatsFragment.newInstance(personId),
                VipUserArticleFragment.newInstance(personId),
                VipUserCommentFragment.newInstance(userId,personId),
        };
        mUserSwipeRefreshLayout.setOnRefreshListener(this);
        mUserSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.base_purplish));
        mVipTabViewpager.setAdapter(new VipPageAdapter(getSupportFragmentManager()));
        mVipTabLayout.setViewPager(mVipTabViewpager);
        mUserSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mUserSwipeRefreshLayout.setRefreshing(true);
            }
        });
        onRefresh();
    }

    private void initAppBar() {
        // 动态改变toolbar
        mVipAppbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    mToobarTitleView.getCenterTextView().setAlpha(0.0f);
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    mToobarTitleView.getCenterTextView().setAlpha(1);
                } else {
                    mToobarTitleView.getCenterTextView().setAlpha(0.0f);
                }

            }
        });
    }


    /**
     * 获取大V用户信息
     */
    private void getVipUserInfo() {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getUserPageInfo(userId, sessionId, personId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<UserPageInfo>() {
                    @Override
                    public void OnSuccess(UserPageInfo userPageInfo) {
                        mUserSwipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mUserSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        if (userPageInfo != null) {
                            setHeaderData(userPageInfo);
                        }
                        mCoordinatorLayout.setVisibility(View.VISIBLE);
                        mUserSwipeRefreshLayout.setEnabled(false);
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mUserSwipeRefreshLayout.setRefreshing(false);
                        SingleToast.show(UserVipPageActivity.this, R.string.network_error);
                    }
                });
        addSubscribe(subscription);
    }


    /**
     * 初始化头部分数据
     */
    private void setHeaderData(UserPageInfo data) {

        mToobarTitleView.setTitleText(data.getNickname());
        mVipName.setText(data.getNickname());
        mVipInfo.setText(data.getSignature());
        mVipNumber.setText(data.getCredentials());
        mVipTags.setText(getJoinString(data.getTags()));
        mVipIntroduce.setText(data.getIntroduce());
        mVipFansCounts.setText(String.valueOf(data.getFans_count()));
        Picasso.with(this).load(data.getLogourl()).placeholder(R.drawable.account_bitmap_list).into(mVipAvator);
        if (data.getFollowed() == 1) // 0 关注 1 已关注
        {
            mVipFocusButton.setSelected(true);
            mVipFocusButton.setText(R.string.user_followed);
        } else {
            mVipFocusButton.setSelected(false);
            mVipFocusButton.setText(R.string.user_follow);
        }

    }

    @Override
    public void onRefresh() {
        getVipUserInfo();
    }

    /**
     * 关注 取消关注 点击
     */
    @OnClick(R.id.vip_focus_button)
    public void onViewClicked() {
        if (EVApplication.isLogin()) {
            final int action = mVipFocusButton.isSelected() ? 0 : 1; // 0 取消关注 1 关注
            Subscription subscription = RetrofitHelper.getInstance().getService()
                    .followSomeOne(personId, EVApplication.getUser().getSessionid(), action)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSubscribe<NoResponeBackModel>() {
                        @Override
                        public void OnSuccess(NoResponeBackModel s) {
                            if (action == 1) // 0 关注 1 已关注
                            {
                                mVipFocusButton.setSelected(true);
                                mVipFocusButton.setText(R.string.user_followed);
                                SingleToast.show(EVApplication.getApp(), R.string.follow_sccuess);
                            } else {
                                mVipFocusButton.setSelected(false);
                                mVipFocusButton.setText(R.string.user_follow);
                                SingleToast.show(EVApplication.getApp(), R.string.follow_cancel);
                            }
                        }

                        @Override
                        public void OnFailue(String msg) {
                            SingleToast.show(EVApplication.getApp(), R.string.opreat_fail);
                        }
                    });
            addSubscribe(subscription);
        } else
            LoginActivity.start(mContext);
    }


    @OnClick({R.id.vip_fans_counts, R.id.vip_fans_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vip_fans_counts:
            case R.id.vip_fans_text:
                Intent fansIntent = new Intent(this, UserFansActivity.class);
                fansIntent.putExtra(AppConstants.USER_ID,personId);
                fansIntent.putExtra(AppConstants.SESSION_ID,sessionId);
                startActivity(fansIntent);
                break;
        }
    }


    private class VipPageAdapter extends FragmentPagerAdapter {

        private VipPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    /**
     * 格式化标签
     */
    public static  String getJoinString(List<TagBean> datas)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < datas.size(); i++) {
            if (i == 0)
                stringBuilder.append(datas.get(i).getName());
            else
            {
                stringBuilder.append(",").append(datas.get(i).getName());
            }
        }
        return stringBuilder.toString();
    }
}
