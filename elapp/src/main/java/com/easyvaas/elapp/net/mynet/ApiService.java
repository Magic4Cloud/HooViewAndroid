package com.easyvaas.elapp.net.mynet;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.UpdateInfoEntity;
import com.easyvaas.elapp.bean.market.MarketExponentModel;
import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.bean.news.NewsCollectStatus;
import com.easyvaas.elapp.bean.news.NewsColumnModel;
import com.easyvaas.elapp.bean.news.NewsDetailModel;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.StockMarketNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopicModel;
import com.easyvaas.elapp.bean.user.CheatsListModel;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.user.UserFollowModel;
import com.easyvaas.elapp.bean.user.UserHistoryTestModel;
import com.easyvaas.elapp.bean.user.UserPageCommentModel;
import com.easyvaas.elapp.bean.user.UserPageInfo;
import com.easyvaas.elapp.bean.user.UserPublishVideoModel;
import com.easyvaas.elapp.bean.video.GoodsVideoListModel;
import com.easyvaas.elapp.bean.video.RecommendVideoListModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.bean.video.VideoCommentModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.hooview.app.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Date   2017/3/28
 * Editor  Misuzu
 * API 接口类
 */

public interface ApiService {


    /*-------------------------------------------资讯----------------------------------------*/

    /**
     * 首页要闻
     */
    @GET("api/v3/news/home")
    Observable<NetResponse<TopRatedModel>> getTopRatedNews(@Query(AppConstants.START) int start);
    /**
     * banner新闻
     */
    @GET("api/news/banners")
    Observable<NetResponse<BannerModel>> getBannerNews();
    /**
     * 专题列表
     */
    @GET("api/v2/news/topic")
    Observable<NetResponse<TopicModel>> getTopicList(@Query("id") String id,@Query(AppConstants.START) int start);

    /**
     * 资讯专栏
     */
    @GET("api/v2/news/column")
    Observable<NetResponse<NewsColumnModel>> getNewsColumn(@Query(AppConstants.START) int start);

    /**
     * 获取新闻详情内容
     */
    @GET("api/news/getnews")
    Observable<NetResponse<NewsDetailModel>> getNewsDetail(@Query(AppConstants.NEWS_ID) String newsId);

    /**
     * 发布评论
     * @param type 话题类型（0，新闻；1，视频；2，股票）
     */
    @GET("api/v2/posts/post")
    Observable<NetResponse<NoResponeBackModel>> sendCommentByType(@Query(AppConstants.TOPIC_ID) String topicid,
                                                                  @Query(AppConstants.USER_ID) String userid,
                                                                  @Query(AppConstants.CONTENT) String content,
                                                                  @Query(AppConstants.TYPE) int type);

    /*-------------------------------------------市场----------------------------------------*/

    /**
     * 行情全球指数
     */
    @GET("api/v2/stock/globalindex")
    Observable<NetResponse<List<MarketGlobalModel>>> getMarketGlobalStock();

    /**
     * 行情市场指数
     */
    @GET("/api/stock/market")
    Observable<NetResponse<MarketExponentModel>> getMarketExponent();

    /**
     * 股市资讯列表
     */
    @GET("api/v2/news/news")
    Observable<NetResponse<StockMarketNewsModel>> getStockMarketNewsList(@Query(AppConstants.CHANNEL_ID) String channelId,
                                                                         @Query(AppConstants.START) int start);

    /*-------------------------------------------用户中心----------------------------------------*/

    /**
     * 获取用户信息
     */
    @GET("api/v2/user/info")
    Observable<NetResponse<User>> getUserInfo(@Query(AppConstants.NAME) String id,
                                              @Query(AppConstants.SESSION_ID) String sessionid);

    /**
     * 获取用户信息V2
     */
    @GET("api/v2/user/info")
    Observable<NetResponse<User>> getUserInfoNew(@Query(AppConstants.USER_ID) String id,
                                              @Query(AppConstants.SESSION_ID) String sessionid);

    /**
     * 获取用户粉丝列表
     */
    @GET(ApiConstant.DEBUG_HOST+"user/fanslist")
    Observable<NetResponse<UserFollowModel>> getUserFans(@Query(AppConstants.NAME) String id,
                                                         @Query(AppConstants.SESSION_ID) String sessionid,
                                                         @Query(AppConstants.START) int start);

