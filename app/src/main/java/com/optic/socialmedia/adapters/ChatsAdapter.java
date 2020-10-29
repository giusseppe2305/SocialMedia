package com.optic.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialmedia.R;
import com.optic.socialmedia.activities.ChatActivity;
import com.optic.socialmedia.models.Chat;
import com.optic.socialmedia.models.Comment;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.ChatsProvider;
import com.optic.socialmedia.providers.MessageProvider;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {
    Context context;
    UserDatabaseProvider mUserProvider;
    MessageProvider mMessageProvider;
    ChatsProvider mChatProvider;
    AuthProviders mAuth;

    public ChatsAdapter(@NonNull FirestoreRecyclerOptions<Chat> options, Context c) {
        super(options);
        mMessageProvider=new MessageProvider();
        context = c;
        mChatProvider=new ChatsProvider();
        mUserProvider = new UserDatabaseProvider();
        mAuth = new AuthProviders();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Chat model) {
        final String idUserToChat;

        if (mAuth.getIdCurrentUser().equals(model.getIdUserFrom())) {
            idUserToChat = model.getIdUserTo();
        } else {
            idUserToChat = model.getIdUserFrom();
        }

        mUserProvider.getUser(idUserToChat).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("nombre")) {
                        holder.nameUserTo.setText(documentSnapshot.get("nombre").toString());
                    }
                    if (documentSnapshot.contains("imageProfile")) {
                        String urlImage = documentSnapshot.getString("imageProfile");
                        if (urlImage != null) {
                            Picasso.with(context).load(urlImage).into(holder.ivUserTo);
                        } else {
                            holder.ivUserTo.setImageResource(R.drawable.ic_person_grey);
                        }
                    }

                } else {
                    Toast.makeText(context, "No existen chats", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("idUserToChat", idUserToChat);
                i.putExtra("idChat",model.getIdChat());
                context.startActivity(i);
            }
        });
        mChatProvider.getChatFromUserToAndUserFrom(model.getIdUserTo(),model.getIdUserFrom()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null && !value.isEmpty()){
                    mMessageProvider.getMessage(value.getDocuments().get(0).getString("idLastMessage")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot ultimoMensajeDocument, @Nullable FirebaseFirestoreException error) {
                            if(ultimoMensajeDocument!=null && ultimoMensajeDocument.exists()){
                                String ultimoMensaje=ultimoMensajeDocument.getString("message");
                                holder.lastMessage.setText(ultimoMensaje);

                                if(ultimoMensajeDocument.get("idsUserFrom").equals(mAuth.getIdCurrentUser())){
                                    Toast.makeText(context, "es iguall", Toast.LENGTH_SHORT).show();
                                    ///comprobar si esta visto o no el mensaje
                                    if (ultimoMensajeDocument.getBoolean("viewed")){
                                        holder.viewed.setImageResource(R.drawable.ic_check_blue);
                                    }else{
                                        holder.viewed.setImageResource(R.drawable.ic_check_grey);
                                    }

                                    holder.viewed.setVisibility(View.VISIBLE);
                                }else{
                                    Toast.makeText(context, "eNOOOO ES iguall", Toast.LENGTH_SHORT).show();
                                    setCountMessagesNoSee(model, holder);
                                    holder.viewed.setVisibility(View.GONE);
                                }
                            }

                        }
                    });

                }
            }
        });

    }

    private void setCountMessagesNoSee(@NonNull final Chat model, @NonNull final ViewHolder holder) {
        String idOpuesto;
        if(model.getIdUserTo().equals(mAuth)){
            idOpuesto=model.getIdUserFrom();
        }else{
            idOpuesto=model.getIdUserTo();
        }
        mMessageProvider.getMessageByChatAndSender(model.getIdChat(),idOpuesto).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                System.out.println("total >>>"+queryDocumentSnapshots.size()+"--"+queryDocumentSnapshots.getDocuments().size());
                System.out.println("total >>>"+model.getIdChat()+"--"+mAuth.getIdCurrentUser());
                if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots.size()>0){
                    holder.backgroundCountNoSeeMesages.setVisibility(View.VISIBLE);
                    holder.countNoSeeMessages.setText(queryDocumentSnapshots.size()+"");
                }else if(queryDocumentSnapshots.size()==0){
                    holder.backgroundCountNoSeeMesages.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Fallo al contar mensajes no leidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("crea el view holders");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_chats, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivUserTo;
        TextView nameUserTo;
        TextView dateLastMessage;
        TextView lastMessage;
        View viewHolder;
        ImageView viewed;
        TextView countNoSeeMessages;
        View backgroundCountNoSeeMesages;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserTo = itemView.findViewById(R.id.ivUserTo);
            nameUserTo = itemView.findViewById(R.id.nameUserTo);
            dateLastMessage = itemView.findViewById(R.id.dateLastMessage);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            countNoSeeMessages = itemView.findViewById(R.id.countNoSeeMessagesYet);
            viewed = itemView.findViewById(R.id.ivViewed);
            backgroundCountNoSeeMesages=itemView.findViewById(R.id.backgroundCountNoSeeMesages);
            viewHolder = itemView;
        }

    }

}
