package com.easyvaas.elapp.ui.base;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.elapp.bean.chat.ChatBarrage;
import com.easyvaas.elapp.bean.chat.ChatComment;
import com.easyvaas.elapp.bean.chat.ChatRedPackInfo;
import com.easyvaas.elapp.bean.chat.ChatUser;
import com.easyvaas.elapp.bean.chat.ChatVideoInfo;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.live.chat.ChatHelper;
import com.easyvaas.elapp.live.chat.IChatHelper;
import com.easyvaas.elapp.utils.Logger;

import java.util.List;
import java.util.Map;

public class BaseChatFragment extends BaseFragment implements IChatHelper.ChatCallback {
    private static final String TAG = "BaseChatFragment";
    protected ChatHelper mChatHelper;
    protected VideoEntity mCurrentVideo;

    public BaseChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setVideoEntity(VideoEntity videoEntity) {
        this.mCurrentVideo = videoEntity;
    }


    public void chatServerInit(boolean isResetData) {
        if (mChatHelper != null) {
            mChatHelper.chatServerInit(isResetData);
        } else if (mCurrentVideo != null) {
            mChatHelper = new ChatHelper(getContext(), mCurrentVideo.getVid(), this);
            mChatHelper.chatServerInit(isResetData);
        }
    }

    public void chatServerDestroy() {
        if (mChatHelper != null) {
            mChatHelper.chatServerDestroy();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatServerDestroy();
    }

    @Override
    public void onConnectError(String errorInfo) {
        Logger.d(TAG, "onConnectError: " + errorInfo);
    }

    @Override
    public void onJoinOK() {
        Logger.d(TAG, "onJoinOK");

    }

    @Override
    public void onNewComment(ChatComment chatComment) {
        Logger.d(TAG, "onNewComment");

    }

    @Override
    public void onNewGift(GiftEntity giftEntity) {
        Logger.d(TAG, "onNewGift");

    }

    @Override
    public void onNewRedPack(Map<String, ChatRedPackInfo> redPackEntityMap) {
        Logger.d(TAG, "onNewRedPack");

    }

    @Override
    public void onBarrage(ChatBarrage chatBarrage) {
        Logger.d(TAG, "onBarrage");
    }

    @Override
    public void onInfoUpdate(ChatVideoInfo chatVideoInfo) {
        Logger.d(TAG, "onInfoUpdate");

    }

    @Override
    public void onStatusUpdate(int status) {
        Logger.d(TAG, "onStatusUpdate");

    }

    @Override
    public void onLike(int likeCount) {
        Logger.d(TAG, "onLike");

    }

    @Override
    public int getLikeCount() {
        Logger.d(TAG, "getLikeCount");
        return 0;
    }

    @Override
    public void onUserJoinList(List<ChatUser> watchingUsers) {

    }

    @Override
    public void onUserLeaveList(List<ChatUser> watchingUsers) {

    }

    @Override
    public void onCallRequest(String name, String nickname, String logoUrl) {

    }

    @Override
    public void onCallAccept(String callId) {

    }

    @Override
    public void onCallCancel(String name) {

    }

    @Override
    public void onCallEnd(String name) {

    }
}
