package com.easyvaas.common.statistics.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class StatisticsDB {
    public static final String KEY_WS_BEST_IP = "key_ws_best_ip";
    public static final String KEY_LAST_GET_WS_BEST_IP = "key_last_get_ws_best_ip";
    public static final String KEY_LATEST_URL = "key_latest_url";
    public static final String KEY_WS_BEST_IP_PUBLISH = "key_ws_best_ip_publish";
    public static final String KEY_LAST_GET_WS_BEST_IP_PUBLISH = "key_last_get_ws_best_ip_publish";
    public static final String KEY_LATEST_URL_PUBLISH = "key_latest_url_publish_publish";
    public static final String KEY_WS_BEST_IP_VALIDITY = "key_ws_best_ip_validity";

    private static final String PREF_NAME = "statistics.local.dbfile";
    private static SharedPreferences mSettings;
    private static final String KEY_CHANNEL = "channel";
    private static final String KEY_NAME = "name";
    private static final String KEY_SESSION_ID = "sessionid";

    private static StatisticsDB mInstance;

    private StatisticsDB(Context context) {
        if (mSettings == null) {
            mSettings = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static StatisticsDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StatisticsDB(context);
        }
        return mInstance;
    }

    public static void updateUserInfo(Context ctx, String name, String sessionId) {
        updateInfo(ctx, getChannel(ctx), name, sessionId);
    }

    public static void updateChannel(Context ctx, String channel) {
        updateInfo(ctx, channel, getName(ctx), getSessionId(ctx));
    }

    private static void updateInfo(Context ctx, String channel, String name, String sessionId) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(KEY_CHANNEL, channel);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_SESSION_ID, sessionId);
        editor.apply();
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return mSettings.getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return mSettings.getString(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSettings.getBoolean(key, defaultValue);
    }

    public void putBoolean(String key, boolean bool) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    public long getLong(String key, long defaultValue) {
        return mSettings.getLong(key, defaultValue);
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor edit = mSettings.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public static String getChannel(Context context) {
        return StatisticsDB.getInstance(context).getString(KEY_CHANNEL);
    }

    public static String getName(Context context) {
        return StatisticsDB.getInstance(context).getString(KEY_NAME);
    }

    public static String getSessionId(Context context) {
        return StatisticsDB.getInstance(context).getString(KEY_SESSION_ID);
    }
}
