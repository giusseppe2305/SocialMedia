package com.optic.socialmedia.providers;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialmedia.models.Comment;
import com.optic.socialmedia.models.Post;

public class CommentDatabaseProvider {
    CollectionReference mCollection;

    public CommentDatabaseProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Comments");
    }

    public Task<Void> save(Comment comment) {
        return mCollection.document().set(comment);
    }

    public Query getAllOfPost(String idPost) {
      return   mCollection.whereEqualTo("idPost",idPost);
    }

    public Task<DocumentSnapshot> getPost(String idComment){
        return mCollection.document(idComment).get();
    }


    public Task<QuerySnapshot> getCommentsFromUser(String idUserToSee) {
        return mCollection.whereEqualTo("idUser",idUserToSee).get();
    }
}
