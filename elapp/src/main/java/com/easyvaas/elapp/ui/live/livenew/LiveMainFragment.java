package com.easyvaas.elapp.ui.live.livenew;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.imageTextLive.CheckImageTextLiveModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.mybase.MyBaseFragment;
import com.easyvaas.elapp.ui.live.GoodsVideoFragment;
import com.easyvaas.elapp.ui.live.ImageTextLiveListFragment;
import com.easyvaas.elapp.ui.live.LivePrepareActivity;
import com.easyvaas.elapp.ui.live.MyImageTextLiveRoomActivity;
import com.easyvaas.elapp.ui.search.GlobalSearchActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.ViewUtil;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Date    2017/4/25
 * Author  xiaomao
 * 直播主界面
 */

public class LiveMainFragment extends MyBaseFragment {

    private static final long ANIM_DURATION = 500;
    @BindView(R.id.live_logo)
    ImageView mLogoIv;
    @BindView(R.id.live_tablayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.live_search)
    ImageView mSearchIv;
    @BindView(R.id.live_tab_viewpager)
    ViewPager mTabViewpager;
    @BindView(R.id.live_operator)
    FrameLayout mLiveOperatorFl;
    @BindView(R.id.live_start)
    ImageView mLiveStartIv;
    @BindView(R.id.live_start_video)
    ImageView mLiveStartVideoIv;
    @BindView(R.id.live_start_image_text)
    ImageView mLiveStartImageTextIv;

    private String[] mTitles;
    private Fragment[] mFragments;

    @Override
    protected int getLayout() {
        return R.layout.fragment_live_main;
    }

    @Override
    protected void initViewAndData() {
        mTitles = new String[]{
                getString(R.string.live_live_video),
                getString(R.string.live_image_text),
                getString(R.string.live_good_video)};
        mFragments = new Fragment[]{
                LiveVideoListFragment.newInstance(),
                new ImageTextLiveListFragment(),
                new GoodsVideoFragment()
        };
        mTabViewpager.setAdapter(new LivePagerAdapter(getChildFragmentManager()));
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
        // 开始直播按钮不可见
        mLiveOperatorFl.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.getInstance(getContext()).isLogin() && EVApplication.isLogin()) {
            if (EVApplication.getUser().getVip() == 1) {
                mLiveOperatorFl.setVisibility(View.VISIBLE);
            } else {
                mLiveOperatorFl.setVisibility(View.INVISIBLE);
            }
        } else {
            mLiveOperatorFl.setVisibility(View.INVISIBLE);
        }
    }

    public static LiveMainFragment newInstance() {
        return new LiveMainFragment();
    }

    /**
     * 搜索
     */
    @OnClick(R.id.live_search)
    public void onSearch() {
        GlobalSearchActivity.start(getActivity());
    }

    /**
     * 开始直播
     * 属性动画
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick(R.id.live_start)
    public void onStartLive() {
        final int translate = (int) ViewUtil.dp2Px(getContext().getApplicationContext(), 70);
        if (!mLiveStartIv.isSelected()) {
            mLiveStartIv.animate()
                    .rotationBy(0.5f)
                    .rotation(180)
                    .setDuration(ANIM_DURATION)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStartIv.setImageResource(R.drawable.btn_video_cancel_n);
                            mLiveStartIv.setEnabled(false);
                        }
                    })
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStartIv.setEnabled(true);
                        }
                    })
                    .start();
            mLiveStartVideoIv.setVisibility(View.VISIBLE);
            mLiveStartVideoIv.animate()
                    .rotationBy(0.5f)
                    .rotation(360)
                    .xBy(-translate)
                    .setDuration(ANIM_DURATION)
                    .setInterpolator(new DecelerateInterpolator())
                    .alpha(1)
                    .start();
            mLiveStartImageTextIv.setVisibility(View.VISIBLE);
            mLiveStartImageTextIv.animate()
                    .rotationBy(0.5f)
                    .rotation(360)
                    .yBy(-translate)
                    .setDuration(ANIM_DURATION)
                    .setInterpolator(new DecelerateInterpolator())
                    .alpha(1)
                    .start();
            mLiveStartIv.setSelected(true);
        } else {
            mLiveStartIv.animate()
                    .rotationBy(0.5f)
                    .rotation(0)
                    .setDuration(ANIM_DURATION)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStartIv.setEnabled(false);
                        }
                    })
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStartIv.setImageResource(R.drawable.btn_launch_n);
                            mLiveStartIv.setEnabled(true);
                        }
                    })
                    .start();
            mLiveStartVideoIv.animate()
                    .rotationBy(0.5f)
                    .rotation(0)
                    .xBy(translate)
                    .setDuration(ANIM_DURATION)
                    .alpha(0)
                    .setInterpolator(new AccelerateInterpolator())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStartVideoIv.setVisibility(View.INVISIBLE);
                        }
                    })
                    .start();
            mLiveStartImageTextIv.animate()
                    .rotationBy(0.5f)
                    .rotation(0)
                    .yBy(translate)
                    .setDuration(ANIM_DURATION)
                    .alpha(0)
                    .setInterpolator(new AccelerateInterpolator())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mLiveStartImageTextIv.setVisibility(View.INVISIBLE);
                        }
                    })
                    .start();
            mLiveStartIv.setSelected(false);
        }
    }

    /**
     * 开始视频直播
     */
    @OnClick(R.id.live_start_video)
    public void onStartLiveVideo() {
        LivePrepareActivity.start(getActivity());
    }

    /**
     * 开始图文直播
     */
    @OnClick(R.id.live_start_image_text)
    public void onStartLiveImageText() {
        mLiveStartImageTextIv.setEnabled(false);
        // 判断
        if (EVApplication.isLogin()) {
            checkoutImageTextLiveRoom();
        } else {
            LoginActivity.start(getContext());
            mLiveStartImageTextIv.setEnabled(true);
        }
    }

    /**
     * 图文直播间
     */
    private void checkoutImageTextLiveRoom() {
        HooviewApiHelper.getInstance().checkImageTextLiveRoom(EVApplication.getUser().getName(), EVApplication.getUser().getNickname(), EVApplication.getUser().getImuser(), new MyRequestCallBack<CheckImageTextLiveModel>() {
            @Override
            public void onSuccess(CheckImageTextLiveModel result) {
                if (result != null) {
                    MyImageTextLiveRoomActivity.start(getActivity(), result);
                }
                mLiveStartImageTextIv.setEnabled(true);
            }

            @Override
            public void onFailure(String msg) {
                mLiveStartImageTextIv.setEnabled(true);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                mLiveStartImageTextIv.setEnabled(true);
            }
        });
    }

    private class LivePagerAdapter extends FragmentPagerAdapter {

        public LivePagerAdapter(FragmentManager fm) {
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

    public ViewPager getViewPager() {
        return mTabViewpager;
    }
}
