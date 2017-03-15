package com.easyvaas.common.chat.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;

import com.easyvaas.common.chat.bean.InviteMessage;

public class InviteMessageDao {
    public static final String TABLE_NAME = "new_friends_msgs";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_FROM = "username";
    public static final String COLUMN_NAME_GROUP_ID = "groupid";
    public static final String COLUMN_NAME_GROUP_Name = "groupname";

    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";
    public static final String COLUMN_NAME_IS_INVITE_FROM_ME = "isInviteFromMe";

    public InviteMessageDao(Context context) {
        ChatDBManager.getInstance().onInit(context);
    }

    /**
     * 保存message
     *
     * @return 返回这条messaged在db中的id
     */
    public Integer saveMessage(InviteMessage message) {
        return ChatDBManager.getInstance().saveMessage(message);
    }

    /**
     * 更新message
     */
    public void updateMessage(int msgId, ContentValues values) {
        ChatDBManager.getInstance().updateMessage(msgId, values);
    }

    /**
     * 获取messges
     */
    public List<InviteMessage> getMessagesList() {
        return ChatDBManager.getInstance().getMessagesList();
    }

    public void deleteMessage(String from) {
        ChatDBManager.getInstance().deleteMessage(from);
    }
}
