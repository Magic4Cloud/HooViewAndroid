package com.easyvaas.common.chat.net;

import java.io.File;

import android.os.Environment;

import com.easyvaas.common.chat.BuildConfig;

public class ApiConstant {
    public static final String CACHE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath() + File.separator + "yizhibo";
    public static final String CACHE_DOWNLOAD_DIR = CACHE_DIR + File.separator + "download";

    public static final int DEFAULT_PAGE_SIZE_ALL = 10000;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_FIRST_PAGE_INDEX = 0;
    public static final String E_PARSE_GSON = "E_PARSE_GSON";
    private static final String DEBUG_HOST = "http://appgw.hooview.com/easyvaas/appgw/"; //TODO change to you appgw
    public static String APP_STAT_HOST = DEBUG_HOST;

    public static final String KEY_RET_VAL = "retval";
    public static final String KEY_RET_VAL_SHORT = "rv";
    public static final String KEY_RET_ERR = "reterr";
    public static final String KEY_RET_ERR_SHORT = "re";
    public static final String KEY_RET_INFO = "retinfo";
    public static final String KEY_RET_INFO_SHORT = "ri";

    public static final String USER_FRIEND_LIST = APP_STAT_HOST + "userfriendlist?";
    public static final String USER_BASE_INFO = APP_STAT_HOST + "user/infos?";
    public static final String USER_BASIC_INFO_LIST = APP_STAT_HOST + "user/infos?";
}

