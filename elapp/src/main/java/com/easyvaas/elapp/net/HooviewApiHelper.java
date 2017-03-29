/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.SplashEntity;
import com.easyvaas.elapp.bean.imageTextLive.CheckImageTextLiveModel;
import com.easyvaas.elapp.bean.imageTextLive.ImageTextLiveHistoryModel;
import com.easyvaas.elapp.bean.market.ExponentListModel;
import com.easyvaas.elapp.bean.market.ExponentListNewModel;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.bean.market.UpsAndDownsDataModel;
import com.easyvaas.elapp.bean.message.UnReadMessageEntity;
import com.easyvaas.elapp.bean.news.ImportantNewsModel;
import com.easyvaas.elapp.bean.news.LastestNewsModel;
import com.easyvaas.elapp.bean.news.MyStockNewsModel;
import com.easyvaas.elapp.bean.news.NewsListModel;
import com.easyvaas.elapp.bean.search.SearchLiveModel;
import com.easyvaas.elapp.bean.search.SearchNewsModel;
import com.easyvaas.elapp.bean.search.SearchStockModel;
import com.easyvaas.elapp.bean.video.GoodsVideoListModel;
import com.easyvaas.elapp.bean.video.LiveCommentModel;
import com.easyvaas.elapp.bean.video.RecommendVideoListModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.db.Preferences;
import com.hyphenate.chat.EMMessage;

import java.util.HashMap;
import java.util.Map;

public class HooviewApiHelper {
    private static final String TAG = "HooviewApiHelper";
    private static HooviewApiHelper mInstance;
    private static RequestHelper sRequestHelper;
    private Preferences mPref;
    private final Context mContext;

    private HooviewApiHelper() {
        mContext = EVApplication.getApp();
        sRequestHelper = RequestHelper.getInstance(mContext);
        mPref = Preferences.getInstance(mContext);
    }

    public static HooviewApiHelper getInstance() {
        if (mInstance == null) {
            mInstance = new HooviewApiHelper();
        }
        return mInstance;
    }

    public void cancelRequest() {
        sRequestHelper.cancelRequest();
    }

    public void getUpAndDownList(MyRequestCallBack<UpsAndDownsDataModel> myRequestCallBack) {
        sRequestHelper.getAsGson(HooviewApiConstant.UP_AND_DOWN_LIST, null, UpsAndDownsDataModel.class, myRequestCallBack);
    }
    public void getUpAndDownListHK(MyRequestCallBack<UpsAndDownsDataModel> myRequestCallBack) {
        sRequestHelper.getAsGson(HooviewApiConstant.UP_AND_DOWN_LIST_HK, null, UpsAndDownsDataModel.class, myRequestCallBack);
    }

    public void getGlobalStockList(MyRequestCallBack<ExponentListModel> myRequestCallBack) {
        sRequestHelper.getAsGson(HooviewApiConstant.GET_EXPONENT_GLOBAL_STOCK_LIST, null, ExponentListModel.class, myRequestCallBack);
    }

    public void getExponentListNew(MyRequestCallBack<ExponentListNewModel> myRequestCallBack) {
        sRequestHelper.getAsGson(HooviewApiConstant.GET_EXPONENT_LIST, null, ExponentListNewModel.class, myRequestCallBack);
    }

    public void getSelectStockList(String listStr, MyRequestCallBack<StockListModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("symbol", listStr);
        sRequestHelper.getAsGson(HooviewApiConstant.GET_EXPONENT_SELECT_STOCK_LIST, map, StockListModel.class, callBack);
    }

    public void postComment(String code, String comment, boolean postStock,
                            MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        String codeKey = postStock ? "stockcode" : "newsid";
        map.put(codeKey, code);
        map.put("content", comment);
        map.put("userid", mPref.getUserNumber());
        map.put("username", mPref.getUserNickname());
        map.put("useravatar", EVApplication.getUser().getLogourl());
        String api = postStock ? HooviewApiConstant.BBS_STOCK_POST : HooviewApiConstant.BBS_NEWS_POST;
        sRequestHelper.postAsString(api, map, callBack);
    }

