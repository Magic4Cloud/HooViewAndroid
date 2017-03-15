package com.easyvaas.common.chat.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.util.Pair;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;

import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.bean.UserArray;
import com.easyvaas.common.chat.net.MyRequestCallBack;

public class GroupUtil {
    public static ArrayList<Pair<Long, EMGroup>> loadGroupWithRecentChat(Context context,
            List<EMGroup> groups, MyRequestCallBack<UserArray> callBack) {
        long lastMsgTime = 0;
        List<String> imUserList = new ArrayList<>();
        ArrayList<Pair<Long, EMGroup>> sortList = new ArrayList<>();
        for (EMGroup emGroup : groups) {
            String imUserName = emGroup.getOwner();
            if (!ChatUserUtil.isExistImUserInfo(emGroup.getOwner())) {
                imUserList.add(imUserName);
            }
            EMConversation conversation = ChatHXSDKHelper.getInstance()
                    .getGroupConversation(emGroup.getGroupId());
            if (conversation.getMsgCount() > 0){
                lastMsgTime = conversation.getLastMessage().getMsgTime();
            } else {
                lastMsgTime = 0;
            }
            sortList.add(new Pair<>(lastMsgTime, emGroup));
        }
        Collections.sort(sortList, new Comparator<Pair<Long, EMGroup>>() {
            @Override
            public int compare(final Pair<Long, EMGroup> con1, final Pair<Long, EMGroup> con2) {
                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        ChatUserUtil.getUserBasicInfoList(context, imUserList, callBack);

        return sortList;
    }
}
