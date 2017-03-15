package com.easyvaas.common.statistics.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.easyvaas.common.statistics.StatisticsHelper;
import com.easyvaas.common.statistics.utils.StatisticsDB;
import com.easyvaas.common.statistics.utils.StatisticsLogger;

public class NetworkStateService extends Service {
    private static final String TAG = "NetworkStateService";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private String type = "";
        private boolean firstConnect = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                StatisticsLogger.d(TAG, "network state was changed");
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    String name = networkInfo.getTypeName();

                    //should get the best ip
                    if (!name.equals(type) && !firstConnect) {
                        StatisticsLogger.d(TAG, "network switch, force to get best ip");
                        StatisticsDB.getInstance(context).putBoolean(StatisticsDB.KEY_WS_BEST_IP_VALIDITY,
                                false);
                        StatisticsHelper.getInstance(context).checkBestIP(true);
                    }

                    if (firstConnect) {
                        firstConnect = false;
                    }

                    type = name;
                } else {
                    type = "";
                    firstConnect = true;
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