    /**
     * 获取用户关注列表
     */
    @GET(ApiConstant.DEBUG_HOST+"user/followerlist")
    Observable<NetResponse<UserFollowModel>> getUserFocus(@Query(AppConstants.NAME) String id,
                                                          @Query(AppConstants.SESSION_ID) String sessionid,
                                                          @Query(AppConstants.START) int start);
    /**
     * 关注用户 0 取消关注 1 关注
     */
    @GET(ApiConstant.DEBUG_HOST+"user/follow")
    Observable<NetResponse<NoResponeBackModel>> followSomeOne(@Query(AppConstants.NAME) String id,
                                                              @Query(AppConstants.SESSION_ID) String sessionid,
                                                              @Query(AppConstants.ACTION) int action);
    /**
     * 用户阅读记录 0 观看 1 文章
     */
    @GET("api/v2/user/histories?type=1")
    Observable<NetResponse<NormalNewsModel>> getUserReadHistory(@Query(AppConstants.USER_ID) String id,
                                                                @Query(AppConstants.SESSION_ID) String sessionid,
                                                                @Query(AppConstants.START) int start);

    /**
     * 用户观看记录 0 观看 1 文章
     */
    @GET("api/v2/user/histories?type=0")
    Observable<NetResponse<UserHistoryTestModel>> getUserWatchHistory(@Query(AppConstants.USER_ID) String id,
                                                                      @Query(AppConstants.SESSION_ID) String sessionid,
                                                                      @Query(AppConstants.START) int start);

    /**
     * 用户阅读记录 测试
     */
    @GET()
    Observable<NetResponse<NormalNewsModel>> getUserReadHistoryTest(@Url String url);

    /**
     * 视频列表测试
     */
    @GET()
    Observable<NetResponse<UserHistoryTestModel>> getHisteryTest(@Url String url);

    /**
     * 用户收藏列表
     */
    @GET("api/v2/user/favorites")
    Observable<NetResponse<NormalNewsModel>> getUserCollection(@Query(AppConstants.USER_ID) String id,
                                                               @Query(AppConstants.START) int start);

    /**
     * 秘籍列表
     */
    @GET("api/v2/user/cheats")
    Observable<NetResponse<CheatsListModel>> getUserCheats(@Query(AppConstants.USER_ID) String userId, @Query(AppConstants.START) int start);

    /**
     * 我的发布---直播
     */
    @GET("api/v2/user/works?type=0")
    Observable<NetResponse<UserPublishVideoModel>> getUserPublishLiving(@Query(AppConstants.USER_ID) String id,
                                                                       @Query(AppConstants.SESSION_ID) String sessionid,
                                                                       @Query(AppConstants.START) int start);

    /**
     * 我的发布---直播，测试
     */
    @GET()
    Observable<NetResponse<UserPublishVideoModel>> getUserPublishLivingTest(@Url String url);

    /**
     * 我的发布---秘籍
     */
    @GET("api/v2/user/works?type=1")
    Observable<NetResponse<CheatsListModel>> getUserPublishCheats(@Query(AppConstants.USER_ID) String id,
                                                                       @Query(AppConstants.SESSION_ID) String sessionid,
                                                                       @Query(AppConstants.START) int start);

    /**
     * 我的发布---秘籍，测试
     */
    @GET()
    Observable<NetResponse<CheatsListModel>> getUserPublishCheatsTest(@Url String url);

    /**
     * 我的发布---文章
     */
    @GET("api/v2/user/works?type=2")
    Observable<NetResponse<NormalNewsModel>> getUserPublishArticle(@Query(AppConstants.USER_ID) String id,
                                                           @Query(AppConstants.SESSION_ID) String sessionid,
                                                           @Query(AppConstants.START) int start);

    /**
     * 我的发布---文章，测试
     */
    @GET()
    Observable<NetResponse<NormalNewsModel>> getUserPublishArticleTest(@Url String url);

    /**
     * 我的购买---视频直播
     */
    @GET("api/v2/user/purchases?type=0")
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyLiving(@Query(AppConstants.USER_ID) String userId,
                                                                        @Query(AppConstants.SESSION_ID) String sessionId,
                                                                        @Query(AppConstants.START) int start);

