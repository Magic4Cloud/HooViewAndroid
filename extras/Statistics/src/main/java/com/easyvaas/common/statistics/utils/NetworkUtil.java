package com.easyvaas.common.statistics.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

class NetworkUtil {
    public static final String TAG = NetworkUtil.class.getSimpleName();
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_2G = 2;
    public static final int NETWORK_TYPE_3G = 3;
    public static final int NETWORK_TYPE_4G = 4;
    public static final int NETWORK_TYPE_WIFI = 10;

    public static String getNetType(Context context) {
        int type = getNetworkType(context);
        String netType = "";
        switch (type) {
            case NETWORK_TYPE_NONE:
                netType = "";
                break;
            case NETWORK_TYPE_2G:
                netType = "2G";
                break;
            case NETWORK_TYPE_3G:
                netType = "3G";
                break;
            case NETWORK_TYPE_4G:
                netType = "4G";
                break;
            case NETWORK_TYPE_WIFI:
                netType = "wifi";
                break;
        }
        return netType;
    }

    public static int getNetworkType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_CDMA:  // telcom
                    case TelephonyManager.NETWORK_TYPE_1xRTT: // telecom
                    case TelephonyManager.NETWORK_TYPE_GPRS:  // unicom
                    case TelephonyManager.NETWORK_TYPE_EDGE:  // cmcc
                        return NETWORK_TYPE_2G;
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  // telecom
                    case TelephonyManager.NETWORK_TYPE_EVDO_0: // telecom
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: // telecom 3.5G
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // telecom 3.5G
                    case TelephonyManager.NETWORK_TYPE_HSPA:   // unicom
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  // unicom
                    case TelephonyManager.NETWORK_TYPE_HSDPA:  // unicom 3.5G
                    case TelephonyManager.NETWORK_TYPE_HSUPA:  // unicom 3.5G
                    case TelephonyManager.NETWORK_TYPE_UMTS:   // unicom
                        return NETWORK_TYPE_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETWORK_TYPE_4G;
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_TYPE_WIFI;
            }
        }
        return NETWORK_TYPE_NONE;
    }

    public static boolean isNetworkAvailable(Context ctx) {
        return isNetworkAvailable(ctx, false);
    }

    public static boolean isNetworkAvailable(Context ctx, boolean withToast) {
        boolean isAvailable = false;
        if (ctx != null) {
            ConnectivityManager connMgr = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    isAvailable = networkInfo.getState() == NetworkInfo.State.CONNECTED;
                }
            }
        }
        if (!isAvailable && withToast) {
        }
        return isAvailable;
    }
}
