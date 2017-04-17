package com.easyvaas.elapp.net.mynet;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.market.MarketExponentModel;
import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.bean.news.NewsColumnModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopicModel;

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

    /**
     * 首页要闻
     */
    @GET("api/v3/news/home")
    Observable<NetResponse<TopRatedModel>> getTopRatedNews(@Query("start") int start);

    @GET
    Observable<NetResponse<TopRatedModel>> getTopRatedNewsTest(@Url String url);

    @GET
    Observable<NetResponse<TopicModel>> getTopicListTest(@Url String url);

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

    @GET("api/v2/stock/globalindex")
    Observable<NetResponse<List<MarketGlobalModel>>> getMarketGlobalStock();

    @GET("api/v2/news/column")
    Observable<NetResponse<NewsColumnModel>> getNewsColumn(@Query("start") int start);

    @GET("/api/stock/market")
    Observable<NetResponse<MarketExponentModel>> getMarketExponent();

    @GET
    Observable<NetResponse<NewsColumnModel>> getNewsColumnTest(@Url String url);

}
