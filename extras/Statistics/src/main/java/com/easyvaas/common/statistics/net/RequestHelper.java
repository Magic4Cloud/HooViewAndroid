package com.easyvaas.common.statistics.net;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import com.easyvaas.common.statistics.utils.StatisticsLogger;

class RequestHelper {
    private static final String TAG = "RequestHelper";
    private static final int MY_SOCKET_TIMEOUT_MS = 4000;
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
    private static final String CACHE_FILE = ApiConstant.CACHE_DOWNLOAD_DIR + File.separator + "cache.tmp";

    private static RequestHelper mInstance;

    private Handler mHandler;
    private OkHttpClient mClient;

    private RequestHelper(final Context context) {
        mHandler = new Handler(context.getMainLooper());

        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(new File(CACHE_FILE), cacheSize);
        Interceptor mNetworkInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request requestOrigin = chain.request();
                Headers headersOrigin = requestOrigin.headers();
                Headers headers = headersOrigin.newBuilder()
                        .set("User-Agent", RequestUtility.getAppUA(context))
                        .set("Charset", "utf-8").build();
                Request request = requestOrigin.newBuilder().headers(headers).build();
                return chain.proceed(request);
            }
        };
        mClient = new OkHttpClient().newBuilder()
                .connectTimeout(MY_SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(MY_SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .writeTimeout(MY_SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(mNetworkInterceptor)
                .cache(cache)
                .build();
    }

    public static RequestHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestHelper(context);
        }
        return mInstance;
    }

    public void cancelRequest() {
        StatisticsLogger.d(TAG, "cancelRequest(), cancel all request");
        String tag = "";
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

    public void getAsString(String url, Map<String, String> params, MyRequestCallBack<String> callBack) {
        url = RequestUtility.assembleUrlWithAllParams(url, params);
        requestAsGson(url, params, String.class, callBack, false);
    }

    public void postAsString(String url, JSONObject jsonObject, Map<String, String> params,
            final MyRequestCallBack<String> callBack) {
        StatisticsLogger.d(TAG, "Request url: " + url);
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(RequestUtility.assembleUrlWithAllParams(url, params))
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

    public void postAsGzip(String url, JSONObject jsonObject, Map<String, String> params,
            final MyRequestCallBack<String> callBack) {
        // TODO Need to implement
    }

    private void requestAsGson(String url, Map<String, String> params, final Class clazz,
            final MyRequestCallBack<?> callBack, boolean isPostMethod) {
        StatisticsLogger.d(TAG, "Request url: " + url);
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (isPostMethod) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
            }
            requestBuilder.post(builder.build());
        } else {
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

    private void handleResponse(final Response response, final MyRequestCallBack<?> callBack, Class clazz)
            throws IOException {
        if (response.isSuccessful()) {
            handleResponse(response.body().string(), callBack, clazz);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onError(response.message());
                }
            });
        }
    }

    public void handleResponse(final String response, final MyRequestCallBack<?> callBack,
            final Class clazz) {
        StatisticsLogger.d(TAG, "Request success: " + response);
        final JSONObject jsonObject = JsonParserUtil.getJsonObject(response);
        boolean isInMainThread = Looper.getMainLooper().getThread() == Thread.currentThread();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JsonParserUtil.isResponseOk(jsonObject)) {
                    if (callBack != null) {
                        final String info = JsonParserUtil.getResultInfo(jsonObject);
                        callBack.parseGson(info, clazz);
                    }
                } else {
                    parseErrorInfo(jsonObject, callBack);
                }
            }
        });
    }

    private void parseErrorInfo(JSONObject jsonObject, MyRequestCallBack callBack) {
        String errorInfo = JsonParserUtil.getResultValue(jsonObject);
        String errorTips = JsonParserUtil.getErrorInfo(jsonObject);
        if (callBack != null) {
            callBack.onError(errorInfo);
        }
        if (errorInfo.isEmpty()) {
            return;
        }
    }

    private void handlerNetworkError(Call call, final Exception exception,
            final MyRequestCallBack<?> callBack) {
        if ("Canceled".equalsIgnoreCase(exception.getMessage())) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onFailure(exception.getMessage());
                }
            }
        });
    }
}
