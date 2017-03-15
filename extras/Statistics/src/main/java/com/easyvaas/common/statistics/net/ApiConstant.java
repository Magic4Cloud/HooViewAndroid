package com.easyvaas.common.statistics.net;

import java.io.File;

import android.os.Environment;

import com.easyvaas.common.statistics.BuildConfig;

class ApiConstant {
    public static final String E_PARSE_GSON = "E_PARSE_GSON";
    public static final String APP_STAT_HMAC_KEY = "yizhibo.log";
    public static final String STATISTICS_HOST = "http://log.yizhibo.tv/yizhibo?";
    public static final String APP_STAT_HOST_RELEASE = "http://log.yizhibo.tv/app?";
    public static final String APP_STAT_HOST_DEBUG = "http://123.57.240.208:18888/test?";
    public static String APP_STAT_HOST = APP_STAT_HOST_DEBUG;

    static {
        if (BuildConfig.DEBUG) {
            APP_STAT_HOST = APP_STAT_HOST_DEBUG;
        } else {
            APP_STAT_HOST = APP_STAT_HOST_RELEASE;
        }
    }

    public static final String KEY_RET_VAL = "retval";
    public static final String KEY_RET_VAL_SHORT = "rv";
    public static final String KEY_RET_ERR = "reterr";
    public static final String KEY_RET_ERR_SHORT = "re";
    public static final String KEY_RET_INFO = "retinfo";
    public static final String KEY_RET_INFO_SHORT = "ri";

    public static final String CACHE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath() + File.separator + "elapp";
    public static final String CACHE_DOWNLOAD_DIR = CACHE_DIR + File.separator + "download";

    public static final String WS_FLV_URL = "http://wsflv.yizhibo.tv/live/0pMJumJx8tqbn.flv";
}

