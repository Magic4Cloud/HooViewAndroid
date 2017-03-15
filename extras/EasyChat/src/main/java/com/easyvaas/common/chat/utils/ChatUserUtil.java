package com.easyvaas.common.chat.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.OnBlackListChangeListener;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.applib.controller.HXSDKHelper;
import com.easyvaas.common.chat.bean.BaseUser;
import com.easyvaas.common.chat.bean.ChatUser;
import com.easyvaas.common.chat.bean.UserArray;
import com.easyvaas.common.chat.db.ChatDB;
import com.easyvaas.common.chat.db.UserDao;
import com.easyvaas.common.chat.net.AppChatHelper;
import com.easyvaas.common.chat.net.MyRequestCallBack;
import com.easyvaas.common.chat.net.RequestUtility;

public class ChatUserUtil {
    private static final String TAG = "ChatUserUtil";
    private static final int MAX_RETRY_LOGIN_TIMES = 4;

    private static Map<String, String> mGetImUserInfoMap = new HashMap<>();
    private static int mRetryLoginTimes = 0;

    public static ChatUser getUserInfo(String username) {
        Map<String, ChatUser> map = ((ChatHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
        ChatUser user = map.get(username);
        if (user == null) {
            user = new ChatUser(username);
        }
        if (TextUtils.isEmpty(user.getNick())) {
            user.setNick(username);
        }
        return user;
    }

    public static String getNickName(String username, Context context) {
        if (TextUtils.isEmpty(username)) {
            return "";
        }
        String nickname = ChatHXSDKHelper.getInstance().getModel().getNickname(username);
        if (username.equals(nickname)) {
            getUserInfoByImUsername(context, username);
        }
        return nickname;
    }

    public static boolean isExistImUserInfo(String imUserName) {
        if (TextUtils.isEmpty(imUserName)) {
            return false;
        }
        String nickname = ChatHXSDKHelper.getInstance().getModel().getNickname(imUserName);
        if (!imUserName.equals(nickname)) {
            return true;
        }
        return false;
    }

    public static String getUserId(String imUsername, Context context) {
        String userId = ChatHXSDKHelper.getInstance().getModel().getUserId(imUsername);
        if (TextUtils.isEmpty(userId)) {
            getUserInfoByImUsername(context, imUsername);
        }
        return userId;
    }

    public static void setUserAvatar(Context context, String imUser, ImageView imageView) {
        String avatar = new UserDao(context).getAvatar(imUser);
        if (!TextUtils.isEmpty(avatar)) {
            CommonUtils.showImage(avatar, R.drawable.somebody, imageView);
        } else {
            imageView.setImageResource(R.drawable.somebody);
        }
    }

    public static void setCurrentUserAvatar(Context context, ImageView imageView) {
        CommonUtils.showImage(ChatDB.getInstance(context)
                .getString(ChatDB.KEY_USER_LOGO), R.drawable.somebody, imageView);
    }

    public static String getCurrentUserNickname() {
        return ChatDB.getInstance(EMChat.getInstance().getAppContext()).getUserNickname();
    }

    public static void updateLocalChatContacts(Context context) {
        try {
            for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
                String username = conversation.getUserName();
                if (!ChatUserUtil.getNickName(username, context).equals(username)) {
                    continue;
                }
                getUserInfoByImUsername(context, conversation.getUserName());
            }
        } catch (Exception ex) {
            ChatLogger.e(TAG, "UpdateLocalChatContacts failed! msg: " + ex.getMessage());
        }
    }

    public static void updateLocalChatContacts(Context context, String imUser) {
        if (!isInLocalContact(imUser)) {
            getUserInfoByImUsername(context, imUser);
        }
    }

    public static void updateLocalChatContacts(ChatUser chatUser) {
        ChatHXSDKHelper.getInstance().saveContact(chatUser);
    }

    public static boolean isInLocalContact(String imUser) {
        return !TextUtils.isEmpty(ChatHXSDKHelper.getInstance().getModel().getUserId(imUser));
    }

    public static List<BaseUser> getFriendList() {
        String json = ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .getString(ChatDB.KEY_FRIEND_LIST_JSON);
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        List<BaseUser> friends = new Gson().fromJson(json, new TypeToken<List<BaseUser>>(){}.getType());
        return friends == null ? new ArrayList<BaseUser>() : friends;
    }

    private static void getUserInfoByImUsername(final Context context, final String imUser) {
        if (mGetImUserInfoMap.containsKey(imUser)) {
            return;
        } else {
            mGetImUserInfoMap.put(imUser, imUser);
        }
        AppChatHelper.getInstance(context).getUserInfo(imUser,
                new MyRequestCallBack<BaseUser>() {
                    @Override
                    public void onSuccess(BaseUser result) {
                        if (result != null) {
                            ChatUser chatUser = new ChatUser(result.getImuser());
                            chatUser.setAvatar(result.getLogourl());
                            chatUser.setNick(result.getNickname());
                            chatUser.setUserId(result.getName());
                            ChatHXSDKHelper.getInstance().saveContact(chatUser);
                        }
                        mGetImUserInfoMap.remove(imUser);
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        mGetImUserInfoMap.remove(imUser);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtility.handleRequestFailed(context);
                        mGetImUserInfoMap.remove(imUser);
                    }
                });
    }

    public static void getUserBasicInfoList(final Context context, final List<String> imUsers,
            final MyRequestCallBack<UserArray> callBack) {
        AppChatHelper.getInstance(context).getUserInfos(imUsers, new MyRequestCallBack<UserArray>() {
            @Override
            public void onSuccess(UserArray result) {
                if (result != null) {
                    List<BaseUser> users = result.getUsers();
                    if (users != null && users.size() > 0) {
                        for (BaseUser user : users) {
                            ChatUser chatUser = new ChatUser(user.getImuser());
                            chatUser.setAvatar(user.getLogourl());
                            chatUser.setNick(user.getNickname());
                            chatUser.setUserId(user.getName());
                            ChatHXSDKHelper.getInstance().saveContact(chatUser);
                        }
                    }
                    callBack.onSuccess(result);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtility.handleRequestFailed(context);
            }
        });
    }

    private static void getImUserInfoAndLogin(String name, final Context context) {
        AppChatHelper.getInstance(context).getUserInfo(name, new MyRequestCallBack<BaseUser>() {
            @Override
            public void onSuccess(BaseUser result) {
                userLoginHX(result.getImuser(), result.getImpwd(), context);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtility.handleRequestFailed(context);
            }
        });
    }

    private static void userLoginHX(final String username, final String password, final Context context) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ChatLogger.e(TAG, "IM login failed, caused null username/password, login ? "
                    + ChatHXSDKHelper.getInstance().isLogined());
            return;
        }
        ChatHXSDKHelper.getInstance().setHXId(username);
        ChatHXSDKHelper.getInstance().setPassword(password);
        EMChatManager.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMChatManager.getInstance().loadAllConversations();
                cacheBlackList();
                ChatLogger.d(TAG, "IM login success !");
            }

