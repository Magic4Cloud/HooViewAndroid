package com.easyvaas.elapp.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class HXPreferenceUtils {
    public static final String PREFERENCE_NAME = "chat_hx_saveInfo";
    private static SharedPreferences mSharedPreferences;
    private static HXPreferenceUtils mPreferenceUtils;
    private static SharedPreferences.Editor editor;

    private String SHARED_KEY_SETTING_NOTIFICATION = "key_setting_notification";
    private String SHARED_KEY_SETTING_SOUND = "key_setting_sound";
    private String SHARED_KEY_SETTING_VIBRATE = "key_setting_vibrate";
    private String SHARED_KEY_SETTING_SPEAKER = "key_setting_speaker";

    private static String SHARED_KEY_SETTING_CHAT_ROOM_OWNER_LEAVE = "key_setting_chat_room_owner_leave";
    private static String SHARED_KEY_SETTING_GROUPS_SYNCED = "KEY_SETTING_GROUPS_SYNCED";
    private static String SHARED_KEY_SETTING_CONTACT_SYNCED = "KEY_SETTING_CONTACT_SYNCED";
    private static String SHARED_KEY_SETTING_BLACKLIST_SYNCED = "KEY_SETTING_BLACKLIST_SYNCED";

    private static String SHARED_KEY_CURRENT_USER_NICK = "KEY_CURRENT_USER_NICK";
    private static String SHARED_KEY_CURRENT_USER_AVATAR = "KEY_CURRENT_USER_AVATAR";

    private HXPreferenceUtils(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new HXPreferenceUtils(cxt);
        }
    }

    /**
     * 单例模式，获取instance实例
     */
    public static HXPreferenceUtils getInstance() {
        if (mPreferenceUtils == null) {
            throw new RuntimeException("please init first!");
        }

        return mPreferenceUtils;
    }

    public void setSettingMsgNotification(boolean paramBoolean) {
        editor.putBoolean(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean);
        editor.commit();
    }

    public boolean getSettingMsgNotification() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_NOTIFICATION, true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        editor.putBoolean(SHARED_KEY_SETTING_SOUND, paramBoolean);
        editor.commit();
    }

    public boolean getSettingMsgSound() {

        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SOUND, true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        editor.putBoolean(SHARED_KEY_SETTING_VIBRATE, paramBoolean);
        editor.commit();
    }

    public boolean getSettingMsgVibrate() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_VIBRATE, true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        editor.putBoolean(SHARED_KEY_SETTING_SPEAKER, paramBoolean);
        editor.commit();
    }

    public boolean getSettingMsgSpeaker() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SPEAKER, true);
    }

    public void setSettingAllowChatRoomOwnerLeave(boolean value) {
        editor.putBoolean(SHARED_KEY_SETTING_CHAT_ROOM_OWNER_LEAVE, value);
        editor.commit();
    }

    public boolean getSettingAllowChatroomOwnerLeave() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_CHAT_ROOM_OWNER_LEAVE, true);
    }

    public void setGroupsSynced(boolean synced) {
        editor.putBoolean(SHARED_KEY_SETTING_GROUPS_SYNCED, synced);
        editor.commit();
    }

    public boolean isGroupsSynced() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_GROUPS_SYNCED, false);
    }

    public void setContactSynced(boolean synced) {
        editor.putBoolean(SHARED_KEY_SETTING_CONTACT_SYNCED, synced);
        editor.commit();
    }

    public boolean isContactSynced() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_CONTACT_SYNCED, false);
    }

    public void setBlacklistSynced(boolean synced) {
        editor.putBoolean(SHARED_KEY_SETTING_BLACKLIST_SYNCED, synced);
        editor.commit();
    }

    public boolean isBlacklistSynced() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_BLACKLIST_SYNCED, false);
    }

    public void setCurrentUserNick(String nick) {
        editor.putString(SHARED_KEY_CURRENT_USER_NICK, nick);
        editor.commit();
    }

    public void setCurrentUserAvatar(String avatar) {
        editor.putString(SHARED_KEY_CURRENT_USER_AVATAR, avatar);
        editor.commit();
    }

    public String getCurrentUserNick() {
        return mSharedPreferences.getString(SHARED_KEY_CURRENT_USER_NICK, null);
    }

    public String getCurrentUserAvatar() {
        return mSharedPreferences.getString(SHARED_KEY_CURRENT_USER_AVATAR, null);
    }

    public void removeCurrentUserInfo() {
        editor.remove(SHARED_KEY_CURRENT_USER_NICK);
        editor.remove(SHARED_KEY_CURRENT_USER_AVATAR);
        editor.commit();
    }
}
