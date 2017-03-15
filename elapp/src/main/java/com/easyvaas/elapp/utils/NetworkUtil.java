/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;

import com.hooview.app.R;

public class NetworkUtil {
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
            SingleToast.show(ctx, R.string.msg_network_invalid);
        }
        return isAvailable;
    }

    public static long getTotalRxBytes() {  // All receive bytes, contain Mobile/WiFi
        return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ?
                0 :
                (TrafficStats.getTotalRxBytes() / 1024);
    }

    public static String formatNetSpeed(long speedBytes) {
        if (speedBytes >= 1024) {
            return String.format("%1$.2f", speedBytes / 1024.0f) + "MB/S";
        }
        return speedBytes + "KB/S";
    }

    // Following method return byte unit is "KB"

    public long getTotalTxBytes() {  // All send bytes, contain Mobile/WiFi
        return TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ?
                0 :
                (TrafficStats.getTotalTxBytes() / 1024);
    }

    public long getTotalRxBytes(int uid) {  // All receive bytes, contain Mobile/WiFi
        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ?
                0 :
                (TrafficStats.getUidRxBytes(uid) / 1024);
    }

    public long getTotalTxBytes(int uid) {  // All send bytes, contain Mobile/WiFi
        return TrafficStats.getUidTxBytes(uid) == TrafficStats.UNSUPPORTED ?
                0 :
                (TrafficStats.getUidTxBytes(uid) / 1024);
    }

    public long getMobileRxBytes() { // All receive bytes, only Mobile
        return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ?
                0 :
                (TrafficStats.getMobileRxBytes() / 1024);
    }

    private String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses();
                     enumIpAddress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            Logger.w(TAG, "getPhoneIp failed !", e);
        }
        return "";
    }

    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
