/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.view;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;

import com.easyvaas.common.emoji.utils.FaceConversionUtil;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.chat.ChatBarrage;
import com.easyvaas.elapp.utils.UserUtil;


public class BarrageAnimationView extends FrameLayout {
    private Context mContext;

    private FrameLayout mBarrageRl;
    private TextView mBarrageNicknameTv;
    private TextView mBarrageContentTv;
    private MyUserPhoto mBarrageUserIv;
    private FrameLayout mBarrageTwoRl;
    private TextView mBarrageNicknameTwoTv;
    private TextView mBarrageContentTwoTv;
    private MyUserPhoto mBarrageUserTwoIv;
    private FrameLayout mBarrageThreeRl;
    private TextView mBarrageNicknameThreeTv;
    private TextView mBarrageContentThreeTv;
    private MyUserPhoto mBarrageUserThreeIv;
    private FrameLayout mBarrageFourRl;
    private TextView mBarrageNicknameFourTv;
    private TextView mBarrageContentFourTv;
    private MyUserPhoto mBarrageUserFourIv;

    protected MyHandler mHandler;
    private OnJoinAnimationListener mAnimationCallBack;
    private OnJoinAnimationListener2 mAnimationCallBack2;
    private OnJoinAnimationListener3 mAnimationCallBack3;
    private OnJoinAnimationListener4 mAnimationCallBack4;

    private int mCurrentJoinCount;
    private int mCurrentJoinCount2;
    private int mCurrentJoinCount3;
    private int mCurrentJoinCount4;
    private int curIndex;
    private int curIndex2;
    private int curIndex3;
    private int curIndex4;
    private Runnable mShowJoinRunnable;
    private Runnable mShowJoinRunnable2;
    private Runnable mShowJoinRunnable3;
    private Runnable mShowJoinRunnable4;
    private List<ChatBarrage> mWatchingUsers = new ArrayList<>();
    private List<ChatBarrage> mWatchingUsers2 = new ArrayList<>();
    private List<ChatBarrage> mWatchingUsers3 = new ArrayList<>();
    private List<ChatBarrage> mWatchingUsers4 = new ArrayList<>();

    private int mLastBarrageViewIndex;

    protected static class MyHandler extends Handler {
        private SoftReference<BarrageAnimationView> softReference;

        public MyHandler(BarrageAnimationView barrageAnimationView) {
            softReference = new SoftReference<>(barrageAnimationView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BarrageAnimationView barrageAnimationView = softReference.get();
            if (barrageAnimationView == null) {
                return;
            }
            switch (msg.what) {
                case 100:
                    ViewPropertyAnimator.animate(barrageAnimationView.mBarrageRl).setDuration(10);
                    ViewPropertyAnimator.animate(barrageAnimationView.mBarrageRl).x(1000);
                    barrageAnimationView.mHandler.sendEmptyMessageDelayed(101, 20);
                    break;
                case 101:
                    barrageAnimationView.mBarrageRl.setVisibility(View.GONE);
                    barrageAnimationView.mAnimationCallBack.onAnimationComplete();
                    break;
                case 200:
                    ViewPropertyAnimator.animate(barrageAnimationView.mBarrageTwoRl).setDuration(10);
                    ViewPropertyAnimator.animate(barrageAnimationView.mBarrageTwoRl).x(1000);
                    barrageAnimationView.mHandler.sendEmptyMessageDelayed(201, 20);
                    break;
                case 201:
                    barrageAnimationView.mBarrageTwoRl.setVisibility(View.GONE);
                    barrageAnimationView.mAnimationCallBack2.onAnimationComplete2();
                    break;
                case 300:
                    ViewPropertyAnimator.animate(barrageAnimationView.mBarrageThreeRl).setDuration(10);
                    ViewPropertyAnimator.animate(barrageAnimationView.mBarrageThreeRl).x(1000);
                    barrageAnimationView.mHandler.sendEmptyMessageDelayed(301, 20);
                    break;
                case 301:
                    barrageAnimationView.mBarrageThreeRl.setVisibility(View.GONE);
                    barrageAnimationView.mAnimationCallBack3.onAnimationComplete3();
                    break;
                case 400:
                    ViewPropertyAnimator.animate(barrageAnimationView.mBarrageFourRl).setDuration(10);
                    ViewPropertyAnimator.animate(barrageAnimationView.mBarrageFourRl).x(1000);
                    barrageAnimationView.mHandler.sendEmptyMessageDelayed(401, 20);
                    break;
                case 401:
                    barrageAnimationView.mBarrageFourRl.setVisibility(View.GONE);
                    barrageAnimationView.mAnimationCallBack4.onAnimationComplete4();
                    break;

            }

        }
    }