            @Override
            public void onError(int i, String s) {
                if (!ChatHXSDKHelper.getInstance().isLogined() && mRetryLoginTimes < MAX_RETRY_LOGIN_TIMES
                        && context != null) {
                    userLogin(context, username, password);
                    mRetryLoginTimes++;
                }
                ChatLogger.e(TAG, "IM login error, reason: " + s
                        + "; username: " + username + ", password:" + password);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    public static void userLogin(Context context, String imUser, String imPassword) {
        //Need to confirm user can not send message caused the following codes
        if (ChatHXSDKHelper.getInstance().isLogined()) {
            ChatLogger.w(TAG, "IM user have login, no need re-login !");
            return;
        }
        if (TextUtils.isEmpty(imUser) || TextUtils.isEmpty(imPassword)) {
            getImUserInfoAndLogin(ChatDB.getInstance(context).getUserNumber(), context);
        } else {
            userLoginHX(imUser, imPassword, context);
        }
    }

    public static void userLogout() {
        ChatHXSDKHelper.getInstance().logout(true, null);
    }

    private static void cacheBlackList() {
        if (ChatHXSDKHelper.getInstance().isBlackListSyncedWithServer()) {
            return;
        }
        ChatHXSDKHelper.getInstance().asyncFetchBlackListFromServer(new EMValueCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                Map<String, Boolean> map = new HashMap<>(strings.size());
                for (String imUser : strings) {
                    map.put(imUser, true);
                }
                ChatDB.getInstance(EMChat.getInstance().getAppContext())
                        .putString(ChatDB.KEY_BLACK_LIST_JSON, new Gson().toJson(map));
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private static void updateBlackList(Context context, String imUser, boolean black) {
        String json = ChatDB.getInstance(context).getString(ChatDB.KEY_BLACK_LIST_JSON);
        Map<String, Boolean> blackUserMap;
        if (!TextUtils.isEmpty(json)) {
            blackUserMap = new Gson()
                    .fromJson(json, new TypeToken<Map<String, Boolean>>() {
                    }.getType());
        } else {
            blackUserMap = new HashMap<>();
        }
        if (black) {
            blackUserMap.put(imUser, true);
        } else {
            blackUserMap.remove(imUser);
        }
        ChatDB.getInstance(EMChat.getInstance().getAppContext())
                .putString(ChatDB.KEY_BLACK_LIST_JSON, new Gson().toJson(blackUserMap));
    }

    public static void addUserToBlacklist(final Activity activity, final String username,
            final OnBlackListChangeListener listener) {
        if (TextUtils.isEmpty(username)) {
            return;
        }
        final ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage(activity.getString(R.string.moving_into_blacklist));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().addUserToBlackList(username, true);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onAddUserSuccess();
                            updateBlackList(activity, username, true);
                        }
                    });
                } catch (EaseMobException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onAddFailed();
                        }
                    });
                    ChatLogger.w(TAG, "Add user into blacklist failed!", e);
                } finally {
                    pd.dismiss();
                }
            }
        }).start();
    }

    public static void removeUserFromBlacklist(final Activity activity, final String username,
            final OnBlackListChangeListener listener) {
        if (TextUtils.isEmpty(username)) {
            return;
        }
        final ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage(activity.getString(R.string.moving_from_blacklist));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().deleteUserFromBlackList(username);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onRemoveUserSuccess();
                            updateBlackList(activity, username, false);
                        }
                    });
                } catch (EaseMobException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onRemoveUserFailed();
                        }
                    });
                    ChatLogger.w(TAG, "Remove user from blacklist failed!", e);
                } finally {
                    pd.dismiss();
                }
            }
        }).start();
    }

    public static boolean isInBlackList(Context context, String imUser) {
        String json = ChatDB.getInstance(context).getString(ChatDB.KEY_BLACK_LIST_JSON);
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        Map<String, Boolean> map = new Gson()
                .fromJson(json, new TypeToken<Map<String, Boolean>>() {
                }.getType());
        if (map == null || map.get(imUser) == null) {
            return false;
        }
        return map.get(imUser);
    }

    public static Boolean checkIsCreateGroup(String imUserName, int anchorLevel) {
        int groupNumber = 0;
        int maxGroupNumber = 0;
        List<EMGroup> groups = ChatHXSDKHelper.getInstance().getLocalAllGroupList();
        if (groups == null) {
            return false;
        }
        for (EMGroup group : groups) {
            if (imUserName.equals(group.getOwner())) {
                groupNumber++;
            }
        }
        maxGroupNumber = getAnchorLevel(anchorLevel);
        if (groupNumber == maxGroupNumber || groupNumber > maxGroupNumber) {
            return false;
        } else {
            return true;
        }
    }

    private static int getAnchorLevel(int anchorLevel) {
        int maxCreateGroupNumber = 0;
        switch (anchorLevel) {
            case 1:
                maxCreateGroupNumber = 0;
                break;
            case 2:
                maxCreateGroupNumber = 1;
                break;
            case 3:
                maxCreateGroupNumber = 3;
                break;
            case 4:
                maxCreateGroupNumber = 5;
                break;
            case 5:
                maxCreateGroupNumber = 8;
                break;
        }
        return maxCreateGroupNumber;
    }

    public static boolean getNamesByImusers(EMGroup emGroup, MyRequestCallBack<UserArray> callBack,
            Context context) {
        List<String> members = emGroup.getMembers();
        String ownerImUser = ChatHXSDKHelper.getInstance().getHXId();
        if (members != null) {
            members.remove(ownerImUser);
            ChatUserUtil.getUserBasicInfoList(context, members, callBack);
        }
        return (members != null) && members.size() > 0;
    }

    private static void showUserInfo(Context context, String userId, String imUser) {
        Intent friendsInfoIntent = new Intent(ChatConstants.EXTERNAL_ACTION_GO_FRIENDS_USER_INFO_ACTIVITY);
        if (ChatDB.getInstance(context).getUserNumber().equals(userId)
                || ChatHXSDKHelper.getInstance().getHXId().equals(userId)) {
            friendsInfoIntent.setAction(ChatConstants.EXTERNAL_ACTION_GO_HOME_MINE);
        } else if (!TextUtils.isEmpty(userId)) {
            friendsInfoIntent.putExtra(ChatConstants.EXTERNAL_EXTRA_KEY_USER_ID, userId);
            friendsInfoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (!TextUtils.isEmpty(imUser)) {
            friendsInfoIntent.putExtra(ChatConstants.EXTERNAL_EXTRA_KEY_USER_ID, getUserId(imUser, context));
            friendsInfoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.sendBroadcast(friendsInfoIntent);
    }

    public static void showUserInfo(Context context, String userId) {
        showUserInfo(context, userId, "");
    }

    public static void showUserInfoByIM(Context context, String imUser) {
        showUserInfo(context, "", imUser);
    }

    public static void showUserPhoto(Context context, String url, ImageView userPhoto) {
        if (TextUtils.isEmpty(url)) {
            userPhoto.setImageResource(R.drawable.somebody);
        } else if (url.startsWith("http")) {
            CommonUtils.showImage(url, R.drawable.somebody, userPhoto);
        } else if (url.startsWith("/")) {
            File file = new File(url);
            showUserPhoto(context, file, userPhoto);
        }
    }

    private static void showUserPhoto(Context context, File file, ImageView imageView) {
        if (file == null || !file.exists()) {
            imageView.setImageResource(R.drawable.somebody);
            return;
        }
        Picasso.with(context).load(file).fit().centerCrop()
                .error(R.drawable.somebody).placeholder(R.drawable.somebody)
                .into(imageView);
    }

    public static void setTextLeftDrawable(Context context, TextView view, int drawableId) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        view.setCompoundDrawables(drawable, null, null, null);
    }

}
