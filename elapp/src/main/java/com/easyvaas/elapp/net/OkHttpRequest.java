/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.BuildConfig;
import com.hooview.app.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

class OkHttpRequest implements IRequestHelper {
    private static final String TAG = "OkHttpRequest";
    //TODO
    private static final int MY_SOCKET_TIMEOUT_MS = 40000;

    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");

    private Handler mHandler;
    private OkHttpClient mClient;
    private Context mContext;

    public OkHttpRequest(Context context) {
        mContext = context;
        mHandler = new Handler(context.getMainLooper());

        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(context.getExternalCacheDir(), cacheSize);
        Interceptor mNetworkInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request requestOrigin = chain.request();
                Headers headersOrigin = requestOrigin.headers();
                Headers headers = headersOrigin.newBuilder()
                        .set("User-Agent", RequestUtil.getAppUA())
                        .set("Charset", "utf-8").build();
                Request request = requestOrigin.newBuilder().headers(headers).build();
                return chain.proceed(request);
            }
        };

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        mClient = new OkHttpClient().newBuilder()
                .connectTimeout(MY_SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(MY_SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .writeTimeout(MY_SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(mNetworkInterceptor)
                .addNetworkInterceptor(logInterceptor)
                .cache(cache)
                .build();
    }

    @Override
    public void getAsString(String url, final MyRequestCallBack<String> callBack) {
        getAsGson(url, String.class, callBack);
    }

    @Override
    public void getAsGson(String url, final Class clazz, final MyRequestCallBack<?> callBack) {
        requestAsGson(url, null, clazz, callBack, false);
    }

    @Override
    public void postAsString(String url, Map<String, String> params,
                             final MyRequestCallBack<String> callBack) {
        postAsGson(url, params, String.class, callBack);
    }

    @Override
    public void postAsGson(String url, Map<String, String> params, final Class clazz,
                           final MyRequestCallBack<?> callBack) {
        requestAsGson(url, params, clazz, callBack, true);
    }

    private void requestAsGson(String url, Map<String, String> params, final Class clazz,
                               final MyRequestCallBack<?> callBack, boolean isPostMethod) {
        Logger.e(TAG, "Request url: " + url);
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (isPostMethod) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
            }
            String json = new JSONObject(params).toString();
            Logger.d(TAG, "Request isPostMethod: "+json);
            requestBuilder.post(builder.build());
            Logger.d(TAG, "Request body: " + builder.toString());
        } else {
            Logger.d(TAG, "Request body: " + requestBuilder.toString());
            requestBuilder.get();
        }
        mClient.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handlerNetworkError(call, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response, callBack, clazz);
            }
        });
    }

    @Override
    public void postAsString(String url, Map<String, String> params, JSONObject jsonObject,
                             final MyRequestCallBack<String> callBack) {
        Logger.d(TAG, "Request post url: " + url);
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(RequestUtil.assembleUrlWithParams(url, params))
                .post(body)
                .build();
        mClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                handlerNetworkError(call, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response, callBack, String.class);
            }
        });
    }

    @Override
    public void postAsGzip(String url, Map<String, String> params, JSONObject jsonObject,
                           MyRequestCallBack<String> callBack) {

    }

    @Override
    public void uploadFile(String url, Map<String, String> params, Bitmap bitmap, String fileName,
                           final MyRequestCallBack<String> callBack) {
        Logger.d(TAG, "Request upload url: " + url);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MEDIA_TYPE_JPG, baos.toByteArray()))
                .build();
        Request request = new Request.Builder()
                .url(RequestUtil.assembleUrlWithParams(url, params))
                .post(requestBody)
                .build();

//     ≥


        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handlerNetworkError(call, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response, callBack, String.class);
            }
        });
    }

    @Override
    public void downloadFile(final String url, final String saveFileDir,
                             final MyRequestCallBack<String> callBack) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mClient.newCall(request);
        String md5Url = url;
        try {
            md5Url = Utils.getMD5(url);
        } catch (NoSuchAlgorithmException e) {
            Logger.w(TAG, "MD5 url failed", e);
        }
        //final String fileName = (url.lastIndexOf("/") < 0) ? md5Url
        //        : url.substring(url.lastIndexOf("/") + 1, url.length());
        final String fileName = md5Url;
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onFailure(MyRequestCallBack.MSG_DOWNLOAD_FILE_FAILED);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(saveFileDir, fileName);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    if (callBack != null) {
                        callBack.onSuccess(file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    if (callBack != null) {
                        callBack.onFailure(MyRequestCallBack.MSG_DOWNLOAD_FILE_FAILED);
                    }
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Logger.w(TAG, "downloadFile , io exception !", e);
                    }
                }
            }
        });
    }

    private void handleResponse(final Response response, final MyRequestCallBack<?> callBack, Class clazz)
            throws IOException {
        if (callBack == null) {
            return;
        }
        if (response.isSuccessful()) {
            handleResponse(response.body().string(), callBack, clazz);
            response.body().close();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onError(response.message());
                }
            });
        }
    }

    @Override
    public void handleResponse(final String response, @NonNull final MyRequestCallBack<?> callBack,
                               final Class clazz) {
        Logger.d(TAG, "Request success: " + response);
        final JSONObject jsonObject = JsonParserUtil.getJsonObject(response);
        boolean isInMainThread = Looper.getMainLooper().getThread() == Thread.currentThread();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JsonParserUtil.isResponseOk(jsonObject)) {
                    final String info = JsonParserUtil.getResultInfo(jsonObject);
                    callBack.parseGson(info, clazz);
                } else {
                    parseErrorInfo(jsonObject, callBack);
                }
            }
        });
    }

    private void parseErrorInfo(JSONObject jsonObject, MyRequestCallBack callBack) {
        String errorInfo = JsonParserUtil.getResultValue(jsonObject);
        String errorTips = JsonParserUtil.getErrorInfo(jsonObject);
        callBack.onError(errorInfo);
        if (errorInfo.isEmpty()) {
            return;
        }
        if (ApiConstant.E_SESSION.equals(errorInfo)) {
            boolean isLastUserLogout = Preferences.getInstance(EVApplication.getApp())
                    .getBoolean(Preferences.KEY_IS_LOGOUT, false);
            if (!isLastUserLogout) {
                // 登录过期
                Preferences.getInstance(EVApplication.getApp()).logout(true);
                EVApplication.setUser(null);
                SingleToast.show(EVApplication.getApp(), EVApplication.getApp().getString(R.string.login_info_expired));
                LoginActivity.start(mContext);
            }
        } else if (!TextUtils.isEmpty(errorTips)) {
            if (!errorTips.equals("无数据"))
            SingleToast.show(EVApplication.getApp(), errorTips);
        }
    }

    private void handlerNetworkError(Call call, final Exception exception,
                                     final MyRequestCallBack<?> callBack) {
        if (callBack == null || "Canceled".equalsIgnoreCase(exception.getMessage())) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onFailure(exception.getMessage());
            }
        });
    }

    @Override
    public void cancelRequest(String tag) {
        boolean isCancelAll = TextUtils.isEmpty(tag);
        for (Call call : mClient.dispatcher().queuedCalls()) {
            if (isCancelAll) {
                call.cancel();
            } else if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
        for (Call call : mClient.dispatcher().runningCalls()) {
            if (isCancelAll) {
                call.cancel();
            } else if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
    }
}
