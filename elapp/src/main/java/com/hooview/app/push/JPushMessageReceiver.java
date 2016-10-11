/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import cn.jpush.android.api.JPushInterface;
import org.json.JSONObject;

import com.hooview.app.utils.Logger;

public class JPushMessageReceiver extends BroadcastReceiver {
    private static final String TAG = JPushMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        processCustomMessage(context, bundle);
    }

    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Logger.d(TAG, "onMessage() message: " + message + ", extra: " + extra);
        if (!TextUtils.isEmpty(extra)) {
            JSONObject messageJson = null;
            try {
                String title = "";
                String description = "";
                String customContent = "";

                messageJson = new JSONObject(extra);
                if (!messageJson.isNull("title")) {
                    title = messageJson.getString("title");
                }
                if (!messageJson.isNull("description")) {
                    description = messageJson.getString("description");
                }
                if (!messageJson.isNull("custom_content")) {
                    customContent = messageJson.getString("custom_content");
                }
                Intent intent = PushHelper.getInstance(context).assembleIntent(context, messageJson.toString());
                PushHelper.getInstance(context).sendNM(context, title, description, intent);
            } catch (Exception e) {
                Logger.w(TAG, "onMessage parsing failed !", e);
            }
        }
    }
}
