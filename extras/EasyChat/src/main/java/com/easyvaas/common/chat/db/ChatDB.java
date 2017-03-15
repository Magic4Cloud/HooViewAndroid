package com.easyvaas.common.chat.db;

import android.content.Context;
import android.content.SharedPreferences;

public class ChatDB {
    public static final String KEY_USER_NUMBER = "name";
    public static final String KEY_USER_NICKNAME = "nickname";
    public static final String KEY_USER_LOGO = "user_logo";
    public static final String KEY_IS_HAVE_UNREAD_MESSAGE = "is_ignore_update";
    public static final String KEY_NOTICE_PUSH_NEW_CHAT = "notice_push_new_chat";
    public static final String KEY_FRIEND_LIST_JSON = "key_friend_list_json";
    public static final String KEY_BLACK_LIST_JSON = "key_black_list_json_array";

    private static final String PREF_NAME = "chat.local.dbfile";
    private static final String KEY_CHANNEL = "channel";
    private static final String KEY_SESSION_ID = "sessionid";

    private static ChatDB mInstance;
    private static SharedPreferences mSettings;

    private ChatDB(Context context) {
        if (mSettings == null) {
            mSettings = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static ChatDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ChatDB(context);
        }
        return mInstance;
    }

    public void updateUserInfo(Context ctx, String name, String sessionId) {
        updateInfo(ctx, getChannel(ctx), name, sessionId);
    }

    private static void updateInfo(Context ctx, String channel, String name, String sessionId) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(KEY_CHANNEL, channel);
        editor.putString(KEY_USER_NUMBER, name);
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
        return ChatDB.getInstance(context).getString(KEY_CHANNEL);
    }

    public static String getSessionId(Context context) {
        return ChatDB.getInstance(context).getString(KEY_SESSION_ID);
    }

    public String getUserNumber() {
        return getString(KEY_USER_NUMBER, "");
    }

    public String getUserNickname() {
        return getString(KEY_USER_NICKNAME, "");
    }
}
