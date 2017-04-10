package com.easyvaas.elapp.net.mynet;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;

import retrofit2.http.GET;
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

    @GET("api/news/banners")
    Observable<NetResponse<BannerModel>> getBannerNews();

}
