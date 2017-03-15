package com.easyvaas.common.chat.net;

import java.lang.reflect.Type;

import com.easyvaas.common.chat.utils.ChatLogger;
import com.google.gson.Gson;

public abstract class MyRequestCallBack<T> {
    private static final String TAG = "RequestCallBack";

    public static final String MSG_DOWNLOAD_FILE_FAILED = "msg_download_file_failed";

    public void onStart() {
    }

    protected void parseGson(String json, Class<T> clazz) {
        if (clazz == String.class) {
            onSuccess((T) json);
        } else {
            try {
                onSuccess(new Gson().fromJson(json, clazz));
            } catch (Exception e) {
                onError(ApiConstant.E_PARSE_GSON);
                ChatLogger.e(TAG, "parse class: " + clazz + ", error", e);
            }
        }
    }

    protected void parseGson(String json, Type type) {
        onSuccess((T) (new Gson().fromJson(json, type)));
    }

    public abstract void onSuccess(T result);

    public void onError(String errorInfo) {
        ChatLogger.w(TAG, "Error info: " + errorInfo);
    }

    public abstract void onFailure(String msg);

}
