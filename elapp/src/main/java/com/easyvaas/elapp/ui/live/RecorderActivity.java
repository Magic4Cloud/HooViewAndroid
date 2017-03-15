/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.ui.live;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.common.widget.focusindicatorview.FocusIndicatorView;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.hooview.app.R;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.video.LivePrepareConfig;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.bean.video.VideoEntityArray;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.live.manager.VideoCallHelper;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.FileUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.ShareHelper;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.PlayerStateView;
import com.easyvaas.sdk.live.base.EVStreamerParameter;
import com.easyvaas.sdk.live.base.OnErrorListener;
import com.easyvaas.sdk.live.base.OnInfoListener;
import com.easyvaas.sdk.live.base.StreamerConstants;
import com.easyvaas.sdk.live.base.audio.EVBgmPlayer;
import com.easyvaas.sdk.live.base.view.CameraPreview;
import com.easyvaas.sdk.live.wrapper.EVLive;
import com.easyvaas.sdk.live.wrapper.LiveConstants;

import java.io.File;
import java.util.List;

public class RecorderActivity extends BaseLiveRoomActivity {
    private static final String TAG = "RecorderActivity";

    private static final float BGM_VOLUME = 1.0f;
    private static final String TEST_MP3 = "/sdcard/elapp/test.mp3";
    protected static final int MSG_COUNTDOWN = 105;

    private String mVid;
    private LivePrepareConfig mLiveConfig;

    private CameraPreview mCameraPreview;
    //    private View mOptionsView;
    private FocusIndicatorView mFocusIndicator;
    private View mCameraZoomView;
    private ImageView mIvCountdown;

    private Dialog mConfirmStopDialog;
    private Dialog mRecordFailedDialog;
    //    private Dialog mNetworkInvalidDialog;
    private String mLiveTitle;
    private int mShareType;
    private PlayerStateView playerStateView;

    private EVLive mEVLive;
    private EVBgmPlayer mEVBgMusicPlayer;
    private long mStartTime;
    private long TIME_RECORD_TWO_MIN = 1000 * 60 * 2;

    public static void start(Context context) {
        Intent starter = new Intent(context, RecorderActivity.class);
        context.startActivity(starter);
    }

