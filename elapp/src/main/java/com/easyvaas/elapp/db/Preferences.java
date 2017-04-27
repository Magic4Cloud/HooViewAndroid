/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.chat.LiveHxHelper;
import com.easyvaas.elapp.utils.Logger;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

public class Preferences {
    private static final String PREF_NAME = "appsys.local.dbfile";

    public static final String KEY_SERVER_TYPE = "server_type";
    public static final String KEY_SESSION_ID = "sessionid";
    public static final String KEY_USER_NUMBER = "name";
    public static final String KEY_USER_NICKNAME = "nickname";
    public static final String KEY_VERSION_CODE = "version_code";
    public static final String KEY_LOGIN_PHONE_NUMBER = "login_phone_number";
    public static final String KEY_LOGIN_QQ_NUMBER = "key_login_qq_number";
    public static final String KEY_IS_LOGOUT = "is_logout";
    public static final String KEY_IS_FORCE_UPDATE = "is_force_update";
    public static final String KEY_IS_HAVE_UPDATE = "is_have_update";
    public static final String KEY_IS_HAVE_SHOW_UPDATE_DIALOG = "is_have_show_update_dialog";
    public static final String KEY_LAST_CHECK_UPDATE_TIME = "last_check_update_time";
    public static final String KEY_SHOW_HOME_LIVE_TIP_COUNT = "show_home_live_tip_count";
    public static final String KEY_COUNTRY_NAME = "countryName";
    public static final String KEY_COUNTRY_CODE = "countryCode";
    public static final String KEY_CACHED_USER_INFO_JSON = "cache_user_info_json";
    public static final String KEY_CACHED_TOPICS_INFO_JSON = "key_cached_topics_info_json";
    public static final String KEY_CACHED_CAROUSEL_INFO_JSON = "key_cached_carousel_info_json";
    public static final String KEY_LAST_LIVE_INTERRUPT_VID = "last_live_interrupt_vid";
    public static final String KEY_LAST_LIVE_IS_AUDIO_MODE = "last_live_is_audio_mode";

    public static final String KEY_LAST_WATCH_PLAYBACK_VID = "last_watch_playback_vid";
    public static final String KEY_LAST_WATCH_PLAYBACK_POSITION = "last_watch_playback_position";
    public static final String KEY_HOME_CURRENT_TOPIC_ID = "key_current_topic_id";
    public static final String KEY_HOME_CURRENT_TOPIC_NAME = "key_current_topic_name";

    public static final String KEY_CACHE_LOCATION = "cache_location";

    public static final String KEY_PARAM_SERVER_LIKE_IMAGE_INFO = "server_like_image_info";
    public static final String KEY_PARAM_INVITE_TITLE = "key_param_invite_title";
    public static final String KEY_PARAM_INVITE_DESC = "key_param_invite_desc";
    public static final String KEY_PARAM_PAY_FAQ_URL = "key_param_pay_faq_url";
    public static final String KEY_PARAM_ASSET_FAQ_URL = "key_param_pay_faq_url";
    public static final String KEY_PARAM_CONTACT_US_URL = "key_param_contact_info_url";
    public static final String KEY_PARAM_FREE_USER_INFO_US_URL = "key_param_free_user_info_us_url";
    public static final String KEY_PARAM_WEB_CHAT_INFO_US_URL = "key_param_web_chat_info_us_url";
    public static final String KEY_PARAM_ASSET_E_COIN_ACCOUNT = "key_param_asset_e_coin_account";
    public static final String KEY_SEARCH_HISTORY_KEYWORD = "key_search_history_keyword";
    public static final String KEY_PARAM_SCREEN_LIST_JSON = "key_param_screen_list_json";
    public static final String KEY_PARAM_SCREEN_LAST_SHOW_INDEX = "key_param_screen_last_show_index";
    public static final String KEY_PARAM_PRIVATE_MANAGER_LIST_JSON = "key_param_private_manager_list_json";

    //params of watermark
    public static final String KEY_PARAM_WATERMARK_JSON = "key_param_watermark_json";
    public static final String KEY_PARAM_WATERMARK_IMAGE_PATH = "key_param_watermark_image_path";

    public static final String KEY_PARAM_SWITCH_SAVE_DURATION = "key_param_switch_save_duration";

    public static final String KEY_LIVE_GAG_TIPS_DIALOG = "key_live_gag_tips_dialog";
    public static final String KEY_REGISTER_USER_IMAGE = "user_Image";
    public static final String KEY_CHANNEL_NAME = "key_channel_name";
    public static final String KEY_CHANNEL_ID = "key_channel_id";
    public static final String KEY_HAVE_SHOW_4G_TIP = "key_have_show_4g_tip";
    public static final String KEY_LAST_SHARE_LIVE_TYPE = "key_last_share_live_type";
    public static final String KEY_LAST_CUSTOM_ALIPAY_COIN = "key_last_custom_alipay_coin";
    public static final String KEY_HOOOVIEW_EYE_TABS = "key_hoooview_eye_tabs";
    public static final String KEY_HOOOVIEW_SELECT_STOCK = "key_hoooview_select_stock";
    public static final String KEY_HOOOVIEW_SELECT_STOCK_STATUS = "key_hoooview_select_stock_status";

    private static Preferences mPreferences;

    private SharedPreferences mSettings;

    private Preferences(Context context) {
        mSettings = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static Preferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = new Preferences(context.getApplicationContext());
        }
        return mPreferences;
    }

    public boolean isContains(String key) {
        return mSettings.contains(key);
    }

    public int getInt(String name, int def) {
        return mSettings.getInt(name, def);
    }

    public void putInt(String name, int value) {
        SharedPreferences.Editor edit = mSettings.edit();
        edit.putInt(name, value);
        edit.apply();
    }

    public String getString(String key) {
        return mSettings.getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return mSettings.getString(key, defaultValue);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor edit = mSettings.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public long getLong(String key, long defaultValue) {
        return mSettings.getLong(key, defaultValue);
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor edit = mSettings.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSettings.getBoolean(key, defaultValue);
    }

    public void putBoolean(String key, boolean bool) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    public void putJsonObject(String key, Object object) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(key, new Gson().toJson(object));
        editor.apply();
    }

    public <T> T getJsonObject(String key, Class<T> tClass) {
        String json = getString(key, "");
        Logger.d("guojun", "getJsonObject: " + json);
        return new Gson().fromJson(json, tClass);
    }


    public void remove(String key) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.clear();
        editor.apply();
    }

    public String getUserNumber() {
        return getString(KEY_USER_NUMBER, "");
    }

    public String getUserNickname() {
        return getString(KEY_USER_NICKNAME, "");
    }

    public String getSessionId() {
        return getString(KEY_SESSION_ID, "");
    }

    public void storageUserInfo(User user) {
        String userId = user.getName();
        if (userId == null)
            userId = user.getUserid();
        putString(KEY_USER_NUMBER, userId);
        putString(KEY_SESSION_ID, user.getSessionid());
        putString(KEY_USER_NICKNAME, user.getNickname());
        putBoolean(KEY_IS_LOGOUT, false);
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(getUserNumber());
    }

    public void logout(boolean forceLogout) {
        if (forceLogout) {
            remove(KEY_USER_NICKNAME);
            remove(KEY_USER_NUMBER);
            remove(KEY_SESSION_ID);
            putBoolean(KEY_IS_LOGOUT, true);
            remove(KEY_CACHED_CAROUSEL_INFO_JSON);
            remove(KEY_CACHED_USER_INFO_JSON);
        }
        LiveHxHelper.getInstance().logout();
        MobclickAgent.onProfileSignOff();
    }
}

