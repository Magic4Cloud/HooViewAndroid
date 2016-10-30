package com.easyvaas.common.chat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easyvaas.common.chat.activity.ChatActivity;
import com.easyvaas.common.chat.bean.BaseUser;
import com.easyvaas.common.chat.bean.ChatUser;
import com.easyvaas.common.chat.db.ChatDB;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

public class ChatManager {
    private static final String TAG = "ChatManager";
    private static ChatManager mChatManager;

    public static ChatManager getInstance() {
        if (mChatManager == null) {
            mChatManager = new ChatManager();
        }
        return mChatManager;
    }

    public void initChat(Application app, String appKey) {
        EMChat.getInstance().init(app);
        EMChat.getInstance().setAppkey(appKey);
        new ChatHXSDKHelper(app).onInit(app);
    }

    public void setUserInfo(String name, String nickname, String sessionId) {
        setUserInfo(name, nickname, ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .getString(ChatDB.KEY_USER_LOGO), sessionId);
    }

    public void setUserInfo(String name, String nickname, String logoUrl, String sessionId) {
        ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .putString(ChatDB.KEY_USER_NUMBER, name);
        ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .putString(ChatDB.KEY_USER_NICKNAME, nickname);
        ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .putString(ChatDB.KEY_USER_LOGO, logoUrl);
        ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .updateUserInfo(EMChat.getInstance().getAppContext(), name, sessionId);
    }

    public void setPushEnable(boolean enable) {
        ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .putBoolean(ChatDB.KEY_NOTICE_PUSH_NEW_CHAT, enable);
    }

    public boolean isPushEnable() {
        return ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .getBoolean(ChatDB.KEY_NOTICE_PUSH_NEW_CHAT, true);
    }

    public void setHaveUnreadMsg(boolean haveUnread) {
        ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .putBoolean(ChatDB.KEY_IS_HAVE_UNREAD_MESSAGE, haveUnread);
    }

    public boolean isHaveUnreadMsg() {
        return ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .getBoolean(ChatDB.KEY_IS_HAVE_UNREAD_MESSAGE, false);
    }

    public void logout() {
        ChatUserUtil.userLogout();
    }

    public void userLogin(String imUser, String imPassword) {
        ChatUserUtil.userLogin(EMChat.getInstance().getAppContext(), imUser, imPassword);

        EMChat.getInstance().setUserName(imUser);
        EMChat.getInstance().setPassword(imPassword);

        ChatDB chatDB = ChatDB.getInstance(EMChat.getInstance().getAppContext());
        saveContact(chatDB.getUserNumber(), chatDB.getUserNickname(), chatDB.getString(ChatDB.KEY_USER_LOGO),
                imUser);
    }

    public void saveContact(String name, String nickname, String userLogo, String imUser) {
        ChatUser user = new ChatUser(imUser, userLogo, nickname);
        user.setUserId(name);
        ChatUserUtil.updateLocalChatContacts(user);
    }

    public void loadGroupList(EMCallBack callBack) {
        ChatHXSDKHelper.getInstance().asyncFetchGroupsFromServer(callBack);
    }

    public List<EMConversation> loadConversationsWithRecentChat() {
        // All conversations, contain strangers
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        List<EMConversation> list = new ArrayList<>();
        try {
            List<Pair<Long, EMConversation>> sortList = new ArrayList<>();
            for (EMConversation conversation : conversations.values()) {
                if (conversation.isGroup()) {
                    continue;
                }
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(),
                            conversation));
                }
                ChatUserUtil.updateLocalChatContacts(
                        EMChat.getInstance().getAppContext(), conversation.getLastMessage().getFrom());
            }
            Collections.sort(sortList, new Comparator<Pair<Long, EMConversation>>() {
                @Override
                public int compare(final Pair<Long, EMConversation> con1,
                        final Pair<Long, EMConversation> con2) {
                    if (con1.first == con2.first) {
                        return 0;
                    } else if (con2.first > con1.first) {
                        return 1;
                    } else {
                        return -1;
                    }
                }

            });
            for (Pair<Long, EMConversation> sortItem : sortList) {
                list.add(sortItem.second);
            }
        } catch (Exception ex) {
            ChatLogger.e(TAG, "loadConversationsWithRecentChat failed !", ex);
        }
        return list;
    }

    public void updateFriendList(List<BaseUser> friends) {
        ChatDB chatDB = ChatDB.getInstance(EMChat.getInstance().getAppContext());
        chatDB.putString(ChatDB.KEY_FRIEND_LIST_JSON, new Gson().toJson(friends));
    }

    public void addUserToBlackList(Activity activity, String imUser,
            OnBlackListChangeListener listener) {
        ChatUserUtil.addUserToBlacklist(activity, imUser, listener);
    }

    public void removeUserFromBlackList(Activity activity, String imUser,
            OnBlackListChangeListener listener) {
        ChatUserUtil.removeUserFromBlacklist(activity, imUser, listener);
    }

    public boolean isInBlackList(String imUser) {
        return ChatUserUtil.isInBlackList(EMChat.getInstance().getAppContext(), imUser);
    }

    public void checkUnreadChatGroupMessage(final Context context) {
        ChatHXSDKHelper.getInstance().asyncFetchGroupsFromServer(
                new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        int unReadMsg = 0;
                        List<EMGroup> emGroups = ChatHXSDKHelper.getInstance().getLocalAllGroupList();
                        for (int i = 0; i < emGroups.size(); i++) {
                            EMConversation conversation = ChatHXSDKHelper.getInstance()
                                    .getGroupConversation(emGroups.get(i).getGroupId());
                            unReadMsg += conversation.getUnreadMsgCount();
                        }
                        if (unReadMsg > 0) {
                            context.sendBroadcast(new Intent(ChatConstants.EXTERNAL_ACTION_SHOW_NEW_MESSAGE_ICON));
                            setHaveUnreadMsg(true);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
    }

    public void chatToGroup(String groupId, String groupName) {
        Intent intent = new Intent(EMChat.getInstance().getAppContext(), ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_IM_CHAT_TYPE, ChatActivity.CHAT_TYPE_GROUP);
        intent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID, groupId);
        intent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_NAME, groupName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        EMChat.getInstance().getAppContext().startActivity(intent);
    }

    public void chatToUser(String imUser, boolean isFollowed) {
        Intent intent = new Intent(EMChat.getInstance().getAppContext(), ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_IM_CHAT_USER_ID, imUser);
        intent.putExtra(ChatActivity.EXTRA_IM_CHAT_TYPE, ChatActivity.CHAT_TYPE_SINGLE);
        intent.putExtra(ChatActivity.EXTRA_IS_USER_FOLLOWED, isFollowed);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        EMChat.getInstance().getAppContext().startActivity(intent);
    }
}
