package com.optic.socialmedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.optic.socialmedia.R;
import com.optic.socialmedia.activities.EditProfileActivity;
import com.optic.socialmedia.models.Comment;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends FirestoreRecyclerAdapter<Comment, CommentsAdapter.ViewHolder> {
    Context context;
    UserDatabaseProvider mUserProvider;

    public CommentsAdapter(Context c, @NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
        context = c;
        mUserProvider = new UserDatabaseProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Comment model) {
        System.out.println("entra a bindddddddddddddddddddddddddddddddd");
        holder.comment.setText(model.getComment());

        mUserProvider.getUser(model.getIdUser()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("nombre")){
                        holder.nameUser.setText(documentSnapshot.get("nombre").toString());
                    }
                    if(documentSnapshot.contains("imageProfile"))
                    {
                        String urlImage = documentSnapshot.getString("imageProfile");
                        if (urlImage != null) {
                            Picasso.with(context).load(urlImage).into(holder.ivPhotoProfile);
                        }else{
                            holder.ivPhotoProfile.setImageResource(R.drawable.ic_person_grey);
                        }
                    }

                }
            }
        });

        Date date = new Date(model.getTimestamp());
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm - dd MMMM yyyy");
        String dateText = df2.format(date);
        holder.date.setText(dateText);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_comment, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivPhotoProfile;
        TextView nameUser;
        TextView comment;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhotoProfile = itemView.findViewById(R.id.ivPhotoProfileCommentCardView);
            nameUser = itemView.findViewById(R.id.nameCommentCardView);
            comment = itemView.findViewById(R.id.commentCommentCardView);
            date = itemView.findViewById(R.id.dateCommentsCardView);
        }

    }

}
