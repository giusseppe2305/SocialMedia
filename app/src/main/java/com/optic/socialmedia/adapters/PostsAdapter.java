package com.optic.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.transition.Hold;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialmedia.R;
import com.optic.socialmedia.activities.FiltersActivity;
import com.optic.socialmedia.activities.PostDetailActivity;
import com.optic.socialmedia.models.Like;
import com.optic.socialmedia.models.Post;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.LikesDatabaseProvider;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {
    AuthProviders mAuth;
    private final LikesDatabaseProvider mLikeProvider;
    final Context context;
    private TextView mTextViewNumberFilter;
    private ListenerRegistration escuchaConstanteCambioNumeroLikesPost;

    public ListenerRegistration getEscuchaConstanteCambioNumeroLikesPost() {
        return escuchaConstanteCambioNumeroLikesPost;
    }

    public PostsAdapter(Context c, @NonNull FirestoreRecyclerOptions<Post> options) {
        super(options);
        context = c;
        mLikeProvider = new LikesDatabaseProvider();
        mAuth = new AuthProviders();
    }

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context c, TextView mTextViewNumberFilter) {
        super(options);
        this.mTextViewNumberFilter = mTextViewNumberFilter;
        mLikeProvider = new LikesDatabaseProvider();
        mAuth = new AuthProviders();
        context = c;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post model) {


        DocumentSnapshot snap = getSnapshots().getSnapshot(position);
        final String postId = snap.getId();
        holder.title.setText(model.getTitle());
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pasar = new Intent(context, PostDetailActivity.class);
                System.out.println("idddd..................." + model.getId());
                pasar.putExtra("idPost", postId);
                context.startActivity(pasar);
            }
        });
        Picasso.with(context).load(model.getImages().get(0)).into(holder.ivPost);

        String desc = model.getDescription();
        if (desc.length() > 200) {
            desc = desc.substring(0, 250) + "...";
        }
        holder.description.setText(desc);
        ////tratamos el boton del like
        checkIfExistLike(postId, holder);
        setCountsLikeOfPost(holder, postId);


        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLike(postId, holder);
            }
        });
    }

    private void doLike(final String idPost, final ViewHolder holder) {
        mLikeProvider.getLikeOfPostAndUser(mAuth.getIdCurrentUser(), idPost).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        ///EXISTE LIKE DE ESTE USER ASI QUE LO ELIMINAMOS
                        mLikeProvider.deleteLike(task.getResult().getDocuments().get(0).getId());
                        holder.btnLike.setImageResource(R.drawable.ic_like);
                    } else {
                        ///NO EXISTE LIKE ASI QIE CREAMOS EL LIKE
                        mLikeProvider.save(new Like(new Date().getTime(), mAuth.getIdCurrentUser(), idPost));
                        holder.btnLike.setImageResource(R.drawable.ic_like_up);
                    }
                } else {
                    Toast.makeText(context, "ERROR AL TRAER LIKE DE USER Y POST", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setCountsLikeOfPost(final ViewHolder holder, String postId) {
        escuchaConstanteCambioNumeroLikesPost = mLikeProvider.getLikesOfPost(postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) {
                   // Toast.makeText(context, "Error al cargar el numero de likes " + holder.title.getText().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    holder.countLikes.setText(String.valueOf(value.size()));
                }
            }
        });
    }

    private void checkIfExistLike(String postId, final PostsAdapter.ViewHolder holder) {
        mLikeProvider.getLikeOfPostAndUser(mAuth.getIdCurrentUser(), postId).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() != 0) {
                        holder.btnLike.setImageResource(R.drawable.ic_like_up);
                    }
                } else {
                    Toast.makeText(context, "Ha habido un erro al recuperar los likes " + task.getException().toString(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_post, parent, false);

        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, countLikes;
        ImageView ivPost;
        ImageView btnLike;
        View viewHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.titlePostCardView);
            this.description = itemView.findViewById(R.id.descriptionPostCardView);
            this.ivPost = itemView.findViewById(R.id.ivImagePostCardView);
            this.countLikes = itemView.findViewById(R.id.countLikes);
            this.btnLike = itemView.findViewById(R.id.btnLike);
            viewHolder = itemView;

        }

    }

}
