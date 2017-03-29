
package com.easyvaas.elapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * 保存崩溃日志的 handler
 */
public class CarNetCrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = "CarNetCrashHandler";
    private Context mContext;
    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString();
    private static CarNetCrashHandler mInstance = new CarNetCrashHandler();

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        saveInfoToSD(mContext, ex);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ExitAppUtils.getInstance().exit();
    }

    private CarNetCrashHandler() {
    }

    public static CarNetCrashHandler getInstance() {
        return mInstance;
    }


    public void setCustomCrashHandler(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private void showToast(final Context context, final String msg) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

    private HashMap<String, String> obtainSimpleInfo(Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        map.put("versionName", mPackageInfo.versionName);
        map.put("versionCode", "" + mPackageInfo.versionCode);

        map.put("MODEL", "" + Build.MODEL);
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
        map.put("PRODUCT", "" + Build.PRODUCT);

        return map;
    }

    private String obtainExceptionInfo(Throwable throwable) {
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        throwable.printStackTrace(mPrintWriter);
        mPrintWriter.close();

        Log.e(TAG, mStringWriter.toString());
        return mStringWriter.toString();
    }

    private String saveInfoToSD(Context context, Throwable ex) {
        String fileName = null;
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> entry : obtainSimpleInfo(context).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.append(key).append(" = ").append(value).append("\n");
        }

        builder.append(obtainExceptionInfo(ex));

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(SDCARD_ROOT + File.separator + "00TimeLog/crash" + File.separator);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                fileName = dir.toString() + File.separator + parseTime(System.currentTimeMillis()) + ".log";
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(builder.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return fileName;

    }

    private String parseTime(long milliseconds) {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time = format.format(new Date(milliseconds));

        return time;
    }

    public static class ExitAppUtils {

        private List<Activity> mActivityList = new LinkedList<Activity>();
        private static ExitAppUtils instance = new ExitAppUtils();

        private ExitAppUtils() {
        }

        public static ExitAppUtils getInstance() {
            return instance;
        }

        public void addActivity(Activity activity) {
            mActivityList.add(activity);
        }

        public void delActivity(Activity activity) {
            mActivityList.remove(activity);
        }

        public void exit() {
            for (Activity activity : mActivityList) {
                activity.finish();
            }

            System.exit(0);
        }

    }
}
