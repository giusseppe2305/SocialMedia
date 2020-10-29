package com.optic.socialmedia.retrofit;

import com.optic.socialmedia.models.FCMBody;
import com.optic.socialmedia.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAObEEd4k:APA91bGIFUJoHswFvl1Kgt_0DpQ2q1KUfb7oGojSYO9jOqO29ly8-KOmrU34L27fzvRbML7pBqtDVFohwURKX65SQAmNymjIR4I-kqUUkyEQ6ksuMSe3silZajyZPBa8P9RSuU6GQTJt"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
