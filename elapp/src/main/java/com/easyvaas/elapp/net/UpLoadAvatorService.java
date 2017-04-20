package com.easyvaas.elapp.net;

import com.easyvaas.elapp.net.mynet.NetResponse;

import okhttp3.MultipartBody;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Editor  Misuzu
 * Date   2017/3/26
 */

public interface UpLoadAvatorService {

    @POST
    Observable<NetResponse> upLoadAvator(@Url String url, @Part MultipartBody.Part part);
}