    public BarrageAnimationView(Context context) {
        super(context);
        init(context);
    }

    public BarrageAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BarrageAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BarrageAnimationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mHandler = new MyHandler(this);
        View barrageAnimationView = LayoutInflater.from(context)
                .inflate(R.layout.view_barrage_animation_view, null);
        mBarrageRl = (FrameLayout) barrageAnimationView.findViewById(R.id.barrage_rl);
        mBarrageNicknameTv = (TextView) barrageAnimationView.findViewById(R.id.barrage_username_tv);
        mBarrageContentTv = (TextView) barrageAnimationView.findViewById(R.id.barrage_content_tv);
        mBarrageUserIv = (MyUserPhoto) barrageAnimationView.findViewById(R.id.barrage_user_iv);

        mBarrageTwoRl = (FrameLayout) barrageAnimationView.findViewById(R.id.barrage_rl2);
        mBarrageNicknameTwoTv = (TextView) barrageAnimationView.findViewById(R.id.barrage_username_tv2);
        mBarrageContentTwoTv = (TextView) barrageAnimationView.findViewById(R.id.barrage_content_tv2);
        mBarrageUserTwoIv = (MyUserPhoto) barrageAnimationView.findViewById(R.id.barrage_user_iv2);

        mBarrageThreeRl = (FrameLayout) barrageAnimationView.findViewById(R.id.barrage_rl3);
        mBarrageNicknameThreeTv = (TextView) barrageAnimationView.findViewById(R.id.barrage_username_tv3);
        mBarrageContentThreeTv = (TextView) barrageAnimationView.findViewById(R.id.barrage_content_tv3);
        mBarrageUserThreeIv = (MyUserPhoto) barrageAnimationView.findViewById(R.id.barrage_user_iv3);

        mBarrageFourRl = (FrameLayout) barrageAnimationView.findViewById(R.id.barrage_rl4);
        mBarrageNicknameFourTv = (TextView) barrageAnimationView.findViewById(R.id.barrage_username_tv4);
        mBarrageContentFourTv = (TextView) barrageAnimationView.findViewById(R.id.barrage_content_tv4);
        mBarrageUserFourIv = (MyUserPhoto) barrageAnimationView.findViewById(R.id.barrage_user_iv4);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        addView(barrageAnimationView);

        setGiftAnimationListener();
    }

    private void setGiftAnimationListener() {
        mAnimationCallBack = getJoinAnimationViewListener();
        mAnimationCallBack2 = getJoinAnimationViewListener2();
        mAnimationCallBack3 = getJoinAnimationViewListener3();
        mAnimationCallBack4 = getJoinAnimationViewListener4();
    }

    private void executeZipGiftAnimation(String nickname, String logo, String content) {
        mBarrageRl.setVisibility(View.VISIBLE);
        mBarrageNicknameTv.setText(nickname);
        assembleContent(mBarrageContentTv, content);
        UserUtil.showUserPhoto(mContext, logo, mBarrageUserIv);
        ViewPropertyAnimator.animate(mBarrageRl).setDuration(8000);
        ViewPropertyAnimator.animate(mBarrageRl).x(-1000);
        mHandler.sendEmptyMessageDelayed(100, 8000);
    }

    private void executeZipGiftAnimation2(String nickname, String logo, String content) {
        mBarrageTwoRl.setVisibility(View.VISIBLE);
        mBarrageNicknameTwoTv.setText(nickname);
        assembleContent(mBarrageContentTwoTv, content);
        UserUtil.showUserPhoto(mContext, logo, mBarrageUserTwoIv);
        ViewPropertyAnimator.animate(mBarrageTwoRl).setDuration(8000);
        ViewPropertyAnimator.animate(mBarrageTwoRl).x(-1000);
        mHandler.sendEmptyMessageDelayed(200, 8000);
    }

