package com.easyvaas.common.chat.net;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.easyvaas.common.chat.utils.ChatLogger;

class JsonParserUtil {
    private static final String TAG = JsonParserUtil.class.getSimpleName();

    private static JSONObject checkResponse(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            ChatLogger.w(TAG, "checkResponse wrong ", e);
        }
        return jsonObject;
    }

    public static JSONObject getJsonObject(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            ChatLogger.w(TAG, "checkResponse wrong ", e);
        }
        return jsonObject;
    }

    public static boolean isResponseOk(JSONObject jsonObject) {
        if (jsonObject != null) {
            String status = jsonObject.optString(ApiConstant.KEY_RET_VAL);
            if (TextUtils.isEmpty(status)) {
                status = jsonObject.optString(ApiConstant.KEY_RET_VAL_SHORT);
            }
            if (jsonObject.has("created_at")) {
                return true;
            } else if ("ok".equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResponseOk(String jsonString) {
        JSONObject jsonObject = checkResponse(jsonString);
        return isResponseOk(jsonObject);
    }

    public static String getResultInfo(JSONObject jsonObject) {
        if (jsonObject == null || !isResponseOk(jsonObject)) {
            return "";
        }
        String info = "";
        info = jsonObject.optString(ApiConstant.KEY_RET_INFO);
        if (TextUtils.isEmpty(info)) {
            info = jsonObject.optString(ApiConstant.KEY_RET_INFO_SHORT);
        }
        if (info.matches("\\s*\\{\\s*\\}\\s*")) {
            return "";
        }
        return info;
    }

    public static String getResultValue(JSONObject jsonObject) {
        if (jsonObject == null) {
            return "";
        }
        String info = "";
        info = jsonObject.optString(ApiConstant.KEY_RET_VAL);
        if (TextUtils.isEmpty(info)) {
            info = jsonObject.optString(ApiConstant.KEY_RET_VAL_SHORT);
        }
        return info;
    }

    public static String getErrorInfo(JSONObject jsonObject) {
        if (jsonObject == null) {
            return "";
        }
        String info = "";
        info = jsonObject.optString(ApiConstant.KEY_RET_ERR);
        if (TextUtils.isEmpty(info)) {
            info = jsonObject.optString(ApiConstant.KEY_RET_ERR_SHORT);
        }
        return info;
    }

    public static String getString(String json, String key) {
        return getString(getJsonObject(json), key);
    }

    public static String getString(JSONObject jsonObject, String key) {
        if (jsonObject == null) {
            return "";
        }
        String info = jsonObject.optString(key, "");
        if (info.matches("\\s*\\{\\s*\\}\\s*")) {
            return "";
        }
        return info;
    }

    public static int getInt(String json, String key) {
        return getInt(getJsonObject(json), key);
    }

    public static int getInt(JSONObject jsonObject, String key) {
        if (jsonObject == null) {
            return -1;
        }
        return jsonObject.optInt(key, -1);
    }

    public static boolean getBoolean(String json, String key) {
        return getBoolean(getJsonObject(json), key);
    }

    public static boolean getBoolean(JSONObject jsonObject, String key) {
        if (jsonObject == null) {
            return false;
        }
        return jsonObject.optBoolean(key, false);
    }
}
