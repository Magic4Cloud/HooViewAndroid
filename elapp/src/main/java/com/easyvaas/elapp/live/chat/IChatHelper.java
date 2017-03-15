/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.live.chat;

import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.elapp.bean.chat.ChatBarrage;
import com.easyvaas.elapp.bean.chat.ChatComment;
import com.easyvaas.elapp.bean.chat.ChatRedPackInfo;
import com.easyvaas.elapp.bean.chat.ChatUser;
import com.easyvaas.elapp.bean.chat.ChatVideoInfo;

import java.util.List;
import java.util.Map;

public interface IChatHelper {
    int STATE_STATUS_SHARING = 2;
    int LIVING = 1;    // Live
    int LIVE_STOP = 0; // Vod

    void chatServerInit(boolean isResetData);

    void chatSendComment(ChatComment comment);

    void chatSendBarrage(ChatComment comment);

    void chatLiveOwnerShutUp(String name, boolean shutUp);

    void chatLiveSetManager(String userName, boolean setManager);

    void chatGetComments(long commentId);

    void chatServerDestroy();

    interface ChatCallback {
        void onConnectError(String errorInfo);

        void onJoinOK();

        void onNewComment(ChatComment chatComment);

        void onNewGift(GiftEntity giftEntity);

        void onNewRedPack(Map<String, ChatRedPackInfo> redPackEntityMap);

        void onBarrage(ChatBarrage chatBarrage);

        void onInfoUpdate(ChatVideoInfo chatVideoInfo);

        void onStatusUpdate(int status);

        void onLike(int likeCount);

        int getLikeCount();

        void onUserJoinList(List<ChatUser> watchingUsers);

        void onUserLeaveList(List<ChatUser> watchingUsers);

        void onCallRequest(String name, String nickname, String logoUrl);

        void onCallAccept(String callId);

        void onCallCancel(String name);

        void onCallEnd(String name);
    }
}
