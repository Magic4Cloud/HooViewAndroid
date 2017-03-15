package com.easyvaas.common.statistics.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.easyvaas.common.statistics.ScheduleInfo;
import com.easyvaas.common.statistics.net.AppStatHelper;
import com.easyvaas.common.statistics.net.MyRequestCallBack;
import com.easyvaas.common.statistics.utils.StatisticsDB;
import com.easyvaas.common.statistics.utils.StatisticsLogger;
import com.easyvaas.common.statistics.utils.StatisticsUtility;

public class ScheduleController {
    private static final String TAG = ScheduleController.class.getSimpleName();

    private static ScheduleController mInstance;
    private Context mContext;
    private StatisticsDB mPref;

    private Map<String, PingStatusEntity> publishIPList;
    private Map<String, PingStatusEntity> playIPList;

    private static final int BEST_IP_TYPE_PUBLISH = 0;
    private static final int BEST_IP_TYPE_PLAY = 1;

    private static final int INTERVAL_GET_BEST_IP = 10 * 60 * 1000;

    private ScheduleController(Context context) {
        mContext = context.getApplicationContext();
        mPref = StatisticsDB.getInstance(context);

        publishIPList = new ConcurrentHashMap<>();
        playIPList = new ConcurrentHashMap<>();
    }

