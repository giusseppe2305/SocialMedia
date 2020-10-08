package com.optic.socialmedia.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optic.socialmedia.models.User;

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


}
