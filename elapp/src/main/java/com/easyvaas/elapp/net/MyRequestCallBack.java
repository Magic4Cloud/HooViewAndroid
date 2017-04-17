/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.net;

import com.easyvaas.elapp.utils.Logger;
import com.google.gson.Gson;

import java.lang.reflect.Type;

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
        Logger.e(TAG, clazz + ":: " + json);
        if (clazz == String.class) {
            onSuccess((T) json);
        } else {
            try {
                onSuccess(new Gson().fromJson(json, clazz));
            } catch (Exception e) {
                onError(ApiConstant.E_PARSE_GSON);
                Logger.e(TAG, "parse class: " + clazz + ", error", e);
            }
        }
    }

    protected void parseGson(String json, Type type) {
        onSuccess((T)(new Gson().fromJson(json, type)));
    }

    public abstract void onSuccess(T result);

    public void onError(String errorInfo) {
        Logger.w(TAG, "Error info: " + errorInfo);
    }

    public abstract void onFailure(String msg);

}
