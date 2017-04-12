package com.easyvaas.elapp.net.mynet;

import com.easyvaas.elapp.net.HooviewApiConstant;
import com.hooview.app.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Editor  Misuzu
 * Date   2017/3/28
 * Api接口管理类
 */

public class RetrofitHelper{

    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;
    private ApiService mApiService;

    private RetrofitHelper()
    {
        initOkHttp();
        initRetrofit();
    }
    public static RetrofitHelper getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        private static RetrofitHelper INSTANCE = new RetrofitHelper();
    }

    //初始化OkHttp
    private void initOkHttp()
    {
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        //设置打印
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            mBuilder.addInterceptor(loggingInterceptor);
        }
        //设置超时
        mBuilder.connectTimeout(15, TimeUnit.SECONDS);
        mBuilder.readTimeout(20, TimeUnit.SECONDS);
        mBuilder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        mBuilder.retryOnConnectionFailure(true);
        mOkHttpClient = mBuilder.build();
    }

    //初始化Retrofit
    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(HooviewApiConstant.HOST_HOOVIEW+"/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
    }


    public ApiService getService()
    {
        if (mApiService == null)
            mApiService =  mRetrofit.create(ApiService.class);
        return mApiService;
    }


}
