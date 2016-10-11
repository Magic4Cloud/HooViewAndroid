/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.net;

import java.util.Map;

import android.graphics.Bitmap;

import org.json.JSONObject;

public interface IRequestHelper {
    void getAsString(String url, MyRequestCallBack<String> callBack);

    void getAsGson(String url, Class clazz, MyRequestCallBack<?> callBack);

    void postAsString(String url, Map<String, String> params, MyRequestCallBack<String> callBack);

    void postAsGson(String url, Map<String, String> params, Class clazz, MyRequestCallBack<?> callBack);

    void postAsString(String url, Map<String, String> params, JSONObject jsonObject,
            MyRequestCallBack<String> callBack);

    void postAsGzip(String url, Map<String, String> params, JSONObject jsonObject,
            MyRequestCallBack<String> callBack);

    void uploadFile(String url, Map<String, String> params, Bitmap bitmap, String fileName,
            MyRequestCallBack<String> callBack);

    void downloadFile(String url, String saveFileDir, MyRequestCallBack<String> callBack);

    void handleResponse(String response, MyRequestCallBack<?> callBack, Class clazz);

    void cancelRequest(String tag);
}
