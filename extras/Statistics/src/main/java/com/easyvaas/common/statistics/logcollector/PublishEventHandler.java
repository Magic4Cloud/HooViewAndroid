package com.easyvaas.common.statistics.logcollector;

import android.content.Context;

import com.easyvaas.common.statistics.utils.StatisticsLogger;

class PublishEventHandler {
    private static final String TAG = PublishEventHandler.class.getSimpleName();

    private static PublishEventHandler mInstance;

    private Context mContext;

    private PublishEventHandler(Context context) {
        mContext = context;
    }

    public static PublishEventHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PublishEventHandler(context);
        }

        return mInstance;
    }

    public void handlePublishLog(String logInfo) {
        StatisticsLogger.d(TAG, "save to log file: " + logInfo);
        LogFileStorage.getInstance(mContext).saveLogFile2Internal(logInfo + "\r\n");
    }
}
