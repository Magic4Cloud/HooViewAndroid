package com.easyvaas.common.statistics.net;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.easyvaas.common.statistics.utils.StatisticsDB;
import com.easyvaas.common.statistics.utils.StatisticsLogger;
import com.easyvaas.common.statistics.utils.StatisticsUtility;

class RequestUtility {
    private static String sAppUA;
    private static String sName;
    private static String sSessionId;

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
                StatisticsLogger.w("RequestUtil", "key: " + entry.getKey() + " is empty !");
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

    private static String encodeValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return value;
        }
        return Uri.encode(value);
    }

    public static String getAppUA(Context context) {
        if (TextUtils.isEmpty(sAppUA)) {
            sAppUA = System.getProperty("http.agent") + "; yizhibo android v"
                    + StatisticsUtility.getVerName(context) + "-"
                    + StatisticsDB.getChannel(context);
        }
        return sAppUA;
    }

    public static String getSessionId(Context context) {
        if (TextUtils.isEmpty(sSessionId)) {
            sSessionId = StatisticsDB.getSessionId(context);
        }
        return sSessionId;
    }

    public static String getName(Context context) {
        if (TextUtils.isEmpty(sName)) {
            sName = StatisticsDB.getSessionId(context);
        }
        return sName;
    }

    public static String getAppSignature(Map<String, String> param, String appKeySecret) {
        String string_to_sign = getStringToSign(param);
        String app_signature = "";
        try {
            app_signature = hmacSha1(string_to_sign, appKeySecret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return app_signature;
    }

    private static String getStringToSign(Map<String, String> map) {
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

    private static String hmacSha1(String base, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        byte[] digest = mac.doFinal(base.getBytes());
        return bytesToHex(digest);
    }

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

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

}
