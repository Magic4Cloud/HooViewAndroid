/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class JumpActivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (ChatConstants.EXTERNAL_ACTION_GO_FRIENDS_USER_INFO_ACTIVITY.equals(intent.getAction())) {
//            intent.putExtra(Constants.EXTRA_KEY_USER_ID,
//                    intent.getStringExtra(ChatConstants.EXTERNAL_EXTRA_KEY_USER_ID));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setClass(context, FriendsUserInfoActivity.class);
//            context.startActivity(intent);
//        } else if (ChatConstants.EXTERNAL_ACTION_GO_UPDATE_ASSERT_INFO.equals(intent.getAction())) {
//            ApiUtil.getAssetInfo(context);
//        } else if (ChatConstants.EXTERNAL_ACTION_GO_WATCH_VIDEO.equals(intent.getAction())) {
//            Utils.watchVideo(context, intent.getStringExtra(ChatConstants.EXTERNAL_EXTRA_KEY_VIDEO_ID));
//        } else if (ChatConstants.EXTERNAL_ACTION_GO_UPDATE_APP_USER_INFO.equals(intent.getAction())) {
//            User user = EVApplication.getUser();
//            if (intent.getBooleanExtra(ChatConstants.EXTERNAL_EXTRA_KEY_IS_ADD_FOLLOW, false)) {
//                user.setFollow_count(user.getFollow_count() + 1);
//            } else {
//                user.setFollow_count(user.getFollow_count() - 1);
//            }
//        }

    }
}
