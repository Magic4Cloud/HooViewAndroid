/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.live.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.sdk.live.base.audio.AudioManager;
import com.easyvaas.sdk.live.base.camera.CameraManager;
import com.easyvaas.sdk.live.base.view.CameraPreview;
import com.google.gson.Gson;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.bean.TopicEntity;
import com.hooview.app.bean.TopicEntityArray;
import com.hooview.app.bean.video.LiveInfoEntity;
import com.hooview.app.bean.video.LivePrepareConfig;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.net.UploadThumbAsyncTask;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DialogUtil;
import com.hooview.app.utils.FileUtil;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.NetworkUtil;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LivePrepareActivity extends BaseActivity {
    private static final String TAG = LivePrepareActivity.class.getSimpleName();
    private static final int MSG_TAKE_PICTURE_FINISH = 10;
    private static final int MSG_SHARE_TO_SINA = 21;
    private static final int MSG_SHARE_TO_WEIXIN = 22;
    private static final int MSG_SHARE_TO_CIRCLE = 23;
    private static final int MSG_SHARE_TO_QQ = 24;

    private static final String IMAGE_FILE_NAME = "video_thumb";
    private static final int REQUEST_CODE_RESULT = 1;
    private static final int REQUEST_CODE_BIND_PHONE = 3;
    private static final int REQUEST_CODE_CAMERA = 10;
    private static final int REQUEST_CODE_IMAGE = 11;
    private static final int REQUEST_CODE_PASSWORD = 12;

    private static final int HEAD_PORTRAIT_WIDTH = 480;
    private static final int HEAD_PORTRAIT_HEIGHT = 480;
    private static final int CAMERA_CAPTURE_SIZE_WIDTH = 480;
    private static final int CAMERA_CAPTURE_SIZE_HEIGHT = 640;

    private static final int SHARE_TYPE_WEIBO = com.hooview.app.R.id.menu_share_weibo;
    private static final int SHARE_TYPE_QQ = com.hooview.app.R.id.menu_share_qq;
    private static final int SHARE_TYPE_WEIXIN = com.hooview.app.R.id.menu_share_weixin;
    private static final int SHARE_TYPE_WEIXIN_CIRCLE = com.hooview.app.R.id.menu_share_weixin_circle;

    private static final int CAMERA_DEFAULT_ROTATION = 90;

    private LivePrepareConfig mLiveConfig;
    private Preferences mPref;

    private CameraManager mCameraManager;
    private CameraPreview mCameraPreview;

    private EditText mLiveTitleEt;
    private ProgressBar mPrepareProgressBar;
    private Button mLiveStartBtn;
    private TextView mTopicTv;
    private TextView mLiveTipsTv;
    private CheckBox mLastCheckedShareToCb;
    private CheckBox mShareToSinaCb;
    private CheckBox mShareToWeixinCb;
    private CheckBox mShareToWeixinCircleCb;
    private CheckBox mShareToQQCb;
    private TextView mShareToSinaTv;
    private TextView mShareToWeixinTv;
    private TextView mShareToWeixinCircleTv;
    private TextView mShareToQQTv;
    private TextView mLiveCoverTv;
    private BottomSheet mSetThumbPanel;
    private LinearLayout mLiveCoverLL;
    private RoundImageView mLivePreCoverRv;
    private View mSetThumbView;

    private File mCoverThumbFile = null;
    private MyHandler mHandler = null;
    private List<TopicEntity> mTopicList;

    private boolean mSafeToTakePicture = false;
    private Dialog mConfirmBindPhoneDialog;
    private AlertDialog mSetPermissionDialog;
    private AlertDialog mSetTopicDialog;
    private AlertDialog mSetVideoPrice;

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            int xTopLeft = 0;
            int yTopLeft = (CAMERA_CAPTURE_SIZE_HEIGHT - CAMERA_CAPTURE_SIZE_WIDTH) / 2;
            Bitmap bitMapRotate;
            Matrix matrix = new Matrix();
            matrix.reset();
            if (null != mCameraManager && !mCameraManager.isUseFrontCamera()) {
                matrix.postRotate(90);
            } else {
                matrix.postRotate(270);
            }
            bitMapRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            bitmap.recycle();

            bitmap = Bitmap.createBitmap(bitMapRotate, xTopLeft, yTopLeft, CAMERA_CAPTURE_SIZE_WIDTH,
                    CAMERA_CAPTURE_SIZE_WIDTH, null, false);

            String path = FileUtil.CACHE_SHARE_DIR + File.separator
                    + "tmp_screenshot_" + System.currentTimeMillis() + ".jpg";
            Utils.saveBitmap(bitmap, path);
            bitMapRotate.recycle();
            bitmap.recycle();

            mLiveConfig.setCustomThumbPath(path);
            mSafeToTakePicture = true;
            final Bitmap currentBitmap = BitmapFactory.decodeFile(path);
            mLiveCoverLL.setVisibility(View.GONE);
            mLivePreCoverRv.setVisibility(View.VISIBLE);
            mLivePreCoverRv.post(new Runnable() {
                @Override
                public void run() {
                    if (mLivePreCoverRv != null) {
                        mLivePreCoverRv.setImageBitmap(currentBitmap);
                    }
                }
            });
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case com.hooview.app.R.id.live_ready_close:
                    finish();
                    break;
                case com.hooview.app.R.id.live_start_btn:
                    liveStart();
                    v.setEnabled(false);
                    break;
                case com.hooview.app.R.id.live_cover_tv:
                    if (mSetThumbPanel != null && mLiveStartBtn.isEnabled()) {
                        mSetThumbPanel.show();
                    }
                    break;
                case com.hooview.app.R.id.live_ready_shoot_thumb_btn:
                    captureByNativeCamera(480, 640, mPictureCallback);
                case com.hooview.app.R.id.live_ready_set_thumb_close:
                    hideSetThumbView();
                    findViewById(com.hooview.app.R.id.live_ready_setting_rl).setVisibility(View.VISIBLE);
                    break;
                case com.hooview.app.R.id.live_pre_cover_rv:
                    if (mSetThumbPanel != null && mLiveStartBtn.isEnabled()) {
                        mSetThumbPanel.show();
                    }
                    break;
                case com.hooview.app.R.id.topic_tv:
                    if (mSetTopicDialog == null) {
                        mSetTopicDialog = DialogUtil.getSetTopicDialog(LivePrepareActivity.this, mTopicList,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mTopicTv.setText("#" + mTopicList.get(which).getTitle() + "#");
                                        mLiveConfig.setTopicId(mTopicList.get(which).getId());
                                        dialog.dismiss();
                                    }
                                });
                    }
                    if (mSetTopicDialog != null) {
                        mSetTopicDialog.show();
                    }
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
                CheckBox switchCameraCb = null;

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    switch (compoundButton.getId()) {
                        case com.hooview.app.R.id.share_weibo_cb:
                            alphaIn(mShareToSinaTv);
                            mHandler.sendEmptyMessageDelayed(MSG_SHARE_TO_SINA, 200);
                            if (isChecked) {
                                mLiveConfig.setShareType(SHARE_TYPE_WEIBO);
                                mPref.putInt(Preferences.KEY_LAST_SHARE_LIVE_TYPE, SHARE_TYPE_WEIBO);
                            } else {
                                mPref.putInt(Preferences.KEY_LAST_SHARE_LIVE_TYPE, -1);
                            }
                            onShareCheckChange(compoundButton, isChecked, getString(com.hooview.app.R.string.weibo));
                            break;
                        case com.hooview.app.R.id.share_weixin_cb:
                            alphaIn(mShareToWeixinTv);
                            mHandler.sendEmptyMessageDelayed(MSG_SHARE_TO_WEIXIN, 200);
                            if (isChecked) {
                                mLiveConfig.setShareType(SHARE_TYPE_WEIXIN);
                                mPref.putInt(Preferences.KEY_LAST_SHARE_LIVE_TYPE, SHARE_TYPE_WEIXIN);
                            } else {
                                mPref.putInt(Preferences.KEY_LAST_SHARE_LIVE_TYPE, -1);
                            }
                            onShareCheckChange(compoundButton, isChecked, getString(com.hooview.app.R.string.weixin));
                            break;
                        case com.hooview.app.R.id.share_weixin_circle_cb:
                            alphaIn(mShareToWeixinCircleTv);
                            mHandler.sendEmptyMessageDelayed(MSG_SHARE_TO_CIRCLE, 200);
                            if (isChecked) {
                                mLiveConfig.setShareType(SHARE_TYPE_WEIXIN_CIRCLE);
                                mPref.putInt(Preferences.KEY_LAST_SHARE_LIVE_TYPE, SHARE_TYPE_WEIXIN_CIRCLE);
                            } else {
                                mPref.putInt(Preferences.KEY_LAST_SHARE_LIVE_TYPE, -1);
                            }
                            onShareCheckChange(compoundButton, isChecked, getString(com.hooview.app.R.string.weixin_circle));
                            break;
                        case com.hooview.app.R.id.share_qq_cb:
                            alphaIn(mShareToQQTv);
                            mHandler.sendEmptyMessageDelayed(MSG_SHARE_TO_QQ, 200);
                            if (isChecked) {
                                mLiveConfig.setShareType(SHARE_TYPE_QQ);
                                mPref.putInt(Preferences.KEY_LAST_SHARE_LIVE_TYPE, SHARE_TYPE_QQ);
                            } else {
                                mPref.putInt(Preferences.KEY_LAST_SHARE_LIVE_TYPE, -1);
                            }
                            onShareCheckChange(compoundButton, isChecked, getString(com.hooview.app.R.string.qq));
                            break;

                        case com.hooview.app.R.id.live_prepare_switch_camera_cb:
                            switchCameraCb = (CheckBox) compoundButton;
                        case com.hooview.app.R.id.live_set_thumb_camera_cb:
                            mCameraManager.toggleCamera();
                            if (compoundButton.getId() != com.hooview.app.R.id.live_prepare_switch_camera_cb) {
                                switchCameraCb = (CheckBox) compoundButton;
                            } else {
                                mLiveConfig.setIsUseFrontCamera(isChecked);
                            }
                            switchCameraCb.setOnCheckedChangeListener(null);
                            switchCameraCb.setChecked(isChecked);
                            switchCameraCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
                            break;
                        case com.hooview.app.R.id.live_pre_beauty_cb:
                            if (isChecked) {

                            } else {

                            }
                            break;
                        case com.hooview.app.R.id.live_limit_cb:
                            showSetPermissionDialog();
                            break;
                    }
                }
            };

    private void showSetPermissionDialog() {
        if (mSetPermissionDialog == null) {
            final CheckBox limitCb = (CheckBox) findViewById(com.hooview.app.R.id.live_limit_cb);
            mSetPermissionDialog = DialogUtil.getVideoPermissionSetDialog(
                    LivePrepareActivity.this, new DialogInterface.OnClickListener() {
                        final String[] permissions = getResources().getStringArray(com.hooview.app.R.array.video_permission);
                        final String sPassword = getString(com.hooview.app.R.string.video_permission_password);
                        final String sPay = getString(com.hooview.app.R.string.video_permission_pay);
                        final String sPrivate = getString(com.hooview.app.R.string.video_permission_private);

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sPassword.equals(permissions[which])) {
                                mLiveConfig.setVideoLimitType(ApiConstant.VALUE_LIVE_PERMISSION_PASSWORD);
                                startActivityForResult(new Intent(getApplicationContext(),
                                        SetPasswordActivity.class), REQUEST_CODE_PASSWORD);
                                limitCb.setChecked(true);
                            } else if (sPay.equals(permissions[which])) {
                                setVideoPrice(limitCb);
                            } else if (sPrivate.equals(permissions[which])) {
                                mLiveConfig.setVideoLimitType(ApiConstant.VALUE_LIVE_PERMISSION_PRIVATE);
                                limitCb.setChecked(true);
                            } else {
                                mLiveConfig.setVideoLimitType(ApiConstant.VALUE_LIVE_PERMISSION_PUBLIC);
                                limitCb.setChecked(false);
                            }
                            dialog.dismiss();
                        }
                    });

        }
        mSetPermissionDialog.show();
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_TAKE_PICTURE_FINISH:
                if (isFinishing()) {
                    return;
                }
                final boolean isChecked = ((CheckBox) findViewById(com.hooview.app.R.id.live_prepare_switch_camera_cb))
                        .isChecked();
                if (isChecked != mCameraManager.isUseFrontCamera()) {
                    try {
                        mCameraManager.toggleCamera();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    CheckBox switchThumbCheckBox = (CheckBox) mSetThumbView
                            .findViewById(com.hooview.app.R.id.live_set_thumb_camera_cb);
                    switchThumbCheckBox.setOnCheckedChangeListener(null);
                    switchThumbCheckBox.setChecked(mLiveConfig.isUseFrontCamera());
                    if (switchThumbCheckBox.isChecked()) {
                        switchThumbCheckBox.setText(com.hooview.app.R.string.live_switch_camera_front);
                    } else {
                        switchThumbCheckBox.setText(com.hooview.app.R.string.live_switch_camera_rear);
                    }
                    switchThumbCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
                }
                break;
            case MSG_SHARE_TO_SINA:
                alphaOut(mShareToSinaTv);
                break;
            case MSG_SHARE_TO_WEIXIN:
                alphaOut(mShareToWeixinTv);
                break;
            case MSG_SHARE_TO_CIRCLE:
                alphaOut(mShareToWeixinCircleTv);
                break;
            case MSG_SHARE_TO_QQ:
                alphaOut(mShareToQQTv);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsCancelRequestAfterDestroy = false;
        super.onCreate(savedInstanceState);
        setContentView(com.hooview.app.R.layout.activity_live_preapre);

        mPref = Preferences.getInstance(this);
        mLiveConfig = new LivePrepareConfig();
        TopicEntityArray topicArray = new Gson()
                .fromJson(mPref.getString(Preferences.KEY_CACHED_TOPICS_INFO_JSON), TopicEntityArray.class);
        mTopicList = (topicArray == null ? new ArrayList<TopicEntity>() : topicArray.getTopics());
        mHandler = new MyHandler<>(this);

        initUIComponents();

        int shareType = mPref.getInt(Preferences.KEY_LAST_SHARE_LIVE_TYPE, SHARE_TYPE_WEIXIN_CIRCLE);
        switch (shareType) {
            case SHARE_TYPE_WEIBO:
                mShareToSinaCb.setChecked(true);
                break;
            case SHARE_TYPE_QQ:
                mShareToQQCb.setChecked(true);
                break;
            case SHARE_TYPE_WEIXIN:
                mShareToWeixinCb.setChecked(true);
                break;
            case SHARE_TYPE_WEIXIN_CIRCLE:
                mShareToWeixinCircleCb.setChecked(true);
                break;
        }

        mSetThumbPanel = new BottomSheet.Builder(this).sheet(com.hooview.app.R.menu.select_thumb)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case com.hooview.app.R.id.menu_select_thumb_by_camera:
                                initShowSetThumbView();
                                break;
                            case com.hooview.app.R.id.menu_select_thumb_by_gallery:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*");
                                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery, REQUEST_CODE_IMAGE);
                                break;
                            case com.hooview.app.R.id.menu_cancel:
                                dialog.dismiss();
                                break;
                        }
                    }
                }).build();

        mCameraManager = new CameraManager();
        mCameraManager.initWithCameraView(mCameraPreview);

        if (mCameraManager.acquireCamera(false) != null && mCameraManager.initImageSize(this)) {
            mLiveTipsTv.setText(com.hooview.app.R.string.live_check_network);
            if (!mCameraManager.isHaveFrontCamera()) {
                findViewById(com.hooview.app.R.id.live_prepare_switch_camera_cb).setVisibility(View.GONE);
            }
            if (!NetworkUtil.isNetworkAvailable(this)) {
                setInitStatus(false, com.hooview.app.R.string.msg_network_invalid);
            }
        } else {
            setInitStatus(false, com.hooview.app.R.string.camera_open_error);
            SingleToast.show(this, com.hooview.app.R.string.msg_no_permission_open_camera);
            return;
        }

        try {
            Class.forName("android.media.MediaCodec");
        } catch (ClassNotFoundException e) {
            Logger.e(TAG, "NoClassDefFoundError: android.media.MediaCodec");
            setInitStatus(false, com.hooview.app.R.string.title_version_not_supported);
            return;
        }
        if (!AudioManager.getInstance().obtainAudioRecordParameters()) {
            setInitStatus(false, com.hooview.app.R.string.title_audio_record_error);
            return;
        }
        if (!AudioManager.getInstance().initAudioProfile(this)) {
            setInitStatus(false, com.hooview.app.R.string.title_audio_encode_error);
            return;
        }
        setInitStatus(true, com.hooview.app.R.string.live_prepare_to_start);
    }

    private void initUIComponents() {
        mCameraPreview = (CameraPreview) findViewById(com.hooview.app.R.id.camera_preview);

        findViewById(com.hooview.app.R.id.live_ready_close).setOnClickListener(mOnClickListener);
        findViewById(com.hooview.app.R.id.live_start_btn).setOnClickListener(mOnClickListener);
        mTopicTv = (TextView) findViewById(com.hooview.app.R.id.topic_tv);
        mTopicTv.setOnClickListener(mOnClickListener);
        mLiveCoverTv = (TextView) findViewById(com.hooview.app.R.id.live_cover_tv);
        mLiveCoverTv.setOnClickListener(mOnClickListener);
        ((CheckBox) findViewById(com.hooview.app.R.id.live_prepare_switch_camera_cb))
                .setOnCheckedChangeListener(mOnCheckedChangeListener);
        mLiveTitleEt = (EditText) findViewById(com.hooview.app.R.id.live_title_et);
        mPrepareProgressBar = (ProgressBar) findViewById(com.hooview.app.R.id.live_prepare_pb);
        mLiveTipsTv = (TextView) findViewById(com.hooview.app.R.id.live_tips_tv);
        mLiveStartBtn = (Button) findViewById(com.hooview.app.R.id.live_start_btn);

        mShareToSinaCb = (CheckBox) findViewById(com.hooview.app.R.id.share_weibo_cb);
        mShareToWeixinCb = (CheckBox) findViewById(com.hooview.app.R.id.share_weixin_cb);
        mShareToWeixinCircleCb = (CheckBox) findViewById(com.hooview.app.R.id.share_weixin_circle_cb);
        mShareToQQCb = (CheckBox) findViewById(com.hooview.app.R.id.share_qq_cb);
        mShareToSinaTv = (TextView) findViewById(com.hooview.app.R.id.share_weibo_tv);
        mShareToWeixinTv = (TextView) findViewById(com.hooview.app.R.id.share_weixin_tv);
        mShareToWeixinCircleTv = (TextView) findViewById(com.hooview.app.R.id.share_weixin_circle_tv);
        mShareToQQTv = (TextView) findViewById(com.hooview.app.R.id.share_qq_tv);
        mShareToSinaCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mShareToWeixinCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mShareToWeixinCircleCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mShareToQQCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mLiveCoverLL = (LinearLayout) findViewById(com.hooview.app.R.id.live_cover_ll);
        mLivePreCoverRv = (RoundImageView) findViewById(com.hooview.app.R.id.live_pre_cover_rv);
        mLivePreCoverRv.setOnClickListener(mOnClickListener);
        ((CheckBox) findViewById(com.hooview.app.R.id.live_pre_beauty_cb)).setOnCheckedChangeListener(mOnCheckedChangeListener);
        ((CheckBox) findViewById(com.hooview.app.R.id.live_limit_cb)).setOnCheckedChangeListener(mOnCheckedChangeListener);

        if (mTopicList.size() > 0) {
            mTopicTv.setText("#" + mTopicList.get(0).getTitle() + "#");
            mLiveConfig.setTopicId(mTopicList.get(0).getId());
        }
    }

    private void initShowSetThumbView() {
        CheckBox cameraCb;
        mSetThumbView = findViewById(com.hooview.app.R.id.live_prepare_set_thumb_rl);
        if (mSetThumbView == null) {
            mSetThumbView = ((ViewStub) findViewById(com.hooview.app.R.id.live_prepare_set_thumb_view_stub)).inflate();
            cameraCb = (CheckBox) mSetThumbView.findViewById(com.hooview.app.R.id.live_set_thumb_camera_cb);
            cameraCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
            mSetThumbView.findViewById(com.hooview.app.R.id.live_ready_shoot_thumb_btn).setOnClickListener(mOnClickListener);
            mSetThumbView.findViewById(com.hooview.app.R.id.live_ready_set_thumb_close).setOnClickListener(mOnClickListener);
            mSetThumbView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true; // For disable manual focus
                }
            });
            if (!mCameraManager.isHaveFrontCamera()) {
                cameraCb.setVisibility(View.GONE);
            }
        }
        findViewById(com.hooview.app.R.id.live_ready_setting_rl).setVisibility(View.GONE);
        mSetThumbView.setVisibility(View.VISIBLE);
    }

    private void hideSetThumbView() {
        mSetThumbView.setVisibility(View.GONE);
        mHandler.sendEmptyMessageDelayed(MSG_TAKE_PICTURE_FINISH, 500);
    }

    private void showBindPhoneDialog() {
        if (mConfirmBindPhoneDialog == null) {
            mConfirmBindPhoneDialog = DialogUtil
                    .getBindPhoneDialog(LivePrepareActivity.this, REQUEST_CODE_BIND_PHONE);
        }
        mConfirmBindPhoneDialog.show();
    }

    private void setInitStatus(boolean isOK, @StringRes int tips) {
        if (isOK) {
            mSafeToTakePicture = true;
            mPrepareProgressBar.setProgress(mPrepareProgressBar.getMax());
            mPrepareProgressBar.setVisibility(View.GONE);
        } else {
            mPrepareProgressBar.setVisibility(View.GONE);
            mLiveStartBtn.setBackgroundResource(com.hooview.app.R.drawable.ic_live_btn_bg_failed_shape);
        }
        mLiveTipsTv.setText(tips);
        mLiveStartBtn.setText(tips);
        mLiveStartBtn.setEnabled(isOK);
        mLiveStartBtn.setClickable(isOK);
    }

    /**
     * 开始直播
     */
    private void liveStart() {
        mLiveConfig.setBeauty(((CheckBox) findViewById(com.hooview.app.R.id.live_pre_beauty_cb)).isChecked());
        if (mCameraManager != null) {
            mCameraManager.releaseCamera();
            mCameraManager = null;
        }
        if (mCameraPreview != null) {
            mCameraPreview.getHolder().removeCallback(mCameraPreview);
            mCameraPreview = null;
        }
        ApiHelper.getInstance().liveStart(mLiveConfig.getLiveTitle(), mLiveConfig.isShowLocation(),
                mLiveConfig.getVideoLimitType(), mLiveConfig.getVideoPassword(), mLiveConfig.getVideoPrice(),
                new MyRequestCallBack<LiveInfoEntity>() {
                    @Override
                    public void onSuccess(LiveInfoEntity result) {
                        Logger.d(TAG, "live start result : " + result);
                        mLiveConfig.setVid(result.getVid());
                        mLiveConfig.setUri(result.getUri());
                        mLiveConfig.setLiveShareUrl(result.getShare_url());
                        setLiveTitle(result.getVid());
                        setTopic(result.getVid());
                        uploadThumb(result.getVid());
                        startToRecord();
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        if (isFinishing()) {
                            return;
                        }
                        if (ApiConstant.E_USER_PHONE_NOT_EXISTS.equals(errorInfo)) {
                            showBindPhoneDialog();
                            setInitStatus(false, com.hooview.app.R.string.live_prepare_load_failed);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        setInitStatus(true, com.hooview.app.R.string.live_prepare_load_failed);
                    }
                });
    }

    private void setVideoPrice(final CheckBox limitCb) {
        if (mSetVideoPrice == null) {
            mSetVideoPrice = DialogUtil.getSetVideoPayDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText amountEt = (EditText) mSetVideoPrice.findViewById(com.hooview.app.R.id.pay_amount_et);
                    if (amountEt != null && !TextUtils.isEmpty(amountEt.getText())) {
                        mLiveConfig.setVideoPrice(Integer.parseInt(amountEt.getText().toString()));
                        mLiveConfig.setVideoLimitType(ApiConstant.VALUE_LIVE_PERMISSION_PAY);
                        limitCb.setChecked(true);
                    }
                    dialog.dismiss();
                }
            });
        }
        mSetVideoPrice.show();
    }

    private void setLiveTitle(String vid) {
        String title = mLiveTitleEt.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            mLiveConfig.setLiveTitle(getString(com.hooview.app.R.string.live_title_default, mPref.getUserNickname()));
        } else {
            mLiveConfig.setLiveTitle(mLiveTitleEt.getText().toString());
            ApiHelper.getInstance().videoSetTitle(vid, mLiveConfig.getLiveTitle(), null);
        }
    }

    private void setTopic(String vid) {
        if (TextUtils.isEmpty(mLiveConfig.getTopicId())) {
            return;
        }
        ApiHelper.getInstance().setTopic(vid, mLiveConfig.getTopicId(), null);
    }

    private void uploadThumb(String vid) {
        if (mCoverThumbFile == null || !mCoverThumbFile.exists()) {
            return;
        }
        AsyncTask uploadTask = new UploadThumbAsyncTask();
        String uploadUrl = ApiConstant.UPLOAD_VIDEO_LOGO
                + "sessionid=" + mPref.getSessionId()
                + "&vid=" + vid
                + "&file=" + mCoverThumbFile.getName();
        uploadTask.execute(uploadUrl, BitmapFactory.decodeFile(mCoverThumbFile.getAbsolutePath()));
    }

    private void startToRecord() {
        Intent intent = new Intent(this, RecorderActivity.class);
        intent.putExtra(Constants.EXTRA_KEY_LIVE_CONFIG, mLiveConfig);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA) {
            mCameraManager.acquireCamera(false);
        }
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE:
                    mCoverThumbFile = Utils.startPhotoZoom(this, data.getData(), HEAD_PORTRAIT_WIDTH,
                            HEAD_PORTRAIT_HEIGHT, REQUEST_CODE_RESULT);
                    if (mCoverThumbFile != null) {
                        mLiveConfig.setCustomThumbPath(mCoverThumbFile.getAbsolutePath());
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        mCoverThumbFile = Utils
                                .startPhotoZoom(LivePrepareActivity.this, Uri.fromFile(tempFile),
                                        HEAD_PORTRAIT_WIDTH, HEAD_PORTRAIT_HEIGHT, REQUEST_CODE_RESULT);
                        if (mCoverThumbFile != null) {
                            mLiveConfig.setCustomThumbPath(mCoverThumbFile.getAbsolutePath());
                        }
                    } else {
                        SingleToast.show(this, getResources().getString(com.hooview.app.R.string.msg_alert_no_sd_card));
                    }
                    break;
                case REQUEST_CODE_BIND_PHONE:
                    String phone = data.getStringExtra(Constants.EXTRA_KEY_USER_PHONE);
                    Logger.d(TAG, "bind phone: " + phone);
                    SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_account_bind_success);
                    setInitStatus(true, com.hooview.app.R.string.live_prepare_to_start);
                    break;
                case REQUEST_CODE_RESULT:
                    if (mCoverThumbFile != null && mCoverThumbFile.exists()) {
                        mLivePreCoverRv.setVisibility(View.VISIBLE);
                        final Bitmap bitmap = BitmapFactory.decodeFile(mCoverThumbFile.getAbsolutePath());
                        mLivePreCoverRv.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mLivePreCoverRv != null) {
                                    mLivePreCoverRv.setImageBitmap(bitmap);
                                }
                            }
                        });
                    }
                    break;
                case REQUEST_CODE_PASSWORD:
                    String password = data.getStringExtra(SetPasswordActivity.EXTRA_KEY_PASSWORD);
                    if (TextUtils.isEmpty(password)) {
                        startActivityForResult(new Intent(getApplicationContext(), SetPasswordActivity.class),
                                REQUEST_CODE_PASSWORD);
                    } else {
                        mLiveConfig.setVideoPassword(password);
                    }
                    break;
            }
        } else {
            if (requestCode == REQUEST_CODE_BIND_PHONE) {
                showBindPhoneDialog();
            } else if (requestCode == REQUEST_CODE_PASSWORD) {
                mLiveConfig.setVideoLimitType(ApiConstant.VALUE_LIVE_PERMISSION_PUBLIC);
                mSetPermissionDialog.getListView().setItemChecked(0, true);
                showSetPermissionDialog();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (mSetThumbView != null && mSetThumbView.isShown()) {
            hideSetThumbView();
            findViewById(com.hooview.app.R.id.live_ready_setting_rl).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraManager != null) {
            mCameraManager.releaseCamera();
        }
        if (mCameraPreview != null) {
            mCameraPreview.getHolder().removeCallback(mCameraPreview);
            mCameraPreview = null;
        }
        if (mSetThumbPanel != null && mSetThumbPanel.isShowing()) {
            mSetThumbPanel.dismiss();
        }
        if (mConfirmBindPhoneDialog != null && mConfirmBindPhoneDialog.isShowing()) {
            mConfirmBindPhoneDialog.dismiss();
        }
        if (mSetPermissionDialog != null && mSetPermissionDialog.isShowing()) {
            mSetPermissionDialog.dismiss();
        }
        if (mSetTopicDialog != null && mSetTopicDialog.isShowing()) {
            mSetTopicDialog.dismiss();
        }
        mSetThumbPanel = null;
    }

    private void onShareCheckChange(CompoundButton compoundButton, boolean isChecked, String targetName) {
        String shareTipTitle = "";
        if (mLastCheckedShareToCb == compoundButton) {
            if (isChecked) {
                shareTipTitle = getString(com.hooview.app.R.string.live_share_to, targetName);
            } else {
                shareTipTitle = getString(com.hooview.app.R.string.live_share_to, "...");
                mLiveConfig.setShareType(0);
            }
        } else {
            if (isChecked) {
                if (mLastCheckedShareToCb != null) {
                    mLastCheckedShareToCb.setOnCheckedChangeListener(null);
                    mLastCheckedShareToCb.setChecked(false);
                    mLastCheckedShareToCb.setOnCheckedChangeListener(mOnCheckedChangeListener);
                }
                shareTipTitle = getString(com.hooview.app.R.string.live_share_to, targetName);
                mLastCheckedShareToCb = (CheckBox) compoundButton;
            }
        }
    }

    private void captureByNativeCamera(int height, int width, Camera.PictureCallback callback) {
        if (mSafeToTakePicture) {
            Camera camera = mCameraManager.getCamera();
            Camera.Parameters params = camera.getParameters();
            params.setPictureSize(width, height);
            camera.setParameters(params);
            camera.setDisplayOrientation(CAMERA_DEFAULT_ROTATION);
            mCameraManager.getCamera().takePicture(null, null, callback);
            mSafeToTakePicture = false;
        }
    }

    private void alphaIn(TextView v) {
        v.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(LivePrepareActivity.this, com.hooview.app.R.anim.alpha_in);
        v.setAnimation(animation);
        animation.start();
    }

    private void alphaOut(TextView v) {
        v.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(LivePrepareActivity.this, com.hooview.app.R.anim.alpha_out);
        v.setAnimation(animation);
        animation.start();
    }
}
