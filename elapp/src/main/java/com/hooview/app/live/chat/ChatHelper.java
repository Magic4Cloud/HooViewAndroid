/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.live.chat;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.easyvaas.sdk.message.wrapper.EVMessage;
import com.easyvaas.sdk.message.wrapper.MessageCallback;
import com.google.gson.Gson;

import com.easyvaas.common.gift.bean.GiftEntity;

import com.hooview.app.bean.chat.ChatBarrage;
import com.hooview.app.bean.chat.ChatBarrageEntity;
import com.hooview.app.bean.chat.ChatComment;
import com.hooview.app.bean.chat.ChatCommentEntity;
import com.hooview.app.bean.chat.ChatRedPackInfo;
import com.hooview.app.bean.chat.ChatStatusEntity;
import com.hooview.app.bean.chat.ChatVideoInfo;
import com.hooview.app.bean.user.BaseUserEntityArray;
import com.hooview.app.live.manager.LiveRoomShutUpUtil;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Logger;

public class ChatHelper implements IChatHelper {
    private static final String TAG = "ChatHelper";
    private static final int MSG_SEND_LIKE_COUNT = 100;
    private static final int INTERVAL_SEND_LIKE_COUNT = 3000;

    private IChatHelper.ChatCallback mCallback;
    private Context mContext;
    private EVMessage mEVMessage;
    private Handler mHandler;
    private String mVid;
    private ChatVideoInfo mVideoInfo;
    private String mTopicId;