    private OnErrorListener mErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(int what) {
            switch (what) {
                case LiveConstants.EV_LIVE_ERROR_VERSION_LOW:
                    showErrorToast(R.string.title_version_not_supported);
                    break;
                case LiveConstants.EV_LIVE_ERROR_OPEN_CAMERA:
                    showErrorToast(R.string.title_call_camera_error);
                    break;
                case LiveConstants.EV_LIVE_ERROR_CREATE_AUDIORECORD:
                    showErrorToast(R.string.title_audio_record_error);
                    break;
                case LiveConstants.EV_LIVE_ERROR_STARTING:
                    break;
                case LiveConstants.EV_LIVE_ERROR_RECONNECT:
                    mHandler.removeMessages(MSG_REFRESH_START_TIME);
                    showNetworkInvalidDialog();
                    playerStateView.showLoadingView();
                    break;
                case LiveConstants.EV_LIVE_ERROR_VIDEO_ALREADY_STOPPED:
                    if (isFinishing()) {
                        return true;
                    }
                    showPlayEndView(mCurrentVideo);
                    break;
                case LiveConstants.EV_LIVE_ERROR_SDK_INIT:
                    showErrorToast(R.string.msg_sdk_init_error);
                    break;
                case LiveConstants.EV_LIVE_PUSH_LOCATE_ERROR:
                    showErrorToast(R.string.msg_push_locate_error);
                    break;
                case LiveConstants.EV_LIVE_PUSH_REDIRECT_ERROR:
                    showErrorToast(R.string.msg_push_redirect_error);
                    break;
            }
            return true;
        }
    };

    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(int what) {
            switch (what) {
                case StreamerConstants.EV_STREAMER_INFO_RECONNECTING:
                    if (isFinishing()) {
                        return true;
                    }
                    SingleToast.show(getApplicationContext(), R.string.network_invalid_try_reconnect);
                    mHandler.removeMessages(MSG_REFRESH_START_TIME);
                    break;
                case StreamerConstants.EV_STREAMER_INFO_RECONNECTED:
                    if (isFinishing()) {
                        return true;
                    }
                    SingleToast.show(getApplicationContext(), R.string.network_ok_message);
                    mHandler.sendEmptyMessage(MSG_REFRESH_START_TIME);
                    playerStateView.setVisibility(View.GONE);
                    break;
                case StreamerConstants.EV_STREAMER_INFO_START_SUCCESS:
                    break;
                case StreamerConstants.EV_STREAMER_INFO_STREAMING:
                    break;
                case StreamerConstants.EV_STREAMER_INFO_STREAM_SUCCESS:
                    mStartTime = System.currentTimeMillis();

                    if (isFinishing()) {
                        return true;
                    }
                    mHandler.sendEmptyMessage(MSG_REFRESH_START_TIME);
                    if (new File(TEST_MP3).exists()) {
                        mEVBgMusicPlayer.start(TEST_MP3, true);
                    }
                    break;
            }
            return true;
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_share:
                    shareVideo();
                    break;
                case R.id.live_comment_iv:
                    break;
                case R.id.iv_close:
                    showConfirmStopDialog();
                    break;
                case R.id.camera_zoom_plus_btn:
                    break;
                case R.id.camera_zoom_less_btn:
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    switch (compoundButton.getId()) {
                        case R.id.ib_camera_switch:
                            // true == use front camera
                            if (mEVLive != null) {
                                mEVLive.switchCamera();
                            }
                            break;
                        case R.id.ib_mute:
                            if (mEVLive != null) {
                                mEVLive.switchAudioMute();
                            }
                            break;
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCurrentVideo = new VideoEntity();
        mLiveConfig = (LivePrepareConfig) getIntent().getSerializableExtra(Constants.EXTRA_KEY_LIVE_CONFIG);
        mLiveTitle = mLiveConfig.getLiveTitle();
        mShareType = mLiveConfig.getShareType();
        mCurrentVideo.setVid(mLiveConfig.getVid());
        mVid = mLiveConfig.getVid();
        setContentView(R.layout.activity_recorder);
        super.onCreate(savedInstanceState);
        mIvCountdown = (ImageView) findViewById(R.id.iv_countdown);
        playerStateView = (PlayerStateView) findViewById(R.id.playerStateView);
        playerStateView.setLoadingPrompt(getString(R.string.net_error));
        initRecorderAndStart();
        mStartTime = System.currentTimeMillis();
    }

    private void showErrorToast(int resId) {
        if (isFinishing()) {
            return;
        }
        SingleToast.show(getApplicationContext(), resId);
        finish();
    }

    @Override
    protected void initView() {
        super.initView();
        initUIComponents();
    }


    private void initRecorderAndStart() {
        mEVBgMusicPlayer = EVBgmPlayer.getInstance();
        mEVBgMusicPlayer.setOnBgmPlayerListener(new EVBgmPlayer.OnBgmPlayerListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError(int err) {

            }
        });
        mEVBgMusicPlayer.setVolume(BGM_VOLUME);

        mEVLive = new EVLive(this);
        EVStreamerParameter.Builder builder = new EVStreamerParameter.Builder();
        builder.setVideoResolution(LiveConstants.VIDEO_RESOLUTION_360P)
                .setMaxVideoBitrate(800)
                .setAudioBitrate(32)
                .setAudioCodec(LiveConstants.AUDIO_CODEC_AAC_HE)
                .setIsBeautyOn(false)
                .setPortrait(false)
                .setDisplayRotation(0)
                .setUseFrontCamera(mLiveConfig.isUseFrontCamera());
        mEVLive.setParameter(builder.build());
        mEVLive.setCameraPreview(mCameraPreview);
        mEVLive.setOnErrorListener(mErrorListener);
        mEVLive.setOnInfoListener(mInfoListener);
        mEVLive.setBgmPlayer(mEVBgMusicPlayer);
        mEVLive.onCreate();
        mVid = mLiveConfig.getVid();
        mEVLive.startStreaming(mLiveConfig.getUri());
        mStartTime = System.currentTimeMillis();

        VideoCallHelper.getInstance().initAnchor(this,
                (FrameLayout) findViewById(R.id.v_call_small_container), mEVLive);
    }

    private void initUIComponents() {
        findViewById(R.id.iv_close).setOnClickListener(mOnClickListener);
        findViewById(R.id.iv_share).setOnClickListener(mOnClickListener);
        ViewStub viewStub = (ViewStub) findViewById(R.id.recorder_view_stub);
        View recorderView = viewStub.inflate();
        mCameraPreview = (CameraPreview) recorderView.findViewById(R.id.camera_preview);
        mFocusIndicator = (FocusIndicatorView) findViewById(R.id.focus_area_pane);

        CheckBox cameraSwitchCb = (CheckBox) findViewById(R.id.ib_camera_switch);
        CheckBox muteCb = (CheckBox) findViewById(R.id.ib_mute);

        if (mLiveConfig != null) {
            cameraSwitchCb.setChecked(mLiveConfig.isUseFrontCamera());
        }
        cameraSwitchCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
        muteCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCameraZoomView = recorderView.findViewById(R.id.camera_zoom_ll);
        mCameraZoomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true; // For disable manual focus
            }
        });
        mCameraZoomView.findViewById(R.id.camera_zoom_plus_btn).setOnClickListener(mOnClickListener);
        mCameraZoomView.findViewById(R.id.camera_zoom_less_btn).setOnClickListener(mOnClickListener);
        showLivingInfo();
    }

    public void getVideoInfo() {
        ApiHelper.getInstance().getVideoInfos(new String[]{mCurrentVideo.getVid()}, new MyRequestCallBack<VideoEntityArray>() {
            @Override
            public void onSuccess(VideoEntityArray result) {
                List<VideoEntity> videoEntities = result.getVideos();
                if (videoEntities != null && videoEntities.size() > 0) {
                    VideoEntity videoEntity = videoEntities.get(0);
                    mCurrentVideo.setShare_url(videoEntity.getShare_url());
                    mCurrentVideo.setShare_thumb_url(videoEntity.getShare_thumb_url());
                }
                Message countdown = Message.obtain();
                countdown.what = MSG_COUNTDOWN;
                countdown.obj = 3;
                mHandler.sendMessage(countdown);
                if (mShareType != 0) {
                    shareRecordVideo();
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }

    private void shareRecordVideo() {
        String url = mLiveConfig.getLiveShareUrl();
        String content = getString(R.string.share_mine_live_content, mPref.getUserNickname());
        String imageUrl = mCurrentVideo.getShare_thumb_url();
        if (mLiveTitle == null) {
            mLiveTitle = "";
        }
        if (url == null) {
            url = "";
        }
        if (imageUrl == null) {
            imageUrl = "";
        }
        shareVideo(mLiveTitle, content, url, imageUrl);
    }

    protected void shareVideo(final String title, final String content, final String url,
                              final String imageUrl) {
        showLoadingDialog(R.string.sharing, false, true);
        AsyncTask loadImageTask = new AsyncTask<Object, Integer, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length > 0) {
                    return Utils.getLocalImagePath(params[0].toString());
                }
                return "";
            }

            @Override
            protected void onPostExecute(String filePath) {
                super.onPostExecute(filePath);
                String shareImage = filePath;
                if (TextUtils.isEmpty(filePath)) {
                    shareImage = getFilesDir() + File.separator + FileUtil.LOGO_FILE_NAME;
                }
                ShareContent sharePic = new ShareContentWebpage(title, content, url, shareImage);
                ShareHelper
                        .getInstance(RecorderActivity.this).shareContent(mShareType, sharePic, Constants.SHARE_TYPE_VIDEO);
                dismissLoadingDialog();
            }
        };
        loadImageTask.execute(imageUrl);
    }

    @Override
    public void onStatusUpdate(int status) {
        if (isFinishing() || isPlayEndViewShow()) {
            return;
        }
        liveStop();
    }

    protected void liveStop() {
        if (mEVLive != null) {
            mEVLive.stopLive();
        }
        if (mEVBgMusicPlayer != null) {
            mEVBgMusicPlayer.release();
        }
        if (mCurrentVideo == null) {
            finish();
            return;
        }
        mCurrentVideo.setWatch_count(mWatchCount);
        mCurrentVideo.setLike_count(mLikeCount);
        mCurrentVideo.setComment_count(mCommentCount);
        mHandler.removeMessages(MSG_REFRESH_START_TIME);
        ApiHelper.getInstance().liveStop(mVid, true, new MyRequestCallBack<VideoEntity>() {
            @Override
            public void onSuccess(VideoEntity result) {
                chatServerDestroy();
                if (isFinishing()) {
                    return;
                }
                showPlayEndView(mCurrentVideo);
                EVApplication.getUser().setVideo_count(EVApplication.getUser().getVideo_count() + 1);
                VideoCallHelper.getInstance().release();
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                if (isFinishing()) {
                    return;
                }

                if (isPlayEndViewShow()) {
                    return;
                }
//                Dialog dialog = DialogUtil.getOneButtonDialog(RecorderActivity.this,
//                        getString(R.string.dialog_title_live_stop), false, false,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        });
//                dialog.show();
                showPlayEndView(mCurrentVideo);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
                chatServerDestroy();
                if (isFinishing()) {
                    return;
                }
                showPlayEndView(mCurrentVideo);
                if (!mLiveConfig.isContinueRecord()) {
                    EVApplication.getUser().setVideo_count(EVApplication.getUser().getVideo_count() + 1);
                }
            }
        });
    }

    private void showLivingInfo() {
        mCurrentVideo.setVid(mLiveConfig.getVid());
        mCurrentVideo.setLiving(VideoEntity.IS_LIVING);
        mCurrentVideo.setName(mPref.getUserNumber());
        mCurrentVideo.setNickname(mPref.getUserNickname());
        mCurrentVideo.setShare_url(mLiveConfig.getLiveShareUrl());
        mCurrentVideo.setTitle(mLiveTitle);
        getVideoInfo();

        if (EVApplication.getUser() == null) {
            return;
        }
        updateWatchLikeCounts(0);
    }

    private void showConfirmStopDialog() {
        if (mConfirmStopDialog != null) {
            mConfirmStopDialog.show();
            return;
        }
        mConfirmStopDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title_confirm_stop_live)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //                        StatisticsHelper.getInstance(getApplication())
                        //                                .reportLiveStopReason(RecorderActivity.this, mVid, getExtra(),
                        //                                Constants.STREAMER_EVENT_LIVE_STOP_NORMAL);
                        liveStop();
                        mPref.remove(Preferences.KEY_LAST_LIVE_INTERRUPT_VID);
                        mPref.remove(Preferences.KEY_LAST_LIVE_IS_AUDIO_MODE);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                })
                .setCancelable(false)
                .create();
        mConfirmStopDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mEVLive != null) {
            mEVLive.onResume();
            mHandler.sendEmptyMessage(MSG_REFRESH_START_TIME);
        }
        dismissLoadingDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mEVLive != null) {
            mEVLive.onPause();
            mHandler.removeMessages(MSG_REFRESH_START_TIME);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_REFRESH_START_TIME);
        if (null != mEVLive) {
            mEVLive.onDestroy();
        }
        if (mConfirmStopDialog != null && mConfirmStopDialog.isShowing()) {
            mConfirmStopDialog.dismiss();
        }
        mConfirmStopDialog = null;
        if (mRecordFailedDialog != null && mRecordFailedDialog.isShowing()) {
            mRecordFailedDialog.dismiss();
        }
        mRecordFailedDialog = null;
