package com.easyvaas.common.chat.applib.model;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

import com.easyvaas.common.chat.applib.controller.HXSDKHelper;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.CommonUtils;

/**
 * this class is subject to be inherited and implement the relative APIs
 */
public class HXNotifier {
    private final static String TAG = HXNotifier.class.getSimpleName();
    Ringtone ringtone = null;

    protected final static String[] msg_eng = { "sent a message", "sent a picture", "sent a voice",
            "sent location message", "sent a video", "sent a file", "%1 contacts sent %2 messages"
    };
    protected final static String[] msg_ch = {
            "发来一条消息",
            "发来一张图片",
            "发来一段语音",
            "发来位置信息",
            "发来一个视频",
            "发来一个文件",
            "%1个联系人发来%2条消息"
    };

    protected static int notifyID = 0x525; // start notification id
    protected static int foregroundNotifyID = 0x555;

    protected NotificationManager notificationManager = null;

    protected HashSet<String> fromUsers = new HashSet<String>();
    protected int notificationNum = 0;

    protected Context appContext;
    protected String packageName;
    protected String[] messageStrings;
    protected long lastNotifyTime;
    protected AudioManager audioManager;
    protected Vibrator vibrator;
    protected HXNotificationInfoProvider notificationInfoProvider;

    public HXNotifier() {
    }

