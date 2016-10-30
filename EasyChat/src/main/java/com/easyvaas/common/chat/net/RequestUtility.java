package com.easyvaas.common.chat.net;

import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.db.ChatDB;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.CommonUtils;
import com.easyvaas.common.chat.utils.SingleToast;

public class RequestUtility {
    private static String sAppUA;
    private static String sName;

    public static void handleRequestFailed(Context context) {
        SingleToast.show(context, R.string.msg_network_bad_check_retry);
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
                ChatLogger.w("RequestUtil", "key: " + entry.getKey() + " is empty !");
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
            sAppUA = System.getProperty("http.agent") + "; easyvaas android v"
                    + CommonUtils.getVerName(context) + "-"
                    + ChatDB.getChannel(context);
        }
        return sAppUA;
    }

    public static String getName(Context context) {
        if (TextUtils.isEmpty(sName)) {
            sName = ChatDB.getSessionId(context);
        }
        return sName;
    }
}
