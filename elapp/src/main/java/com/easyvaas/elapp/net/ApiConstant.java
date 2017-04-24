/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.net;

import android.content.Context;

import com.hooview.app.BuildConfig;
import com.hooview.app.R;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.db.Preferences;

public class ApiConstant {
    private static final String RELEASE_HOST = "http://appgw.hooview.com/easyvaas/appgw/";
    private static final String RELEASE_WEB_APP_HOST = "http://appgw.hooview.com/easyvaas/webapp2";
    public static final String DEBUG_HOST = "http://appgwdev.hooview.com/easyvaas/appgw/";
    private static final String DEBUG_WEB_APP_HOST = "http://appgwdev.hooview.com/easyvaas/webapp2";
//    private static final String DEBUG_HOST = RELEASE_HOST;
//    private static final String DEBUG_WEB_APP_HOST = RELEASE_WEB_APP_HOST;

    public static final String MOCK_HOST = "http://192.168.8.191:8888";

    private static final boolean IS_SUPPORT_SSL = false;

    private static String HOST = DEBUG_HOST;
    private static String HOSTS;
    private static String HOST_WEBAPP = DEBUG_WEB_APP_HOST;
    private static String PAY_HOSTS;

    static {
        if (BuildConfig.FLAVOR.equals("dev")) {
            updateServerHost();
        } else if (BuildConfig.BUILD_TYPE.equals("release") || BuildConfig.BUILD_TYPE.equals("rel")) {
            HOST = RELEASE_HOST;
            HOST_WEBAPP = RELEASE_WEB_APP_HOST;
        }
        if (IS_SUPPORT_SSL) {
            HOSTS = HOST.replace("http", "https");
            PAY_HOSTS = HOST.replace("http", "https");
        } else {
            HOSTS = HOST;
            PAY_HOSTS = HOST;
        }
//        ChatManager.getInstance().setAppGwHost(HOST);
    }

    public static void updateServerHost() {
        Context context = EVApplication.getApp();
        String server = Preferences.getInstance(context).getString(Preferences.KEY_SERVER_TYPE);
        if (context.getString(R.string.server_release).equals(server)) {
            HOST = RELEASE_HOST;
            HOST_WEBAPP = RELEASE_WEB_APP_HOST;
        } else {
            HOST = DEBUG_HOST;
            HOST_WEBAPP = DEBUG_WEB_APP_HOST;
        }
    }

    static String getAppGWHost() {
        return HOST;
    }

    static String getWebAppHost() {
        return HOST_WEBAPP;
    }

    public static boolean isUserReleaseServer() {
        return HOST.equals(RELEASE_HOST);
    }

    //================== EasyVass API 1.0.0 ===================================
    // User
    public static final String USER_REGISTER = HOSTS + "user/register?";
    public static final String USER_LOGIN = HOSTS + "user/login?";
    public static final String USER_LOGOUT = HOSTS + "user/logout?";
    public static final String USER_AUTH_BIND = HOSTS + "user/bind?";
    public static final String USER_AUTH_UNBIND = HOSTS + "user/unbind?";
    public static final String USER_RESET_PASSWORD = HOSTS + "user/resetpwd?";
    public static final String USER_INFO = HOST + "user/info?";
    public static final String USER_BASE_INFO = HOST + "user/baseinfo?";
    public static final String USER_INFOS = HOST + "user/infos?";
    public static final String USER_EDIT_INFO = HOST + "user/edit?";
    public static final String USER_SETTING_INFO = HOST + "user/getparam?";
    public static final String USER_SETTING_EDIT = HOST + "user/setparam?";
    public static final String USER_FOLLOW = HOST + "user/follow?";
    public static final String USER_FOLLOWER_LIST = HOST + "user/followerlist?";
    public static final String USER_FANS_LIST = HOST + "user/fanslist?";
    public static final String USER_VIDEO_LIST = HOST + "user/videolist?";
    public static final String USER_UPLOAD_LOGO = HOST + "user/userlogo?";
    public static final String USER_SESSION_CHECK = HOST + "user/sessioncheck?";
    public static final String SEARCH_INFO = HOST + "user/search?";
    public static final String COLLECT = HOST + "user/collect?";
    public static final String COLLECTLIST = HOST + "user/collectlist?";
    public static final String COLLECT_TYPE_NEWS = "1";
    public static final String COLLECT_TYPE_VIDEO = "2";
    public static final String COLLECT_TYPE_EXPONENT = "3";
    public static final String COLLECT_TYPE_STOCK = "4";
    public static final String COLLECT_ACTION_ADD = "1";
    public static final String COLLECT_ACTION_DELETE = "2";
    public static final String HISTORY=HOST+"user/historylist?";

