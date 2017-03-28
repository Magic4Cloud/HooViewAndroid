package com.easyvaas.elapp.net;

public class HooviewApiConstant {
    public static final String HOST_WEB_APP;
    private static final String HOST_EV_APPGW;

    //private final static String HOST_HOOVIEW = BuildConfig.DEBUG?"http://dev.hooview.com":"http://dev.hooview.com";
//    private final static String HOST_HOOVIEW = "http://openapi.hooview.com";
    private static final String HOST_HOOVIEW = "http://dev.hooview.com";

    static {
        HOST_EV_APPGW = ApiConstant.getAppGWHost();
        HOST_WEB_APP = ApiConstant.getWebAppHost();
    }

    public static final String RECOMMEND_VIDEO_LIST = HOST_EV_APPGW + "video/recommendlist?";
    public static final String GOODS_VIDEO_LIST = HOST_EV_APPGW + "video/vodlist?";

    public static final String UP_AND_DOWN_LIST = HOST_HOOVIEW + "/api/stock/changelist";
    public static final String UP_AND_DOWN_LIST_HK = HOST_HOOVIEW + "/api/stock/changelist?market=hk";
    public static final String GET_EXPONENT_LIST = HOST_HOOVIEW + "/api/stock/market";
    public static final String GET_EXPONENT_GLOBAL_STOCK_LIST = HOST_HOOVIEW + "/api/stock/globalindex";
    public static final String GET_EXPONENT_SELECT_STOCK_LIST = HOST_HOOVIEW + "/api/stock/realtime?";

    public static final String GET_IMPORTANT_NEWS_LIST = HOST_HOOVIEW + "/api/news/gethomeNews?";
    public static final String GET_FASTER_NEWS_LIST = HOST_HOOVIEW + "/api/news/getnewsflash?";
    public static final String GET_MY_STOCK_NEWS = HOST_HOOVIEW + "/api/news/customnews?";
    public static final String GET_NEWS_LIST = HOST_HOOVIEW + "/api/news/getlist?";
    public static final String SEARCH_NEWS = HOST_HOOVIEW + "/api/search/news?";
    public static final String SEARCH_STOCK = HOST_HOOVIEW + "/api/search/stock?";
    public static final String GOODS_VIDEO_COMMENT_LIST = HOST_HOOVIEW + "/api/bbs/videoconversatons?";
    public static final String SEND_VIDEO_COMMENT = HOST_HOOVIEW + "/api/bbs/videopost";
    public static final String BBS_STOCK_POST = HOST_HOOVIEW + "/api/bbs/stockpost";
    public static final String BBS_NEWS_POST = HOST_HOOVIEW + "/api/bbs/newspost";
    public static final String TEXT_LIVE_LIST = HOST_HOOVIEW + "/api/textlive/home?";
    public static final String CHECK_IMAGE_TEXT_LIVE_ROOM = HOST_HOOVIEW + "/api/textlive/owner?";
    public static final String UPLOAD_CHAT_MESSAGE = HOST_HOOVIEW + "/api/textlive/chat?";
    public static final String GET_CHAT_HISTORY = HOST_HOOVIEW + "/api/textlive/history?";
    public static final String GET_USER_STOCKS = HOST_HOOVIEW + "/api/user/stocks?";    // 自选股列表
    public static final String UPDATE_STOCKS = HOST_HOOVIEW+"/api/user/modifystocks"; // 修改上传自选股

    public static final String AD_SPLASH = "http://dev.hooview.com" + "/api/ad/appstart";
    public static final String GET_BANNERS = HOST_HOOVIEW + "/api/news/banners?";

}
