package com.easyvaas.elapp.net.mynet;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.market.MarketExponentModel;
import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.bean.news.NewsColumnModel;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.bean.news.TopicModel;
import com.easyvaas.elapp.bean.user.CheatsListModel;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.user.UserHistoryTestModel;
import com.easyvaas.elapp.bean.user.UserPublishVideoModel;
import com.easyvaas.elapp.net.ApiConstant;

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
    Observable<NetResponse<TopRatedModel>> getTopRatedNews(@Query("start") int start);
    /**
     * banner新闻
     */
    @GET("api/news/banners")
    Observable<NetResponse<BannerModel>> getBannerNews();
    /**
     * 专题列表
     */
    @GET("api/v2/news/topic")
    Observable<NetResponse<TopicModel>> getTopicList(@Query("id") String id,@Query("start") int start);

    /**
     * 资讯专栏
     */
    @GET("api/v2/news/column")
    Observable<NetResponse<NewsColumnModel>> getNewsColumn(@Query("start") int start);

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
    @GET(ApiConstant.DEBUG_HOST+"user/info")
    Observable<NetResponse<User>> getUserInfo(@Query("name") String id,
                                              @Query("sessionid") String sessionid);
    /**
     * 用户阅读记录
     * @param type 0 观看 1 文章
     */
    @GET("api/v2/user/historylist")
    Observable<NetResponse<NormalNewsModel>> getUserReadHistory(@Query("userid") String id,
                                                                        @Query("sessionid") String sessionid,
                                                                        @Query("type") String type);

    /**
     * 用户观看记录
     * @param type 0 观看 1 文章
     */
    @GET("api/v2/user/historylist")
    Observable<NetResponse<ArrayList<HomeNewsBean>>> getUserWatchHistory(@Query("userid") String id,
                                                                         @Query("sessionid") String sessionid,
                                                                         @Query("type") String type);

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
    Observable<NetResponse<NormalNewsModel>> getUserCollection(@Query("userid") String id,
                                                               @Query("sessionid") String sessionid);

    /**
     * 秘籍列表
     */
    @GET("api/v2/user/cheats")
    Observable<NetResponse<CheatsListModel>> getUserCheats(@Query("userid") String userId, @Query("start") int start);

    /**
     * 我的发布---直播
     */
    @GET("api/v2/user/works?type=0")
    Observable<NetResponse<UserPublishVideoModel>> getUserPublishLiving(@Query("userid") String id,
                                                                       @Query("sessionid") String sessionid,
                                                                       @Query("start") int start);

    /**
     * 我的发布---直播，测试
     */
    @GET()
    Observable<NetResponse<UserPublishVideoModel>> getUserPublishLivingTest(@Url String url);

    /**
     * 我的发布---秘籍
     */
    @GET("api/v2/user/works?type=1")
    Observable<NetResponse<CheatsListModel>> getUserPublishCheats(@Query("userid") String id,
                                                                       @Query("sessionid") String sessionid,
                                                                       @Query("start") int start);

    /**
     * 我的发布---秘籍，测试
     */
    @GET()
    Observable<NetResponse<CheatsListModel>> getUserPublishCheatsTest(@Url String url);

    /**
     * 我的发布---文章
     */
    @GET("api/v2/user/works?type=2")
    Observable<NetResponse<NormalNewsModel>> getUserPublishArticle(@Query("userid") String id,
                                                           @Query("sessionid") String sessionid,
                                                           @Query("start") int start);

    /**
     * 我的发布---文章，测试
     */
    @GET()
    Observable<NetResponse<NormalNewsModel>> getUserPublishArticleTest(@Url String url);

    /**
     * 我的购买---视频直播
     */
    @GET("api/v2/user/purchase?type=0")
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyLiving(@Query("userid") String id,
                                                                        @Query("sessionid") String sessionid,
                                                                        @Query("start") int start);

    /**
     * 我的购买---视频直播，测试
     */
    @GET()
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyLivingTest(@Url String url);

    /**
     * 我的购买---精品视频
     */
    @GET("api/v2/user/purchase?type=1")
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyVideo(@Query("userid") String id,
                                                                    @Query("sessionid") String sessionid,
                                                                    @Query("start") int start);

    /**
     * 我的购买---精品视频，测试
     */
    @GET()
    Observable<NetResponse<UserPublishVideoModel>> getUserBuyVideoTest(@Url String url);

    /**
     * 我的购买---已买秘籍
     */
    @GET("api/v2/user/purchase?type=1")
    Observable<NetResponse<CheatsListModel>> getUserBuyCheats(@Query("userid") String id,
                                                                   @Query("sessionid") String sessionid,
                                                                   @Query("start") int start);

    /**
     * 我的购买---已买秘籍，测试
     */
    @GET()
    Observable<NetResponse<CheatsListModel>> getUserBuyCheatsTest(@Url String url);


}
