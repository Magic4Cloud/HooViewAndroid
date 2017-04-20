package com.easyvaas.elapp.net.mynet;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.market.MarketExponentModel;
import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.bean.news.NewsColumnModel;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.bean.news.TopicModel;
import com.easyvaas.elapp.bean.user.UserHistoryTestModel;

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
    Observable<NetResponse<TopicModel>> getTopicList(@Query("id") String id, @Query("start") int start);

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
     * 用户阅读记录
     *
     * @param type 0 观看 1 文章
     */
    @GET("api/v2/user/historylist")
    Observable<NetResponse<NormalNewsModel>> getUserReadHistory(@Query("userid") String id,
                                                                @Query("sessionid") String sessionid,
                                                                @Query("type") String type);

    /**
     * 用户观看记录
     *
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
    @GET
    Observable<NetResponse<UserHistoryTestModel>> getHisteryTest(@Url String url);

}
