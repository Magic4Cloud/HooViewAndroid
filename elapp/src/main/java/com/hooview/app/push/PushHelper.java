/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONObject;

import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.Logger;

public class PushHelper {
    private static final String TAG = PushHelper.class.getSimpleName();
    private static final String PREF_NAME = "push.local.dbfile";

    private static final String KEY_PUSH_CHANNEL_ID = "push_channelid";

    private static final int NOTIFICATION_ID = 0;
    private static final int NOTIFICATION_TYPE_DEFAULT = 0;
    private static final int NOTIFICATION_TYPE_VIDEO = NOTIFICATION_TYPE_DEFAULT;
    private static final int NOTIFICATION_TYPE_FOLLOWED = 2;
    private static final int NOTIFICATION_TYPE_MESSAGE = 3;

    private static PushHelper mInstance;

    private SharedPreferences mSettings;
    private Context mContext;

    private PushHelper(Context context) {
        mSettings = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.OnSharedPreferenceChangeListener mOnPrefChangeListener
                = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (KEY_PUSH_CHANNEL_ID.equals(key)) {
                    deviceRegister();
                }
            }
        };
        mSettings.registerOnSharedPreferenceChangeListener(mOnPrefChangeListener);
        mContext = context;
    }

    public static PushHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PushHelper(context);
        }
        return mInstance;
    }

    public void savePushInfo(String appId, String userId, String channelId) {
        SharedPreferences.Editor editor = mSettings.edit();
        if (!TextUtils.isEmpty(channelId) && !TextUtils.isEmpty(channelId.replaceAll("j_", ""))) {
            editor.putString(KEY_PUSH_CHANNEL_ID, channelId);
        }
        editor.apply();
    }

    public String getChannelID() {
        return mSettings.getString(KEY_PUSH_CHANNEL_ID, "");
    }

    public void deviceRegister() {
        String channelID = getChannelID();
        if (TextUtils.isEmpty(channelID) || TextUtils.isEmpty(channelID.replaceAll("j_", ""))) {
            Logger.w(TAG, "Channel id is empty, can not register device! " + channelID);
            return;
        }
        ApiHelper.getInstance().deviceRegister(null);
    }

    protected Intent assembleIntent(Context context, String customContent) {
        Intent intent = null;
        try {
            if (!Preferences.getInstance(context).isLogin()) {
                intent = new Intent(context,
                        Class.forName("LoginMainActivity"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                return intent;
            }

            String vid = "";
            String name = "";
            String groupId = "";
            String groupName = "";
            int type = NOTIFICATION_TYPE_DEFAULT;

            JSONObject customJson = new JSONObject(customContent);
            if (!customJson.isNull("vid")) {
                vid = customJson.getString("vid");
            }
            if (!customJson.isNull("type")) {
                type = customJson.getInt("type");
            }
            if (!customJson.isNull("name")) {
                name = customJson.getString("name");
            }
            if (!customJson.isNull("groupid")) {
                groupId = customJson.getString("groupid");
            }
            if (!customJson.isNull("groupname")) {
                groupName = customJson.getString("groupname");
            }
            switch (type) {
                case NOTIFICATION_TYPE_VIDEO:
                    intent = new Intent(context,
                            Class.forName("PlayerActivity"));
                    intent.putExtra(Constants.EXTRA_KEY_VIDEO_ID, vid);
                    intent.putExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, true);
                    break;
                case NOTIFICATION_TYPE_FOLLOWED:
                    intent = new Intent(context, Class
                            .forName("com.easyvaas.elapp.activity.usercenter.FriendsUserInfoActivity"));
                    intent.putExtra(Constants.EXTRA_KEY_USER_ID, name);
                    break;
                case NOTIFICATION_TYPE_MESSAGE:
                    intent = new Intent(context,
                            Class.forName("com.easyvaas.elapp.activity.MessageListActivity"));
                    intent.putExtra(Constants.EXTRA_KEY_MESSAGE_GROUP_ID, Long.parseLong(groupId));
                    intent.putExtra(Constants.EXTRA_KEY_MESSAGE_GROUP_NAME, groupName);
                    break;
                default:
                    intent = new Intent(context,
                            Class.forName("HomeTabActivity"));
                    break;
            }
            intent.putExtra(Constants.EXTRA_KEY_IS_FROM_PUSH, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            Logger.w(TAG, "assembleIntent failed !", e);
        }

        return intent;
    }

    protected void sendNM(Context context, String title, String desc, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notify = new Notification.Builder(context)
                .setAutoCancel(true)
                .setTicker(desc)
                .setSmallIcon(com.hooview.app.R.mipmap.app_logo)
                .setContentTitle(title)
                .setContentText(desc)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .setDefaults(Notification.DEFAULT_ALL)
                .getNotification();
        nm.notify(NOTIFICATION_ID, notify);
    }
}
