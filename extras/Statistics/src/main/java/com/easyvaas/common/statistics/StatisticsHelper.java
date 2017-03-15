package com.easyvaas.common.statistics;

import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.easyvaas.common.statistics.logcollector.LogCollector;
import com.easyvaas.common.statistics.qualitymonitor.QualityMonitor;
import com.easyvaas.common.statistics.schedule.ScheduleController;
import com.easyvaas.common.statistics.service.NetworkStateService;
import com.easyvaas.common.statistics.service.ResourceMonitorService;
import com.easyvaas.common.statistics.utils.StatisticsDB;

public class StatisticsHelper implements IStatisticsManager {

    private static StatisticsHelper mInstance;
    private Context mContext;

    private StatisticsHelper(Context context) {
        mContext = context;
    }

    public static StatisticsHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StatisticsHelper(context);
        }
        return mInstance;
    }

    @Override
    public void init(String channel) {
        StatisticsDB.updateChannel(mContext, channel);
        Intent intent = new Intent(mContext, NetworkStateService.class);
        mContext.startService(intent);
        mContext.startService(new Intent(mContext, ResourceMonitorService.class));

        LogCollector.init(mContext);
        LogCollector.upload(false);

        QualityMonitor.init(mContext);
    }

    @Override
    public void setUserInfo(String name, String sessionId) {
        StatisticsDB.updateUserInfo(mContext, name, sessionId);
    }

    @Override
    public void checkBestIP(boolean force) {
        ScheduleController.getInstance(mContext).checkBestIP(false);
    }

    @Override
    public ScheduleInfo getPlayBestUrl(String url) {
        return ScheduleController.getInstance(mContext).getPlayBestUrl(url);
    }

    @Override
    public String getPublishBestUrl(String url) {
        return ScheduleController.getInstance(mContext).getPublishBestUrl(url);
    }

    @Override
    public void playbackStart(String vid, String extra, boolean isLive, String ip, String url) {
        QualityMonitor.playbackStart(vid, extra, isLive, ip, url);
    }

    @Override
    public void playbackEvent(String vid, String extra, int type) {
        QualityMonitor.playbackEvent(vid, extra, type);
    }

    @Override
    public void playbackBufferingEnd(String vid, String extra, int duration) {
        QualityMonitor.playbackBufferingEnd(vid, extra, duration);
    }

    @Override
    public void playbackPrepared(String vid, String extra, int type, int openTime) {
        QualityMonitor.playbackPrepared(vid, extra, type, openTime);
    }

    @Override
    public void playbackError(String vid, String extra, int fw_err, int impl_err) {
        QualityMonitor.playbackError(vid, extra, fw_err, impl_err);
    }

    @Override
    public void playbackStatistics(String vid, String extra, boolean live, String sip, int speed,
            int bandwidth, int percent) {
        QualityMonitor.playbackStatistics(vid, extra, live, sip, speed, bandwidth, percent);
    }

    @Override
    public void addLiveStatistics(Context context, String vid, Map<String, String> param, String sip) {
        QualityMonitor.addLiveStatistics(context, vid, param, sip);
    }

    @Override
    public void reportStartLiveAction(Context context, String action, String rtmpUrl, String vid,
            int backCamWidth, int backCamHeight, int frontCamWidth, int frontCamHeight, String extra) {
        QualityMonitor.reportStartLiveAction(context, action, rtmpUrl, vid, backCamWidth, backCamHeight,
                frontCamWidth, frontCamHeight, extra);
    }

    @Override
    public void reportLiveInterrupt(Context context, String vid, String extra, String type) {
        QualityMonitor.reportLiveInterrupt(context, vid, extra, type);
    }

    @Override
    public void reportPublishStatus(Context context, String vid, String extra, String status) {
        QualityMonitor.reportPublishStatus(context, vid, extra, status);
    }

    @Override
    public void reportLiveStopReason(Context context, String vid, String extra, String reason) {
        QualityMonitor.reportLiveStopReason(context, vid, extra, reason);
    }

    @Override
    public void setLastPublishUrl(Context context, String url) {
        StatisticsDB.getInstance(context).putString(StatisticsDB.KEY_LATEST_URL_PUBLISH, url);
    }

    @Override
    public void setLastUrl(Context context, String url) {
        StatisticsDB.getInstance(context).putString(StatisticsDB.KEY_LATEST_URL, url);
    }

    @Override
    public void clear() {
        QualityMonitor.clear();
    }
}
