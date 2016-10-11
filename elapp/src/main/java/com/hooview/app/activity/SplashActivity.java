/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.emoji.utils.EmoticonsUtils;
import com.easyvaas.common.sharelogin.ShareBlock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hooview.app.activity.home.HomeTabActivity;
import com.hooview.app.activity.login.LoginMainActivity;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.bean.UpdateInfoEntity;
import com.hooview.app.bean.serverparam.SplashScreen;
import com.hooview.app.db.Preferences;
import com.hooview.app.live.activity.PlayerActivity;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.push.PushHelper;
import com.hooview.app.utils.ChannelUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.FileUtil;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.UpdateManager;
import com.hooview.app.utils.UserUtil;
import com.hooview.app.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.ref.SoftReference;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int MSG_EXIT = 0x10;
    private static final int MSG_REFRESH_COUNTDOWN_TIME = 0x20;
    private static final int SHOW_DURATION_TIMEOUT = 4000;
    private static final int SHOW_DURATION_AFTER_VERIFY = 3000;
    private static final int INTERVAL_CHECK_UPDATE = 12 * 3600 * 1000;

    private boolean mIsLogin;
    private MyHandler mHandler;
    private Preferences mPref;
    private UpdateManager mUpdateManager;

    private TextView mCountdownTimerTv;
    private View preLoadMainHome=null;

    private static class MyHandler extends Handler {
        private SoftReference<SplashActivity> softReference;

        public MyHandler(SplashActivity activity) {
            softReference = new SoftReference<SplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity activity = softReference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case MSG_REFRESH_COUNTDOWN_TIME:
                    int countDownTime = (int) msg.obj;
                    activity.mCountdownTimerTv.setText(activity
                            .getString(com.hooview.app.R.string.splash_time_countdown, countDownTime));
                    countDownTime--;
                    if (countDownTime > 0) {
                        Message countdownMsg = obtainMessage(MSG_REFRESH_COUNTDOWN_TIME, countDownTime);
                        sendMessageDelayed(countdownMsg, 1000);
                    } else {
                        sendEmptyMessage(MSG_EXIT);
                    }
                    break;
                case MSG_EXIT:
                    Logger.d(TAG, "Exit splash ...");
                    if (activity.mUpdateManager.isForceUpdate()) {
                        return;
                    }
                    activity.finish();
                    if (activity.mIsLogin) {
                        Intent intent = new Intent(activity, HomeTabActivity.class);
                        intent.putExtra(Constants.EXTRA_KEY_IS_FROM_SPLASH, true);
                        Uri uri = activity.getIntent().getData();
                        if (uri != null) {
                            String host = uri.getHost();
                            String dataString = activity.getIntent().getDataString();
                            String path = uri.getPath();
                            String queryString = uri.getQuery();
                            if (Constants.WEB_HOST_VIDEO.equals(host)) {
                                String vid = uri.getQueryParameter(Constants.WEB_HOST_PARAM_VID);
                                intent.putExtra(Constants.EXTRA_KEY_VIDEO_ID, vid);
                                intent.setClass(activity, PlayerActivity.class);
                                Logger.d(TAG, "vid:" + vid);
                            } else if (Constants.WEB_HOST_INDEX.equals(host)) {
                                // Do nothing
                            }
                            Logger.d(TAG, "dataString:" + dataString);
                            Logger.d(TAG, "path:" + path);
                        }
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(activity, LoginMainActivity.class);
                        activity.startActivity(intent);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsCancelRequestAfterDestroy = false;
        super.onCreate(savedInstanceState);

        mHandler = new MyHandler(this);
        mPref = Preferences.getInstance(getApplicationContext());
        mUpdateManager = UpdateManager.getInstance(this);

        int lastVersionCode = mPref.getInt(Preferences.KEY_VERSION_CODE, 0);

        setContentView(com.hooview.app.R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        setTitle(com.hooview.app.R.string.app_name);

        mCountdownTimerTv = (TextView) findViewById(com.hooview.app.R.id.splash_timer_tv);
        findViewById(com.hooview.app.R.id.splash_jump_over_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delayExit(0);
            }
        });
        cleanHomeTopicCache();
        EmoticonsUtils.initEmoticonsDB(getApplicationContext(), false);

        PushHelper.getInstance(this).deviceRegister();

        // Init ShareLogin library Key
        ShareBlock.getInstance().initShare(Constants.WEIXIN_APP_ID, Constants.WEIBO_APP_ID,
                Constants.QQ_APP_ID, Constants.WEIXIN_APP_SECRET);

        // Umeng statistics
        MobclickAgent.enableEncrypt(true);
        MobclickAgent.setDebugMode(false);
        MobclickAgent.setCheckDevice(!ChannelUtil.isGoogleChannel(this));
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(this,
                ChannelUtil.getAppKey(this), ChannelUtil.getChannelName(this),
                MobclickAgent.EScenarioType.E_UM_NORMAL, true);
        MobclickAgent.startWithConfigure(config);

        long lastCheckUpdate = mPref.getLong(Preferences.KEY_LAST_CHECK_UPDATE_TIME, 0);
        if (lastCheckUpdate + INTERVAL_CHECK_UPDATE < System.currentTimeMillis()
                || mUpdateManager.isForceUpdate()) {
            checkUpdate();
        }
        if (mPref.isLogin()) {
            showDynamicSplashImage();
        }
        checkUserSession();
    }


    private void cleanHomeTopicCache() {
        mPref.remove(Preferences.KEY_HOME_CURRENT_TOPIC_NAME);
        mPref.remove(Preferences.KEY_HOME_CURRENT_TOPIC_ID);
    }

    private void showDynamicSplashImage() {
        String json = mPref.getString(Preferences.KEY_PARAM_SCREEN_LIST_JSON);
        List<SplashScreen> screenEntityArray =
                new Gson().fromJson(json, new TypeToken<List<SplashScreen>>(){}.getType());
        if (screenEntityArray == null || screenEntityArray.size() == 0) {
            return;
        }
        int lastShowIndex = mPref.getInt(Preferences.KEY_PARAM_SCREEN_LAST_SHOW_INDEX, -1);
        if (lastShowIndex < 0 || lastShowIndex >= screenEntityArray.size() - 1) {
            lastShowIndex = 0;
        } else {
            lastShowIndex += 1;
        }
        final SplashScreen selectScreen = screenEntityArray.get(lastShowIndex);
        mPref.putInt(Preferences.KEY_PARAM_SCREEN_LAST_SHOW_INDEX, lastShowIndex);

        ImageView screenIv = (ImageView) findViewById(com.hooview.app.R.id.splash_screen_iv);
        View splashDynamicRl = findViewById(com.hooview.app.R.id.splash_dynamic_rl);
        if (splashDynamicRl == null || screenIv == null) {
            return;
        }
        screenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectScreen.getType() == SplashScreen.TYPE_SPLASH_AD){
                    mHandler.removeMessages(MSG_REFRESH_COUNTDOWN_TIME);
                    mHandler.removeMessages(MSG_EXIT);
                    Intent adDetailIntent = new Intent(getApplicationContext(), WebViewActivity.class);
                    adDetailIntent.putExtra(WebViewActivity.EXTRA_KEY_TITLE, " ");
                    adDetailIntent.putExtra(WebViewActivity.EXTRA_KEY_URL, selectScreen.getAd_url());
                    startActivity(adDetailIntent);
                }
            }
        });
        if (DateTimeUtil.isCurrentDateInSpan(selectScreen.getStart_time(), selectScreen.getEnd_time())) {
            String fileName = selectScreen.getUrl();
            try {
                fileName = Utils.getMD5(selectScreen.getUrl());
            } catch (NoSuchAlgorithmException e) {
                Logger.w(TAG, "MD5 url failed", e);
            }
            String splashImagePath = FileUtil.CACHE_SPLASH_DIR + File.separator + fileName;
            if (!FileUtil.isFileIsExist(splashImagePath)) {
                splashDynamicRl.setVisibility(View.GONE);
                Utils.cachedImageAsync(this, selectScreen.getUrl(), FileUtil.CACHE_SPLASH_DIR);
            } else {
                Message msg = mHandler.obtainMessage(MSG_REFRESH_COUNTDOWN_TIME, selectScreen.getDuration());
                mHandler.sendMessage(msg);
                Utils.showImage(splashImagePath, 0, screenIv);
                splashDynamicRl.setVisibility(View.VISIBLE);
            }
        } else {
            splashDynamicRl.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mHandler.hasMessages(MSG_REFRESH_COUNTDOWN_TIME) && !mHandler.hasMessages(MSG_EXIT)) {
            delayExit(0);
        }
        if (mUpdateManager.getDownloadStatus() == UpdateManager.DownloadStatus.Complete
                && mUpdateManager.isForceUpdate()) {
            mUpdateManager.showDownloadDialog();
        }
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkUpdate() {
        mUpdateManager.checkUpdateInfo(new UpdateManager.UpdateListener() {
            @Override
            public void onUpdateReturned(UpdateInfoEntity result) {
                mPref.putLong(Preferences.KEY_LAST_CHECK_UPDATE_TIME, System.currentTimeMillis());
                switch (result.getUpdate()) {
                    case UpdateManager.UpdateStatus.Yes: // has update
                        if (mHandler != null) {
                            mHandler.removeMessages(MSG_EXIT);
                            mPref.putBoolean(Preferences.KEY_IS_HAVE_SHOW_UPDATE_DIALOG, true);
                        }
                        break;
                    case UpdateManager.UpdateStatus.No: // has no update
                    case UpdateManager.UpdateStatus.NoneWifi: // none wifi
                    case UpdateManager.UpdateStatus.Timeout: // time out
                        break;
                }
            }
        }, new UpdateManager.UpdateDialogListener(){
            @Override
            public void onClick(int updateStatus) {
                switch (updateStatus) {
                    case UpdateManager.UpdateStatus.Update:
                        if (!mUpdateManager.isForceUpdate()) {
                            delayExit(0);
                        }
                        break;
                    case UpdateManager.UpdateStatus.Ignore:
                    case UpdateManager.UpdateStatus.NotNow:
                        if (!mUpdateManager.isForceUpdate()) {
                            delayExit(0);
                        }
                        break;
                    case UpdateManager.DownloadStatus.Intercept:
                        break;
                }
                if (mHandler != null && !mHandler.hasMessages(MSG_EXIT)) {
                    checkUserSession();
                }
            }
        });
    }

    private void checkUserSession() {
        if (TextUtils.isEmpty(mPref.getSessionId()) || mPref.getBoolean(Preferences.KEY_IS_LOGOUT, false)) {
            mIsLogin = false;
            delayExit(SHOW_DURATION_AFTER_VERIFY);
            return;
        } else {
            mIsLogin = true;
            delayExit(SHOW_DURATION_TIMEOUT);
        }
        final long startVerifyTime = System.currentTimeMillis();
        ApiHelper.getInstance().userSessionCheck(new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (isFinishing()) {
                    return;
                }

                UserUtil.handleAfterLoginBySession(getApplicationContext());

                preLoadResource();
                mIsLogin = true;
                delayExitAfterVerify(startVerifyTime);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                if (isFinishing()) {
                    return;
                }
                if (ApiConstant.E_PARAM.equals(errorInfo)) {
                    mIsLogin = false;
                }
                delayExitAfterVerify(startVerifyTime);
            }

            @Override
            public void onFailure(String msg) {
                if (isFinishing()) {
                    return;
                }
                mIsLogin = !TextUtils.isEmpty(mPref.getSessionId());
                delayExitAfterVerify(startVerifyTime);
            }
        });
    }

    private void delayExitAfterVerify(long startVerifyTime) {
        if (System.currentTimeMillis() - startVerifyTime < SHOW_DURATION_AFTER_VERIFY) {
            delayExit(SHOW_DURATION_AFTER_VERIFY);
        } else {
            delayExit(0);
        }
    }

    private void delayExit(long delayDuration) {
        if (mHandler == null || mHandler.hasMessages(MSG_REFRESH_COUNTDOWN_TIME)) {
            return;
        }
        if (delayDuration > 0) {
            mHandler.removeMessages(MSG_EXIT);
            mHandler.sendEmptyMessageDelayed(MSG_EXIT, delayDuration);
        } else {
            mHandler.sendEmptyMessage(MSG_EXIT);
        }
    }

    protected void preLoadResource() {
        if (preLoadMainHome == null) {
            preLoadMainHome = View.inflate(this, com.hooview.app.R.layout.activity_home, null);
        }
    }
}
