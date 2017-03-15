package com.easyvaas.common.statistics.logcollector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easyvaas.common.statistics.net.AppStatHelper;
import com.easyvaas.common.statistics.net.MyRequestCallBack;

class UploadLogManager {
    private static final String TAG = "UploadLogManager";

    private static UploadLogManager sInstance;

    private Context mContext;
    private HandlerThread mHandlerThread;
    private boolean mUploadByGzip = false;
    private volatile boolean isRunning = false;

    private UploadLogManager(Context c) {
        mContext = c.getApplicationContext();
        mHandlerThread = new HandlerThread(TAG + ":HandlerThread");
        mHandlerThread.start();
    }

    public static synchronized UploadLogManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new UploadLogManager(c);
        }
        return sInstance;
    }

    public void uploadLogFile(boolean bGzip) {
        mUploadByGzip = bGzip;
        MyHandler handler = new MyHandler(mHandlerThread.getLooper());
        if (mHandlerThread == null) {
            return;
        }
        if (isRunning) {
            return;
        }
        handler.sendMessage(handler.obtainMessage());
        isRunning = true;
    }

    private JSONObject eventsToJson(List<Map<String, String>> events) {
        JSONArray jsonArray = new JSONArray();
        JSONObject retObject = new JSONObject();
        for (int i = 0; i < events.size(); ++i) {
            Map<String, String> itemMap = events.get(i);

            Iterator<Map.Entry<String, String>> iterator = itemMap.entrySet().iterator();

            JSONObject object = new JSONObject();

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                try {
                    object.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            jsonArray.put(object);
        }

        try {
            retObject.put("events", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retObject;
    }

    private List<Map<String, String>> getEventsFromFile(File logFile) {
        List<Map<String, String>> events = new ArrayList<>();

        try {
            FileInputStream is = new FileInputStream(logFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                Map<String, String> itemMap = new HashMap<>();
                itemMap.put("event", line);
                events.add(itemMap);

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }

    private JSONObject listToJson(List<String> events) {
        JSONArray jsonArray = new JSONArray();
        JSONObject retObject = new JSONObject();
        for (int i = 0; i < events.size(); ++i) {
            String event = events.get(i);

            jsonArray.put(event);
        }

        try {
            retObject.put("events", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retObject;
    }

    private List<String> getListFromFile(File logFile) {
        List<String> events = new ArrayList<>();

        try {
            FileInputStream is = new FileInputStream(logFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                if (!TextUtils.isEmpty(line)) {
                    events.add(line);
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }

    private final class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            File logFile = LogFileStorage.getInstance(mContext).getUploadLogFile();
            if (logFile == null) {
                isRunning = false;
                return;
            }
            //JSONObject jsonEvents = eventsToJson(getEventsFromFile(logFile));
            JSONObject jsonEvents = listToJson(getListFromFile(logFile));
            AppStatHelper.getInstance(mContext).postAppLog(jsonEvents, mUploadByGzip,
                    new MyRequestCallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            LogFileStorage.getInstance(mContext).deleteUploadLogFile();
                            isRunning = false;
                        }

                        @Override
                        public void onFailure(String msg) {
                            isRunning = false;
                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
                            isRunning = false;
                        }
                    });
        }
    }
}
