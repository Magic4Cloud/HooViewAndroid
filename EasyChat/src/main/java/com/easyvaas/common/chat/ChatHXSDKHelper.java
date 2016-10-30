package com.easyvaas.common.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.json.JSONObject;

import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;

import com.easyvaas.common.chat.activity.ChatActivity;
import com.easyvaas.common.chat.activity.VideoCallActivity;
import com.easyvaas.common.chat.applib.controller.HXSDKHelper;
import com.easyvaas.common.chat.applib.model.HXNotifier;
import com.easyvaas.common.chat.applib.model.HXSDKModel;
import com.easyvaas.common.chat.bean.ChatUser;
import com.easyvaas.common.chat.db.ChatDB;
import com.easyvaas.common.chat.db.ChatDBManager;
import com.easyvaas.common.chat.receiver.CallReceiver;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.utils.CommonUtils;
import com.easyvaas.common.chat.utils.SingleToast;

public class ChatHXSDKHelper extends HXSDKHelper {
    private static final String TAG = ChatHXSDKHelper.class.getSimpleName();

    private static ChatHXSDKHelper mInstance;
    protected EMEventListener eventListener = null;
    private Map<String, ChatUser> contactList;

    private CallReceiver callReceiver;

    private UserProfileManager userProManager;
    private Context context;

    private List<Activity> activityList = new ArrayList<>();

    public ChatHXSDKHelper(Context context) {
        super();
        mInstance = this;
        this.context = context;
        contactList = new HashMap<>();
    }

    public static ChatHXSDKHelper getInstance() {
        return mInstance;
    }