    public ChatHelper(Context context, String vid, ChatCallback callback) {
        mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_SEND_LIKE_COUNT:
                        chatAddLikeCount();
                        sendEmptyMessageDelayed(MSG_SEND_LIKE_COUNT, INTERVAL_SEND_LIKE_COUNT);
                        break;
                }
            }
        };
        mContext = context;
        mCallback = callback;
        mEVMessage = new EVMessage(context);
        mVideoInfo = new ChatVideoInfo();
        mVid = vid;

        final MessageCallback messageCallback = new MessageCallback() {
            @Override
            public void onConnected() {
                Logger.d(TAG, "onConnected");
                mEVMessage.joinTopic(mVid);
            }

            @Override
            public void onJoinOK(final List<String> users, String topicId) {
                Logger.d(TAG, "onJoinOk content: " + (users != null ? users.toString() : "")
                        + " topic: " + topicId);
                mTopicId = topicId;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onJoinOK();
                    }
                });
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Need change when message server callback ready
                        mVideoInfo.setWatching_count(users != null ? users.size() : 0);
                        mCallback.onInfoUpdate(mVideoInfo);
                    }
                });
                if (users != null) {
                    usersChanged(users, true);
                    Logger.d(TAG, "onJoinOk ... size: " + users.size());
                }
                mHandler.sendEmptyMessageDelayed(MSG_SEND_LIKE_COUNT, INTERVAL_SEND_LIKE_COUNT);
            }

            @Override
            public void onNewMessage(String message, String userdata, String userId, String topicId) {
                Logger.d(TAG, "onNewMessage message:" + message + "content: " + userdata
                        + " userid: " + userId + " topic: " + topicId);
                String liveJson = "";
                String liveStatusJson = "";
                if (userdata != null && userdata.contains("msg=")) {
                    String[] json = userdata.split("=");
                    if (json.length > 1) {
                        liveJson = json[1];
                    }
                    try {
                        liveStatusJson = URLDecoder.decode(liveJson, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Logger.w(TAG, "Decode live status json failed!", e);
                    }
                }
                if (!TextUtils.isEmpty(liveStatusJson)) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(liveStatusJson);
                        JSONObject lvstJson = jsonObject.getJSONObject("lvst");
                        if (lvstJson != null && !TextUtils.isEmpty(lvstJson.toString())) {
                            if (lvstJson.has("st")) {
                                int liveStatus = (Integer) lvstJson.get("st");
                                mCallback.onStatusUpdate(liveStatus);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    final ChatStatusEntity entity = new Gson().fromJson(userdata, ChatStatusEntity.class);
                    final ChatComment comment = entity != null ? entity.getComment(userId, message) : null;
                    final ChatBarrage barrage = entity != null ? entity.getBarrage(userId, message) : null;
                    final GiftEntity gift = entity != null ? entity.getGiftEntity(userId) : null;
                    final ChatRedPackInfo redPackInfo = entity != null ? entity.getRedPack(userdata) : null;
                    new Handler(mContext.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (comment != null) {
                                mCallback.onNewComment(comment);
                            }
                            if (barrage != null) {
                                mCallback.onBarrage(barrage);
                            }
                            if (gift != null) {
                                mCallback.onNewGift(gift);
                            }
                            if (redPackInfo != null) {
                                Map<String, ChatRedPackInfo> map = new HashMap<>();
                                map.put(redPackInfo.getId(), redPackInfo);
                                mCallback.onNewRedPack(map);
                            }
                        }
                    });
                }
            }

            @Override
            public void onUserJoin(List<String> list) {
                usersChanged(list, true);
                Logger.d(TAG, "onUserJoin ... size: " + list.size());
            }

            @Override
            public void onUserLeave(List<String> list) {
                usersChanged(list, false);
                Logger.d(TAG, "onUserLeave ... size: " + list.size());
            }

            @Override
            public void onLikeCount(int i) {
                mVideoInfo.setLike_count(mVideoInfo.getLike_count() + i);
                mCallback.onInfoUpdate(mVideoInfo);
                mCallback.onLike(i);
            }

            @Override
            public void onWatchingCount(int i) {
                mVideoInfo.setWatching_count(i);
                mCallback.onInfoUpdate(mVideoInfo);
            }

            @Override
            public void onWatchedCount(int i) {
                mVideoInfo.setWatch_count(i);
                mCallback.onInfoUpdate(mVideoInfo);
            }

            @Override
            public void onRiceRoll(int i) {
                mVideoInfo.setRiceRoll_count(i);
                mCallback.onInfoUpdate(mVideoInfo);
            }

            @Override
            public void onManagers(List<String> list) {
                LiveRoomShutUpUtil.cacheLiveManagers(list, mContext);
            }

            @Override
            public void onShutups(List<String> list) {
                LiveRoomShutUpUtil.addLiveShutUp(list, mVid);
            }

            @Override
            public void onKickUsers(List<String> list) {

            }

            @Override
            public void onPrivateInfo(List<String> list) {
            }

            @Override
            public void onError(int i) {
                mCallback.onConnectError("i");
                Logger.d(TAG, "onError " + i);
            }

            @Override
            public void onReconnecting() {

            }

            @Override
            public void onReconnected() {

            }

            @Override
            public void onReconnectFailed() {

            }

            @Override
            public void onClose() {
                Logger.d(TAG, "onClose");
                mTopicId = "";
                mHandler.removeMessages(MSG_SEND_LIKE_COUNT);
            }
        };
        mEVMessage.setMessageCallback(messageCallback);
    }

    @Override
    public void chatServerInit(boolean isResetData) {
        mEVMessage.connect();
    }

    @Override
    public void chatSendComment(ChatComment comment) {
        ChatStatusEntity statusEntity = new ChatStatusEntity();
        ChatCommentEntity commentEntity = new ChatCommentEntity();
        commentEntity.setNk(comment.getNickname());
        commentEntity.setRnk(comment.getReply_nickname());
        commentEntity.setRnm(comment.getReply_name());
        statusEntity.setExct(commentEntity);
        String json = new Gson().toJson(statusEntity);
        Logger.d(TAG, "chatSendComment json: " + json);
        mEVMessage.send(mVid, comment.getContent(), json);
    }

    @Override
    public void chatSendBarrage(ChatComment comment) {
        ChatStatusEntity statusEntity = new ChatStatusEntity();
        ChatBarrageEntity barrageEntity = new ChatBarrageEntity();
        barrageEntity.setNk(comment.getNickname());
        barrageEntity.setLg(comment.getLogourl());
        statusEntity.setExbr(barrageEntity);
        String json = new Gson().toJson(statusEntity);
        Logger.d(TAG, "chatSendBarrage json: " + json);
        mEVMessage.send(mVid, comment.getContent(), json);
    }

    @Override
    public void chatLiveOwnerShutUp(String name, boolean shutUp) {

    }

    @Override
    public void chatLiveSetManager(String userName, boolean setManager) {

    }

    @Override
    public void chatGetComments(long commentId) {

    }

    private void chatAddLikeCount() {
        mEVMessage.addLikeCount(mTopicId, mCallback.getLikeCount());
    }

    @Override
    public void chatServerDestroy() {
        mEVMessage.leaveTopic(mVid);
        mEVMessage.close();
    }

    private void usersChanged(List<String> users, final boolean join) {
        ApiHelper.getInstance().getUserInfos(users, new MyRequestCallBack<BaseUserEntityArray>() {
            @Override
            public void onSuccess(BaseUserEntityArray result) {
                if (join) {
                    mCallback.onUserJoinList(result.convertChatUsers());
                } else {
                    mCallback.onUserLeaveList(result.convertChatUsers());
                }
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }
}