    private void executeZipGiftAnimation3(String nickname, String logo, String content) {
        mBarrageThreeRl.setVisibility(View.VISIBLE);
        mBarrageNicknameThreeTv.setText(nickname);
        assembleContent(mBarrageContentThreeTv, content);
        UserUtil.showUserPhoto(mContext, logo, mBarrageUserThreeIv);
        ViewPropertyAnimator.animate(mBarrageThreeRl).setDuration(8000);
        ViewPropertyAnimator.animate(mBarrageThreeRl).x(-1000);
        mHandler.sendEmptyMessageDelayed(300, 8000);
    }

    private void executeZipGiftAnimation4(String nickname, String logo, String content) {
        mBarrageFourRl.setVisibility(View.VISIBLE);
        mBarrageNicknameFourTv.setText(nickname);
        assembleContent(mBarrageContentFourTv, content);
        UserUtil.showUserPhoto(mContext, logo, mBarrageUserFourIv);
        ViewPropertyAnimator.animate(mBarrageFourRl).setDuration(8000);
        ViewPropertyAnimator.animate(mBarrageFourRl).x(-1000);
        mHandler.sendEmptyMessageDelayed(400, 8000);
    }

    private void assembleContent(TextView contentTv, String content) {
        SpannableString spannableString = new SpannableString(content);
        FaceConversionUtil.getInstace().getExpressionString(mContext, spannableString);
        contentTv.setText(spannableString);
    }

    public void execute(List<ChatBarrage> barrageEntities) {
        for (ChatBarrage entity : barrageEntities) {
            if (mLastBarrageViewIndex == 0) {
                mLastBarrageViewIndex = 1;
                startBarrageAnimation(entity);
            } else if (mLastBarrageViewIndex == 1) {
                mLastBarrageViewIndex = 2;
                startBarrageAnimation2(entity);
            } else if (mLastBarrageViewIndex == 2) {
                mLastBarrageViewIndex = 3;
                startBarrageAnimation3(entity);
            } else if (mLastBarrageViewIndex == 3) {
                mLastBarrageViewIndex = 0;
                startBarrageAnimation4(entity);
            }
        }
    }