//        if (mNetworkInvalidDialog != null && mNetworkInvalidDialog.isShowing()) {
//            mNetworkInvalidDialog.dismiss();
//        }
//        mNetworkInvalidDialog = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getPointerCount() == 2) {
//            Logger.d(TAG, "detected zoom");
//            if (mCameraZoomView != null) {
//                mCameraZoomView.setVisibility(View.VISIBLE);
//                return mScaleGestureDetector.onTouchEvent(event);
//            } else {
//                return super.onTouchEvent(event);
//            }
//        } else {
//        }
        //Use margin to set the focus indicator to the touched area.
        if (null != mFocusIndicator) {
            ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(
                    mFocusIndicator.getLayoutParams());
            int x = Float.valueOf(event.getRawX()).intValue() - margin.width / 2;
            int y = Float.valueOf(event.getRawY()).intValue() - margin.height / 2;
            margin.setMargins(x, y, x + margin.width, y + margin.height);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(margin);
            mFocusIndicator.setLayoutParams(layoutParams);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (!isPlayEndViewShow()) {
            showConfirmStopDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == MSG_COUNTDOWN) {
            int time = (int) msg.obj;
            Logger.d(TAG, "handleMessage: time=" + time);
            if (time == 3) {
                time--;
                sendCountdownMsg(time);
                mIvCountdown.setVisibility(View.VISIBLE);
                mIvCountdown.setImageResource(R.drawable.ic_countdown3);
            } else if (time == 2) {
                time--;
                sendCountdownMsg(time);
                mIvCountdown.setVisibility(View.VISIBLE);
                mIvCountdown.setImageResource(R.drawable.ic_countdown2);
            } else if (time == 1) {
                time--;
                sendCountdownMsg(time);
                mIvCountdown.setVisibility(View.VISIBLE);
                mIvCountdown.setImageResource(R.drawable.ic_countdown1);
            } else {
                mIvCountdown.setVisibility(View.GONE);
            }

        }
    }

    private void sendCountdownMsg(int time) {
        Message countdown = Message.obtain();
        countdown.what = MSG_COUNTDOWN;
        countdown.obj = time;
        mHandler.sendMessageDelayed(countdown, 1000);
    }

    protected void showNetworkInvalidDialog() {
        if (isFinishing()) {
            return;
        }
        playerStateView.showLoadingView();
//        if (mNetworkInvalidDialog == null) {
//            mNetworkInvalidDialog = DialogUtil.getOneButtonDialog(this,
//                    getResources().getString(R.string.no_network_dialog), false, false,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });
//        }
//        mNetworkInvalidDialog.show();
    }

    @Override
    protected void showPlayEndView(VideoEntity videoEntity) {
        super.showPlayEndView(videoEntity);
        ViewStub viewSub = (ViewStub) findViewById(R.id.live_end_view_stub);
        View view = viewSub.inflate();
        ImageView lepCloseIv = (ImageView) findViewById(R.id.lep_close_iv);
        TextView tvSaveTips = (TextView) findViewById(R.id.tv_save_tips);
        TextView tvTime = (TextView) findViewById(R.id.tv_time);
        TextView tvWatchCount = (TextView) findViewById(R.id.tv_watch_count);
        TextView tvFollowCount = (TextView) findViewById(R.id.tv_follow_count);
        TextView tvCoinCount = (TextView) findViewById(R.id.tv_coin_count);
        lepCloseIv.setOnClickListener(this);
        tvWatchCount.setText(mWatchCount + "");
        tvFollowCount.setText(mLikeCount+ "");
        tvCoinCount.setText(mHooviewCoinCount + "");
        view.setVisibility(View.VISIBLE);
        isShowEndView = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        lepCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        long timeNow = System.currentTimeMillis();
        tvTime.setText(DateTimeUtil.getDurationTime(getApplicationContext(), timeNow - mStartTime));
        if (timeNow - mStartTime < TIME_RECORD_TWO_MIN) {
            tvSaveTips.setText(R.string.save_tips);
            tvSaveTips.setVisibility(View.VISIBLE);
            removeVideo();
        } else {
            tvSaveTips.setVisibility(View.GONE);
        }
    }

    private void removeVideo() {
        ApiHelper.getInstance().videoRemove(mVid, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.lep_close_iv:
                finish();
                break;
        }
    }

    protected void hideInitLoadingView() {
        // TODO: 2017/1/10
//        checkShowGuide();
//        if (mLoadingView != null && mLoadingView.isShown()) {
//            mLoadingView.setVisibility(View.GONE);
//        }
        mHandler.removeMessages(MSG_REFRESH_LIVE_NET_SPEED);
    }
}
