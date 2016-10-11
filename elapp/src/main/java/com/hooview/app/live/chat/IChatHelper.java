/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.live.chat;

import java.util.List;
import java.util.Map;

import com.easyvaas.common.gift.bean.GiftEntity;

import com.hooview.app.bean.chat.ChatBarrage;
import com.hooview.app.bean.chat.ChatComment;
import com.hooview.app.bean.chat.ChatRedPackInfo;
import com.hooview.app.bean.chat.ChatUser;
import com.hooview.app.bean.chat.ChatVideoInfo;

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
    }
}