    private void startBarrageAnimation(ChatBarrage messageEntity) {
        try {
            mWatchingUsers.add(messageEntity);
            if (mCurrentJoinCount == 0) {
                startJoinAnimation();
            }
            mCurrentJoinCount = mWatchingUsers.size();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBarrageAnimation2(ChatBarrage messageEntity) {
        try {
            mWatchingUsers2.add(messageEntity);
            if (mCurrentJoinCount2 == 0) {
                startJoinAnimation2();
            }
            mCurrentJoinCount2 = mWatchingUsers2.size();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBarrageAnimation3(ChatBarrage messageEntity) {
        try {
            mWatchingUsers3.add(messageEntity);
            if (mCurrentJoinCount3 == 0) {
                startJoinAnimation3();
            }
            mCurrentJoinCount3 = mWatchingUsers3.size();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBarrageAnimation4(ChatBarrage messageEntity) {
        try {
            mWatchingUsers4.add(messageEntity);
            if (mCurrentJoinCount4 == 0) {
                startJoinAnimation4();
            }
            mCurrentJoinCount4 = mWatchingUsers4.size();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startJoinAnimation() {
        mShowJoinRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (curIndex < mWatchingUsers.size() && mWatchingUsers.get(curIndex) != null) {
                        executeZipGiftAnimation(mWatchingUsers.get(curIndex).getNickname(),
                                mWatchingUsers.get(curIndex).getLogo(),
                                mWatchingUsers.get(curIndex).getContent());
                        curIndex++;
                    } else {
                        mAnimationCallBack.onAnimationComplete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        post(mShowJoinRunnable);
    }

    private void startJoinAnimation2() {
        mShowJoinRunnable2 = new Runnable() {
            @Override
            public void run() {
                try {
                    if (curIndex2 < mWatchingUsers2.size() && mWatchingUsers2.get(curIndex2) != null) {
                        executeZipGiftAnimation2(mWatchingUsers2.get(curIndex2).getNickname(),
                                mWatchingUsers2.get(curIndex2).getLogo(),
                                mWatchingUsers2.get(curIndex2).getContent());
                        curIndex2++;
                    } else {
                        mAnimationCallBack2.onAnimationComplete2();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        post(mShowJoinRunnable2);
    }

    private void startJoinAnimation3() {
        mShowJoinRunnable3 = new Runnable() {
            @Override
            public void run() {
                try {
                    if (curIndex3 < mWatchingUsers3.size() && mWatchingUsers3.get(curIndex3) != null) {
                        executeZipGiftAnimation3(mWatchingUsers3.get(curIndex3).getNickname(),
                                mWatchingUsers3.get(curIndex3).getLogo(),
                                mWatchingUsers3.get(curIndex3).getContent());
                        curIndex3++;
                    } else {
                        mAnimationCallBack3.onAnimationComplete3();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        post(mShowJoinRunnable3);
    }

    private void startJoinAnimation4() {
        mShowJoinRunnable4 = new Runnable() {
            @Override
            public void run() {
                try {
                    if (curIndex4 < mWatchingUsers4.size() && mWatchingUsers4.get(curIndex4) != null) {
                        executeZipGiftAnimation4(mWatchingUsers4.get(curIndex4).getNickname(),
                                mWatchingUsers4.get(curIndex4).getLogo(),
                                mWatchingUsers4.get(curIndex4).getContent());
                        curIndex4++;
                    } else {
                        mAnimationCallBack4.onAnimationComplete4();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        post(mShowJoinRunnable4);
    }

    public interface OnJoinAnimationListener {
        void onAnimationComplete();
    }

    public interface OnJoinAnimationListener2 {
        void onAnimationComplete2();
    }

    public interface OnJoinAnimationListener3 {
        void onAnimationComplete3();
    }

    public interface OnJoinAnimationListener4 {
        void onAnimationComplete4();
    }

    public OnJoinAnimationListener getJoinAnimationViewListener() {
        return mOnJoinAnimationListener;
    }

    public OnJoinAnimationListener2 getJoinAnimationViewListener2() {
        return mOnJoinAnimationListener2;
    }

    public OnJoinAnimationListener3 getJoinAnimationViewListener3() {
        return mOnJoinAnimationListener3;
    }

    public OnJoinAnimationListener4 getJoinAnimationViewListener4() {
        return mOnJoinAnimationListener4;
    }

    private OnJoinAnimationListener mOnJoinAnimationListener = new OnJoinAnimationListener() {

        @Override
        public void onAnimationComplete() {
            mCurrentJoinCount--;
            if (mCurrentJoinCount > 0) {
                postDelayed(mShowJoinRunnable, 200);
            } else {
                mCurrentJoinCount = 0;
            }
        }
    };
    private OnJoinAnimationListener2 mOnJoinAnimationListener2 = new OnJoinAnimationListener2() {

        @Override
        public void onAnimationComplete2() {
            mCurrentJoinCount2--;
            if (mCurrentJoinCount2 > 0) {
                postDelayed(mShowJoinRunnable2, 200);
            } else {
                mCurrentJoinCount2 = 0;
            }
        }
    };
    private OnJoinAnimationListener3 mOnJoinAnimationListener3 = new OnJoinAnimationListener3() {

        @Override
        public void onAnimationComplete3() {
            mCurrentJoinCount3--;
            if (mCurrentJoinCount3 > 0) {
                postDelayed(mShowJoinRunnable3, 200);
            } else {
                mCurrentJoinCount3 = 0;
            }
        }
    };
    private OnJoinAnimationListener4 mOnJoinAnimationListener4 = new OnJoinAnimationListener4() {

        @Override
        public void onAnimationComplete4() {
            mCurrentJoinCount4--;
            if (mCurrentJoinCount4 > 0) {
                postDelayed(mShowJoinRunnable4, 200);
            } else {
                mCurrentJoinCount4 = 0;
            }
        }
    };
}

