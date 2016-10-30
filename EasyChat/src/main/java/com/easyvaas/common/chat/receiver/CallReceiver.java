package com.easyvaas.common.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.activity.VideoCallActivity;
import com.easyvaas.common.chat.activity.VoiceCallActivity;
import com.easyvaas.common.chat.utils.ChatLogger;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ChatHXSDKHelper.getInstance().isLogined())
            return;
        String from = intent.getStringExtra("from");
        //call type
        String type = intent.getStringExtra("type");
        if ("video".equals(type)) { //视频通话
            context.startActivity(new Intent(context, VideoCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            context.startActivity(new Intent(context, VoiceCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        ChatLogger.d("CallReceiver", "app received a incoming call");
    }

}