    /**
     * 我的购买---视频直播，测试
     */
    @GET()
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyLivingTest(@Url String url);

    /**
     * 我的购买---精品视频
     */
    @GET("api/v2/user/purchases?type=1")
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyVideo(@Query(AppConstants.USER_ID) String userId,
                                                                    @Query(AppConstants.SESSION_ID) String sessionId,
                                                                    @Query(AppConstants.START) int start);

    /**
     * 我的购买---精品视频，测试
     */
    @GET()
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyVideoTest(@Url String url);

    /**
     * 我的购买---已买秘籍
     */
    @GET("api/v2/user/purchases?type=2")
    Observable<NetResponse<CheatsListModel>> getUserBuyCheats(@Query(AppConstants.USER_ID) String userId,
                                                                   @Query(AppConstants.SESSION_ID) String sessionId,
                                                                   @Query(AppConstants.START) int start);

    /**
     * 我的购买---已买秘籍，测试
     */
    @GET()
    Observable<NetResponse<CheatsListModel>> getUserBuyCheatsTest(@Url String url);

    /**
     * 大V直播列表
     */
    @GET("api/v2/user/videos")
    Observable<NetResponse<UserPublishVideoModel>> getVipUserPublishLiving(@Query(AppConstants.USER_ID) String id,
                                                                           @Query(AppConstants.START) int start);

    /**
     * 大V秘籍列表
     */
    @GET("api/v2/user/cheats")
    Observable<NetResponse<CheatsListModel>> getVipUserBuyCheats(@Query(AppConstants.USER_ID) String id,
                                                               @Query(AppConstants.START) int start);

    /**
     * 大V文章列表
     */
    @GET("api/v2/user/news")
    Observable<NetResponse<NormalNewsModel>> getVipUserPublishArticle(@Query(AppConstants.USER_ID) String id,
                                                                    @Query(AppConstants.START) int start);

    /**
     * 查看他人主页信息
     */
    @GET("api/v2/user/baseinfo")
    Observable<NetResponse<UserPageInfo>> getUserPageInfo(@Query(AppConstants.USER_ID) String id,
                                                          @Query(AppConstants.SESSION_ID) String sessionid,
                                                          @Query(AppConstants.PERSON_ID) String personId);

    /**
     * 评论点赞
     * @param type （0，新闻；1，股票；2，视频）
     * @param action （0，取消；1，点赞）
     */
    @GET("api/v2/posts/like")
    Observable<NetResponse<NoResponeBackModel>> praiseClick(@Query(AppConstants.USER_ID) String id,
                                                            @Query(AppConstants.SESSION_ID) String sessionid,
                                                            @Query(AppConstants.POST_ID) String postid,
                                                            @Query(AppConstants.TYPE) int type,
                                                            @Query(AppConstants.ACTION) int action);


    /**
     * 添加已读文章历史记录
     */
    @GET("api/v2/news/history")
    Observable<NetResponse<NoResponeBackModel>> addReadNewsInfo(@Query(AppConstants.USER_ID) String id,
                                                          @Query(AppConstants.SESSION_ID) String sessionid,
                                                          @Query(AppConstants.NEWS_ID) String newsid);
    /**
     * 添加观看历史记录
     */
    @GET("api/v2/news/history")
    Observable<NetResponse<NoResponeBackModel>> addWatchVideoInfo(@Query(AppConstants.USER_ID) String id,
                                                                @Query(AppConstants.SESSION_ID) String sessionid,
                                                                @Query(AppConstants.VIDEO_ID) String videoid);

    /**
     * 用户评论列表
     */
    @GET("api/v2/users/posts")
    Observable<NetResponse<UserPageCommentModel>> getUserPostCommentList(@Query(AppConstants.USER_ID) String id,
                                                                         @Query(AppConstants.PERSON_ID) String personid,
                                                                         @Query(AppConstants.START) int start);

    /**
     * 添加文章收藏  0 取消收藏 1 收藏
     */
    @GET("api/v2/news/favorite")
    Observable<NetResponse<NoResponeBackModel>> addNewsToColloction(@Query(AppConstants.USER_ID) String id,
                                                                    @Query(AppConstants.SESSION_ID) String sessionid,
                                                                    @Query(AppConstants.NEWS_ID) String newsid,
                                                                    @Query(AppConstants.ACTION) int action);


