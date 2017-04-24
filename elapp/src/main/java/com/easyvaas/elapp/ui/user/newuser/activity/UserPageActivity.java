package com.easyvaas.elapp.ui.user.newuser.activity;

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
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.ui.user.newuser.fragment.UserFansFragment;
import com.easyvaas.elapp.ui.user.newuser.fragment.UserPageCommentFragment;
import com.easyvaas.elapp.utils.SingleToast;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/23
 * Editor  Misuzu
 * 普通用户主页
 */

public class UserPageActivity extends MyBaseActivity implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.user_page_avator)
    ImageView mUserPageAvator;
    @BindView(R.id.user_page_info)
    TextView mUserPageInfoText;
    @BindView(R.id.user_page_name)
    TextView mUserPageName;
    @BindView(R.id.user_page_fans_text)
    TextView mUserPageFansText;
    @BindView(R.id.user_page_fans_counts)
    TextView mUserPageFansCounts;
    @BindView(R.id.user_page_focus_text)
    TextView mUserPageFocusText;
    @BindView(R.id.user_page_focus_counts)
    TextView mUserPageFocusCounts;
    @BindView(R.id.user_page_focus_button)
    TextView mUserPageFocusButton;
    @BindView(R.id.user_page_collapsing_layout)
    CollapsingToolbarLayout mUserPageCollapsingLayout;
    @BindView(R.id.user_page_tab_layout)
    SlidingTabLayout mUserPageTabLayout;
    @BindView(R.id.user_page_appbar_layout)
    AppBarLayout mUserPageAppbarLayout;
    @BindView(R.id.user_page_tab_viewpager)
    ViewPager mUserPageTabViewpager;
    @BindView(R.id.user_page_CoordinatorLayout)
    CoordinatorLayout mUserPageCoordinatorLayout;
    @BindView(R.id.user_swipe_refresh_layout)
    SwipeRefreshLayout mUserSwipeRefreshLayout;
    private String[] titles;
    private Fragment[] mFragments;
    private UserPageInfo mUserPageInfo;
    private String userId;
    private String sessionId;
    private String personId;

    @Override
    protected int getLayout() {
        return R.layout.activity_user_page_layout;
    }

    @Override
    protected String getTitleText() {
        return "浅海野";
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        mToobarTitleView.getRightImage().setPadding(0, 0, 0, 0);
        mToobarTitleView.setTitleRightImg(R.drawable.btn_report_n, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleToast.show(UserPageActivity.this, R.string.msg_report_success);
            }
        });
    }

    @Override
    protected void initViewAndData() {
        userId = getIntent().getStringExtra(AppConstants.USER_ID);
        sessionId = getIntent().getStringExtra(AppConstants.SESSION_ID);
        personId = getIntent().getStringExtra(AppConstants.PERSON_ID);
        if (userId.equals(personId)) //自己看自己主页 隐藏关注按钮
            mUserPageFocusButton.setVisibility(View.GONE);
        initTabView();
        initAppBar();
    }

    private void initTabView() {
        titles = new String[]{
                getString(R.string.comment),
                getString(R.string.collect)};
        mFragments = new Fragment[]{
                UserPageCommentFragment.newInstance(userId,personId),
                UserFansFragment.newInstance(personId,sessionId)};
        mUserSwipeRefreshLayout.setOnRefreshListener(this);
        mUserSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.base_purplish));
        mUserPageTabViewpager.setAdapter(new VipPageAdapter(getSupportFragmentManager()));
        mUserPageTabLayout.setViewPager(mUserPageTabViewpager);
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
        mUserPageAppbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRangle = appBarLayout.getTotalScrollRange();
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
     * 获取用户信息
     */
    private void getVipUserInfo() {
        mUserPageCoordinatorLayout.setVisibility(View.VISIBLE);
    }


    /**
     * 初始化头部分数据
     */
    private void setHeaderData(UserPageInfo data) {

        mUserPageName.setText(data.getNickname());
        mUserPageInfoText.setText(data.getSignature());
        mUserPageFansCounts.setText(String.valueOf(data.getFans_count()));
        mUserPageFocusCounts.setText(String.valueOf(data.getFollow_count()));
        Picasso.with(this).load(data.getLogourl()).placeholder(R.drawable.account_bitmap_list).into(mUserPageAvator);
        if (data.getFollowed() == 1) // 0 关注 1 已关注
        {
            mUserPageFocusButton.setSelected(true);
            mUserPageFocusButton.setText(R.string.user_followed);
        } else {
            mUserPageFocusButton.setSelected(false);
            mUserPageFocusButton.setText(R.string.user_follow);
        }

    }

    @Override
    public void onRefresh() {
        Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        getVipUserInfo();
                        mUserSwipeRefreshLayout.setRefreshing(false);
                        mUserSwipeRefreshLayout.setEnabled(false);
                    }
                });
    }

    /**
     * 关注 取消关注 点击
     */
    @OnClick(R.id.user_page_focus_button)
    public void onViewClicked() {

        if (EVApplication.isLogin()) {
            final int action = mUserPageFocusButton.isSelected() ? 0 : 1; // 0 取消关注 1 关注
            Subscription subscription = RetrofitHelper.getInstance().getService()
                    .followSomeOne("id", EVApplication.getUser().getSessionid(), action)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSubscribe<NoResponeBackModel>() {
                        @Override
                        public void OnSuccess(NoResponeBackModel s) {
                            if (action == 1) // 0 关注 1 已关注
                            {
                                mUserPageFocusButton.setSelected(true);
                                mUserPageFocusButton.setText(R.string.user_followed);
                                SingleToast.show(EVApplication.getApp(), R.string.follow_sccuess);
                            } else {
                                mUserPageFocusButton.setSelected(false);
                                mUserPageFocusButton.setText(R.string.user_follow);
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


    @OnClick({R.id.user_page_fans_text, R.id.user_page_fans_counts, R.id.user_page_focus_text, R.id.user_page_focus_counts})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_page_fans_text:
            case R.id.user_page_fans_counts:
                break;
            case R.id.user_page_focus_text:
            case R.id.user_page_focus_counts:
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

}
