package com.easyvaas.common.statistics;

import java.util.Map;

import android.content.Context;

interface IStatisticsManager {
    void init(String channel);

    void setUserInfo(String name, String sessionId);

    void checkBestIP(boolean force);

    ScheduleInfo getPlayBestUrl(String url);

    String getPublishBestUrl(String url);

    void playbackStart(String vid, String extra, boolean isLive, String ip, String url);

    void playbackEvent(String vid, String extra, int type);

    void playbackBufferingEnd(String vid, String extra, int duration);

    void playbackPrepared(String vid, String extra, int type, int openTime);

    void playbackError(String vid, String extra, int fw_err, int impl_err);

    void playbackStatistics(String vid, String extra, boolean live, String sip, int speed, int bandwidth,
            int percent);

    void addLiveStatistics(Context context, String vid, Map<String, String> param, String sip);

    void reportStartLiveAction(Context context, String action, String rtmpUrl, String vid,
            int backCamWidth, int backCamHeight, int frontCamWidth, int frontCamHeight, String extra);

    void reportLiveInterrupt(Context context, String vid, String extra, String type);

    void reportPublishStatus(Context context, String vid, String extra, String status);

    void reportLiveStopReason(Context context, String vid, String extra, String reason);

    void setLastPublishUrl(Context context, String url);

    void setLastUrl(Context context, String url);

    void clear();
}
