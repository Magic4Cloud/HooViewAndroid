package com.easyvaas.common.chat.applib.model;

/**
 * UI Demo HX Model implementation
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.easyvaas.common.chat.applib.utils.HXPreferenceUtils;
import com.easyvaas.common.chat.db.UserDao;

public class DefaultHXSDKModel extends HXSDKModel {
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PWD = "pwd";
    UserDao dao = null;
    protected Context context = null;
    protected Map<Key, Object> valueCache = new HashMap<Key, Object>();

    public DefaultHXSDKModel(Context ctx) {
        context = ctx;
        HXPreferenceUtils.init(context);
    }

    @Override
    public void setSettingMsgNotification(boolean paramBoolean) {
        HXPreferenceUtils.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    @Override
    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if (val == null) {
            val = HXPreferenceUtils.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }

        return (Boolean) (val);
    }

    @Override
    public void setSettingMsgSound(boolean paramBoolean) {
        HXPreferenceUtils.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    @Override
    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if (val == null) {
            val = HXPreferenceUtils.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }

        return (Boolean) (val);
    }

    @Override
    public void setSettingMsgVibrate(boolean paramBoolean) {
        HXPreferenceUtils.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    @Override
    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if (val == null) {
            val = HXPreferenceUtils.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }

        return (Boolean) (val);
    }

    @Override
    public void setSettingMsgSpeaker(boolean paramBoolean) {
        HXPreferenceUtils.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    @Override
    public boolean getSettingMsgSpeaker() {
        Object val = valueCache.get(Key.SpakerOn);

        if (val == null) {
            val = HXPreferenceUtils.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }

        return (Boolean) (val);
    }

    @Override
    public boolean getUseHXRoster() {
        return false;
    }

    @Override
    public boolean saveHXId(String hxId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_USERNAME, hxId).commit();
    }

    @Override
    public String getHXId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_USERNAME, null);
    }

    @Override
    public boolean savePassword(String pwd) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_PWD, pwd).commit();
    }

    @Override
    public String getPwd() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_PWD, null);
    }

    @Override
    public String getAppProcessName() {
        return null;
    }

    public void setDisabledGroups(List<String> groups) {
        if (dao == null) {
            dao = new UserDao(context);
        }

        dao.setDisabledGroups(groups);
        valueCache.put(Key.DisabledGroups, groups);
    }

    public List<String> getDisabledGroups() {
        Object val = valueCache.get(Key.DisabledGroups);

        if (dao == null) {
            dao = new UserDao(context);
        }

        if (val == null) {
            val = dao.getDisabledGroups();
            valueCache.put(Key.DisabledGroups, val);
        }

        return (List<String>) val;
    }

    public void setDisabledIds(List<String> ids) {
        if (dao == null) {
            dao = new UserDao(context);
        }

        dao.setDisabledIds(ids);
        valueCache.put(Key.DisabledIds, ids);
    }

    public List<String> getDisabledIds() {
        Object val = valueCache.get(Key.DisabledIds);

        if (dao == null) {
            dao = new UserDao(context);
        }

        if (val == null) {
            val = dao.getDisabledIds();
            valueCache.put(Key.DisabledIds, val);
        }

        return (List<String>) val;
    }

    public void allowChatroomOwnerLeave(boolean value) {
        HXPreferenceUtils.getInstance().setSettingAllowChatRoomOwnerLeave(value);
    }

    public boolean isChatroomOwnerLeaveAllowed() {
        return HXPreferenceUtils.getInstance().getSettingAllowChatroomOwnerLeave();
    }

    public void setGroupsSynced(boolean synced) {
        HXPreferenceUtils.getInstance().setGroupsSynced(synced);
    }

    public boolean isGroupsSynced() {
        return HXPreferenceUtils.getInstance().isGroupsSynced();
    }

    public void setContactSynced(boolean synced) {
        HXPreferenceUtils.getInstance().setContactSynced(synced);
    }

    public boolean isContactSynced() {
        return HXPreferenceUtils.getInstance().isContactSynced();
    }

    public void setBlacklistSynced(boolean synced) {
        HXPreferenceUtils.getInstance().setBlacklistSynced(synced);
    }

    public boolean isBacklistSynced() {
        return HXPreferenceUtils.getInstance().isBlacklistSynced();
    }

    enum Key {
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
