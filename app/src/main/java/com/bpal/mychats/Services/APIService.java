package com.bpal.mychats.Services;

import com.bpal.mychats.BuildConfig;
import com.google.android.gms.common.internal.service.Common;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
        {
                    "Content-Type:application/json",
                    "Authorization:key="+ BuildConfig.BASE_FCM_KEY
        }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
