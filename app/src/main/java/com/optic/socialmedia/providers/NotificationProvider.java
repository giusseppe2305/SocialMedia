package com.optic.socialmedia.providers;

import com.optic.socialmedia.models.FCMBody;
import com.optic.socialmedia.models.FCMResponse;
import com.optic.socialmedia.retrofit.IFCMApi;
import com.optic.socialmedia.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {
    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