    public static ScheduleController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ScheduleController(context);
        }
        return mInstance;
    }

    public void checkBestIP(boolean force) {
        String playUrl = mPref.getString(StatisticsDB.KEY_LATEST_URL, "");
        if (TextUtils.isEmpty(playUrl)) {
            playUrl = Constants.WS_FLV_URL;
        }
        long lastGetTime = mPref.getLong(StatisticsDB.KEY_LAST_GET_WS_BEST_IP, 0);
        if (lastGetTime + INTERVAL_GET_BEST_IP < System.currentTimeMillis() || force) {
            StatisticsLogger.d(TAG, "get ws best ip for url: " + playUrl);
            getWSBestIP(playUrl);
        }

        String publishUrl = mPref.getString(StatisticsDB.KEY_LATEST_URL_PUBLISH, "");
        if (TextUtils.isEmpty(publishUrl)) {
            publishUrl = Constants.WS_PUSH_URL;
            StatisticsLogger.d(TAG, "latest publish url was empty, use the default : " + publishUrl);
        }
        long lastPublishGetTime = mPref.getLong(StatisticsDB.KEY_LAST_GET_WS_BEST_IP_PUBLISH, 0);
        if (lastPublishGetTime + INTERVAL_GET_BEST_IP < System.currentTimeMillis() || force) {
            StatisticsLogger.d(TAG, "get ws best ip for url: " + publishUrl);
            getWSBestIP(publishUrl);
        }
    }

    public String getPublishBestUrl(String url) {
        ScheduleInfo info = new ScheduleInfo();
        info.setUrl(url);
        info.setIp("");
        info.setUse_best_ip(false);

        if (url.startsWith(Constants.WS_PUSH_START_WITH)) {
            String bestPublishIp = mPref.getString(StatisticsDB.KEY_WS_BEST_IP_PUBLISH, "");
            if (TextUtils.isEmpty(bestPublishIp)) {
                StatisticsLogger.d(TAG, "use the origin publish url: " + url);
                return url;
            } else {
                String bestUrl = url.replace(Constants.WS_PUSH_DOMAIN, bestPublishIp) +
                        "?wsiphost=ipdbm&wsHost=" + Constants.WS_PUSH_DOMAIN;

                StatisticsLogger.d(TAG, "get the best publish url: " + bestUrl);
                return bestUrl;
            }
        } else {
            StatisticsLogger.d(TAG, "It was not ws publish url, use the origin publish url: " + url);
            return url;
        }
    }

    public ScheduleInfo getPlayBestUrl(String url) {
        ScheduleInfo info = new ScheduleInfo();
        info.setUrl(url);
        info.setIp("");
        info.setUse_best_ip(false);
        if (url.startsWith(Constants.WS_FLV_START_WITH)) {
            String bestPlayIp = mPref.getString(StatisticsDB.KEY_WS_BEST_IP, "");
            if (TextUtils.isEmpty(bestPlayIp)) {
                StatisticsLogger.d(TAG, "use the origin publish url: " + url);
                return info;
            } else {
                String destUrl = url;

                String protocol_arr[] = url.split(":");
                if (protocol_arr.length >= 2) {

                    String protocol = protocol_arr[0];

                    String sub_str = url.substring(7);
                    String substr_arr[] = sub_str.split("/");
                    if (substr_arr.length >= 3) {
                        String app = substr_arr[1];
                        String stream = substr_arr[2];
                        String domain = substr_arr[0];

                        destUrl = protocol + "://" + bestPlayIp + "/" + domain + "/" + app + "/"
                                + stream
                                + "?wsiphost=ipdbm";
                        StatisticsLogger.d(TAG, "get the best play url: " + destUrl);
                        info.setUrl(destUrl);
                        info.setIp(bestPlayIp);
                        info.setUse_best_ip(true);
                        return info;
                    }
                }

                return info;
            }
        } else {
            StatisticsLogger.d(TAG, "it was not ws play url, use the origin play url: " + url);
            return info;
        }
    }

    private String getBestIP(Map<String, PingStatusEntity> ipMap) {
        String bestIP = "";
        int rttAvgMin = Integer.MAX_VALUE;
        boolean allpinged = true;
        for (Map.Entry<String, PingStatusEntity> entry : ipMap.entrySet()) {
            PingStatusEntity pingStatus = entry.getValue();
            if (!pingStatus.isPinged()) {
                allpinged = false;
                break;
            }

            if (pingStatus.getRttAvg() < rttAvgMin) {
                bestIP = entry.getKey();
                rttAvgMin = pingStatus.getRttAvg();
            }
        }

        if (allpinged) {
            return bestIP;
        } else {
            for (Map.Entry<String, PingStatusEntity> entry : ipMap.entrySet()) {
                PingStatusEntity pingStatus = entry.getValue();
                if (pingStatus.getIndex() == 0) {
                    bestIP = entry.getKey();
                    break;
                }
            }

            return bestIP;
        }
    }

    public void getWSBestIP(final String url) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(Constants.KEY_WS_URL, url);
        headers.put(Constants.KEY_WS_RETIP_NUM, "3");
        headers.put(Constants.KEY_WS_URL_TYPE, "1");

        String reqUrl = Constants.WS_NGB_HOST + "/?key=" + StatisticsUtility.getRandomString(16);

        StatisticsLogger.d(TAG, "http get ws best ip, url: " + reqUrl);

        AppStatHelper.getInstance(mContext).getWSBestIP(reqUrl, headers, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                int type = BEST_IP_TYPE_PLAY;
                if (url.contains(Constants.WS_FLV_DOMAIN)) {
                    type = BEST_IP_TYPE_PLAY;
                    playIPList.clear();
                    mPref.putLong(StatisticsDB.KEY_LAST_GET_WS_BEST_IP, System.currentTimeMillis());
                } else if (url.contains(Constants.WS_PUSH_DOMAIN)) {
                    type = BEST_IP_TYPE_PUBLISH;
                    publishIPList.clear();
                    mPref.putLong(StatisticsDB.KEY_LAST_GET_WS_BEST_IP_PUBLISH, System.currentTimeMillis());
                }

                String ipArray[] = result.split("\n");
                int size = ipArray.length;
                List<String> ipList = new ArrayList<String>();
                for (int i = 0; i < size; ++i) {
                    StatisticsLogger.d(TAG, "ws best ip: " + ipArray[i]);
                    ipList.add(ipArray[i]);

                    PingStatusEntity pingStatus = new PingStatusEntity();
                    if (type == BEST_IP_TYPE_PUBLISH) {
                        publishIPList.put(ipArray[i], pingStatus);
                    } else {
                        playIPList.put(ipArray[i], pingStatus);
                    }
                }

                startPing(ipList, type);
            }

            @Override
            public void onFailure(String msg) {
                StatisticsLogger.e(TAG, "get ws best ip failed");
            }
        });
    }

    private void startPing(List<String> ipList, final int type) {
        AsyncTask pingTask = new AsyncTask<Object, Integer, String>() {
            @Override
            protected String doInBackground(Object... objects) {
                List<String> ips;
                int pingType;
                if (objects != null && objects.length > 1) {
                    ips = (List<String>) objects[0];
                    pingType = (int) objects[1];

                    int ipSize = ips.size();
                    for (int i = 0; i < ipSize; ++i) {
                        ping(ips.get(i), i, 3, pingType);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //best ip save to preference
                if (type == BEST_IP_TYPE_PUBLISH) {
                    if (publishIPList.size() > 0) {
                        String bestPublishIp = getBestIP(publishIPList);
                        StatisticsLogger.d(TAG, "save best publish ip: " + bestPublishIp);
                        mPref.putString(StatisticsDB.KEY_WS_BEST_IP_PUBLISH, bestPublishIp);
                    }
                } else {
                    if (playIPList.size() > 0) {
                        String bestPlayIp = getBestIP(playIPList);
                        StatisticsLogger.d(TAG, "save best play ip: " + bestPlayIp);
                        mPref.putString(StatisticsDB.KEY_WS_BEST_IP, bestPlayIp);
                    }
                }
            }
        };
        pingTask.execute(ipList, type);
    }

    private String ping(String ip, int index, int count, int type) {
        String result = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec("ping -c " + count + " -i 0.2 -f " + ip);
            int status = p.waitFor();

            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String line = "";
            while ((line = in.readLine()) != null) {
                StatisticsLogger.d(TAG, "ping out: " + line);

                parsePingOut(ip, line, index, type);
            }

            if (status == 0) {
                result = "success";
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void parsePingOut(String ip, String out, int index, int type) {
        if (out.contains("time=")) {
            //TODO
        } else if (out.contains("min/avg/max/mdev")) {
            int equalIndex = out.indexOf("=");
            int msIndex = out.indexOf("ms");
            out = out.substring(equalIndex, msIndex);
            out = out.replace("=", "");
            out = out.replace(" ", "");

            String timeArray[] = out.split("/");

            if (timeArray.length == 4) {
                int min = Float.valueOf(timeArray[0]).intValue();
                int avg = Float.valueOf(timeArray[1]).intValue();
                int max = Float.valueOf(timeArray[2]).intValue();
                int mdev = Float.valueOf(timeArray[3]).intValue();

                //update
                PingStatusEntity pingStatus = new PingStatusEntity();
                pingStatus.setPinged(true);
                pingStatus.setRttMin(min);
                pingStatus.setRttAvg(avg);
                pingStatus.setRttMax(max);
                pingStatus.setIndex(index);
                pingStatus.setRttMdev(mdev);
                updatePingStatus(ip, pingStatus, type);
            }
        }
    }

    private void updatePingStatus(String ip, PingStatusEntity status, int type) {
        if (type == BEST_IP_TYPE_PUBLISH && publishIPList.size() > 0) {
            if (publishIPList.containsKey(ip)) {
                PingStatusEntity pingStatus = publishIPList.get(ip);
                pingStatus.setIndex(status.getIndex());
                pingStatus.setPinged(status.isPinged());
                pingStatus.setRttMin(status.getRttMin());
                pingStatus.setRttAvg(status.getRttAvg());
                pingStatus.setRttMax(status.getRttMax());
                pingStatus.setRttMdev(status.getRttMdev());
            }
        } else if (type == BEST_IP_TYPE_PLAY && playIPList.size() > 0) {
            if (playIPList.containsKey(ip)) {
                PingStatusEntity pingStatus = playIPList.get(ip);
                pingStatus.setIndex(status.getIndex());
                pingStatus.setPinged(status.isPinged());
                pingStatus.setRttMin(status.getRttMin());
                pingStatus.setRttAvg(status.getRttAvg());
                pingStatus.setRttMax(status.getRttMax());
                pingStatus.setRttMdev(status.getRttMdev());
            }
        }
    }
    /*private static final int INTERVAL_GET_BEST_IP = 10 * 60 * 1000;

    public void checkBestIP(boolean force) {
        String bestIP = mPref.getString(StatisticsDB.KEY_WS_BEST_IP, "");
        String url = mPref.getString(StatisticsDB.KEY_LATEST_URL, "");
        if (TextUtils.isEmpty(url)) {
            url = ApiConstant.WS_FLV_URL;
        }
        long lastGetTime = mPref.getLong(StatisticsDB.KEY_LAST_GET_WS_BEST_IP, 0);
        if (lastGetTime + INTERVAL_GET_BEST_IP < System.currentTimeMillis() || TextUtils.isEmpty(bestIP)
                || force) {
            getWSBestIP(url);
        }
    }

    private void getWSBestIP(String url) {
        VideoSdk.GetBestIps(url, 1, new DetectTaskListener() {
            @Override
            public void onStartTask() {

            }

            @Override
            public void onSuccess(List list) {
                if (list.size() >= 1) {
                    String bestIP = (String) list.get(0);
                    StatisticsLogger.d("GetBestIps", "the best ip: " + bestIP);
                    if (!TextUtils.isEmpty(bestIP)) {
                        mPref.putString(StatisticsDB.KEY_WS_BEST_IP, bestIP);
                        mPref.putLong(StatisticsDB.KEY_LAST_GET_WS_BEST_IP, System.currentTimeMillis());
                        mPref.putBoolean(StatisticsDB.KEY_WS_BEST_IP_VALIDITY, true);
                    }
                }
            }

            @Override
            public void onFailed(int errorCode, String msg) {
                StatisticsLogger.e("GetBestIps", "error code: " + errorCode + ", msg: " + msg);
                mPref.putLong(StatisticsDB.KEY_LAST_GET_WS_BEST_IP, System.currentTimeMillis());
                mPref.putBoolean(StatisticsDB.KEY_WS_BEST_IP_VALIDITY, false);
            }
        });
    }*/
}
