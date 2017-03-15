package com.easyvaas.common.statistics.net;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;

import org.json.JSONObject;

import com.easyvaas.common.statistics.logcollector.LogCollector;
import com.easyvaas.common.statistics.qualitymonitor.QualityStatisticsEntity;
import com.easyvaas.common.statistics.utils.StatisticsDB;
import com.easyvaas.common.statistics.utils.StatisticsUtility;

public class AppStatHelper {
    private static final String TAG = "AppStatHelper";

    private static AppStatHelper mInstance;
    private Context mContext;
    private RequestHelper mRequestHelper;
    private StatisticsDB mPref;

    private DecimalFormat formatPercent = new DecimalFormat("##0.0");

    private AppStatHelper(Context context) {
        mRequestHelper = RequestHelper.getInstance(context);
        mContext = context;
        mPref = StatisticsDB.getInstance(context);
    }

    public static AppStatHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppStatHelper(context);
        }
        return mInstance;
    }

    public void cancelRequest() {
        mRequestHelper.cancelRequest();
    }

    public void reportLiveMsg(String actionType, String vid, String extra, String osVersion, String netType,
            String publishUrl, String frontCameraResolution, String backCameraResolution) {
        Map<String, String> param = new LinkedHashMap<String, String>();
        param.put("from", "android");
        param.put("session", RequestUtility.getSessionId(mContext));
        param.put("name", RequestUtility.getName(mContext));
        param.put("pts", System.currentTimeMillis() + "");
        param.put("module", "live");
        param.put("extra", extra);
        param.put("action", actionType);
        param.put("vid", vid);
        param.put("ostype", "android");
        param.put("osver", osVersion);
        param.put("nettype", netType);
        param.put("puburl", publishUrl);
        param.put("frontcamres", frontCameraResolution);
        param.put("backcamres", backCameraResolution);
        LogCollector.publishEvent(RequestUtility.assembleUrlWithAllParams("", param));
    }

    public void reportPublishStatus(String actionType, String vid, String extra, String status) {
        Map<String, String> param = new LinkedHashMap<String, String>();
        param.put("from", "android");
        param.put("session", RequestUtility.getSessionId(mContext));
        param.put("name", RequestUtility.getName(mContext));
        param.put("pts", System.currentTimeMillis() + "");
        param.put("module", "live");
        param.put("extra", extra);
        param.put("action", actionType);
        param.put("vid", vid);
        param.put("status", status);
        LogCollector.publishEvent(RequestUtility.assembleUrlWithAllParams("", param));
    }

    public void reportLiveParameters(String actionType, String vid, Map<String, String> params,
            String sip, QualityStatisticsEntity qualityStatistics) {
        Map<String, String> param = new LinkedHashMap<String, String>();
        param.put("from", "android");
        param.put("session", RequestUtility.getSessionId(mContext));
        param.put("name", RequestUtility.getName(mContext));
        param.put("pts", System.currentTimeMillis() + "");
        param.put("module", "live");
        param.put("action", actionType);
        param.put("vid", vid);

        params.put("speed", formatPercent.format(Float.parseFloat(params.get("speed"))));
        param.putAll(params);

        param.put("sip", sip);
        param.put("cputotal", formatPercent.format(qualityStatistics.getCpuTotal()));
        param.put("cpucur", formatPercent.format(qualityStatistics.getCpuAM()));
        param.put("memused", formatPercent.format(qualityStatistics.getMemUsed()));
        param.put("memcur", formatPercent.format(qualityStatistics.getMemAM()));
        param.put("memtotal", qualityStatistics.getMemTotal() + "");
        LogCollector.publishHeartbeat(RequestUtility.assembleUrlWithAllParams("", param));
    }

    public void reportLiveInterrupt(String actionType, String vid, String extra, String interruptType) {
        Map<String, String> param = new LinkedHashMap<String, String>();
        param.put("from", "android");
        param.put("session", RequestUtility.getSessionId(mContext));
        param.put("name", RequestUtility.getName(mContext));
        param.put("pts", System.currentTimeMillis() + "");
        param.put("module", "live");
        param.put("extra", extra);
        param.put("action", actionType);
        param.put("vid", vid);
        param.put("type", interruptType);
        LogCollector.publishEvent(RequestUtility.assembleUrlWithAllParams("", param));
    }

    public void reportLiveStop(String actionType, String vid, String extra, String reason) {
        Map<String, String> param = new LinkedHashMap<String, String>();
        param.put("from", "android");
        param.put("session", RequestUtility.getSessionId(mContext));
        param.put("name", RequestUtility.getName(mContext));
        param.put("pts", System.currentTimeMillis() + "");
        param.put("module", "live");
        param.put("extra", extra);
        param.put("action", actionType);
        param.put("vid", vid);
        param.put("reason", reason);
        LogCollector.publishLiveStop(RequestUtility.assembleUrlWithAllParams("", param));
    }

    public void postAppLog(JSONObject log, boolean bGzip, MyRequestCallBack<String> callBack) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("nonce", StatisticsUtility.getRandomString(16));
        param.put("signature", RequestUtility.getAppSignature(param, ApiConstant.APP_STAT_HMAC_KEY));

        if (bGzip) {
            mRequestHelper.postAsGzip(ApiConstant.APP_STAT_HOST, log, param, callBack);
        } else {
            mRequestHelper.postAsString(ApiConstant.APP_STAT_HOST, log, param, callBack);
        }
    }

    public void getWSBestIP(String reqUrl, Map<String, String> params, MyRequestCallBack<String> callBack) {
        mRequestHelper.getAsString(reqUrl, params, callBack);
    }
}
