package com.easyvaas.common.chat.db;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.easyvaas.common.chat.bean.ChatUser;

public class UserDao {
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    public static final String COLUMN_NAME_USER_ID = "userid";

    public static final String PREF_TABLE_NAME = "pref";
    public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public static final String ROBOT_TABLE_NAME = "robots";
    public static final String ROBOT_COLUMN_NAME_ID = "username";
    public static final String ROBOT_COLUMN_NAME_NICK = "nick";
    public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";

    public UserDao(Context context) {
        ChatDBManager.getInstance().onInit(context);
    }

    public String getAvatar(String username) {
        return ChatDBManager.getInstance().getAvatar(username);
    }

    public String getNickname(String username) {
        return ChatDBManager.getInstance().getNickname(username);
    }

    public String getUserId(String username) {
        return ChatDBManager.getInstance().getUserId(username);
    }

    public void saveContactList(List<ChatUser> contactList) {
        ChatDBManager.getInstance().saveContactList(contactList);
    }

    public Map<String, ChatUser> getContactList(Context context) {
        return ChatDBManager.getInstance().getContactList(context);
    }

    public void saveContact(ChatUser user) {
        ChatDBManager.getInstance().saveContact(user);
    }

    public void setDisabledGroups(List<String> groups) {
        ChatDBManager.getInstance().setDisabledGroups(groups);
    }

    public List<String> getDisabledGroups() {
        return ChatDBManager.getInstance().getDisabledGroups();
    }

    public void setDisabledIds(List<String> ids) {
        ChatDBManager.getInstance().setDisabledIds(ids);
    }

    public List<String> getDisabledIds() {
        return ChatDBManager.getInstance().getDisabledIds();
    }

}