    public void getImportantNewsList(String start, String count, MyRequestCallBack<ImportantNewsModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start);
        map.put("count", count);
        sRequestHelper.getAsGson(HooviewApiConstant.GET_IMPORTANT_NEWS_LIST, map, ImportantNewsModel.class, callBack);
    }

    public void getUnReadMessageCount(MyRequestCallBack<UnReadMessageEntity> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("sessionid", mPref.getSessionId());
        sRequestHelper.getAsGson(ApiConstant.MESSAGE_UNREAD_COUNT, map, UnReadMessageEntity.class, callBack);
    }


    public void getMyStockNewsList(String symbol, String start, String count, MyRequestCallBack<MyStockNewsModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start);
        map.put("count", count);
        map.put("symbol", symbol);
        sRequestHelper.getAsGson(HooviewApiConstant.GET_MY_STOCK_NEWS, map, MyStockNewsModel.class, callBack);
    }

    public void getLastestNewsList(String start, String count, MyRequestCallBack<LastestNewsModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start);
        map.put("count", count);
        sRequestHelper.getAsGson(HooviewApiConstant.GET_FASTER_NEWS_LIST, map, LastestNewsModel.class, callBack);
    }

    public void searchNews(String title, String start, String count, MyRequestCallBack<SearchNewsModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("start", start);
        map.put("count", count);
        sRequestHelper.getAsGson(HooviewApiConstant.SEARCH_NEWS, map, SearchNewsModel.class, callBack);
    }

    public void searchStock(String title,String userid ,String start, String count, MyRequestCallBack<SearchStockModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start);
        map.put("count", count);
        map.put("name", title);
        map.put("userid",userid);
        sRequestHelper.getAsGson(HooviewApiConstant.SEARCH_STOCK, map, SearchStockModel.class, callBack);
    }

    public void searchLive(String keyWord, String start, String count, MyRequestCallBack<SearchLiveModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start);
        map.put("count", count);
        map.put("keyword", keyWord);
        sRequestHelper.getAsGson(ApiConstant.VIDEO_SEARCH, map, SearchLiveModel.class, callBack);
    }

    public void getNewsListByChannelId(String channelId, String programId, int start, int count, MyRequestCallBack<NewsListModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start + "");
        map.put("count", count + "");
        map.put("programid", channelId + "");
        map.put("channelid", programId + "");
        sRequestHelper.getAsGson(HooviewApiConstant.GET_NEWS_LIST, map, NewsListModel.class, callBack);
    }

    public void getRecommendVideoList(String count, String start, MyRequestCallBack<RecommendVideoListModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start + "");
        map.put("count", count + "");
        sRequestHelper.getAsGson(HooviewApiConstant.RECOMMEND_VIDEO_LIST, map, RecommendVideoListModel.class, callBack);
    }

    public void getGoodVideoList(String count, String start, MyRequestCallBack<GoodsVideoListModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start + "");
        map.put("count", count + "");
        sRequestHelper.getAsGson(HooviewApiConstant.GOODS_VIDEO_LIST, map, GoodsVideoListModel.class, callBack);

    }

    /**
     * @param orderby  dateline、heats
     * @param count
     * @param start
     * @param callBack
     */
    public void getGoodVideoCommentList(String vid, String orderby, String count, String start, MyRequestCallBack<LiveCommentModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start + "");
        map.put("count", count + "");
        map.put("orderby", orderby);
        map.put("vid", vid);
        sRequestHelper.getAsGson(HooviewApiConstant.GOODS_VIDEO_COMMENT_LIST, map, LiveCommentModel.class, callBack);
    }

    //    vid true string 视频vid
