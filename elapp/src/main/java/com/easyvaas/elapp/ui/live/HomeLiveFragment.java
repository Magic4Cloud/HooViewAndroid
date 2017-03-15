package com.easyvaas.elapp.ui.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hooview.app.R;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.imageTextLive.CheckImageTextLiveModel;
import com.easyvaas.elapp.bean.imageTextLive.ImageTextLiveRoomModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseFragment;
import com.easyvaas.elapp.ui.search.GlobalSearchActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.ViewUtil;
import com.easyvaas.elapp.view.ImageTextLiveInputView;

import java.util.ArrayList;
import java.util.List;

public class HomeLiveFragment extends BaseFragment implements View.OnClickListener {
    public static final long ANIM_DURATION = 500;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyAdapter myAdapter;
    private ImageView mStartLive;
    private ImageView mIvStartVideLive;
    private ImageView mIvStartImageText;
    private RelativeLayout mRlLiveBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_home_live, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mStartLive = (ImageView) view.findViewById(R.id.iv_live_start);
        view.findViewById(R.id.iv_live_start).setOnClickListener(this);
        view.findViewById(R.id.iv_search).setOnClickListener(this);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mRlLiveBtn = (RelativeLayout) view.findViewById(R.id.rl_live);
        mIvStartVideLive = (ImageView) view.findViewById(R.id.iv_start_video_live);
        mIvStartImageText = (ImageView) view.findViewById(R.id.iv_start_image_text_live);
        mTabLayout.setupWithViewPager(mViewPager);
        mIvStartVideLive.setOnClickListener(this);
        mIvStartImageText.setOnClickListener(this);
        myAdapter = new MyAdapter(getChildFragmentManager());
        mViewPager.setAdapter(myAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.getInstance(getContext()).isLogin() && EVApplication.isLogin()) {
            if (EVApplication.getUser().getVip() == 1) {
                mRlLiveBtn.setVisibility(View.VISIBLE);
            } else {
                mRlLiveBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            mRlLiveBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_live_start:
                onLiveStartBtnClick();
                break;
            case R.id.iv_start_image_text_live:
                mIvStartImageText.setEnabled(false);
                gotoImageTextLiveRoom();
                break;
            case R.id.iv_start_video_live:
                LivePrepareActivity.start(getActivity());
                break;
            case R.id.iv_search:
                GlobalSearchActivity.start(getActivity());
                break;
        }
    }

    private void gotoImageTextLiveRoom() {
        if (EVApplication.isLogin()) {
            checkoutImageTextLiveRoom();
        } else {
            LoginActivity.start(getContext());
            mIvStartImageText.setEnabled(true);
        }
    }

    private void checkoutImageTextLiveRoom() {
        HooviewApiHelper.getInstance().checkImageTextLiveRoom(EVApplication.getUser().getName(), EVApplication.getUser().getNickname(), EVApplication.getUser().getImuser(), new MyRequestCallBack<CheckImageTextLiveModel>() {
            @Override
            public void onSuccess(CheckImageTextLiveModel result) {
                if (result != null) {
                    MyImageTextLiveRoomActivity.start(getActivity(), result);
                }
                mIvStartImageText.setEnabled(true);
            }

            @Override
            public void onFailure(String msg) {
                mIvStartImageText.setEnabled(true);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                mIvStartImageText.setEnabled(true);
            }
        });
    }


    public class MyAdapter extends FragmentPagerAdapter {
        private String[] mTabsTitle;
        List<Fragment> fragments;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mTabsTitle = getResources().getStringArray(R.array.live_tabs);
            fragments = new ArrayList<>();
            fragments.add(new VideoLiveListFragment());
            fragments.add(new ImageTextLiveListFragment());
            fragments.add(new GoodsVideoFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }

    private void onLiveStartBtnClick() {
        final int translate = (int) ViewUtil.dp2Px(getContext().getApplicationContext(), 64);
        if (!mStartLive.isSelected()) {
            mStartLive.animate().rotationBy(0.5f).rotation(180).setDuration(ANIM_DURATION)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            mStartLive.setImageResource(R.drawable.btn_video_cancel_n);
                            mStartLive.setEnabled(false);
                        }
                    })
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mStartLive.setEnabled(true);
                        }
                    }).start();
            mIvStartVideLive.setVisibility(View.VISIBLE);
            mIvStartImageText.setVisibility(View.VISIBLE);
            mIvStartVideLive.animate().rotationBy(0.5f).rotation(360).xBy(-translate).setDuration(ANIM_DURATION).setInterpolator(new DecelerateInterpolator()).alpha(1).start();
            mIvStartImageText.animate().rotationBy(0.5f).rotation(360).yBy(-translate).setDuration(ANIM_DURATION).setInterpolator(new DecelerateInterpolator()).alpha(1).start();
            mStartLive.setSelected(true);
        } else {
            mStartLive.animate().rotationBy(0.5f).rotation(0).setDuration(ANIM_DURATION)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            mStartLive.setEnabled(false);
                        }
                    })
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mStartLive.setImageResource(R.drawable.btn_launch_n);
                            mStartLive.setEnabled(true);
                        }
                    }).start();
            mIvStartVideLive.animate().rotationBy(0.5f).rotation(0).xBy(translate).setDuration(ANIM_DURATION).alpha(0).setInterpolator(new AccelerateInterpolator())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mIvStartVideLive.setVisibility(View.INVISIBLE);
                        }
                    }).start();
            mIvStartImageText.animate().rotationBy(0.5f).rotation(0).yBy(translate).setDuration(ANIM_DURATION).alpha(0).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mIvStartImageText.setVisibility(View.INVISIBLE);
                }
            }).start();
            mStartLive.setSelected(false);
        }
    }
}
