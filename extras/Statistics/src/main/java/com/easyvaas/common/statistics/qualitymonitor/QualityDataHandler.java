package com.easyvaas.common.statistics.qualitymonitor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.easyvaas.common.statistics.net.AppStatHelper;
import com.easyvaas.common.statistics.service.ResourceMonitorService;

class QualityDataHandler {
    private static final String TAG = QualityDataHandler.class.getSimpleName();

    private static QualityDataHandler mInstance;

    private int maxSamples = 25;

    private Context mContext;

    private List<Map<String, String>> statList;
    private ResourceMonitorService resourceMonitorService;

    private QualityStatisticsEntity qualityStatistics;

    private DecimalFormat formatPercent = new DecimalFormat("##0.0");

    private QualityDataHandler(Context context) {
        mContext = context;
        statList = new ArrayList<>(maxSamples);
        mContext.bindService(new Intent(mContext, ResourceMonitorService.class), mServiceConnection, 0);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            resourceMonitorService = ((ResourceMonitorService.ResourceDataBinder) iBinder).getService();
        }

        @Override public void onServiceDisconnected(ComponentName componentName) {
            resourceMonitorService = null;
        }
    };

    public static QualityDataHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new QualityDataHandler(context);
        }

        return mInstance;
    }

    public void clearSampleList() {
        statList.clear();
        qualityStatistics = new QualityStatisticsEntity("", 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public QualityStatisticsEntity getQualityStatistics() {
        return qualityStatistics;
    }

    public void addLiveStatistics(String vid, Map<String, String> param, String sip) {
        while (statList.size() >= maxSamples) {
            statList.remove(statList.size() - 1);
        }

        statList.add(0, param);

        qualityStatistics.setVid(vid);
        String videoBufferLength = param.get("videoBufferLength");
        //qualityStatistics.setBuffer_duration(Integer.parseInt());
        //calculate quality data
        calculateQualityData();
        if (resourceMonitorService != null) {
            qualityStatistics.setCpuTotal(resourceMonitorService.getCPUTotalP().get(0));
            qualityStatistics.setCpuAM(resourceMonitorService.getCPUAMP().get(0));
            qualityStatistics.setMemTotal(resourceMonitorService.getMemTotal());
            qualityStatistics.setMemUsed(
                    Integer.parseInt(resourceMonitorService.getMemUsed().get(0)) * 100
                            / (float) resourceMonitorService.getMemTotal());
            qualityStatistics.setMemAM(
                    resourceMonitorService.getMemoryAM().get(0) * 100 / (float) resourceMonitorService
                            .getMemTotal());
        }

        AppStatHelper.getInstance(mContext).reportLiveParameters(Constants.STREAMER_EVENT_HEARTBEAT, vid,
                param, sip, qualityStatistics);
    }

    private void calculateQualityData() {
        int size = statList.size();

        if (size <= 1) {
            return;
        }

        Map<String, String> stat = null;

        double speedSum = 0;
        for (int i = 0; i < size; i++) {
            stat = statList.get(i);
            speedSum = speedSum + Double.parseDouble(stat.get("speed"));
        }

        double speed_avg = speedSum / size;

        double allSquare = 0;
        for (int i = 0; i < size; i++) {
            double speed = Double.parseDouble(statList.get(i).get("speed"));
            allSquare += (speed - speed_avg) * (speed - speed_avg);
        }

        allSquare /= (size - 1);

        double speed_std_dev = Math.sqrt(allSquare);

        qualityStatistics.setSpeed_avg((int) speed_avg);
        qualityStatistics.setSpeed_dev((int) speed_std_dev);
    }
}
