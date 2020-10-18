package com.optic.socialmedia.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialmedia.models.Like;

public class LikesDatabaseProvider {
    CollectionReference mCollection;

    public LikesDatabaseProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }

    public Task<Void> save(Like mLike) {
        DocumentReference doc=mCollection.document();
        mLike.setId(doc.getId());
        return doc.set(mLike);
    }

    public Task<QuerySnapshot> getLikeOfPostAndUser(String user, String post){
       return mCollection.whereEqualTo("idPost",post).whereEqualTo("idUser",user).get();
    }
    public Query getLikesOfPost(String post){
        return mCollection.whereEqualTo("idPost",post);
    }
    public void deleteLike(String id){
        System.out.println("borrarrr like  "+id);
        mCollection.document(id).delete();
    }
    public Task<DocumentSnapshot> getLike(String idPost){
        return mCollection.document(idPost).get();
    }


}