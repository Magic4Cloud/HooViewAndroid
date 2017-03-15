package com.easyvaas.common.chat.net;

public class ApiConstant {
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_FIRST_PAGE_INDEX = 0;

    public static String APP_STAT_HOST = "http://dev.yizhibo.tv/easyvaas/appgw/";

    static final String KEY_RET_VAL = "retval";
    static final String KEY_RET_VAL_SHORT = "rv";
    static final String KEY_RET_ERR = "reterr";
    static final String KEY_RET_ERR_SHORT = "re";
    static final String KEY_RET_INFO = "retinfo";
    static final String KEY_RET_INFO_SHORT = "ri";
    static final String E_PARSE_GSON = "E_PARSE_GSON";

    static final String USER_FRIEND_LIST = APP_STAT_HOST + "userfriendlist?";
    static final String USER_BASE_INFO = APP_STAT_HOST + "user/infos?";
}