    public HXNotifier init(Context context) {
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        packageName = appContext.getApplicationInfo().packageName;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            messageStrings = msg_ch;
        } else {
            messageStrings = msg_eng;
        }

        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);

        return this;
    }

    public void reset() {
        resetNotificationCount();
        cancelNotification();
    }

    void resetNotificationCount() {
        notificationNum = 0;
        fromUsers.clear();
    }

    void cancelNotification() {
        if (notificationManager != null)
            notificationManager.cancel(notifyID);
    }

    public synchronized void onNewMsg(EMMessage message) {
        if (EMChatManager.getInstance().isSlientMessage(message)) {
            return;
        }

        if (CommonUtils.isAppRunningBackground(appContext)) {
            ChatLogger.d(TAG, "app is running in backgroud");
            sendNotification(message, false);
        } else {
            sendNotification(message, true);
        }

        vibrateAndPlayTone(message);
    }

    public synchronized void onNewMsg(List<EMMessage> messages) {
        if (EMChatManager.getInstance().isSlientMessage(messages.get(messages.size() - 1))) {
            return;
        }
        if (CommonUtils.isAppRunningBackground(appContext)) {
            ChatLogger.d(TAG, "app is running in backgroud");
            sendNotification(messages, false);
        } else {
            sendNotification(messages, true);
        }
        vibrateAndPlayTone(messages.get(messages.size() - 1));
    }

    protected void sendNotification(List<EMMessage> messages, boolean isForeground) {
        for (EMMessage message : messages) {
            if (!isForeground) {
                notificationNum++;
                fromUsers.add(message.getFrom());
            }
        }
        sendNotification(messages.get(messages.size() - 1), isForeground, false);
    }

    protected void sendNotification(EMMessage message, boolean isForeground) {
        sendNotification(message, isForeground, true);
    }

    /**
     * 发送通知栏提示
     * This can be override by subclass to provide customer implementation
     */
    protected void sendNotification(EMMessage message, boolean isForeground, boolean numIncrease) {
        String username = message.getFrom();
        try {
            String notifyText = username + " ";
            switch (message.getType()) {
                case TXT:
                    notifyText += messageStrings[0];
                    break;
                case IMAGE:
                    notifyText += messageStrings[1];
                    break;
                case VOICE:
                    notifyText += messageStrings[2];
                    break;
                case LOCATION:
                    notifyText += messageStrings[3];
                    break;
                case VIDEO:
                    notifyText += messageStrings[4];
                    break;
                case FILE:
                    notifyText += messageStrings[5];
                    break;
            }

            PackageManager packageManager = appContext.getPackageManager();

            // notification title
            String contentTitle = (String) packageManager
                    .getApplicationLabel(appContext.getApplicationInfo());
            if (notificationInfoProvider != null) {
                String customNotifyText = notificationInfoProvider.getDisplayedText(message);
                String customContentTitle = notificationInfoProvider.getTitle(message);
                if (customNotifyText != null) {
                    notifyText = customNotifyText;
                }
                if (customContentTitle != null) {
                    contentTitle = customContentTitle;
                }
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                    .setSmallIcon(appContext.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);

            Intent msgIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (notificationInfoProvider != null) {
                msgIntent = notificationInfoProvider.getLaunchIntent(message);
            }

            PendingIntent pendingIntent = PendingIntent
                    .getActivity(appContext, notifyID, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (numIncrease) {
                // prepare latest event info section
                if (!isForeground) {
                    notificationNum++;
                    fromUsers.add(message.getFrom());
                }
            }

            int fromUsersNum = fromUsers.size();
            String summaryBody = messageStrings[6].replaceFirst("%1", Integer.toString(fromUsersNum))
                    .replaceFirst("%2", Integer.toString(notificationNum));

            if (notificationInfoProvider != null) {
                // latest text
                String customSummaryBody = notificationInfoProvider
                        .getLatestText(message, fromUsersNum, notificationNum);
                if (customSummaryBody != null) {
                    summaryBody = customSummaryBody;
                }

                // small icon
                int smallIcon = notificationInfoProvider.getSmallIcon(message);
                if (smallIcon != 0) {
                    mBuilder.setSmallIcon(smallIcon);
                }
            }

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setTicker(notifyText);
            mBuilder.setContentText(summaryBody);
            mBuilder.setContentIntent(pendingIntent);
            // mBuilder.setNumber(notificationNum);
            Notification notification = mBuilder.build();

            if (isForeground) {
                notificationManager.notify(foregroundNotifyID, notification);
                notificationManager.cancel(foregroundNotifyID);
            } else {
                notificationManager.notify(notifyID, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机震动和声音提示
     */
    public void vibrateAndPlayTone(EMMessage message) {
        if (message != null) {
            if (EMChatManager.getInstance().isSlientMessage(message)) {
                return;
            }
        }

        HXSDKModel model = HXSDKHelper.getInstance().getModel();
        if (!model.getSettingMsgNotification()) {
            return;
        }

        if (System.currentTimeMillis() - lastNotifyTime < 1000) {
            // received new messages within 2 seconds, skip play ringtone
            return;
        }

        try {
            lastNotifyTime = System.currentTimeMillis();

            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                ChatLogger.w(TAG, "in slient mode now");
                return;
            }

            if (model.getSettingMsgVibrate()) {
                long[] pattern = new long[] { 0, 180, 80, 120 };
                vibrator.vibrate(pattern, -1);
            }

            if (model.getSettingMsgSound()) {
                if (ringtone == null) {
                    Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                    if (ringtone == null) {
                        ChatLogger.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
                        return;
                    }
                }

                if (!ringtone.isPlaying()) {
                    String vendor = Build.MANUFACTURER;

                    ringtone.play();
                    // for samsung S3, we meet a bug that the phone will
                    // continue ringtone without stopAsync
                    // so add below special handler to stopAsync it after 3s if
                    // needed
                    if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                        Thread ctlThread = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    if (ringtone.isPlaying()) {
                                        ringtone.stop();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        };
                        ctlThread.run();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置NotificationInfoProvider
     *
     * @param provider
     */
    public void setNotificationInfoProvider(HXNotificationInfoProvider provider) {
        notificationInfoProvider = provider;
    }

    public interface HXNotificationInfoProvider {
        /**
         * 设置发送notification时状态栏提示新消息的内容(比如Xxx发来了一条图片消息)
         *
         * @param message 接收到的消息
         * @return null为使用默认
         */
        String getDisplayedText(EMMessage message);

        /**
         * 设置notification持续显示的新消息提示(比如2个联系人发来了5条消息)
         *
         * @param message      接收到的消息
         * @param fromUsersNum 发送人的数量
         * @param messageNum   消息数量
         * @return null为使用默认
         */
        String getLatestText(EMMessage message, int fromUsersNum, int messageNum);

        /**
         * 设置notification标题
         *
         * @return null为使用默认
         */
        String getTitle(EMMessage message);

        /**
         * 设置小图标
         *
         * @return 0使用默认图标
         */
        int getSmallIcon(EMMessage message);

        /**
         * 设置notification点击时的跳转intent
         *
         * @param message 显示在notification上最近的一条消息
         * @return null为使用默认
         */
        Intent getLaunchIntent(EMMessage message);
    }
}
