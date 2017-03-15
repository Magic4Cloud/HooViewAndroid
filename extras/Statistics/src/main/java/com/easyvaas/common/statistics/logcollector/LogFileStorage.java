package com.easyvaas.common.statistics.logcollector;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;

import com.easyvaas.common.statistics.utils.StatisticsLogger;
import com.easyvaas.common.statistics.utils.StatisticsUtility;

class LogFileStorage {
    private static final String TAG = "LogFileStorage";

    private static final String LOG_SUFFIX = ".log";
    private static final String CHARSET = "UTF-8";

    private static LogFileStorage sInstance;

    private Context mContext;

    private LogFileStorage(Context ctx) {
        mContext = ctx.getApplicationContext();
    }

    public static synchronized LogFileStorage getInstance(Context ctx) {
        if (sInstance == null) {
            sInstance = new LogFileStorage(ctx);
        }
        return sInstance;
    }

    public File getUploadLogFile() {
        File dir = mContext.getFilesDir();
        File logFile = new File(dir, StatisticsUtility.getMid(mContext) + LOG_SUFFIX);
        if (logFile.exists()) {
            return logFile;
        } else {
            return null;
        }
    }

    public boolean deleteUploadLogFile() {
        File dir = mContext.getFilesDir();
        File logFile = new File(dir, StatisticsUtility.getMid(mContext) + LOG_SUFFIX);
        return logFile.delete();
    }

    public boolean saveLogFile2Internal(String logString) {
        try {
            File dir = mContext.getFilesDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File logFile = new File(dir, StatisticsUtility.getMid(mContext) + LOG_SUFFIX);
            FileOutputStream fos = new FileOutputStream(logFile, true);
            fos.write(logString.getBytes(CHARSET));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            StatisticsLogger.e(TAG, "saveLogFile2Internal failed!");
            return false;
        }
        return true;
    }

    public boolean saveLogFile2SDcard(String logString, boolean isAppend) {
        if (!StatisticsUtility.isSDcardExsit()) {
            StatisticsLogger.e(TAG, "sdcard not exist");
            return false;
        }
        try {
            File logDir = getExternalLogDir();
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            File logFile = new File(logDir, StatisticsUtility.getMid(mContext) + LOG_SUFFIX);
            /*if (!isAppend) {
                if (logFile.exists() && !logFile.isFile())
					logFile.delete();
			}*/
            StatisticsLogger.d(TAG, logFile.getPath());

            FileOutputStream fos = new FileOutputStream(logFile, isAppend);
            fos.write(logString.getBytes(CHARSET));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            StatisticsLogger.e(TAG, "saveLogFile2SDcard failed!", e);
            return false;
        }
        return true;
    }

    private File getExternalLogDir() {
        File logDir = StatisticsUtility.getExternalDir(mContext, "Log");
        StatisticsLogger.d(TAG, "getExternalLogDir, " + logDir.getPath());
        return logDir;
    }
}
