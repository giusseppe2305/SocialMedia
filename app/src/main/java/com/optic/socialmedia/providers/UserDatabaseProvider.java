package com.optic.socialmedia.providers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optic.socialmedia.models.User;

import java.util.Map;

public class UserDatabaseProvider {

    private CollectionReference database;

    public UserDatabaseProvider() {
        database = FirebaseFirestore.getInstance().collection("Users");
    }
    public CollectionReference getCollection(){
        return database;
    }

    public Task<Void> createUser(User miUser) {
        DocumentReference inter = database.document(miUser.getId());
        miUser.setId(null);
        return inter.set(miUser);
    }
    public Task<DocumentSnapshot> getUser(String id){
        return database.document(id).get();
    }
    public Task<Void> updateUser(String id, User user){
        Map<String,Object> update = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).convertValue(user, Map.class);

        return database.document(id).update(update);
    }



}
