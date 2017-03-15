package com.easyvaas.common.statistics.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class StatisticsUtility {
    private static final String TAG = "StatisticsUtility";

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable() && mWiFiNetworkInfo.isConnected();
            }
        }
        return false;
    }

    public static File getExternalDir(Context context, String dirName) {
        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/";
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + cacheDir + dirName + "/");
    }

    public static boolean isSDcardExsit() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getMid(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        String AndroidID = android.provider.Settings.System
                .getString(context.getContentResolver(), "android_id");
        String serialNo = getDeviceSerialForMid2();
        return getMD5Str("" + imei + AndroidID + serialNo);
    }

    public static String getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(date);
        return time;
    }

    public static String getVerName(Context c) {
        PackageManager pm = c.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            StatisticsLogger.e(TAG, "Error while collect package info", e);
            e.printStackTrace();
            return "error";
        }
        if (pi == null) {
            return "error1";
        }
        String versionName = pi.versionName;
        if (versionName == null) {
            return "not set";
        }
        return versionName;
    }

    public static String getVerCode(Context c) {
        PackageManager pm = c.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            StatisticsLogger.e(TAG, "Error while collect package info", e);
            e.printStackTrace();
            return "error";
        }
        if (pi == null) {
            return "error1";
        }
        int versionCode = pi.versionCode;

        return String.valueOf(versionCode);
    }

    private static String getDeviceSerialForMid2() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }

    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getNetType(Context context) {
        return NetworkUtil.getNetType(context);
    }
}
