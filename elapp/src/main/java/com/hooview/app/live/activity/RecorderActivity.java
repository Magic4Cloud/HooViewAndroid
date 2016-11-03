/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.live.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.common.widget.focusindicatorview.FocusIndicatorView;
import com.easyvaas.sdk.live.base.EVStreamerParameter;
import com.easyvaas.sdk.live.base.OnErrorListener;
import com.easyvaas.sdk.live.base.OnInfoListener;
import com.easyvaas.sdk.live.base.StreamerConstants;
import com.easyvaas.sdk.live.base.audio.EVBgmPlayer;
import com.easyvaas.sdk.live.base.view.CameraPreview;
import com.easyvaas.sdk.live.wrapper.EVLive;
import com.easyvaas.sdk.live.wrapper.LiveConstants;
import com.hooview.app.app.EVApplication;
import com.hooview.app.bean.video.LivePrepareConfig;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.bean.video.VideoEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DialogUtil;
import com.hooview.app.utils.FileUtil;
import com.hooview.app.utils.ShareHelper;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * 直播页面
 */
public class RecorderActivity extends LiveRoomBaseActivity {
    private static final String TAG = "RecorderActivity";

    private static final float BGM_VOLUME = 1.0f;
    private static final String TEST_MP3 = "/sdcard/elapp/test.mp3";

    private String mVid;
    private LivePrepareConfig mLiveConfig;

    private CameraPreview mCameraPreview;
    private View mOptionsView;
    private FocusIndicatorView mFocusIndicator;
    private View mCameraZoomView;

    private Dialog mConfirmStopDialog;
    private Dialog mRecordFailedDialog;
    private Dialog mNetworkInvalidDialog;
    private String mLiveTitle;
    private int mShareType;

    private EVLive mEVLive;
    private EVBgmPlayer mEVBgMusicPlayer;

