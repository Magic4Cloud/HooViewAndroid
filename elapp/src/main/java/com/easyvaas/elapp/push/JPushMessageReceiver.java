/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.easyvaas.elapp.activity.WebViewActivity;
import com.easyvaas.elapp.ui.MainActivity;
import com.easyvaas.elapp.ui.common.WebDetailActivity;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.ui.user.VIPUserInfoDetailActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;

public class JPushMessageReceiver extends BroadcastReceiver {
    private static final String TAG = JPushMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        processCustomMessage(context, action, bundle);
    }

    private void processCustomMessage(Context context, String action, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Logger.e(TAG, "onMessage() message: " + message + ", extra: " + extra);
        /**
         * 120c83f76017a8ba94a
         * living videoId:------6k1Pgl0bi90MiOEj
         * good video videoId:------5pkvLb8kHp6F1oKD
         * newsID:------8796
         * type 跳转类型 （0，H5；1，新闻；2，精品视频；3，视频直播间；4，图文直播间；5，个人主页）。resource 资源ID或H5链接
         * extra: {"resource":"http:\/\/www.hooview.com","type":"0"}
         */
        String resource = "";
        String type = "";
        if (!TextUtils.isEmpty(extra)) {
            JSONObject object = null;
            try {
                object = new JSONObject(extra);
                if (!object.isNull("resource")) {
                    resource = object.optString("resource");
                }
                if (!object.isNull("type")) {
                    type = object.optString("type");
                }
            } catch (JSONException e) {
                Logger.e(TAG, "onMessage parsing failed !", e);
            }
        }
        /**
         * 用户点击打开通知
         */
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
            Logger.e(TAG, "JPush----click: " + type);
            Intent intent = null;
            switch (type) {
                // H5
                case "0":
                    intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.EXTRA_KEY_TYPE, WebViewActivity.TYPE_ACTIVITY);
                    intent.putExtra(WebViewActivity.EXTRA_KEY_URL, resource);
                    intent.putExtra(Constants.EXTRA_KEY_TITLE, "");
                    break;
                // 新闻
                case "1":
                    intent = new Intent(context, WebDetailActivity.class);
                    intent.putExtra(WebDetailActivity.EXTRA_NAME, "");
                    intent.putExtra(WebDetailActivity.EXTRA_CODE, resource);
                    intent.putExtra(WebDetailActivity.EXTRA_TITLE, "");
                    intent.putExtra(WebDetailActivity.EXTRA_TYPE, WebDetailActivity.TYPE_NEWS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                // 精品视频
                case "2":
                    intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_VIDEO_ID, resource);
                    intent.putExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, false);
                    intent.putExtra(Constants.EXTRA_KEY_VIDEO_GOOD_VIDEO, true);
                    break;
                // 视频直播间
                case "3":
                    intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_VIDEO_ID, resource);
                    intent.putExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, true);
                    intent.putExtra(Constants.EXTRA_KEY_VIDEO_GOOD_VIDEO, false);
                    break;
                // 图文直播间
                case "4":
                    // TODO: 2017/4/13
                    break;
                // 个人主页
                case "5":
                    intent = new Intent(context, VIPUserInfoDetailActivity.class);
                    intent.putExtra(VIPUserInfoDetailActivity.EXTRA_NAME, resource);
                    break;
                // 主界面
                default:
                    intent = new Intent(context, MainActivity.class);
                    break;
            }
            intent.putExtra(Constants.EXTRA_KEY_IS_FROM_PUSH, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        /*if (!TextUtils.isEmpty(extra)) {
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
                Intent intent = PushHelper.getInstance(context).assembleIntent(context, customContent);
                PushHelper.getInstance(context).sendNM(context, title, description, intent);
            } catch (Exception e) {
                Logger.w(TAG, "onMessage parsing failed !", e);
            }
        }*/
    }
}
