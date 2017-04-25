package com.easyvaas.elapp.net.mynet;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.market.MarketExponentModel;
import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.bean.news.NewsColumnModel;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopicModel;
import com.easyvaas.elapp.bean.user.CheatsListModel;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.user.UserFollowModel;
import com.easyvaas.elapp.bean.user.UserHistoryTestModel;
import com.easyvaas.elapp.bean.user.UserPageCommentModel;
import com.easyvaas.elapp.bean.user.UserPageInfo;
import com.easyvaas.elapp.bean.user.UserPublishVideoModel;
import com.easyvaas.elapp.bean.video.RecommendVideoListModel;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;

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

    /*-------------------------------------------用户中心----------------------------------------*/

    /**
     * 获取用户信息
     */
    @GET("api/v2/user/info")
    Observable<NetResponse<User>> getUserInfo(@Query("name") String id,
                                              @Query(AppConstants.SESSION_ID) String sessionid);

    /**
     * 获取用户信息V2
     */
    @GET("api/v2/user/info")
    Observable<NetResponse<User>> getUserInfoNew(@Query("name") String id,
                                              @Query(AppConstants.SESSION_ID) String sessionid);

    /**
     * 获取用户粉丝列表
     */
    @GET(ApiConstant.DEBUG_HOST+"user/fanslist")
    Observable<NetResponse<UserFollowModel>> getUserFans(@Query("name") String id,
                                                         @Query(AppConstants.SESSION_ID) String sessionid,
                                                         @Query(AppConstants.START) int start);

    /**
     * 获取用户关注列表
     */
    @GET(ApiConstant.DEBUG_HOST+"user/followerlist")
    Observable<NetResponse<UserFollowModel>> getUserFocus(@Query("name") String id,
                                                          @Query(AppConstants.SESSION_ID) String sessionid,
                                                          @Query(AppConstants.START) int start);
    /**
     * 关注用户 0 取消关注 1 关注
     */
    @GET(ApiConstant.DEBUG_HOST+"user/follow")
    Observable<NetResponse<NoResponeBackModel>> followSomeOne(@Query("name") String id,
                                                              @Query(AppConstants.SESSION_ID) String sessionid,
                                                              @Query("action") int action);
    /**
     * 用户阅读记录 0 观看 1 文章
     */
    @GET("api/v2/user/histories?type=1")
    Observable<NetResponse<NormalNewsModel>> getUserReadHistory(@Query(AppConstants.USER_ID) String id,
                                                                        @Query(AppConstants.SESSION_ID) String sessionid);

    /**
     * 用户观看记录 0 观看 1 文章
     */
    @GET("api/v2/user/histories?type=0")
    Observable<NetResponse<UserHistoryTestModel>> getUserWatchHistory(@Query(AppConstants.USER_ID) String id,
                                                                         @Query(AppConstants.SESSION_ID) String sessionid);

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
    @GET("api/v2/user/favoritelist")
    Observable<NetResponse<NormalNewsModel>> getUserCollection(@Query(AppConstants.USER_ID) String id);

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
    @GET("api/v2/user/purchase?type=0")
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyLiving(@Query(AppConstants.USER_ID) String id,
                                                                        @Query(AppConstants.SESSION_ID) String sessionid,
                                                                        @Query(AppConstants.START) int start);

    /**
     * 我的购买---视频直播，测试
     */
    @GET()
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyLivingTest(@Url String url);

    /**
     * 我的购买---精品视频
     */
    @GET("api/v2/user/purchase?type=1")
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyVideo(@Query(AppConstants.USER_ID) String id,
                                                                    @Query(AppConstants.SESSION_ID) String sessionid,
                                                                    @Query(AppConstants.START) int start);

    /**
     * 我的购买---精品视频，测试
     */
    @GET()
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyVideoTest(@Url String url);

    /**
     * 我的购买---已买秘籍
     */
    @GET("api/v2/user/purchase?type=1")
    Observable<NetResponse<CheatsListModel>> getUserBuyCheats(@Query(AppConstants.USER_ID) String id,
                                                                   @Query(AppConstants.SESSION_ID) String sessionid,
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
    Observable<NetResponse<CheatsListModel>> getVUserBuyCheats(@Query(AppConstants.USER_ID) String id,
                                                               @Query(AppConstants.START) int start);

    /**
     * 大V文章列表
     */
    @GET("api/v2/user/news")
    Observable<NetResponse<NormalNewsModel>> getVUserPublishArticle(@Query(AppConstants.USER_ID) String id,
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
     */
    @GET("api/v2/posts/like")
    Observable<NetResponse<NoResponeBackModel>> praiseClick(@Query(AppConstants.USER_ID) String id,
                                                            @Query(AppConstants.SESSION_ID) String sessionid,
                                                            @Query(AppConstants.POST_ID) String postid);


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
                                                                    @Query("action") int action);





    /*-------------------------------------------直播----------------------------------------*/
    @GET(ApiConstant.DEBUG_HOST+"video/recommendlist")
    Observable<NetResponse<RecommendVideoListModel>> getLiveVideo(@Query(AppConstants.START) int start);

}