    /**
     * 判断收藏状态
     * type	1 资讯 2 视频 3 指数 4 股票
     */
    @GET(ApiConstant.DEBUG_HOST+"user/collect")
    Observable<NetResponse<NewsCollectStatus>> getNewsCollectStatus(@Query(AppConstants.SESSION_ID) String sessionid,
                                                                    @Query(AppConstants.CODE) String newsid,
                                                                    @Query(AppConstants.ACTION) int action,
                                                                    @Query(AppConstants.TYPE) int type);
    /**
     * 编辑个人资料
     */
    @GET("api/v2/user/edit")
    Observable<NetResponse<Object>> editUserInfo(@Query(AppConstants.USER_ID) String userId,
                                         @Query(AppConstants.SESSION_ID) String sessionId,
                                         @Query("nickname") String nickname,
                                         /*@Query("logoUrl") String logoUrl,*/
                                         @Query("location") String location,
                                         @Query("birthday") String birthday,
                                         @Query("signature") String signature,
                                         @Query("gender") String gender,
                                         @Query("credentials") String credentials,
                                         @Query("introduce") String introduce);

    /**
     * 搜索文章列表
     */
    @GET("api/v2/search/news")
    Observable<NetResponse<NormalNewsModel>> searchNews(@Query(AppConstants.TITLE) String title,
                                                        @Query(AppConstants.START) int start);

    /**
     * 清空历史记录
     * 类型（0，直播记录；1，文章记录）
     */
    @GET("api/v2/user/clean")
    Observable<NetResponse<NoResponeBackModel>> cleanHistoryList(@Query(AppConstants.USER_ID) String userId,
                                                                 @Query(AppConstants.SESSION_ID) String sessionid,
                                                                 @Query(AppConstants.TYPE) int type);

    /**
     * 检查版本更新
     */
    @GET(ApiConstant.DEBUG_HOST + "sys/appupdate?device=android&version=" + BuildConfig.VERSION_NAME + "&versioncode=" + BuildConfig.VERSION_CODE)
    Observable<NetResponse<UpdateInfoEntity>> versionCheck();

    /*-------------------------------------------直播----------------------------------------*/

    /**
     * 直播---视频直播列表
     */
    @GET(ApiConstant.DEBUG_HOST + "video/recommendlist")
    Observable<NetResponse<RecommendVideoListModel>> getLiveVideo(@Query(AppConstants.START) int start,
                                                                  @Query(AppConstants.COUNT) int count,
                                                                  @Query(AppConstants.PERSON_ID) String userId);

    /**
     *  直播付费
     */
    @GET(ApiConstant.DEBUG_HOST+"video/livepay")
    Observable<NetResponse<NoResponeBackModel>> payForVideo(@Query(AppConstants.SESSION_ID) String sessionid,
                                                            @Query(AppConstants.VID) String vid);

    /**
     * 直播---图文直播列表
     */
    @GET("api/textlive/home")
    Observable<NetResponse<TextLiveListModel>> getLiveImageText(@Query(AppConstants.START) int start);

    /**
     * 直播---精品视频列表
     */
    @GET(ApiConstant.DEBUG_HOST + "video/vodlist")
    Observable<NetResponse<GoodsVideoListModel>> getLiveGoodVideo(@Query(AppConstants.START) int start);

    /**
     * 评论列表
     * @param type 0 新闻 1视频 2股票
     */
    @GET("api/v2/posts/posts")
    Observable<NetResponse<VideoCommentModel>> getCommentListByType(@Query(AppConstants.TOPIC_ID) String topicid,
                                                                   @Query(AppConstants.USER_ID) String userid,
                                                                   @Query(AppConstants.TYPE) int type,
                                                                   @Query(AppConstants.START) int start,
                                                                   @Query(AppConstants.ORDER_BY) String orderBy);
    /**
     * 视频推荐列表
     */
    @GET("api/v2/recommend/videos")
    Observable<NetResponse<ArrayList<VideoEntity>>> getVideoRecommendList(@Query(AppConstants.VIDEO_ID) String vid,
                                                                          @Query(AppConstants.START) int start);
}