//    userid true string 用户ID
//    username true string 用户昵称
//    useravatar true string 用户头像
//    content true string 评论内容
    public void sendVideoComment(String vid, String userNumber, String username, String useravater, String content, MyRequestCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("vid", vid);
        map.put("userid", userNumber);
        map.put("username", username);
        map.put("useravatar", useravater);
        map.put("content", content);
        sRequestHelper.postAsString(HooviewApiConstant.SEND_VIDEO_COMMENT, map, callBack);
    }

    public void getTextLiveList(int count, int start, MyRequestCallBack<TextLiveListModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start + "");
        map.put("count", count + "");
        sRequestHelper.getAsGson(HooviewApiConstant.TEXT_LIVE_LIST, map, TextLiveListModel.class, callBack);
    }

    public void checkImageTextLiveRoom(String userId, String nickname, String easemobid, MyRequestCallBack<CheckImageTextLiveModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("userid", userId);
        map.put("nickname", nickname);
        map.put("easemobid", easemobid);
        sRequestHelper.getAsGson(HooviewApiConstant.CHECK_IMAGE_TEXT_LIVE_ROOM, map, CheckImageTextLiveModel.class, callBack);
    }

    /**
     * to true string 聊天室ID
     * from true string 发消息人环信ID
     * nk true string 发消息人名字
     * msgid true string 聊天记录ID
     * msgtype true string 消息类型（txt，文本消息；img，图片）
     * msg true string 聊天内容（若为图片，则为空）
     * tp true string 内容类型（st，置顶消息；hl，高亮消息；nor，普通消息）
     * rct false string 回复内容
     * rnk false string 回复人名字
     * timestamp true string 聊天时间（Unix时间戳）
     * img
     *
     * @param callBack
     */
    public void uploadChatMessage(EMMessageWrapper emMessageWrapper, MyRequestCallBack callBack) {
        EMMessage emMessage = emMessageWrapper.emaMessage;
        Map<String, String> map = new HashMap<>();
        map.put("to", TextUtils.isEmpty(emMessage.getTo()) ? "" : emMessage.getTo());
        map.put("from", TextUtils.isEmpty(emMessage.getFrom()) ? "" : emMessage.getFrom());
        map.put("nk", TextUtils.isEmpty(emMessageWrapper.nickname) ? "" : emMessageWrapper.nickname);
        map.put("msgid", TextUtils.isEmpty(emMessage.getMsgId()) ? "" : emMessage.getMsgId());
//        map.put("msgtype", TextUtils.isEmpty(emMessage.getType().toString()) ? "" : emMessage.getType().toString());
        map.put("msgtype","txt");
        map.put("msg", TextUtils.isEmpty(emMessageWrapper.content) ? "" : emMessageWrapper.content);
        map.put("tp", TextUtils.isEmpty(emMessageWrapper.type) ? "" : emMessageWrapper.type);
        map.put("rct", TextUtils.isEmpty(emMessageWrapper.replyContent) ? "" : emMessageWrapper.replyContent);
        map.put("rnk", TextUtils.isEmpty(emMessageWrapper.replyNickname) ? "" : emMessageWrapper.replyNickname);
        map.put("timestamp", emMessage.getMsgTime()+ "");
        map.put("img", TextUtils.isEmpty(emMessageWrapper.imageUrl) ? "" : emMessageWrapper.imageUrl);
        Log.d("Misuzu",emMessageWrapper.toString());
        sRequestHelper.postAsString(HooviewApiConstant.UPLOAD_CHAT_MESSAGE, map, callBack);
    }

    public void getImageTextLiveHistory(String roomId,int start, String count, long stime, MyRequestCallBack<ImageTextLiveHistoryModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("streamid", roomId);
        map.put("count", count);
        map.put("stime", stime + "");
        map.put("start", start + "");
        sRequestHelper.getAsGson(HooviewApiConstant.GET_CHAT_HISTORY, map, ImageTextLiveHistoryModel.class, callBack);
    }

    public void getBannerInfo(MyRequestCallBack<BannerModel> callBack) {
        Map<String, String> map = new HashMap<>();
        sRequestHelper.getAsGson(HooviewApiConstant.GET_BANNERS, map, BannerModel.class, callBack);
    }

    public void getSplashInfo(MyRequestCallBack<SplashEntity> callBack) {
        Map<String, String> map = new HashMap<>();
        sRequestHelper.getAsGson(HooviewApiConstant.AD_SPLASH, map, SplashEntity.class, callBack);
    }

    /**
     * 得到自选股的列表
     * @param userid
     * @param callBack
     */
    public void getUserStockList(String userid, MyRequestCallBack<StockListModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put(ApiConstant.USER_ID, userid);
        sRequestHelper.getAsGson(HooviewApiConstant.GET_USER_STOCKS, map, StockListModel.class, callBack);
    }

    /**
     * 更新自选股 顺序 数量
     * @param userId 用户ID
     * @param symbols  股票代码
     * @param type  0 更新 1 添加 2 删除
     */
    public void updateStocks(String userId,String symbols,String type,MyRequestCallBack callBack)
    {
        Map<String,String> map = new HashMap<>();
        map.put("userid",userId);
        map.put("symbol",symbols);
        map.put("type",type);
        sRequestHelper.getAsString(HooviewApiConstant.UPDATE_STOCKS,map,callBack);
    }

    /**
     * 获取自选股新闻
     * @param userid 用户ID
     */
    public void getUserStockNewsList(String userid, String start, String count, MyRequestCallBack<MyStockNewsModel> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("start", start);
        map.put("count", count);
        map.put("userid", userid);
        sRequestHelper.getAsGson(HooviewApiConstant.GET_USER_STOCK_NEWS, map, MyStockNewsModel.class, callBack);
    }

    /**
     * 是否添加过该自选股
     * @param userid 用户ID
     */
    public void isStockAdded(String userid, String symbol, MyRequestCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("symbol", symbol);
        map.put("userid", userid);
        sRequestHelper.getAsString(HooviewApiConstant.IS_STOCK_ADDED, map,callBack);
    }

}
