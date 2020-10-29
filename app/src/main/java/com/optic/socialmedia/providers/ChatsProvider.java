package com.optic.socialmedia.providers;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.optic.socialmedia.models.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatsProvider {
    CollectionReference mCollection;
    public ChatsProvider(){
        mCollection= FirebaseFirestore.getInstance().collection("Chats");
    }
    public void create(Chat mChat)
    {

        mCollection.document(mChat.getIdUserFrom()+mChat.getIdUserTo() ).set(mChat);

    }

    public Query getAllChatsFromUser(String idCurrentUser) {

        return mCollection.whereArrayContains("idsChats",idCurrentUser);
    }
    public Query getChatFromUserToAndUserFrom(String userTo, String userFrom){
        //comprobar si existen las dos combinaciones
        ArrayList<String> ids= new ArrayList<>();
        ids.add(userFrom+userTo);
        ids.add(userTo+userFrom);

        return mCollection.whereIn("idChat",ids);
    }

    public void updateLastMessageOnChat(final String idCurrentChat, final String idMessage, final Context c) {
        Map<String,Object> update=new HashMap<>();
        update.put("idLastMessage",idMessage);
        mCollection.document(idCurrentChat).update(update).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, "Fallo actualizar id last message on chat ", Toast.LENGTH_SHORT).show();
                System.out.println("Fallo actualizar id last message on chat  --"+idCurrentChat+"---"+idMessage);
            }
        });
    }
}