    private OnErrorListener mErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(int what) {
            switch (what) {
                case LiveConstants.EV_LIVE_ERROR_VERSION_LOW:
                    showErrorToast(com.hooview.app.R.string.title_version_not_supported);
                    break;
                case LiveConstants.EV_LIVE_ERROR_OPEN_CAMERA:
                    showErrorToast(com.hooview.app.R.string.title_call_camera_error);
                    break;
                case LiveConstants.EV_LIVE_ERROR_CREATE_AUDIORECORD:
                    showErrorToast(com.hooview.app.R.string.title_audio_record_error);
                    break;
                case LiveConstants.EV_LIVE_ERROR_STARTING:
                    break;
                case LiveConstants.EV_LIVE_ERROR_RECONNECT:
                    mHandler.removeMessages(MSG_REFRESH_START_TIME);
                    showNetworkInvalidDialog();

                    break;
                case LiveConstants.EV_LIVE_ERROR_VIDEO_ALREADY_STOPPED:
                    if (isFinishing()) {
                        return true;
                    }
                    showPlayEndView(mCurrentVideo);
                    break;
                case LiveConstants.EV_LIVE_ERROR_SDK_INIT:
                    showErrorToast(com.hooview.app.R.string.msg_sdk_init_error);
                    break;
                case LiveConstants.EV_LIVE_PUSH_LOCATE_ERROR:
                    showErrorToast(com.hooview.app.R.string.msg_push_locate_error);
                    break;
                case LiveConstants.EV_LIVE_PUSH_REDIRECT_ERROR:
                    showErrorToast(com.hooview.app.R.string.msg_push_redirect_error);
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
                    SingleToast.show(getApplicationContext(), com.hooview.app.R.string.network_invalid_try_reconnect);
                    mHandler.removeMessages(MSG_REFRESH_START_TIME);

                    break;
                case StreamerConstants.EV_STREAMER_INFO_RECONNECTED:
                    if (isFinishing()) {
                        return true;
                    }
                    SingleToast.show(getApplicationContext(), com.hooview.app.R.string.network_ok_message);
                    mHandler.sendEmptyMessage(MSG_REFRESH_START_TIME);

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
                    mDurationTv.setVisibility(View.VISIBLE);
                    mOptionsView.setVisibility(View.VISIBLE);
                    mVideoTitleTv.setVisibility(View.VISIBLE);
                    mVideoTitleTv.setText("vid: " + mVid);
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
            hideUserPopupView();
            switch (v.getId()) {
                case com.hooview.app.R.id.live_options_left_arrow_iv:
                    findViewById(com.hooview.app.R.id.live_options_right_ll).setVisibility(View.VISIBLE);
                    findViewById(com.hooview.app.R.id.live_options_left_ll).setVisibility(View.GONE);
                    break;
                case com.hooview.app.R.id.live_options_right_arrow_iv:
                    findViewById(com.hooview.app.R.id.live_options_right_ll).setVisibility(View.GONE);
                    findViewById(com.hooview.app.R.id.live_options_left_ll).setVisibility(View.VISIBLE);
                    break;
                case com.hooview.app.R.id.live_share_iv:
                    shareVideo();
                    break;
                case com.hooview.app.R.id.live_comment_iv:
                    showCommentTextBox();
                    break;

                //关闭直播
                case com.hooview.app.R.id.live_close_iv:
                    showConfirmStopDialog();
                    break;
                case com.hooview.app.R.id.camera_zoom_plus_btn:

                    break;
                case com.hooview.app.R.id.camera_zoom_less_btn:

                    break;
                case com.hooview.app.R.id.live_send_red_pack_iv:
                    //DialogUtil.showCreateRedPackDialog(RecorderActivity.this, mVid);
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    hideUserPopupView();
                    switch (compoundButton.getId()) {
                        case com.hooview.app.R.id.live_switch_camera_cb:
                            // true == use front camera
                            if (mEVLive != null) {
                                mEVLive.switchCamera();
                            }
                            CheckBox checkBox = (CheckBox) findViewById(com.hooview.app.R.id.live_flash_cb);
                            checkBox.setEnabled(!isChecked);
                            if (isChecked) {
                                checkBox.setChecked(false);
                            }
                            break;
                        case com.hooview.app.R.id.live_mute_cb:
                            if (mEVLive != null) {
                                mEVLive.switchAudioMute();
                            }
                            break;
                        case com.hooview.app.R.id.live_flash_cb:
                            if (mEVLive != null) {
                                mEVLive.switchFlashlight();
                            }
                            break;
                        case com.hooview.app.R.id.live_beauty_cb:
                            mEVLive.switchBeauty(isChecked);
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
        super.onCreate(savedInstanceState);
        initRecorderAndStart();
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
        findViewById(com.hooview.app.R.id.live_gift_iv).setVisibility(View.GONE);
        findViewById(com.hooview.app.R.id.player_bottom_action_bar).setVisibility(View.INVISIBLE);
        initUIComponents();
    }

    @Override
    protected void hideGiftToolsBar() {
    }

    @Override
    protected void showGiftToolsBar() {
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
                .setIsBeautyOn(mLiveConfig.isBeauty())
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
    }

    private void initUIComponents() {
        if (mBubbleView != null) {
            mBubbleView.setEnabled(false);
        }
        ViewStub viewStub = (ViewStub) findViewById(com.hooview.app.R.id.recorder_view_stub);
        View recorderView = viewStub.inflate();

        mCameraPreview = (CameraPreview) recorderView.findViewById(com.hooview.app.R.id.camera_preview);
        mFocusIndicator = (FocusIndicatorView) findViewById(com.hooview.app.R.id.focus_area_pane);

        mOptionsView = recorderView.findViewById(com.hooview.app.R.id.live_options_ll);
        mOptionsView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true; // For disable manual focus
            }
        });
        mOptionsView.findViewById(com.hooview.app.R.id.live_options_left_arrow_iv).setOnClickListener(mOnClickListener);
        mOptionsView.findViewById(com.hooview.app.R.id.live_comment_iv).setOnClickListener(mOnClickListener);
        mOptionsView.findViewById(com.hooview.app.R.id.live_share_iv).setOnClickListener(mOnClickListener);
        mOptionsView.findViewById(com.hooview.app.R.id.live_mute_cb).setOnClickListener(mOnClickListener);
        mOptionsView.findViewById(com.hooview.app.R.id.live_options_right_arrow_iv).setOnClickListener(mOnClickListener);
        CheckBox flashSwitchCb = (CheckBox) recorderView.findViewById(com.hooview.app.R.id.live_flash_cb);
        CheckBox cameraSwitchCb = (CheckBox) recorderView.findViewById(com.hooview.app.R.id.live_switch_camera_cb);
        CheckBox beautySwitchCb = (CheckBox) recorderView.findViewById(com.hooview.app.R.id.live_beauty_cb);
        if (mLiveConfig != null) {
            cameraSwitchCb.setChecked(mLiveConfig.isUseFrontCamera());
            flashSwitchCb.setEnabled(!mLiveConfig.isUseFrontCamera());
            beautySwitchCb.setChecked(mLiveConfig.isBeauty());
        }
        flashSwitchCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
        cameraSwitchCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
        beautySwitchCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
        ((CheckBox) mOptionsView.findViewById(com.hooview.app.R.id.live_mute_cb))
                .setOnCheckedChangeListener(mOnCheckedChangeListener);

        mCameraZoomView = recorderView.findViewById(com.hooview.app.R.id.camera_zoom_ll);
        mCameraZoomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true; // For disable manual focus
            }
        });
        mCameraZoomView.findViewById(com.hooview.app.R.id.camera_zoom_plus_btn).setOnClickListener(mOnClickListener);
        mCameraZoomView.findViewById(com.hooview.app.R.id.camera_zoom_less_btn).setOnClickListener(mOnClickListener);
        if (mLiveConfig != null
                && mLiveConfig.getVideoLimitType() == ApiConstant.VALUE_LIVE_PERMISSION_PUBLIC) {
            recorderView.findViewById(com.hooview.app.R.id.live_send_red_pack_iv).setOnClickListener(mOnClickListener);
        } else {
            recorderView.findViewById(com.hooview.app.R.id.live_send_red_pack_iv).setVisibility(View.INVISIBLE);
        }
        recorderView.findViewById(com.hooview.app.R.id.live_send_red_pack_iv).setVisibility(View.INVISIBLE);

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
        String content = getString(com.hooview.app.R.string.share_mine_live_content, mPref.getUserNickname());
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
        showLoadingDialog(com.hooview.app.R.string.sharing, false, true);
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
                Dialog dialog = DialogUtil.getOneButtonDialog(RecorderActivity.this,
                        getString(com.hooview.app.R.string.dialog_title_live_stop), false, false,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                dialog.show();
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
        showAllInfoViews(true);
        mTopInfoAreaView.findViewById(com.hooview.app.R.id.live_close_iv).setOnClickListener(mOnClickListener);
        showRecordOptionView();

        if (EVApplication.getUser() == null) {
            return;
        }
        Utils.showImage(EVApplication.getUser().getLogourl(), com.hooview.app.R.drawable.somebody, mUserLogoIv);
        mNicknameTv.setText(EVApplication.getUser().getNickname());
        updateWatchLikeCounts(0);
        mEmotionKeyBoardBar.clearText();
    }

    private void showConfirmStopDialog() {
        if (mConfirmStopDialog != null) {
            mConfirmStopDialog.show();
            return;
        }
        mConfirmStopDialog = new AlertDialog.Builder(this)
                .setTitle(com.hooview.app.R.string.title_confirm_stop_live)
                .setPositiveButton(com.hooview.app.R.string.confirm, new DialogInterface.OnClickListener() {
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
                .setNegativeButton(com.hooview.app.R.string.cancel, null)
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
        if (mNetworkInvalidDialog != null && mNetworkInvalidDialog.isShowing()) {
            mNetworkInvalidDialog.dismiss();
        }
        mNetworkInvalidDialog = null;
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
        if (mEmotionKeyBoardBar.isInputShow()) {
            showRecordOptionView();
            mEmotionKeyBoardBar.hideInput();
        } else if (!isPlayEndViewShow()) {
            showConfirmStopDialog();
        } else {
            super.onBackPressed();
        }
    }

    public void showRecordOptionView() {
        mOptionsView.setVisibility(View.VISIBLE);
    }

    protected void showNetworkInvalidDialog() {
        if (isFinishing()) {
            return;
        }
        if (mNetworkInvalidDialog == null) {
            mNetworkInvalidDialog = DialogUtil.getOneButtonDialog(this,
                    getResources().getString(com.hooview.app.R.string.no_network_dialog), false, false,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
        }
        mNetworkInvalidDialog.show();
    }

    protected void hideInitLoadingView() {
        checkShowGuide();
        if (mLoadingView != null && mLoadingView.isShown()) {
            mLoadingView.setVisibility(View.GONE);
        }
        mHandler.removeMessages(MSG_REFRESH_LIVE_NET_SPEED);
    }
}
