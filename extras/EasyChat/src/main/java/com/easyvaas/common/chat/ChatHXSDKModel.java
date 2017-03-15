package com.easyvaas.common.chat;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.easyvaas.common.chat.applib.model.DefaultHXSDKModel;
import com.easyvaas.common.chat.bean.ChatUser;
import com.easyvaas.common.chat.db.ChatDBManager;
import com.easyvaas.common.chat.db.UserDao;

public class ChatHXSDKModel extends DefaultHXSDKModel {

    public ChatHXSDKModel(Context ctx) {
        super(ctx);
    }

    public boolean getUseHXRoster() {
        return true;
    }

    public boolean isDebugMode() {
        return BuildConfig.DEBUG;
    }

    public String getNickname(String username) {
        UserDao dao = new UserDao(context);
        return dao.getNickname(username);
    }

    public String getUserId(String username) {
        UserDao dao = new UserDao(context);
        return dao.getUserId(username);
    }

    public boolean saveContactList(List<ChatUser> contactList) {
        UserDao dao = new UserDao(context);
        dao.saveContactList(contactList);
        return true;
    }

    public Map<String, ChatUser> getContactList() {
        UserDao dao = new UserDao(context);
        return dao.getContactList(context);
    }

    public void saveContact(ChatUser user) {
        UserDao dao = new UserDao(context);
        dao.saveContact(user);
    }

    public void closeDB() {
        ChatDBManager.getInstance().closeDB();
    }

    @Override
    public String getAppProcessName() {
        return context.getPackageName();
    }
}
