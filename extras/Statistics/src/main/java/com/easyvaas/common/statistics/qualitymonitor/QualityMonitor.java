package com.easyvaas.common.statistics.qualitymonitor;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.easyvaas.common.statistics.logcollector.LogCollector;
import com.easyvaas.common.statistics.net.AppStatHelper;
import com.easyvaas.common.statistics.utils.StatisticsLogger;
import com.easyvaas.common.statistics.utils.StatisticsUtility;

public class QualityMonitor {
    private static final String TAG = "QualityMonitor";

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

    public static void clear() {
        QualityDataHandler.getInstance(mContext).clearSampleList();
    }

    public static void ping(List<String> ipList, final int times, final PingResultCallback callback) {
        AsyncTask pingTask = new AsyncTask<Object, Integer, String>() {
            List<PingResultEntity> results = new ArrayList<>();

            @Override protected String doInBackground(Object... objects) {
                List<String> ips;
                if (objects != null && objects.length > 1) {
                    ips = (List<String>) objects[0];

                    int ipSize = ips.size();
                    for (int i = 0; i < ipSize; ++i) {
                        NetPing ping = new NetPing(new NetPing.NetPingListener() {
                            @Override public void OnNetPingFinished(String log, PingResultEntity result) {
                                results.add(result);
                            }
                        }, times);

                        ping.exec(ips.get(i));
                    }
                }
                return null;
            }

            @Override protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //best ip save to preference

                callback.OnPingFinished(results);
            }
        };
        pingTask.execute(ipList);
    }

    public static void addLiveStatistics(Context context, String vid, Map<String, String> param,
            String sip) {
        QualityDataHandler.getInstance(context).addLiveStatistics(vid, param, sip);
    }

    public static void reportStartLiveAction(Context context, String action, String rtmpUrl, String vid,
            int backCamWidth, int backCamHeight, int frontCamWidth, int frontCamHeight, String extra) {
        String networkType = StatisticsUtility.getNetType(context);

        String encodeURL = "";
        try {
            encodeURL = URLEncoder.encode(rtmpUrl, "UTF-8");
        } catch (Exception e) {
            StatisticsLogger.e(TAG, "reportStartLiveAction() Encode failed, url:" + rtmpUrl, e);
        }

        String frontCameraResolution = String.format("%dx%d", frontCamHeight, frontCamWidth);
        String backCameraResolution = String.format("%dx%d", backCamHeight, backCamWidth);

        AppStatHelper.getInstance(context).reportLiveMsg(action, vid, extra, Build.VERSION.RELEASE,
                networkType, encodeURL, frontCameraResolution, backCameraResolution);
    }

    public static void reportLiveInterrupt(Context context, String vid, String extra, String type) {
        AppStatHelper.getInstance(context).reportLiveInterrupt(Constants.STREAMER_EVENT_INTERRUPT, vid, extra,
                type);
    }

    public static void reportPublishStatus(Context context, String vid, String extra, String status) {
        AppStatHelper.getInstance(context).reportPublishStatus(Constants.STREAMER_EVENT_PUBLISH, vid, extra,
                status);
    }

    public static void reportLiveStopReason(Context context, String vid, String extra, String reason) {
        AppStatHelper.getInstance(context).reportLiveStop(Constants.STREAMER_EVENT_LIVE_STOP, vid, extra,
                reason);
    }

    public static void playbackStart(String vid, String extra, boolean isLive, String ip, String url) {
        LogCollector.playbackStart(vid, extra, isLive, ip, url);
    }

    public static void playbackEvent(String vid, String extra, int type) {
        LogCollector.playbackEvent(vid, extra, type);
    }

    public static void playbackBufferingEnd(String vid, String extra, int duration) {
        LogCollector.playbackBufferingEnd(vid, extra, duration);
    }

    public static void playbackPrepared(String vid, String extra, int type, int openTime) {
        LogCollector.playbackPrepared(vid, extra, type, openTime);
    }

    public static void playbackError(String vid, String extra, int fw_err, int impl_err) {
        LogCollector.playbackError(vid, extra, fw_err, impl_err);
    }

    public static void playbackStatistics(String vid, String extra, boolean live, String sip, int speed,
            int bandwidth, int percent) {
        LogCollector.playbackStatistics(vid, extra, live, sip, speed, bandwidth, percent);
    }

    public static QualityStatisticsEntity getQualityStatistics() {
        return QualityDataHandler.getInstance(mContext).getQualityStatistics();
    }
}
