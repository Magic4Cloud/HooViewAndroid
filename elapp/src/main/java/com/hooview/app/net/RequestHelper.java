/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.net;

import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import org.json.JSONObject;

import com.hooview.app.utils.Logger;

public class RequestHelper {
    private static final String TAG = RequestHelper.class.getSimpleName();

    private static IRequestHelper sRequestHelper;
    private static RequestHelper mInstance;

    private RequestHelper(Context context) {
        sRequestHelper = new OkHttpRequest(context);
    }

    public static RequestHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestHelper(context);
        }
        return mInstance;
    }

    public void cancelRequest() {
        Logger.d(TAG, "cancelRequest(), cancel all request");
        sRequestHelper.cancelRequest("");
    }

    public void getAsString(String url, Map<String, String> params, MyRequestCallBack<String> callBack) {
        statisticRequestEvent(url);
        url = RequestUtil.assembleUrlWithAllParams(url, params);
        sRequestHelper.getAsString(url, callBack);
    }

    public void getAsGson(String url, Map<String, String> params, final Class clazz,
            MyRequestCallBack<?> callBack) {
        statisticRequestEvent(url);
        url = RequestUtil.assembleUrlWithParams(url, params);
        sRequestHelper.getAsGson(url, clazz, callBack);
    }

    public void postAsGson(String url, Map<String, String> params,
            final Class clazz, final MyRequestCallBack<?> callBack) {
        sRequestHelper.postAsGson(url, params, clazz, callBack);
    }

    public void postAsString(String url, Map<String, String> params,
            final MyRequestCallBack<String> callBack) {
        sRequestHelper.postAsString(url, params, callBack);
    }

    public void postAsString(String url, JSONObject jsonObject, Map<String, String> params,
            final MyRequestCallBack<String> callBack) {
        statisticRequestEvent(url);
        sRequestHelper.postAsString(url, params, jsonObject, callBack);
    }

    public void postAsGzip(String url, JSONObject jsonObject, Map<String, String> params,
            final MyRequestCallBack<String> callBack) {
        statisticRequestEvent(url);
        sRequestHelper.postAsGzip(url, params, jsonObject, callBack);
    }

    public void uploadFile(String url, Map<String, String> params, Bitmap bitmap, String fileName,
            MyRequestCallBack<String> callBack) {
        statisticRequestEvent(url);
        sRequestHelper.uploadFile(url, params, bitmap, fileName, callBack);
    }

    public void downloadFile(String url, String saveFileDir, MyRequestCallBack<String> callBack) {
        sRequestHelper.downloadFile(url, saveFileDir, callBack);
    }

    private void statisticRequestEvent(String url) {
        if (TextUtils.isEmpty(url)) {
        } else if (url.contains("?")) {
            String event = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        }
    }
}
