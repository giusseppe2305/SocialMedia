package com.optic.socialmedia.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.optic.socialmedia.R;
import com.optic.socialmedia.models.Message;

import java.util.Date;

public class NotificationHelper extends ContextWrapper {
    private static final String CHANNEL_ID="com.optic.socialmedia";
    private static final String CHANNEL_NAME="SocialMedia";

    private NotificationManager manager;

    public NotificationHelper(Context context) {
        super(context);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            //solo es obiligatorio crea el canal en versiones superiorres o iguales a android 8
            createChannerls();
        }
    }

    //obligario en versines superiores a android 8
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannerls(){
        NotificationChannel notificationChannel=new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager(){
        if (manager == null) {
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    /////versiones anteriorires
    public NotificationCompat.Builder getNotificaion(String title,String body){
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setColor(Color.GRAY)
                .setSmallIcon(R.drawable.ic_chat)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }
    
    public NotificationCompat.Builder getNotificaionMessage(Message[] messages){
        Person person1= new Person.Builder()
                .setName("Andres")
                .setIcon(IconCompat.createWithResource(getApplicationContext(),R.mipmap.ic_launcher))
                .build();
        Person person2= new Person.Builder()
                .setName("Carlos")
                .setIcon(IconCompat.createWithResource(getApplicationContext(),R.mipmap.ic_launcher))
                .build();
        NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(getActiveNotification(1));
        NotificationCompat.MessagingStyle messagingStyle= new NotificationCompat.MessagingStyle(person1);
        NotificationCompat.MessagingStyle.Message message1= new NotificationCompat.MessagingStyle.Message(
                "Ultimo mensaje",
                new Date().getTime(),
                person1
        );
        messagingStyle.addMessage(message1);
        for(Message m:messages){
            NotificationCompat.MessagingStyle messagingStyleI= new NotificationCompat.MessagingStyle(person1);
            NotificationCompat.MessagingStyle.Message messageI= new NotificationCompat.MessagingStyle.Message(
                    m.getMessage(),
                    m.getTimestamp(),
                    person2
            );
            messagingStyle.addMessage(messageI);
        }
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(messagingStyle);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Notification getActiveNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] barNotifications = notificationManager.getActiveNotifications();
        for(StatusBarNotification notification: barNotifications) {
            if (notification.getId() == notificationId) {
                return notification.getNotification();
            }
        }
        return null;
    }
}
