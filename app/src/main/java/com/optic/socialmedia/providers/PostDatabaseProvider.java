package com.optic.socialmedia.providers;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optic.socialmedia.models.Post;

public class PostDatabaseProvider {
    CollectionReference mCollection;
    public PostDatabaseProvider() {
        mCollection= FirebaseFirestore.getInstance().collection("Posts");
    }
    public Task<Void> save(Post mPost){
        return mCollection.document().set(mPost);
    }
}
