package com.easyvaas.elapp.net.mynet;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.bean.news.NewsColumnModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;

import java.util.List;

import retrofit2.http.GET;
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
    @GET("api/v2/news/home")
    Observable<NetResponse<TopRatedModel>> getTopRatedNews();

    @GET
    Observable<NetResponse<TopRatedModel>> getTopRatedNewsTest(@Url String url);

    @GET("api/news/banners")
    Observable<NetResponse<BannerModel>> getBannerNews();

    @GET("api/v2/stock/globalindex")
    Observable<NetResponse<List<MarketGlobalModel>>> getMarketGlobalStock();

    @GET("api/v2/news/column")
    Observable<NetResponse<NewsColumnModel>> getNewsColumn();

    @GET
    Observable<NetResponse<NewsColumnModel>> getNewsColumnTest(@Url String url);

}
