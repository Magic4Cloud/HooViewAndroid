package com.easyvaas.common.statistics.logcollector;

import java.io.File;

import android.content.Context;

import com.easyvaas.common.statistics.LogConstants;
import com.easyvaas.common.statistics.utils.StatisticsLogger;
import com.easyvaas.common.statistics.utils.StatisticsUtility;

public class LogCollector {
    private static final String TAG = "LogCollector";

    private static Context mContext;

    private static boolean isInit = false;

    public static void init(Context c) {

        if (c == null) {
            return;
        }

        if (isInit) {
            return;
        }

        mContext = c;

        isInit = true;

    }

    public static void upload(boolean isWifiOnly) {
        if (mContext == null) {
            StatisticsLogger.d(TAG, "please check if init() or not");
            return;
        }
        if (!StatisticsUtility.isNetworkConnected(mContext)) {
            return;
        }

        boolean isWifiMode = StatisticsUtility.isWifiConnected(mContext);

        if (isWifiOnly && !isWifiMode) {
            return;
        }

        UploadLogManager.getInstance(mContext).uploadLogFile(false);
    }

    private static void checkUploadForcibly() {
        if (mContext == null) {
            StatisticsLogger.d(TAG, "please check if init() or not");
            return;
        }

        File logFile = LogFileStorage.getInstance(mContext).getUploadLogFile();
        if (logFile == null) {
            return;
        }

        int fileSize = Integer.parseInt(String.valueOf(logFile.length() / 1024));
        if (fileSize < 5) {
            StatisticsLogger.d(TAG, "log file size was less than 5KB, file size: " + fileSize + "KB");
            return;
        }

        StatisticsLogger.d(TAG, "log file size was larger than 5KB, upload it forcibly");
        UploadLogManager.getInstance(mContext).uploadLogFile(false);
    }

    public static void publishEvent(String log) {
        PublishEventHandler.getInstance(mContext).handlePublishLog(log);
    }

    public static void publishHeartbeat(String log) {
        PublishEventHandler.getInstance(mContext).handlePublishLog(log);
        checkUploadForcibly();
    }

    public static void publishLiveStop(String log) {
        PublishEventHandler.getInstance(mContext).handlePublishLog(log);
        upload(false);
    }

    public static void playbackStart(String vid, String extra, boolean isLive, String ip, String url) {
        PlaybackEventHandler.getInstance(mContext).handleStart(vid, extra, isLive, ip, url);
    }

    public static void playbackEvent(String vid, String extra, int type) {
        PlaybackEventHandler.getInstance(mContext).handlePlaybackEvent(vid, extra, type);
        if (type == LogConstants.PLAYBACK_CLOSE) {
            checkUploadForcibly();
        }
    }

    public static void playbackBufferingEnd(String vid, String extra, int duration) {
        PlaybackEventHandler.getInstance(mContext).handleBufferingEnd(vid, extra, duration);
    }

    public static void playbackPrepared(String vid, String extra, int type, int openTime) {
        PlaybackEventHandler.getInstance(mContext).handlePlaybackPrepared(vid, extra, type, openTime);
    }

    public static void playbackError(String vid, String extra, int fw_err, int impl_err) {
        PlaybackEventHandler.getInstance(mContext).handlePlaybackError(vid, extra, fw_err, impl_err);
    }

    public static void playbackStatistics(String vid, String extra, boolean live, String sip, int speed,
            int bandwidth, int percent) {
        PlaybackEventHandler.getInstance(mContext)
                .handleStatistics(vid, extra, live, sip, speed, bandwidth, percent);
    }
}
