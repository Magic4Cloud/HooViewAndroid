package com.easyvaas.elapp.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hooview.app.R;

public class PlayerStateView extends FrameLayout {
    private RelativeLayout mRlLoading;
    private ImageView mIvLoading;
    private RelativeLayout mRlEnd;
    private ImageView mIvIconEnd;
    private RelativeLayout mRlNetError;
    private ImageView mIvIconError;
    private RotateAnimation rotateAnimation;
    private TextView mTvEndPrompt;

    public PlayerStateView(Context context) {
        this(context, null);
    }

    public PlayerStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.layout_play_state_container, this);
        mRlLoading = (RelativeLayout) findViewById(R.id.rl_loading);
        mIvLoading = (ImageView) findViewById(R.id.iv_loading);
        mRlEnd = (RelativeLayout) findViewById(R.id.rl_end);
        mIvIconEnd = (ImageView) findViewById(R.id.iv_icon_end);
        mRlNetError = (RelativeLayout) findViewById(R.id.rl_net_error);
        mIvIconError = (ImageView) findViewById(R.id.iv_icon_error);
        mTvEndPrompt = (TextView) findViewById(R.id.tv_end_prompt);
    }

    public void setLoadingPrompt(String prompt) {

    }

    public void showLoadingView() {
        mRlLoading.setVisibility(VISIBLE);
        mRlEnd.setVisibility(GONE);
        mRlNetError.setVisibility(GONE);
        if (rotateAnimation == null) {
            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setRepeatCount(Integer.MAX_VALUE);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setDuration(2000);
        }
        mIvLoading.startAnimation(rotateAnimation);
    }

    public void hideLoadingView() {
        mRlLoading.setVisibility(GONE);
        mIvLoading.clearAnimation();
    }

    public void showEndView() {
        mRlEnd.setVisibility(VISIBLE);
        mRlLoading.setVisibility(GONE);
        mRlNetError.setVisibility(GONE);
    }

    public void showEndView(String prompt) {
        mRlEnd.setVisibility(VISIBLE);
        mRlLoading.setVisibility(GONE);
        mRlNetError.setVisibility(GONE);
        mTvEndPrompt.setText(prompt);
    }


    public void hideEndView() {
        mRlLoading.setVisibility(GONE);
        mRlEnd.setVisibility(GONE);
        mRlNetError.setVisibility(GONE);
    }

    public void showNetError() {
        mRlEnd.setVisibility(GONE);
        mRlLoading.setVisibility(GONE);
        mRlNetError.setVisibility(VISIBLE);
    }

    public void hideNetError() {
        mRlLoading.setVisibility(GONE);
        mRlEnd.setVisibility(GONE);
        mRlNetError.setVisibility(GONE);
    }

}
