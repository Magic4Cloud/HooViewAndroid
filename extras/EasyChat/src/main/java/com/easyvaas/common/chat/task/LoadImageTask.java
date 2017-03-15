package com.easyvaas.common.chat.task;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.ImageUtils;

import com.easyvaas.common.chat.activity.ShowBigImageActivity;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.CommonUtils;
import com.easyvaas.common.chat.utils.ImageCache;

public class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
    private static final String TAG = LoadImageTask.class.getSimpleName();

    private ImageView iv = null;
    String localFullSizePath = null;
    String thumbnailPath = null;
    String remotePath = null;
    EMMessage message = null;
    ChatType chatType;
    Activity activity;

    @Override
    protected Bitmap doInBackground(Object... args) {
        thumbnailPath = (String) args[0];
        localFullSizePath = (String) args[1];
        remotePath = (String) args[2];
        chatType = (ChatType) args[3];
        iv = (ImageView) args[4];
        // if(args[2] != null) {
        activity = (Activity) args[5];
        // }
        message = (EMMessage) args[6];
        File file = new File(thumbnailPath);
        if (file.exists()) {
            return ImageUtils.decodeScaleImage(thumbnailPath, 160, 160);
        } else {
            if (message.direct == EMMessage.Direct.SEND) {
                return ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
            } else {
                return null;
            }
        }

    }

    protected void onPostExecute(Bitmap image) {
        if (image != null) {
            iv.setImageBitmap(image);
            ImageCache.getInstance().put(thumbnailPath, image);
            iv.setClickable(true);
            iv.setTag(thumbnailPath);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String vid = "";
                    try {
                        vid = message.getStringAttribute("vid");
                    } catch (EaseMobException e) {
                        ChatLogger.e(TAG, "get vid failed!", e);
                    }
                    if (!TextUtils.isEmpty(vid)) {
                        Intent watchVideoIntent = new Intent(ChatConstants.EXTERNAL_ACTION_GO_WATCH_VIDEO);
                        watchVideoIntent.putExtra(ChatConstants.EXTERNAL_EXTRA_KEY_VIDEO_ID, vid);
                        activity.sendBroadcast(watchVideoIntent);
                    } else if (thumbnailPath != null) {
                        Intent intent = new Intent(activity, ShowBigImageActivity.class);
                        File file = new File(localFullSizePath);
                        if (file.exists()) {
                            Uri uri = Uri.fromFile(file);
                            intent.putExtra(ShowBigImageActivity.LOCAL_IMAGE_URL, uri);
                        } else {
                            // The local full size pic does not exist yet.
                            // ShowBigImage needs to download it from the server
                            // first
                            intent.putExtra(ShowBigImageActivity.REMOTE_IMAGE_URL, remotePath);
                        }
                        if (message != null && message.getChatType() != ChatType.Chat) {
                            // delete the image from server after download
                        }
                        if (message != null && message.direct == EMMessage.Direct.RECEIVE
                                && !message.isAcked
                                && message.getChatType() != ChatType.GroupChat
                                && message.getChatType() != ChatType.ChatRoom) {
                            message.isAcked = true;
                            try {
                                // 看了大图后发个已读回执给对方
                                EMChatManager.getInstance()
                                        .ackMessageRead(message.getFrom(), message.getMsgId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        activity.startActivity(intent);
                    }
                }
            });
        } else {
            if (message.status == EMMessage.Status.FAIL) {
                if (CommonUtils.isNetWorkConnected(activity)) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            EMChatManager.getInstance().asyncFetchMessage(message);
                        }
                    }).start();
                }
            }

        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