    // Video
    public static final String HOT_LIVE_LIST = HOST + "video/hotlist?";
    public static final String LIVE_START = HOST + "video/start?";
    public static final String LIVE_STOP = HOST + "video/stop?";
    public static final String VIDEO_INFO = HOST + "video/info?";
    public static final String VIDEO_INFOS = HOST + "video/infos?";
    public static final String WATCH_START = HOST + "video/watch?";
    public static final String VIDEO_SET_TITLE = HOST + "video/title?";
    public static final String VIDEO_FRIEND = HOST + "video/friend?";
    public static final String VIDEO_REMOVE = HOSTS + "video/remove?";
    // SMS
    public static final String SMS_SEND = HOST + "sms/send?";
    public static final String SMS_VERIFY = HOST + "sms/verify?";
    // System
    public static final String DEVICE_ONLINE = HOST + "sys/device?";
    public static final String SYS_SETTINGS = HOST + "sys/settings?";
    public static final String CHECK_UPDATE = HOST + "sys/appupdate?";
    public static final String SYS_TAG_LIST = HOST + "sys/taglist?";
    //================== EasyVass API 1.0.0 End ===============================
    // User
    public static final String USER_INFORM = HOST + "user/userinform?";
    public static final String USER_AUTH_PHONE_CHANGE = HOSTS + "user/authphonechange?";
    public static final String USER_UPDATE_PASSWORD = HOSTS + "user/modifypassword?";
    public static final String USER_FRIENDS = HOSTS + "user/friend?";
    public static final String USER_TAG_SET = HOSTS + "user/tagset?";
    // Video
    public static final String UPLOAD_VIDEO_LOGO = HOST + "video/videologo?";
    public static final String VIDEO_PAY = HOST + "video/livepay?";
    public static final String VIDEO_TOPIC_LIST = HOST + "video/topiclist?";
    public static final String VIDEO_TOPIC_VIDEO_LIST = HOST + "video/topicvideo?";
    public static final String VIDEO_SET_TOPIC = HOST + "video/settopicvideo?";
    public static final String INTERACT_SHUT_UP = HOST + "interact/shutup?";
    public static final String INTERACT_SET_MANAGER = HOST + "interact/setmanager?";
    public static final String INTERACT_KICK_USER = HOST + "interact/kickuser?";
    public static final String VIDEO_CAROUSEL_INFO = HOST + "video/carouselinfo?";
    public static final String VIDEO_CALL_REQUEST = HOST + "video/callrequest?";
    public static final String VIDEO_CALL_ACCEPT = HOST + "video/callaccept?";
    public static final String VIDEO_CALL_END = HOST + "video/callend?";
    public static final String VIDEO_CALL_CANCEL = HOST + "video/callcancel?";
    public static final String VIDEO_SEARCH = HOST + "video/search?";

