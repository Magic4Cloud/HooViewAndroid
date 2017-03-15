/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.live.activity;

public class PlayerActivity extends LiveRoomBaseActivity {
//    private static final String TAG = "PlayerActivity";
//
//    private MediaController mMediaController;
//    private EVVideoView mVideoView;
//    private EVPlayer mEVPlayer;
//    private String mPassword;
//
//    private Dialog mPayInfoDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mVideoId = getIntent().getStringExtra(Constants.EXTRA_KEY_VIDEO_ID);
//        mIsPlayLive = getIntent().getBooleanExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, false);
//        mPassword = getIntent().getStringExtra(SetPasswordActivity.EXTRA_KEY_PASSWORD);
//        if (TextUtils.isEmpty(mVideoId)) {
//            finish();
//        }
//
//        initPlayer();
//        loadVideoInfo();
//        showInitLoadingView();
//    }
//
//    private void loadVideoInfo() {
//        ApiHelper.getInstance().getWatchVideo(mVideoId, mPassword,
//                new MyRequestCallBack<VideoEntity>() {
//                    @Override
//                    public void onSuccess(VideoEntity result) {
//                        if (result != null) {
//                            mCurrentVideo = result;
//                            if (result.getPermission() == ApiConstant.VALUE_LIVE_PERMISSION_PAY
//                                    && result.getPrice() > 0) {
//                                videoPay(result.getVid());
//                            } else {
//                                updateVideoInfo(result);
//                                startWatchLive();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(String errorInfo) {
//                        super.onError(errorInfo);
//                        loadVideoInfoError(errorInfo);
//                    }
//
//                    @Override
//                    public void onFailure(String msg) {
//                        RequestUtil.handleRequestFailed(msg);
//                        loadVideoInfoError(msg);
//                    }
//                });
//    }
//
//    private void videoPay(final String vid) {
//        if (mPayInfoDialog == null) {
//            mPayInfoDialog = DialogUtil.getButtonsDialog(this, getString(R.string.content_video_pay_info,
//                    mCurrentVideo.getPrice()), false, false, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(final DialogInterface dialog, int which) {
//                    ApiHelper.getInstance().videoPay(vid, new MyRequestCallBack<String>() {
//                        @Override
//                        public void onSuccess(String result) {
//                            updateVideoInfo(mCurrentVideo);
//                            startWatchLive();
//                            dialog.dismiss();
//                        }
//
//                        @Override
//                        public void onError(String errorInfo) {
//                            super.onError(errorInfo);
//                            dialog.dismiss();
//                            if (ApiConstant.E_PAYMENT_NO.equals(errorInfo)) {
//                                SingleToast.show(getApplicationContext(),
//                                        R.string.msg_gift_buy_failed_e_coin_not_enough);
//                            }
//                            finish();
//                        }
//
//                        @Override
//                        public void onFailure(String msg) {
//                            finish();
//                        }
//                    });
//                }
//            }, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    finish();
//                }
//            });
//        }
//        mPayInfoDialog.show();
//    }
//
//    public void updateVideoInfo(VideoEntity result) {
//        chatServerInit(true);
//        String currentUserId = mPref.getUserNumber();
//        mPlayerBottomShareBtn.setVisibility(View.VISIBLE);
//        UserUtil.showUserPhoto(getApplicationContext(), result.getLogourl(), mUserLogoIv);
//        mUserLogoIv.setIsVip(result.getVip());
//        mNicknameTv.setText(result.getNickname());
//        updateWatchLikeCounts(result.getWatching_count());
//        mVideoTitleTv.setText(result.getTitle());
//        if (result.getLiving() == VideoEntity.IS_LIVING) {
//            mIsPlayLive = true;
//            findViewById(R.id.player_bottom_progress_btn).setVisibility(View.GONE);
//            mDurationTv.setText(R.string.is_living);
//        } else {
//            mIsPlayLive = false;
//            mDurationTv.setText(R.string.is_playback);
//            findViewById(R.id.player_bottom_comment_btn).setVisibility(View.GONE);
//            findViewById(R.id.player_bottom_v_call_btn).setVisibility(View.GONE);
//        }
//        findViewById(R.id.live_gift_iv).setVisibility(View.VISIBLE);
//        if (result.getName().equals(currentUserId)) {
//            mBubbleView.setEnabled(false);
//        }
//        showAllInfoViews(false);
//    }
//
//    public void loadVideoInfoError(String errorInfo) {
//        Logger.d(PlayerActivity.class, "error result: " + errorInfo);
//        if (isFinishing()) {
//            return;
//        }
//        if (ApiConstant.E_VIDEO_NOT_EXISTS.equals(errorInfo)) {
//            DialogUtil.getOneButtonDialog(PlayerActivity.this,
//                    getResources().getString(R.string.not_video_dialog), false,
//                    false, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            finish();
//                        }
//                    })
//                    .show();
//        } else {
//            finish();
//        }
//    }
//
//    private void initPlayer() {
//        if (mVideoView == null) {
////            ViewStub viewStub = (ViewStub) findViewById(R.id.video_view_Rstub);
////            mVideoView = (EVVideoView) viewStub.inflate().findViewById(R.id.player_surface_view);
//        }
//
//        mEVPlayer = new EVPlayer(this);
//        EVPlayerParameter.Builder builder = new EVPlayerParameter.Builder();
//        builder.setLive(mIsPlayLive);
//        mEVPlayer.setParameter(builder.build());
//        mEVPlayer.setVideoView(mVideoView);
//        mEVPlayer.setOnPreparedListener(mOnPreparedListener);
//        mEVPlayer.setOnCompletionListener(mOnCompletionListener);
//        mEVPlayer.setOnInfoListener(mOnInfoListener);
//        mEVPlayer.setOnErrorListener(mOnErrorListener);
//        mEVPlayer.onCreate();
//        mMediaController = new MediaController(this);
//        mMediaController.setAnchorView(mVideoView);
//        mEVPlayer.setMediaController(mMediaController);
//
//        VideoCallHelper.getInstance().initAssistant(this,
//                (FrameLayout) findViewById(R.id.v_call_big_container),
//                (FrameLayout) findViewById(R.id.v_call_small_container),
//                new VideoCallHelper.OnVideoCallListener() {
//                    @Override
//                    public void onJoin() {
//                        mEVPlayer.onDestroy();
//                        mVideoView.setVisibility(View.GONE);
//                        Logger.d(TAG, "OnVideoCallListener onJoin ...");
//                    }
//
//                    @Override
//                    public void onLeave() {
//                        mEVPlayer.onCreate();
//                        startWatchLive();
//                        mVideoView.setVisibility(View.VISIBLE);
//                        Logger.d(TAG, "OnVideoCallListener onLeave ...");
//                    }
//                });
//    }
//
//    private EVPlayerBase.OnPreparedListener mOnPreparedListener = new EVPlayerBase.OnPreparedListener() {
//        @Override public boolean onPrepared() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    hideInitLoadingView();
//                }
//            });
//            return true;
//        }
//    };
//
//    private EVPlayerBase.OnCompletionListener mOnCompletionListener = new EVPlayerBase.OnCompletionListener() {
//        @Override public boolean onCompletion() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    stopPlayerAndShowEndView();
//                }
//            });
//            return true;
//        }
//    };
//
//    private EVPlayerBase.OnInfoListener mOnInfoListener = new EVPlayerBase.OnInfoListener() {
//        @Override public boolean onInfo(int what, int extra) {
//            switch (what) {
//                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!isFinishing()) {
//                                showLoadingDialog(R.string.loading_data, true, true);
//                            }
//                        }
//                    });
//                    break;
//                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            dismissLoadingDialog();
//                            mHandler.sendEmptyMessage(MSG_REFRESH_START_TIME);
//                        }
//                    });
//                    break;
//            }
//            return true;
//        }
//    };
//
//    private EVPlayerBase.OnErrorListener mOnErrorListener = new EVPlayerBase.OnErrorListener() {
//        @Override public boolean onError(int what, int extra) {
//            switch (what) {
//                case PlayerConstants.EV_PLAYER_ERROR_SDK_INIT:
//                    showToastOnUiThread(R.string.msg_sdk_init_error);
//                    break;
//                default:
//                    hideInitLoadingView();
//                    dismissLoadingDialog();
//                    break;
//            }
//
//            return true;
//        }
//    };
//
//    private void showToastOnUiThread(final int resId) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (isFinishing()) {
//                    return;
//                }
//                SingleToast.show(getApplicationContext(), resId);
//                finish();
//            }
//        });
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        toggleProgressBar(false);
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    protected void initView() {
//        findViewById(R.id.player_bottom_action_bar).setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void onStatusUpdate(int status) {
//        super.onStatusUpdate(status);
//        if (status == IChatHelper.LIVE_STOP) {
//            stopPlayerAndShowEndView();
//        }
//    }
//
//    @Override
//    protected void handleMessage(Message msg) {
//        switch (msg.what) {
//            case MSG_REFRESH_START_TIME:
//                if (mIsPlayLive) {
//                    mDurationTv.setText(R.string.is_living);
//                } else {
//                    mDurationTv.setText(R.string.is_playback);
//                }
//                break;
//        }
//
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        // TODO Not work, need to improve.
//        String vid = intent.getStringExtra(Constants.EXTRA_KEY_VIDEO_ID);
//        if (mVideoId.equals(vid)) {
//        } else if (!TextUtils.isEmpty(vid)) {
//            resetChatData();
//            startWatchLive();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (null != mEVPlayer) {
//            mEVPlayer.onStart();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (null != mEVPlayer) {
//            mEVPlayer.onResume();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (null != mEVPlayer) {
//            mEVPlayer.onStop();
//        }
//    }
//
//    @Override
//    protected void checkShowGuide() {
//        super.checkShowGuide();
//    }
//
//    @Override
//    protected boolean isFlipping() {
//        return true;
//    }
//
//    @Override
//    public void onClick(final View v) {
//        super.onClick(v);
//        switch (v.getId()) {
//            case R.id.player_bottom_progress_btn:
//                toggleProgressBar(true);
//                break;
////            case R.id.player_report_btn:
////                hideUserPopupView();
////                DialogUtil.showReportUserDialog(PlayerActivity.this, mCurrentVideo.getName());
////                break;
//            case R.id.live_gift_iv:
//                showGiftToolsBar();
//                break;
//            case R.id.burst_iv:
//                mExpressionGiftLayout.checkBurst();
//                break;
//        }
//
//    }
//
//    @Override
//    protected void toggleProgressBar(boolean show) {
//        if (mMediaController == null) {
//            return;
//        }
//        View actionBar = findViewById(R.id.player_bottom_action_bar);
//        if (show) {
//            actionBar.setVisibility(View.INVISIBLE);
//            mMediaController.show();
//        } else {
//            actionBar.setVisibility(View.VISIBLE);
//            mMediaController.hide();
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (mMediaController.isShowing()) {
//            toggleProgressBar(false);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (null != mEVPlayer) {
//            mEVPlayer.onDestroy();
//        }
//    }
//
//    private void startWatchLive() {
//        if (TextUtils.isEmpty(mVideoId)) {
//            SingleToast.show(this, R.string.msg_video_url_null);
//            return;
//        }
//        if (mCurrentVideo.getHorizontal() == VideoEntity.IS_HORIZONTAL) {
//            mEVPlayer.rotateVideo();
//        }
//        mEVPlayer.watchStart(mCurrentVideo.getUri());
//    }
//
//    private synchronized void stopPlayerAndShowEndView() {
//        //socket io callback living end and player callback completion
//        if (isPlayEndViewShow()) {
//            return;
//        }
//        mEVPlayer.watchStop();
//        dismissLoadingDialog();
//        mCurrentVideo.setWatch_count(mWatchCount);
//        mCurrentVideo.setLike_count(mLikeCount);
//        mCurrentVideo.setComment_count(mCommentCount);
//        if (mMediaController != null) {
//            mMediaController.hide();
//        }
//        showPlayEndView(mCurrentVideo);
//        chatServerDestroy();
//    }
//
//    private void showInitLoadingView() {
//        if (!mLoadingView.isShown()) {
//            mLoadingView.setVisibility(View.VISIBLE);
//        }
//        Message msg = mHandler.obtainMessage(MSG_REFRESH_LIVE_NET_SPEED, NetworkUtil.getTotalRxBytes());
//        msg.arg1 = MSG_ARG_1_FIRST_REFRESH_NET_SPEED;
//        mHandler.sendMessageDelayed(msg, 1000);
//    }
//
//    private void hideInitLoadingView() {
//        checkShowGuide();
//        if (mLoadingView != null && mLoadingView.isShown()) {
//            mLoadingView.setVisibility(View.GONE);
//        }
//        mHandler.removeMessages(MSG_REFRESH_LIVE_NET_SPEED);
//    }
}
