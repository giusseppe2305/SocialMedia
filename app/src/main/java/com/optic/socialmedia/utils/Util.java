package com.optic.socialmedia.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.optic.socialmedia.activities.PostDetailActivity;
import com.optic.socialmedia.models.Comment;
import com.optic.socialmedia.models.FCMBody;
import com.optic.socialmedia.models.FCMResponse;
import com.optic.socialmedia.models.Message;
import com.optic.socialmedia.models.Photo;
import com.optic.socialmedia.providers.MessageProvider;
import com.optic.socialmedia.providers.NotificationProvider;
import com.optic.socialmedia.providers.TokenProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Util {

    public static void takePhoto(Activity parent, Photo mPhoto, int REQUEST_CODE) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(parent.getPackageManager()) != null) {
            System.out.println("ENTRO A LA CAMARAAAAA");
            File photoFile = null;
            try {
                photoFile = createPhotoFile(parent);
            } catch (Exception e) {
                Toast.makeText(parent, "Hubo un error con el archivo", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                mPhoto.setIsNecessaryUploadPhotoToServer(true);
                System.out.println("instancion de nuevo la current photo");
                mPhoto.setAbsolutePhotoPathRecovered(photoFile.getAbsolutePath());
                Uri photoUri = FileProvider.getUriForFile(parent, "com.optic.socialmedia", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                parent.startActivityForResult(takePictureIntent, REQUEST_CODE);


            }

        } else {
            System.out.println("OTRA OPCIONNASDNASJDAS");
        }
    }

    private static File createPhotoFile(Activity parent) throws IOException {
        File storageDir = parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );

        return photoFile;
    }

    public static void openGallery(Activity parent, int REQUEST_CODE) {
        Intent intentGallery = new Intent();
        intentGallery.setType("image/*");
        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
        parent.startActivityForResult(Intent.createChooser(intentGallery, "Titulooooo guay"), REQUEST_CODE);
    }

    public static int stringToInt(String path) {
        String dev = "";
        for (int i = 0; i < path.length(); i++) {
            dev += (int) path.charAt(i);
        }

        return Integer.parseInt(dev);

    }

    public static void sendNotification(final Context contexto, final String idUserToSendNotification, final String title, final String body) {
        TokenProvider mTokenProvier = new TokenProvider();

        mTokenProvier.getToken(idUserToSendNotification).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    final String tokenUserOwnPost = documentSnapshot.getString("token");
                    if (tokenUserOwnPost != null) {
                        sendNotificationInside(contexto,tokenUserOwnPost,title,body,null,null);
                    } else {
                        Toast.makeText(contexto, "El token del usuario no existe", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(contexto, "Fallo el gete token", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public static void sendNotificationMessage(final Context contexto, final String idUserToSendNotification,  final Message model,final String idNotification) {
        TokenProvider mTokenProvier = new TokenProvider();

        mTokenProvier.getToken(idUserToSendNotification).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    final String tokenUserOwnPost = documentSnapshot.getString("token");
                    if (tokenUserOwnPost != null) {
                        getLastThreeMessages(contexto,idUserToSendNotification,model,tokenUserOwnPost,idNotification);

                    } else {
                        Toast.makeText(contexto, "El token del usuario no existe", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(contexto, "Fallo el gete token", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static void getLastThreeMessages(final Context context, String idUserFrom, final Message model, final String tokenUserOwnPost, final String idNotification) {
        MessageProvider mMessageProvider=new MessageProvider();
        mMessageProvider.getLastThreeMessagesByChatAndSender(model.getIdChat(),idUserFrom).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){
                    ArrayList<Message> messagesArrayList=new ArrayList<>();
                    for(DocumentSnapshot i:queryDocumentSnapshots.getDocuments()){
                        Message miMensaje=i.toObject(Message.class);
                        messagesArrayList.add(miMensaje);
                    }
                    //verificacion si es 0 EL ARRAY NO LA PONGO

                    messagesArrayList.add(model);
                    Gson gson = new Gson();
                    String devMessages=gson.toJson(messagesArrayList);

                    sendNotificationInside(context,tokenUserOwnPost,"Nuevo Mensaje",model.getMessage(),idNotification,devMessages);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Fallo al recoger ultimos tres mensajes y notifiaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void sendNotificationInside(final Context contexto,final String tokenUserOwnPost, final String title, final String mBody, final String idNotifiacion,final String messages) {
        final NotificationProvider mNotificationProvider = new NotificationProvider();
        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("body", mBody);
        if(idNotifiacion!=null){
            data.put("idNotification",idNotifiacion);
        }
        if(messages!=null){
            data.put("messages",messages);
        }
//        if (values.length > 0) {
//            for (int i = 0; i < values.length; i += 2) {
//                data.put(values[i], values[i + 1]);
//            }
//        }
        FCMBody body = new FCMBody(tokenUserOwnPost, "high", "4500s", data);
        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if (response.body() != null) {
                    System.out.println(response.body().getSuccess());
                    if (response.body().getSuccess() == 1) {
                        Toast.makeText(contexto, "La Notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(contexto, "1 Fallo al enviar notificaciones " + response.message() + " - " + call.request().method(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(contexto, " 2 Fallo al enviar notificaciones " + response.message() + " - " + response.errorBody(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });
    }
}
