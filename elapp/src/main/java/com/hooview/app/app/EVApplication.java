/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.app;

import java.io.File;

import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import cn.jpush.android.api.JPushInterface;

import com.easyvaas.sdk.core.EVSdk;
import com.google.gson.Gson;
import com.hooview.app.activity.login.LoginMainActivity;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.NukeSSLCerts;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.push.PushHelper;
import com.hooview.app.receiver.JumpActivityReceiver;
import com.hooview.app.utils.ChannelUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.FileUtil;
import com.hooview.app.utils.Logger;
import com.umeng.analytics.MobclickAgent;

import com.easyvaas.common.chat.ChatManager;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.feedback.FeedbackHelper;
import com.easyvaas.common.sharelogin.weibo.AccessTokenKeeper;

import com.hooview.app.bean.user.User;
import com.hooview.app.utils.SingleToast;

public class EVApplication extends android.support.multidex.MultiDexApplication {
    private static final String TAG = EVApplication.class.getSimpleName();
    private static EVApplication app;
    private static User mUser;

    private boolean mIsHaveLaunchedHome;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        ChannelUtil.initChannelFromApk(this);

        //if (BuildConfig.DEBUG) {
            NukeSSLCerts.nuke();
        //}

        FileUtil.checkCacheDir();
        String logoFilePath = getFilesDir().getAbsolutePath() + File.separator + FileUtil.LOGO_FILE_NAME;
        if (!new File(logoFilePath).exists()) {
            FileUtil.copyAssetsFiles(this, FileUtil.LOGO_FILE_NAME, logoFilePath);
        }

        String waterFilePath = getFilesDir().getAbsolutePath() + File.separator + FileUtil.WATERMARK_FILE_NAME;
        if (!new File(waterFilePath).exists()) {
            FileUtil.copyAssetsFiles(this, FileUtil.WATERMARK_FILE_NAME, waterFilePath);
        }

        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        PushHelper.getInstance(this)
                .savePushInfo("JPushID", "", "j_" + JPushInterface.getRegistrationID(this));

        String name = Preferences.getInstance(this).getUserNumber();
        String nickname = Preferences.getInstance(this).getUserNickname();
        String sessionId = Preferences.getInstance(this).getSessionId();

        ChatManager.getInstance().initChat(app, Constants.HX_CHAT_APP_KEY);
        ChatManager.getInstance().setUserInfo(name, nickname, sessionId);
        FeedbackHelper.getInstance(this).init(this, Constants.APP_FEEDBACK_KEY);
        initReceiver();

        //EVSdk.enableDebugLog();
    }

    public static EVApplication getApp() {
        return app;
    }

    public static User getUser() {
        if (mUser == null) {
            //TODO work around method, we muss to insure user is not null
            String userJson = Preferences.getInstance(app).getString(Preferences.KEY_CACHED_USER_INFO_JSON);
            if (!TextUtils.isEmpty(userJson)) {
                mUser = new Gson().fromJson(userJson, User.class);
                User user = new User();
                user.setName(Preferences.getInstance(app).getUserNumber());
                user.setSessionid(Preferences.getInstance(app).getSessionId());
                updateUserInfo();
            }
        }
        return mUser;
    }

    public static void setUser(User user) {
        Logger.d(TAG, "serUser(), name: " + user.getName());
        EVApplication.mUser = user;
        EVSdk.init(app.getApplicationContext(), Constants.EV_APP_ID, Constants.EV_ACCESS_ID,
                Constants.EV_SECRET_ID, mUser.getName());
        if (user.getAuth() != null) {
            for (int i = 0, n = user.getAuth().size(); i < n; i++) {
                User.AuthEntity authEntity = user.getAuth().get(i);
                if (User.AUTH_TYPE_SINA.equals(authEntity.getType())) {
                    AccessTokenKeeper
                            .writeAccessToken(getApp(), Constants.WEIBO_APP_ID, authEntity.getToken(),
                                    DateTimeUtil.formatServerDate(authEntity.getExpire_time()) - System
                                            .currentTimeMillis());
                } else if (User.AUTH_TYPE_PHONE.equals(authEntity.getType())) {
                    Preferences.getInstance(app)
                            .putString(Preferences.KEY_LOGIN_PHONE_NUMBER, authEntity.getToken());
                } else if (User.AUTH_TYPE_QQ.equals(authEntity.getType())) {
                    Preferences.getInstance(app)
                            .putString(Preferences.KEY_LOGIN_QQ_NUMBER, authEntity.getToken());
                }
            }
        }

        String userJson = new Gson().toJson(user);
        Preferences.getInstance(app).putString(Preferences.KEY_CACHED_USER_INFO_JSON, userJson);

        ChatManager.getInstance().setUserInfo(user.getName(), user.getNickname(), user.getLogourl(),
                user.getSessionid());

        MobclickAgent.onProfileSignIn(user.getAuthtype(), user.getName());
    }

    private static void updateUserInfo() {
        String sessionId = Preferences.getInstance(app).getSessionId();
        if (TextUtils.isEmpty(sessionId)) {
            return;
        }
        ApiHelper.getInstance().getUserInfo("", new MyRequestCallBack<User>() {
                    @Override
                    public void onSuccess(User result) {
                        setUser(result);
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        Logger.e(TAG, "Update user info error, " + errorInfo);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                    }
                });
    }

    public static void reLogin() {
        SingleToast.show(app, com.hooview.app.R.string.msg_session_invalid, SingleToast.LENGTH_LONG);
        Intent intent = new Intent(app, LoginMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        app.startActivity(intent);
        Preferences.getInstance(app).logout(false);
    }

    private void initReceiver() {
        JumpActivityReceiver jumpActivityReceiver = new JumpActivityReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ChatConstants.EXTERNAL_ACTION_GO_FRIENDS_USER_INFO_ACTIVITY);
        filter.addAction(ChatConstants.EXTERNAL_ACTION_GO_UPDATE_ASSERT_INFO);
        filter.addAction(ChatConstants.EXTERNAL_ACTION_GO_WATCH_VIDEO);
        filter.addAction(ChatConstants.EXTERNAL_ACTION_GO_UPDATE_APP_USER_INFO);
        registerReceiver(jumpActivityReceiver, filter);
    }

    public void setHaveLaunchedHome(boolean launched) {
        mIsHaveLaunchedHome = launched;
    }

    public boolean isHaveLaunchedHome() {
        return mIsHaveLaunchedHome;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