    // Message
    public static final String MESSAGE_GROUP_LIST = HOST + "msg/messagegrouplist?";
    public static final String MESSAGE_ITEM_LIST = HOST + "msg/messageitemlist?";
    public static final String MESSAGE_UNREAD_COUNT = HOST + "msg/messageunreadcount?";
    // Pay
    public static final String PAY_RECHARGE = PAY_HOSTS + "pay/recharge?";
    public static final String PAY_ASSET = PAY_HOSTS + "pay/getuserasset?";
    public static final String PAY_CASH_IN_OPTION = PAY_HOSTS + "pay/cashinoption?";
    public static final String PAY_CASH_OUT = PAY_HOSTS + "pay/cashout?";
    public static final String PAY_CASH_OUT_RECORD = PAY_HOSTS + "pay/cashoutrecord?";
    public static final String PAY_RECHARGE_RECORD = PAY_HOSTS + "pay/rechargerecord?";
    public static final String PAY_GIFT_LIST = PAY_HOSTS + "pay/goods?";
    public static final String PAY_SEND_GIFT = PAY_HOSTS + "pay/sendgift?";
    public static final String PAY_SEND_RED_PACK = PAY_HOSTS + "pay/sendredpack?";
    public static final String PAY_OPEN_RED_PACK = PAY_HOSTS + "pay/openredpack?";
    public static final String PAY_CONTRIBUTOR_LIST = PAY_HOSTS + "pay/getcontributor?";
    public static final String PAY_ASSETS_RANK_LIST = PAY_HOSTS + "pay/assetsranklist?";
    //================== EasyVass API 1.0.5 End ===============================

    // Extra uri which user by h5
    public static final String EXTRA_URI_SHARE = "elapp://share?";
    public static final String EXTRA_URI_VIDEO = "elapp://video?";

    //================================= PARAM KEY ===========================//
    public static final String KEY_RET_VAL = "retval";
    public static final String KEY_RET_VAL_SHORT = "rv";
    public static final String KEY_RET_ERR = "reterr";
    public static final String KEY_RET_ERR_SHORT = "re";
    public static final String KEY_RET_INFO = "retinfo";
    public static final String KEY_RET_INFO_SHORT = "ri";
    public static final String KEY_SMS_ID = "sms_id";
    public static final String KEY_REGISTERED = "registered";
    public static final String KEY_CONFLICTED = "conflicted";
    public static final String KEY_TOTAL_AMOUNT = "total";

    public static final String E_SERVER = "E_SERVER";
    public static final String E_SESSION = "E_SESSION";
    public static final String E_PARAM = "E_PARAM";
    public static final String E_AUTH = "E_AUTH";
    public static final String E_AUTH_MERGE_CONFLICTS = "E_AUTH_MERGE_CONFLICTS";
    public static final String E_USER_NOT_EXISTS = "E_USER_NOT_EXISTS";
    public static final String E_USER_EXISTS = "E_USER_EXISTS";
    public static final String E_USER_PHONE_NOT_EXISTS = "E_USER_PHONE_NOT_EXISTS";
    public static final String E_VIDEO_NOT_EXISTS = "E_VIDEO_NOT_EXISTS";
    public static final String E_SMS_SERVICE = "E_SMS_SERVICE";
    public static final String E_SMS_INTERVAL = "E_SMS_INTERVAL";
    public static final String E_PARSE_GSON = "E_PARSE_GSON";
    public static final String E_PAYMENT_NO = "E_PAYMENT_NO";

    public static final String VALUE_COUNTRY_CODE_CHINA = "86_";
    public static String VALUE_COUNTRY_CODE = "86_";

    public static final int VALUE_PHONE_HAVE_REGISTERED = 1;
    public static final int VALUE_PHONE_NOT_REGISTERED = 0;
    public static final int VALUE_SNS_HAVE_BIND = 1;
    public static final int VALUE_SNS_NOT_BIND = 0;

    public static final int VALUE_LIVE_PERMISSION_PUBLIC = 0;
    public static final int VALUE_LIVE_PERMISSION_PRIVATE = 2;
    public static final int VALUE_LIVE_PERMISSION_PASSWORD = 6;
    public static final int VALUE_LIVE_PERMISSION_PAY = 7;

    public static final String VALUE_ANCHOR_LIST_TYPE_ALL = "all";

    public static final String VALUE_SEARCH_TYPE_ALL = "all";
    public static final String VALUE_SEARCH_TYPE_USER = "user";
    public static final String VALUE_SEARCH_TYPE_LIVE = "live";
    public static final String VALUE_SEARCH_TYPE_VIDEO = "video";

    //================================= HTTP PARAM ==========================//
    public static final int DEFAULT_PAGE_SIZE_ALL = 10000;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_FIRST_PAGE_INDEX = 0;

    //================================= PARAM API ===========================//
    public static final String USER_ID = "userid";

}
