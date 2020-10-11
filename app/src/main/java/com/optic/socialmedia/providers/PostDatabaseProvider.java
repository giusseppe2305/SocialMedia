package com.optic.socialmedia.providers;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialmedia.models.Post;

public class PostDatabaseProvider {
    CollectionReference mCollection;

    public PostDatabaseProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Posts");
    }

    public Task<Void> save(Post mPost) {
        return mCollection.document().set(mPost);
    }

    public Query getAll() {
      return   mCollection.orderBy("timestamp",Query.Direction.DESCENDING);
    }

    public Task<DocumentSnapshot> getPost(String idPost){
        return mCollection.document(idPost).get();
    }


    public Task<QuerySnapshot> getPostFromUser(String idUserToSee) {
        return mCollection.whereEqualTo("idUser",idUserToSee).get();
    }
}
