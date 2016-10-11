/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.net;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.net.Uri;
import android.text.TextUtils;

import com.hooview.app.BuildConfig;
import com.hooview.app.app.EVApplication;
import com.hooview.app.utils.ChannelUtil;
import com.hooview.app.utils.SingleToast;

public class RequestUtil {
    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    private static String sAppUA;

    public static void handleRequestFailed(String msg) {
        SingleToast.show(EVApplication.getApp(), com.hooview.app.R.string.msg_network_bad_check_retry);
     }

    public static String assembleUrlWithParams(String url, Map<String, String> params) {
        url = url + getParams(params, false);
        return url.replace(" ", "%20");
    }

    public static String assembleUrlWithAllParams(String url, Map<String, String> params) {
        url = url + getParams(params, true);
        return url.replace(" ", "%20");
    }

    private static String getParams(Map<String, String> map, boolean allowNullParam) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder("");
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String value = entry.getValue();
            if (!allowNullParam && (TextUtils.isEmpty(value) || "null".equals(value))) {
                continue;
            }
            if (iterator.hasNext()) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(encodeValue(value));
                builder.append("&");
            } else {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(encodeValue(value));
            }
        }
        return builder.toString();
    }

    public static String getAppUA() {
        if (TextUtils.isEmpty(sAppUA)) {
            sAppUA = System.getProperty("http.agent") + "; easyvaas android v" + BuildConfig.VERSION_NAME + "-"
                    + ChannelUtil.getChannelName(EVApplication.getApp());
        }
        return sAppUA;
    }

    public static String encodeValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return value;
        }
        String result = Uri.encode(value);
        return result;
    }

    /**
     * Get only url host and path
     */
    public static String decodeUrlPage(String strURL) {
        String strPage = null;
        String[] arrSplit = null;

        strURL = strURL.trim().toLowerCase();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 0) {
            if (arrSplit.length > 1) {
                if (arrSplit[0] != null) {
                    strPage = arrSplit[0];
                }
            }
        }

        return strPage;
    }

    public static Map<String, String> decodeUrlParam(String url) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;

        String strUrlParam = getUrlParam(url);
        if (strUrlParam == null) {
            return mapRequest;
        }
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            if (arrSplitEqual.length > 1) {
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else if (!arrSplitEqual[0].isEmpty()) {
                mapRequest.put(arrSplitEqual[0], "");
            }
        }
        return mapRequest;
    }

    /**
     * Remove url path, only leave param part.
     */
    private static String getUrlParam(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }
        return strAllParam;
    }

    public static String hmacSha1(String base, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        byte[] digest = mac.doFinal(base.getBytes());
        return bytesToHex(digest);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getStringToSign(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }

        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> lhs, Map.Entry<String, String> rhs) {
                return (lhs.getKey()).compareTo(rhs.getKey());
            }
        });

        StringBuilder builder = new StringBuilder("");

        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                builder.append(list.get(i).getKey());
                builder.append("=");
                builder.append(encodeValue(list.get(i).getValue()));
                builder.append("&");
            } else {
                builder.append(list.get(i).getKey());
                builder.append("=");
                builder.append(encodeValue(list.get(i).getValue()));
            }
        }

        return builder.toString();
    }
}
