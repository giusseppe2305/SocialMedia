package com.optic.socialmedia.services;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.optic.socialmedia.channel.NotificationHelper;
import com.optic.socialmedia.models.Message;
import com.optic.socialmedia.utils.Util;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingServiceClient extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> data=remoteMessage.getData();

        String title= data.get("title");
        String body= data.get("body");


        if(title!=null){
            if(title.equals("Nuevo Mensaje")){

                shownNotificationMessage(data);
            }else
            shownNotification(title,body);
        }
    }

    private void shownNotification(String title,String body){
        NotificationHelper notificationHelper=new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder= notificationHelper.getNotificaion(title,body);
        notificationHelper.getManager().notify(new Random().nextInt(10000),builder.build());
    }
    private void shownNotificationMessage(Map<String,String> data){
        int idNotification= Util.stringToInt(data.get("idNotification"));
        String title= data.get("title");
        String body= data.get("body");
        String messagesJSON= data.get("messages");
        Gson gson=new Gson();
        Message[] misMensajes=gson.fromJson(messagesJSON,Message[].class);

        NotificationHelper notificationHelper=new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder= notificationHelper.getNotificaionMessage(misMensajes);
        notificationHelper.getManager().notify(idNotification,builder.build());
    }
}