    public void pushActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(0, activity);
        }
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    @Override
    public synchronized boolean onInit(Context context) {
        if (super.onInit(context)) {
            getUserProfileManager().onInit(context);

            //if your app is supposed to user Google Push, please set project number
            String projectNumber = "562451699741";
            EMChatManager.getInstance().setGCMProjectNumber(projectNumber);
            return true;
        }

        return false;
    }

    @Override
    protected void initHXOptions() {
        super.initHXOptions();

        // you can also get EMChatOptions to set related SDK options
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
    }

    @Override
    protected void initListener() {
        super.initListener();
        IntentFilter callFilter = new IntentFilter(
                EMChatManager.getInstance().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }

        appContext.registerReceiver(callReceiver, callFilter);
        initEventListener();
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void initEventListener() {
        eventListener = new EMEventListener() {
            boolean isNoticeNewChat = ChatDB
                    .getInstance(context)
                    .getBoolean(ChatDB.KEY_NOTICE_PUSH_NEW_CHAT, true);

            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if (event.getData() instanceof EMMessage) {
                    message = (EMMessage) event.getData();
                    ChatLogger.d(TAG,
                            "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                }
                if (message == null) {
                    return;
                }

                if (isRedPackMessage(message)) {
                    ChatDBManager.getInstance().saveRedPack(message);
                }

                switch (event.getEvent()) {
                    case EventNewMessage:
                        if (activityList.size() <= 0) {
                            if (isNoticeNewChat) {
                                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                            }
                        }
                        appContext.sendBroadcast(new Intent(ChatConstants.EXTERNAL_ACTION_SHOW_NEW_MESSAGE_ICON));
                        if (message.getChatType() == ChatType.GroupChat) {
                            appContext.sendBroadcast(new Intent(ChatConstants.EXTERNAL_ACTION_SHOW_GROUP_MESSAGE_CHANGED));
                        } else if (message.getChatType() == ChatType.Chat) {
                            Intent newMsgIntent = new Intent(ChatConstants.EXTERNAL_ACTION_SHOW_NEW_CHAT_MESSAGE);
                            ChatUserUtil.updateLocalChatContacts(context, message.getFrom());
                            appContext.sendBroadcast(newMsgIntent);
                        }
                        break;
                    case EventOfflineMessage:
                        if (activityList.size() <= 0) {
                            ChatLogger.d(TAG, "received offline messages");
                            List<EMMessage> messages = (List<EMMessage>) event.getData();
                            if (isNoticeNewChat) {
                                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                            }
                        }
                        break;
                    // below is just giving a example to show a cmd toast, the app should not follow this
                    // so be careful of this
                    case EventNewCMDMessage: {
                        CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                        final String action = cmdMsgBody.action;
                        ChatLogger.d(TAG,
                                String.format("透传消息：action:%s,message:%s", action, message.toString()));
                        if (ChatConstants.ACTION_HX_CMD_MESSAGE_UPDATE_GROUP.equals(action)) {
                            Intent broadcastIntent = new Intent(ChatConstants.ACTION_GO_UPDATE_GROUP_INFO);
                            broadcastIntent
                                    .putExtra(ChatConstants.EXTRA_KEY_HX_CMD_MESSAGE_UPDATE_GROUP, message);
                            appContext.sendBroadcast(broadcastIntent, null);
                        }
                        break;
                    }
                    case EventDeliveryAck:
                        message.setDelivered(true);
                        break;
                    case EventReadAck:
                        message.setAcked(true);
                        break;
                    // add other events in case you are interested in
                    default:
                        break;
                }

            }
        };

        EMChatManager.getInstance().registerEventListener(eventListener);

        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {
            private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
            private final IntentFilter filter = new IntentFilter(ROOM_CHANGE_BROADCAST);
            private boolean registered = false;

            private void showToast(String value) {
                if (!registered) {
                    appContext.registerReceiver(new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            SingleToast.show(appContext, intent.getStringExtra("value"));
                        }

                    }, filter);
                    registered = true;
                }

                Intent broadcastIntent = new Intent(ROOM_CHANGE_BROADCAST);
                broadcastIntent.putExtra("value", value);
                appContext.sendBroadcast(broadcastIntent, null);
            }

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                showToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                ChatLogger.i("info", "onChatRoomDestroyed=" + roomName);
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showToast("member : " + participant + " join the room : " + roomId);
                ChatLogger.i("info", "onmemberjoined=" + participant);

            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                    String participant) {
                showToast("member : " + participant + " leave the room : " + roomId + " room name : "
                        + roomName);
                ChatLogger.i("info", "onMemberExited=" + participant);

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                    String participant) {
                showToast(
                        "member : " + participant + " was kicked from the room : " + roomId + " room name : "
                                + roomName);
                ChatLogger.i("info", "onMemberKicked=" + participant);

            }

        });

    }

    /**
     * 自定义通知栏提示内容
     */
    @Override
    protected HXNotifier.HXNotificationInfoProvider getNotificationListener() {
        return new HXNotifier.HXNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, appContext);
                return ChatUserUtil.getNickName(message.getFrom(), appContext) + ": " + ticker;
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                Intent intent = new Intent(appContext, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ChatActivity.EXTRA_IM_CHAT_USER_ID, message.getFrom());
                try {
                    intent = new Intent(appContext,
                            Class.forName("com.easyvaas.common.chat.activity.ChatActivity"));
                    if (isVideoCalling) {
                        intent = new Intent(appContext,
                                Class.forName("com.easyvaas.common.chat.activity.VideoCallActivity"));
                        intent = new Intent(appContext, VideoCallActivity.class);
                    } else if (isVoiceCalling) {
                        intent = new Intent(appContext,
                                Class.forName("com.easyvaas.common.chat.activity.VoiceCallActivity"));
                    } else {
                        ChatType chatType = message.getChatType();
                        if (chatType == ChatType.Chat) {
                            intent.putExtra(ChatActivity.EXTRA_IM_CHAT_USER_ID, message.getFrom());
                            intent.putExtra(ChatActivity.EXTRA_IM_CHAT_TYPE, ChatActivity.CHAT_TYPE_SINGLE);
                        }
                    }
                } catch (Exception exception) {
                    ChatLogger.e(TAG, "getLaunchIntent() ", exception);
                }
                return intent;
            }
        };
    }

    @Override
    protected void onConnectionConflict() {
        ChatLogger.w(TAG, "onConnectionConflict !");
        // TODO: 9/18/16 Need to logout
    }

    @Override
    protected void onCurrentAccountRemoved() {
        ChatLogger.w(TAG, "onCurrentAccountRemoved !");
        ChatUserUtil.userLogin(context, getHXId(), getPassword());
    }

    @Override
    protected HXSDKModel createModel() {
        return new ChatHXSDKModel(appContext);
    }

    @Override
    public HXNotifier createNotifier() {
        return new HXNotifier() {
            public synchronized void onNewMsg(final EMMessage message) {
                if (EMChatManager.getInstance().isSlientMessage(message)) {
                    return;
                }
                String chatUserName;
                List<String> notNotifyIds;
                // 获取设置的不提示新消息的用户或者群组ids
                if (message.getChatType() == ChatType.Chat) {
                    chatUserName = message.getFrom();
                    notNotifyIds = ((ChatHXSDKModel) hxModel).getDisabledGroups();
                } else {
                    chatUserName = message.getTo();
                    notNotifyIds = ((ChatHXSDKModel) hxModel).getDisabledIds();
                }

                if (notNotifyIds == null || !notNotifyIds.contains(chatUserName)) {
                    // 判断app是否在后台
                    if (CommonUtils.isAppRunningBackground(appContext)) {
                        ChatLogger.d(TAG, "app is running in backgroud");
                        sendNotification(message, false);
                    } else {
                        sendNotification(message, true);
                    }
                    vibrateAndPlayTone(message);
                }
            }
        };
    }

    public void sendGroupUpdateNotice(String groupId) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMsg.setChatType(ChatType.GroupChat);
        CmdMessageBody cmdBody=new CmdMessageBody(ChatConstants.ACTION_HX_CMD_MESSAGE_UPDATE_GROUP);
        cmdMsg.setReceipt(groupId);
        cmdMsg.addBody(cmdBody);
        EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    public ChatHXSDKModel getModel() {
        return (ChatHXSDKModel) hxModel;
    }

    public Map<String, ChatUser> getContactList() {
        if (getHXId() != null) {
            contactList = getModel().getContactList();
        }
        return contactList;
    }

    public boolean isRobotMenuMessage(EMMessage message) {
        try {
            JSONObject jsonObj = message.getJSONObjectAttribute(ChatConstants.MESSAGE_ATTR_ROBOT_MSG_TYPE);
            if (jsonObj.has("choice")) {
                return true;
            }
        } catch (Exception e) {
            ChatLogger.d(TAG, "Parse robot message type failed!");
        }
        return false;
    }

    public boolean isRedPackMessage(EMMessage message) {
        try {
            JSONObject jsonObj = message.getJSONObjectAttribute(ChatConstants.MESSAGE_ATTR_RED_PACK_INFO);
            if (jsonObj.has(ChatConstants.MESSAGE_ATTR_RED_PACK_CODE)) {
                return true;
            }
        } catch (Exception e) {
            ChatLogger.d(TAG, "Parse red pack message type failed!");
        }
        return false;
    }

    public String getRobotMenuMessageDigest(EMMessage message) {
        String title = "";
        try {
            JSONObject jsonObj = message.getJSONObjectAttribute(ChatConstants.MESSAGE_ATTR_ROBOT_MSG_TYPE);
            if (jsonObj.has("choice")) {
                JSONObject jsonChoice = jsonObj.getJSONObject("choice");
                title = jsonChoice.getString("title");
            }
        } catch (Exception e) {
        }
        return title;
    }

    public void saveContact(ChatUser user) {
        if (contactList == null) {
            contactList = getModel().getContactList();
        }
        contactList.put(user.getUsername(), user);
        getModel().saveContact(user);
    }

    @Override
    public void logout(final boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        super.logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                if (contactList != null) {
                    contactList.clear();
                }
                getUserProfileManager().reset();
                getModel().closeDB();
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                if (callback != null) {
                    callback.onError(code, message);
                }
            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

        });
    }

    void endCall() {
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update User cach And db
     */
    public void updateContactList(List<ChatUser> contactInfoList) {
        for (ChatUser u : contactInfoList) {
            contactList.put(u.getUsername(), u);
        }
        ArrayList<ChatUser> mList = new ArrayList<ChatUser>();
        mList.addAll(contactList.values());
        getModel().saveContactList(mList);
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }

}
