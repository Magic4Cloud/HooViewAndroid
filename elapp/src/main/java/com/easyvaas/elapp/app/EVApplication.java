/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.app;

import android.text.TextUtils;
import android.util.Log;

import com.easyvaas.common.feedback.FeedbackHelper;
import com.easyvaas.common.sharelogin.weibo.AccessTokenKeeper;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.chat.LiveHxHelper;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.event.UserInfoUpdateEvent;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.ApiUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.NukeSSLCerts;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.push.PushHelper;
import com.easyvaas.elapp.utils.CarNetCrashHandler;
import com.easyvaas.elapp.utils.ChannelUtil;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.FileUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.NetworkUtil;
import com.easyvaas.sdk.core.EVSdk;
import com.google.gson.Gson;
import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.hooview.app.BuildConfig;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

import static com.tencent.open.utils.Global.getContext;

public class EVApplication extends android.support.multidex.MultiDexApplication {
    private static final String TAG = EVApplication.class.getSimpleName();
    private static EVApplication app;
    private static User mUser;

    private boolean mIsHaveLaunchedHome;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

        if (BuildConfig.DEBUG) CarNetCrashHandler.getInstance().setCustomCrashHandler(getApplicationContext());

        ChannelUtil.initChannelFromApk(this);
        GrowingIO.startWithConfiguration(this, new Configuration()
                .useID()
                .trackAllFragments()
                .setChannel("dev"));

        //if (BuildConfig.DEBUG) {
        NukeSSLCerts.nuke();
        //}
        initRealm();
        FileUtil.checkCacheDir(this);
        String logoFilePath = getFilesDir().getAbsolutePath() + File.separator + FileUtil.LOGO_FILE_NAME;
        if (!new File(logoFilePath).exists()) {
            FileUtil.copyAssetsFiles(this, FileUtil.LOGO_FILE_NAME, logoFilePath);
        }

        String waterFilePath = getFilesDir().getAbsolutePath() + File.separator + FileUtil.WATERMARK_FILE_NAME;
        if (!new File(waterFilePath).exists()) {
            FileUtil.copyAssetsFiles(this, FileUtil.WATERMARK_FILE_NAME, waterFilePath);
        }

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        String registrationId = JPushInterface.getRegistrationID(this);
        Log.e("JPushInterface", "registrationId: .....  " + registrationId);
        PushHelper.getInstance(this).savePushInfo("JPushID", "", "j_" + registrationId);

        String name = Preferences.getInstance(this).getUserNumber();
        String nickname = Preferences.getInstance(this).getUserNickname();
        String sessionId = Preferences.getInstance(this).getSessionId();

//        ChatManager.getInstance().initChat(app, Constants.HX_CHAT_APP_KEY);
//        ChatManager.getInstance().setUserInfo(name, nickname, sessionId);
        FeedbackHelper.getInstance(this).init(this, Constants.APP_FEEDBACK_KEY);
        initReceiver();

//        EVSdk.enableDebugLog();
        if (NetworkUtil.isNetworkAvailable(this))
        {
            if (ApiConstant.isUserReleaseServer()) {
                EVSdk.init(app.getApplicationContext(), Constants.EV_APP_ID, Constants.EV_ACCESS_ID,
                        Constants.EV_SECRET_ID, "");
            } else {
                EVSdk.init(app.getApplicationContext(), Constants.EV_APP_ID_DEV, Constants.EV_ACCESS_ID_DEV,
                        Constants.EV_SECRET_ID_DEV, "");
            }
        }
        ApiUtil.checkSession(getContext());
        initHyphenate();
        getUser();
    }

    /**
     * 初始
     */
    private void initHyphenate() {
        LiveHxHelper.getInstance().initHxOptions(this);
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
        EVApplication.mUser = user;
        if (user == null) {
            return;
        }
        if (ApiConstant.isUserReleaseServer()) {
            EVSdk.init(app.getApplicationContext(), Constants.EV_APP_ID, Constants.EV_ACCESS_ID,
                    Constants.EV_SECRET_ID, mUser.getName());
        } else {
            EVSdk.init(app.getApplicationContext(), Constants.EV_APP_ID_DEV, Constants.EV_ACCESS_ID_DEV,
                    Constants.EV_SECRET_ID_DEV, mUser.getName());
        }

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
//
//        ChatManager.getInstance().setUserInfo(user.getName(), user.getNickname(), user.getLogourl(),
//                user.getSessionid());
        EventBus.getDefault().post(new UserInfoUpdateEvent());
        MobclickAgent.onProfileSignIn(user.getAuthtype(), user.getName());
    }

    public static void updateUserInfo() {
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

//    public static void reLogin() {
//        SingleToast.show(app, R.string.msg_session_invalid, SingleToast.LENGTH_LONG);
//        Intent intent = new Intent(app, LoginMainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        app.startActivity(intent);
//        Preferences.getInstance(app).logout(false);

//    }

    private void initReceiver() {
//        JumpActivityReceiver jumpActivityReceiver = new JumpActivityReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ChatConstants.EXTERNAL_ACTION_GO_FRIENDS_USER_INFO_ACTIVITY);
//        filter.addAction(ChatConstants.EXTERNAL_ACTION_GO_UPDATE_ASSERT_INFO);
//        filter.addAction(ChatConstants.EXTERNAL_ACTION_GO_WATCH_VIDEO);
//        filter.addAction(ChatConstants.EXTERNAL_ACTION_GO_UPDATE_APP_USER_INFO);
//        registerReceiver(jumpActivityReceiver, filter);
    }

    public void setHaveLaunchedHome(boolean launched) {
        mIsHaveLaunchedHome = launched;
    }

    public boolean isHaveLaunchedHome() {
        return mIsHaveLaunchedHome;
    }
//
//    @Override
//    public void onTerminate() {
////        super.onTerminate();
//    }

    public static boolean isLogin() {
        return mUser != null && !TextUtils.isEmpty(mUser.getSessionid());
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(RealmHelper.DB_NAME)
                .schemaVersion(1)
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
