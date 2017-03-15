package com.easyvaas.common.statistics.logcollector;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.easyvaas.common.statistics.LogConstants;
import com.easyvaas.common.statistics.utils.StatisticsDB;
import com.easyvaas.common.statistics.utils.StatisticsLogger;
import com.easyvaas.common.statistics.utils.StatisticsUtility;

class PlaybackEventHandler {
    private static final String TAG = PlaybackEventHandler.class.getSimpleName();

    private static final String CHARSET = "UTF-8";

    private static PlaybackEventHandler mInstance;

    private String mLogPrefix;

    private Context mContext;

    private PlaybackEventHandler(Context context) {
        mContext = context;

        String username = StatisticsDB.getName(context);
        mLogPrefix = "from=android&name=" + username + "&module=playback";
    }

    public static PlaybackEventHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PlaybackEventHandler(context);
        }

        return mInstance;
    }

    public void handleStart(String vid, String extra, boolean isLive, String ip, String url) {
        String prefix = getPrefix(vid, extra, LogConstants.PLAYBACK_START);
        String logInfo = String.format("%s&live=%d&ip=%s&playurl=%s\r\n", prefix, isLive ? 1 : 0, ip,
                encodeValue(url));

        StatisticsLogger.d(TAG, "save to log file: " + logInfo);

        LogFileStorage.getInstance(mContext).saveLogFile2Internal(logInfo);
    }

    public void handleStatistics(String vid, String extra, boolean live, String sip, int speed, int bandwidth,
            int percent) {
        String prefix = getPrefix(vid, extra, LogConstants.PLAYBACK_STATISTICS);
        String logInfo = String.format("%s&live=%d&sip=%s&speed=%d&bandwidth=%d&percent=%d\r\n", prefix,
                live ? 1 : 0, sip, speed, bandwidth, percent);

        StatisticsLogger.d(TAG, "save to log file: " + logInfo);

        LogFileStorage.getInstance(mContext).saveLogFile2Internal(logInfo);
    }

    public void handlePlaybackEvent(String vid, String extra, int type) {
        String prefix = getPrefix(vid, extra, type);
        String logInfo = String.format("%s\r\n", prefix);

        StatisticsLogger.d(TAG, "save to log file: " + logInfo);

        LogFileStorage.getInstance(mContext).saveLogFile2Internal(logInfo);
    }

    public void handleBufferingEnd(String vid, String extra, int duration) {
        String prefix = getPrefix(vid, extra, LogConstants.PLAYBACK_BUFFERING_END);
        String logInfo = String.format("%s&duration=%d\r\n", prefix, duration);

        StatisticsLogger.d(TAG, "save to log file: " + logInfo);

        LogFileStorage.getInstance(mContext).saveLogFile2Internal(logInfo);
    }

    public void handlePlaybackPrepared(String vid, String extra, int type, int opentime) {
        String prefix = getPrefix(vid, extra, type);
        String logInfo = String.format("%s&opentime=%d\r\n", prefix, opentime);
        StatisticsLogger.d(TAG, "prepared, save to log file: " + logInfo);

        LogFileStorage.getInstance(mContext).saveLogFile2Internal(logInfo);
    }

    public void handlePlaybackError(String vid, String extra, int fw_err, int impl_err) {
        String prefix = getPrefix(vid, extra, LogConstants.PLAYBACK_ERROR);
        String logInfo = String.format("%s&errorcode=%d\r\n", prefix, impl_err);
        LogFileStorage.getInstance(mContext).saveLogFile2Internal(logInfo);
    }

    private String getAction(int type) {
        String action = "unknown";

        switch (type) {
            case LogConstants.PLAYBACK_START:
                action = LogConstants.START;
                break;
            case LogConstants.PLAYBACK_OPEN_INPUT:
                action = LogConstants.OPEN_INPUT;
                break;
            case LogConstants.PLAYBACK_FIND_STREAM_INFO:
                action = LogConstants.FIND_STREAM_INFO;
                break;
            case LogConstants.PLAYBACK_PREPARED:
                action = LogConstants.PREPARED;
                break;
            case LogConstants.PLAYBACK_BUFFERING_START:
                action = LogConstants.BUFFERING_START;
                break;
            case LogConstants.PLAYBACK_BUFFERING_END:
                action = LogConstants.BUFFERING_END;
                break;
            case LogConstants.PLAYBACK_STATISTICS:
                action = LogConstants.STATISTICS;
                break;
            case LogConstants.PLAYBACK_CLOSE:
                action = LogConstants.CLOSE;
                break;
            case LogConstants.PLAYBACK_ERROR:
                action = LogConstants.ERROR;
                break;
            case LogConstants.PLAYBACK_DRAG:
                action = LogConstants.DRAG;
                break;
        }

        return action;
    }

    private String getPrefix(String vid, String extra, int type) {
        String action = getAction(type);
        String nettype = StatisticsUtility.getNetType(mContext);
        String logInfo = String.format("%s&action=%s&pts=%s&vid=%s&nettype=%s&extra=%s", mLogPrefix,
                action, System.currentTimeMillis() + "", vid, nettype, extra);

        return logInfo;
    }

    private static String encodeValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return value;
        }
        return Uri.encode(value);
    }
}
