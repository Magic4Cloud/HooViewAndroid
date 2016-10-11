/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.live.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.hooview.app.db.Preferences;

public class LiveRoomShutUpUtil {
    public static ArrayList<String> mPrivateManagerList = new ArrayList<>();
    private static HashMap<String, List<String>> mShutUpMap = new HashMap<>();
    private static ArrayList<String> mAnchorList = new ArrayList<>();

    public static boolean checkIsLiveManager(String userName, Context context) {
        boolean isPrivateManager = false;
        if (TextUtils.isEmpty(userName)) {
            return false;
        }
        if (mPrivateManagerList.size() == 0) {
            String managerListJson = Preferences.getInstance(context)
                    .getString(Preferences.KEY_PARAM_PRIVATE_MANAGER_LIST_JSON);
            List<String> userList = new Gson().<List<String>>fromJson(managerListJson,
                    new TypeToken<List<String>>() {
                    }.getType());
            if (userList != null) {
                mPrivateManagerList.addAll(userList);
            }

        }
        if (mPrivateManagerList.contains(userName)) {
            isPrivateManager = true;
        }
        return isPrivateManager;
    }

    public static void addLiveManager(String userName, Context context) {
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        if (!mPrivateManagerList.contains(userName)) {
            mPrivateManagerList.add(userName);
            updateLiveManagerCache(context, true, userName);
        }

    }

    public static void removeLiveManager(String userName, Context context) {
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        if (mPrivateManagerList.contains(userName)) {
            mPrivateManagerList.remove(userName);
            updateLiveManagerCache(context, false, userName);
        }
    }

    public static boolean checkIsLiveShutUp(String userName, String vid) {
        boolean isLivingShutUp = false;
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(vid)) {
            return false;
        }
        if (mShutUpMap.containsKey(vid)) {
            List<String> userNames = mShutUpMap.get(vid);
            if (userNames.contains(userName)) {
                isLivingShutUp = true;
            }
        }
        return isLivingShutUp;
    }

    public static void addLiveShutUp(List<String> userNames, String vid) {
        if (userNames == null || userNames.size() == 0) {
            return;
        }
        if (mShutUpMap.containsKey(vid)) {
            mShutUpMap.get(vid).addAll(userNames);
        } else {
            mShutUpMap.put(vid, userNames);
        }
    }

    public static void addLiveShutUp(String userName, String vid) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(vid)) {
            return;
        }
        List<String> userNames = new ArrayList<>();
        userNames.add(userName);
        addLiveShutUp(userNames, vid);
    }

    public static void removeLivingShutUp(String userName, String vid) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(vid)) {
            return;
        }
        if (mShutUpMap.containsKey(vid)) {
            List<String> userNames = mShutUpMap.get(vid);
            if (userNames.contains(userName)) {
                userNames.remove(userName);
            }
        }
    }

    public static void updateLiveManagerCache(Context context, boolean isAdd, String userName) {
        String managerListJson = Preferences.getInstance(context)
                .getString(Preferences.KEY_PARAM_PRIVATE_MANAGER_LIST_JSON);
        List<String> userList = new Gson().<List<String>>fromJson(managerListJson,
                new TypeToken<List<String>>() {
                }.getType());
        if (userList == null) {
            return;
        }
        if (!isAdd) {
            if (userList.contains(userName)) {
                userList.remove(userName);
            }
        } else {
            userList.add(userName);
        }
        String json = new Gson().toJson(userList);
        Preferences.getInstance(context).putString(Preferences.KEY_PARAM_PRIVATE_MANAGER_LIST_JSON, json);
    }

    public static void cacheLiveManagers(List<String> userIdList, Context context) {
        if (userIdList == null || userIdList.size() == 0) {
            return;
        }
        mPrivateManagerList.clear();
        ArrayList<String> userNames = new ArrayList<String>();
        for (String userID : userIdList) {
            addLiveManager(userID, context);
        }
        String json = new Gson().toJson(userNames);
        Preferences.getInstance(context).putString(Preferences.KEY_PARAM_PRIVATE_MANAGER_LIST_JSON, json);
    }
}
