/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.live.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;

import com.easyvaas.sdk.player.EVPlayer;
import com.easyvaas.sdk.player.PlayerConstants;
import com.easyvaas.sdk.player.base.EVPlayerBase;
import com.easyvaas.sdk.player.base.EVPlayerParameter;
import com.easyvaas.sdk.player.base.EVVideoView;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.live.chat.IChatHelper;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DialogUtil;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.NetworkUtil;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.UserUtil;
import com.hooview.app.view.MediaController;

import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * 播放直播
 */
public class PlayerActivity extends LiveRoomBaseActivity {
    private static final String TAG = "PlayerActivity";

    private MediaController mMediaController;
    private EVVideoView mVideoView;
    private EVPlayer mEVPlayer;
    private String mPassword;

    private Dialog mPayInfoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoId = getIntent().getStringExtra(Constants.EXTRA_KEY_VIDEO_ID);
        mIsPlayLive = getIntent().getBooleanExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, false);
        mPassword = getIntent().getStringExtra(SetPasswordActivity.EXTRA_KEY_PASSWORD);
        if (TextUtils.isEmpty(mVideoId)) {
            finish();
        }

        initPlayer();
        loadVideoInfo();
        showInitLoadingView();
    }

    //加载VideoInfo信息
    private void loadVideoInfo() {
        ApiHelper.getInstance().getWatchVideo(mVideoId, mPassword,
                new MyRequestCallBack<VideoEntity>() {
                    @Override
                    public void onSuccess(VideoEntity result) {
                        if (result != null) {
                            mCurrentVideo = result;
                            if (result.getPermission() == ApiConstant.VALUE_LIVE_PERMISSION_PAY
                                    && result.getPrice() > 0) {
                                videoPay(result.getVid());
                            } else {
                                updateVideoInfo(result);
                                startWatchLive();
                            }
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        loadVideoInfoError(errorInfo);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        loadVideoInfoError(msg);
                    }
                });
    }

    private void videoPay(final String vid) {
        if (mPayInfoDialog == null) {
            mPayInfoDialog = DialogUtil.getButtonsDialog(this, getString(com.hooview.app.R.string.content_video_pay_info,
                    mCurrentVideo.getPrice()), false, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    ApiHelper.getInstance().videoPay(vid, new MyRequestCallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            updateVideoInfo(mCurrentVideo);
                            startWatchLive();
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
                            dialog.dismiss();
                            if (ApiConstant.E_PAYMENT_NO.equals(errorInfo)) {
                                SingleToast.show(getApplicationContext(),
                                        com.hooview.app.R.string.msg_gift_buy_failed_e_coin_not_enough);
                            }
                            finish();
                        }

                        @Override
                        public void onFailure(String msg) {
                            finish();
                        }
                    });
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
        mPayInfoDialog.show();
    }


    //在开始直播前的设置
    public void updateVideoInfo(VideoEntity result) {
        chatServerInit(true);
        String currentUserId = mPref.getUserNumber();
        mPlayerBottomShareBtn.setVisibility(View.VISIBLE);
        UserUtil.showUserPhoto(getApplicationContext(), result.getLogourl(), mUserLogoIv);
        mUserLogoIv.setIsVip(result.getVip());
        mNicknameTv.setText(result.getNickname());
        updateWatchLikeCounts(result.getWatching_count());
        mVideoTitleTv.setText(result.getTitle());
        if (result.getLiving() == VideoEntity.IS_LIVING) {
            mIsPlayLive = true;
            findViewById(com.hooview.app.R.id.player_bottom_progress_btn).setVisibility(View.GONE);
            mDurationTv.setText(com.hooview.app.R.string.is_living);
        } else {
            mIsPlayLive = false;
            mDurationTv.setText(com.hooview.app.R.string.is_playback);
            findViewById(com.hooview.app.R.id.player_bottom_comment_btn).setVisibility(View.GONE);
        }

        //Gone
        findViewById(com.hooview.app.R.id.live_gift_iv).setVisibility(View.INVISIBLE);
        findViewById(com.hooview.app.R.id.live_gift_iv).setEnabled(false);

        if (result.getName().equals(currentUserId)) {
            mBubbleView.setEnabled(false);
        }
        showAllInfoViews(false);
    }

    public void loadVideoInfoError(String errorInfo) {
        Logger.d(PlayerActivity.class, "error result: " + errorInfo);
        if (isFinishing()) {
            return;
        }
        if (ApiConstant.E_VIDEO_NOT_EXISTS.equals(errorInfo)) {
            DialogUtil.getOneButtonDialog(PlayerActivity.this,
                    getResources().getString(com.hooview.app.R.string.not_video_dialog), false,
                    false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }

    private void initPlayer() {
        if (mVideoView == null) {
            ViewStub viewStub = (ViewStub) findViewById(com.hooview.app.R.id.video_view_stub);
            mVideoView = (EVVideoView) viewStub.inflate().findViewById(com.hooview.app.R.id.player_surface_view);
        }

        mEVPlayer = new EVPlayer(this);
        EVPlayerParameter.Builder builder = new EVPlayerParameter.Builder();

        //当前是否是直播
        builder.setLive(mIsPlayLive);

        //参数
        mEVPlayer.setParameter(builder.build());

        //设置View
        mEVPlayer.setVideoView(mVideoView);

        //设置准备。完成，信息，错误的监听
        mEVPlayer.setOnPreparedListener(mOnPreparedListener);
        mEVPlayer.setOnCompletionListener(mOnCompletionListener);
        mEVPlayer.setOnInfoListener(mOnInfoListener);
        mEVPlayer.setOnErrorListener(mOnErrorListener);
        mEVPlayer.onCreate();

        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(mVideoView);

        //给播放设置Player
        mEVPlayer.setMediaController(mMediaController);
    }

    private EVPlayerBase.OnPreparedListener mOnPreparedListener = new EVPlayerBase.OnPreparedListener() {
        @Override public boolean onPrepared() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideInitLoadingView();
                }
            });
            return true;
        }
    };

    private EVPlayerBase.OnCompletionListener mOnCompletionListener = new EVPlayerBase.OnCompletionListener() {
        @Override public boolean onCompletion() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopPlayerAndShowEndView();
                }
            });
            return true;
        }
    };

    private EVPlayerBase.OnInfoListener mOnInfoListener = new EVPlayerBase.OnInfoListener() {
        @Override public boolean onInfo(int what, int extra) {
            switch (what) {
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                showLoadingDialog(com.hooview.app.R.string.loading_data, true, true);
                            }
                        }
                    });
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            mHandler.sendEmptyMessage(MSG_REFRESH_START_TIME);
                        }
                    });
                    break;
            }
            return true;
        }
    };

    private EVPlayerBase.OnErrorListener mOnErrorListener = new EVPlayerBase.OnErrorListener() {
        @Override public boolean onError(int what, int extra) {
            switch (what) {
                case PlayerConstants.EV_PLAYER_ERROR_SDK_INIT:
                    showToastOnUiThread(com.hooview.app.R.string.msg_sdk_init_error);
                    break;
                default:
                    //                    runOnUiThread(new Runnable() {
                    //                        @Override
                    //                        public void run() {
                    //                            DialogUtil.getOneButtonDialog(PlayerActivity.this,
                    //                                    getString(R.string.msg_play_error), false, false,
                    //                                    new DialogInterface.OnClickListener() {
                    //                                        @Override
                    //                                        public void onClick(DialogInterface dialog, int which) {
                    //                                            dialog.dismiss();
                    //                                            dismissLoadingDialog();
                    //                                            if (null != mEVPlayer) {
                    //                                                mEVPlayer.onStop();
                    //                                            }
                    //                                            finish();
                    //                                        }
                    //                                    }).show();
                    //                        }
                    //                    });
                    hideInitLoadingView();
                    dismissLoadingDialog();
                    break;
            }

            return true;
        }
    };


    //SDK初始化失败的弹框
    private void showToastOnUiThread(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                SingleToast.show(getApplicationContext(), resId);
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        toggleProgressBar(false);
        return super.onTouchEvent(event);
    }


    //初始化交互的View
    protected void initView() {
        findViewById(com.hooview.app.R.id.player_bottom_action_bar).setVisibility(View.VISIBLE);
    }

    @Override
    public void onStatusUpdate(int status) {
        super.onStatusUpdate(status);
        if (status == IChatHelper.LIVE_STOP) {
            stopPlayerAndShowEndView();
        }
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_REFRESH_START_TIME:
                if (mIsPlayLive) {
                    mDurationTv.setText(com.hooview.app.R.string.is_living);
                } else {
                    mDurationTv.setText(com.hooview.app.R.string.is_playback);
                }
                break;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // TODO Not work, need to improve.
        String vid = intent.getStringExtra(Constants.EXTRA_KEY_VIDEO_ID);
        if (mVideoId.equals(vid)) {
        } else if (!TextUtils.isEmpty(vid)) {
            resetChatData();
            startWatchLive();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != mEVPlayer) {
            mEVPlayer.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mEVPlayer) {
            mEVPlayer.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mEVPlayer) {
            mEVPlayer.onStop();
        }
    }

    @Override
    protected void checkShowGuide() {
        super.checkShowGuide();
    }

    @Override
    protected boolean isFlipping() {
        return true;
    }

    @Override
    public void onClick(final View v) {
        super.onClick(v);
        switch (v.getId()) {
            case com.hooview.app.R.id.player_bottom_progress_btn:
                toggleProgressBar(true);
                break;
            case com.hooview.app.R.id.player_report_btn:
                hideUserPopupView();
                DialogUtil.showReportVideoDialog(PlayerActivity.this, mCurrentVideo.getVid());
                break;
            case com.hooview.app.R.id.live_gift_iv:
                showGiftToolsBar();
                break;
            case com.hooview.app.R.id.burst_iv:
                mExpressionGiftLayout.checkBurst();
                break;
        }

    }

    @Override
    protected void toggleProgressBar(boolean show) {
        if (mMediaController == null) {
            return;
        }
        View actionBar = findViewById(com.hooview.app.R.id.player_bottom_action_bar);
        if (show) {
            actionBar.setVisibility(View.INVISIBLE);
            mMediaController.show();
        } else {
            actionBar.setVisibility(View.VISIBLE);
            mMediaController.hide();
        }
    }

    @Override
    public void onBackPressed() {
        if (mMediaController.isShowing()) {
            toggleProgressBar(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mEVPlayer) {
            mEVPlayer.onDestroy();
        }
    }

    //开始直播
    private void startWatchLive() {
        if (TextUtils.isEmpty(mVideoId)) {
            SingleToast.show(this, com.hooview.app.R.string.msg_video_url_null);
            return;
        }
        mEVPlayer.watchStart(mCurrentVideo.getUri());

    }

    //停止直播的页面
    private synchronized void stopPlayerAndShowEndView() {
        //socket io callback living end and player callback completion
        if (isPlayEndViewShow()) {
            return;
        }
        mEVPlayer.watchStop();
        dismissLoadingDialog();
        mCurrentVideo.setWatch_count(mWatchCount);
        mCurrentVideo.setLike_count(mLikeCount);
        mCurrentVideo.setComment_count(mCommentCount);
        if (mMediaController != null) {
            mMediaController.hide();
        }
        showPlayEndView(mCurrentVideo);
        chatServerDestroy();
    }

    //网络开小车中的Message
    private void showInitLoadingView() {
        if (!mLoadingView.isShown()) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        Message msg = mHandler.obtainMessage(MSG_REFRESH_LIVE_NET_SPEED, NetworkUtil.getTotalRxBytes());
        msg.arg1 = MSG_ARG_1_FIRST_REFRESH_NET_SPEED;
        mHandler.sendMessageDelayed(msg, 1000);
    }

    private void hideInitLoadingView() {
        checkShowGuide();
        if (mLoadingView != null && mLoadingView.isShown()) {
            mLoadingView.setVisibility(View.GONE);
        }
        mHandler.removeMessages(MSG_REFRESH_LIVE_NET_SPEED);
    }
}
