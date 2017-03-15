package com.easyvaas.elapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.easyvaas.elapp.event.RefreshExponentEvent;
import com.easyvaas.elapp.event.RefreshLastestNewsEvent;
import com.easyvaas.elapp.event.RefreshStockEvent;
import com.easyvaas.elapp.utils.Logger;

import org.greenrobot.eventbus.EventBus;


public class AppService extends Service {
    private static final String TAG = "AppService";
    public static final int REFRESH_INTERVAL_TIME_ONE_MIN = 1000 * 60;
    public static final int REFRESH_INTERVAL_TIME_FIVE_MIN = REFRESH_INTERVAL_TIME_ONE_MIN * 5;
    public static final int MSG_REFRESH_STOCK = 1;
    public static final int MSG_REFRESH_EXPONENT = 2;
    public static final int MSG_REFRESH_LASTEST_NEWS = 3;
    public static final String ACTION_REFRESH_STOCK = "action.refresh.stock";
    public static final String ACTION_REFRESH_EXPONENT = "action.refresh.exponent";
    public static final String ACTION_REFRESH_LASTEST_NEWS = "action.refresh.lastest.news";

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_STOCK: {
                    Logger.d(TAG, "handleMessage: refresh stock!!");
                    EventBus.getDefault().post(new RefreshStockEvent());
                    Message message = Message.obtain();
                    message.what = MSG_REFRESH_STOCK;
                    mHandler.sendMessageDelayed(message, REFRESH_INTERVAL_TIME_ONE_MIN);
                    break;
                }
                case MSG_REFRESH_EXPONENT: {
                    Logger.d(TAG, "handleMessage: refresh exponent!!");
                    EventBus.getDefault().post(new RefreshExponentEvent());
                    Message message = Message.obtain();
                    message.what = MSG_REFRESH_EXPONENT;
                    mHandler.sendMessageDelayed(message, REFRESH_INTERVAL_TIME_ONE_MIN);
                }
                case MSG_REFRESH_LASTEST_NEWS: {
                    EventBus.getDefault().post(new RefreshLastestNewsEvent());
                    Message message = Message.obtain();
                    message.what = MSG_REFRESH_LASTEST_NEWS;
                    mHandler.sendMessageDelayed(message, REFRESH_INTERVAL_TIME_ONE_MIN);
                }
                default: {

                }
            }
        }
    };

    public static void startRefreshStock(Context context) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(ACTION_REFRESH_STOCK);
        context.startService(intent);
    }

    public static void startRefreshExponent(Context context) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(ACTION_REFRESH_EXPONENT);
        context.startService(intent);
    }

    public static void startRefreshLastestNews(Context context) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(ACTION_REFRESH_LASTEST_NEWS);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return super.onStartCommand(intent, flags, startId);
            }
            if (action.equals(ACTION_REFRESH_EXPONENT)) {
                startRefreshExponent();
            } else if (action.equals(ACTION_REFRESH_STOCK)) {
                startRefreshStock();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startRefreshStock() {
        mHandler.removeMessages(MSG_REFRESH_STOCK);
        Message message = Message.obtain();
        message.what = MSG_REFRESH_STOCK;
        mHandler.sendMessage(message);
    }

    private void startRefreshExponent() {
        mHandler.removeMessages(MSG_REFRESH_EXPONENT);
        Message message = Message.obtain();
        message.what = MSG_REFRESH_EXPONENT;
        mHandler.sendMessage(message);
    }

    private void startRefreshLastestNews() {
        mHandler.removeMessages(MSG_REFRESH_LASTEST_NEWS);
        Message message = Message.obtain();
        message.what = MSG_REFRESH_LASTEST_NEWS;
        mHandler.sendMessage(message);
    }

    @Override
    public boolean stopService(Intent name) {
        mHandler.removeMessages(MSG_REFRESH_STOCK);
        mHandler.removeMessages(MSG_REFRESH_EXPONENT);
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_REFRESH_STOCK);
        mHandler.removeMessages(MSG_REFRESH_EXPONENT);
    }

}
