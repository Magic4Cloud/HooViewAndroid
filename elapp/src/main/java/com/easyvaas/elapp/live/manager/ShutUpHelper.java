/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.live.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.easyvaas.elapp.db.Preferences;

public class ShutUpHelper {
    public static final int TYPE_MANAGER = 1;
    public static final int TYPE_SHUTUP = 2;
    public static final int TYPE_KICK = 3;

    private static ArrayList<String> mManagerList = new ArrayList<>();
    private static ArrayList<String> mShutUpList = new ArrayList<>();
    private static ArrayList<String> mKickList = new ArrayList<>();
    private static ArrayList<String> mAnchorList = new ArrayList<>();

    private static class SingletonHolder {
        private static final ShutUpHelper singleton = new ShutUpHelper();
    }

    private ShutUpHelper() {

    }

    public static ShutUpHelper getInstance() {
        return SingletonHolder.singleton;
    }

    //============================== Check Method ======================================

    boolean checkIsManager(String userName) {
        return isInUserList(userName, TYPE_MANAGER);
    }

    boolean checkIsShutUp(String userName) {
        return isInUserList(userName, TYPE_SHUTUP);
    }

    boolean checkIsKicked(String userName) {
        return isInUserList(userName, TYPE_SHUTUP);
    }

    //============================== Common Related Method =============================

    public void addUsers(List<String> userNames, int type) {
        if (userNames != null) {
            for (String userName : userNames) {
                addUser(userName, type);
            }
        }
    }

    private void addUser(String userName, int type) {
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        if (!getUserList(type).contains(userName)) {
            getUserList(type).add(userName);
        }
    }

    public void removeUsers(List<String> userNames, int type) {
        if (userNames != null) {
            for (String userName : userNames) {
                removeUser(userName, type);
            }
        }
    }

    private void removeUser(String userName, int type) {
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        if (getUserList(type).contains(userName)) {
            getUserList(type).remove(userName);
        }
    }

    private boolean isInUserList(String userName, int type) {
        return getUserList(type).contains(userName);
    }

    private List<String> getUserList(int type) {
        if (type == TYPE_SHUTUP) {
            return mShutUpList;
        } else if (type == TYPE_KICK) {
            return mKickList;
        } else if (type == TYPE_MANAGER) {
            return mManagerList;
        }
        return new ArrayList<>();
    }

    //============================== Call Method ==================================

    void liveShutUp(String vid, final String name, final boolean shutUp,
                    final TextView shutUpTv) {
        ApiHelper.getInstance().liveShutUp(vid, name, shutUp, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (shutUp) {
                    shutUpTv.setText(R.string.shut_up_cancel);
                    addUser(name, ShutUpHelper.TYPE_SHUTUP);
                } else {
                    shutUpTv.setText(R.string.shut_up);
                    removeUser(name, ShutUpHelper.TYPE_SHUTUP);
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
