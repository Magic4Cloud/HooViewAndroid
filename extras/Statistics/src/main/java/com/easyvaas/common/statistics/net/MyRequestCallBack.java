package com.easyvaas.common.statistics.net;

import java.lang.reflect.Type;

import com.google.gson.Gson;

import com.easyvaas.common.statistics.utils.StatisticsLogger;

public abstract class MyRequestCallBack<T> {
    private static final String TAG = "RequestCallBack";

    public static final String MSG_DOWNLOAD_FILE_FAILED = "msg_download_file_failed";

    public void onStart() {
    }

    public void onCancelled() {
    }

    public void onLoading(long total, long current, boolean isUploading) {
    }

    protected void parseGson(String json, Class<T> clazz) {
        if (clazz == String.class) {
            onSuccess((T) json);
        } else {
            try {
                onSuccess(new Gson().fromJson(json, clazz));
            } catch (Exception e) {
                onError(ApiConstant.E_PARSE_GSON);
                StatisticsLogger.e(TAG, "parse class: " + clazz + ", error", e);
            }
        }
    }

    protected void parseGson(String json, Type type) {
        onSuccess((T) (new Gson().fromJson(json, type)));
    }

    public abstract void onSuccess(T result);

    public void onError(String errorInfo) {
        StatisticsLogger.w(TAG, "Error info: " + errorInfo);
    }

    public abstract void onFailure(String msg);

}
