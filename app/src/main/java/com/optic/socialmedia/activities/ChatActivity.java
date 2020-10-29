package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialmedia.R;
import com.optic.socialmedia.adapters.MessageAdapter;
import com.optic.socialmedia.models.Chat;
import com.optic.socialmedia.models.Message;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.ChatsProvider;
import com.optic.socialmedia.providers.MessageProvider;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.optic.socialmedia.utils.MyAppCompactActivity;
import com.optic.socialmedia.utils.RelativeTime;
import com.optic.socialmedia.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends MyAppCompactActivity {
    ChatsProvider mChatsProvider;
    AuthProviders mAuth;
    UserDatabaseProvider mUserProvider;
    MessageProvider mMessageProvider;
    String idUserToChat;
    MaterialToolbar toolbar;
    CircleImageView btnSendMessage;
    EditText editTextMessageToSend;
    private String idCurrentChat;

    CircleImageView photoUserToToolbar;
    TextView nameUserToToolbar;
    TextView statusUserToToolbar;

    RecyclerView mRecyclerViewMessage;
    MessageAdapter mAdapterMessage;
    private LinearLayoutManager linearLayoutManager;
    private ListenerRegistration escuchaConstanteCambioStatusConnectionUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar=findViewById(R.id.toolbarChat);

        setSupportActionBar(toolbar);
       getSupportActionBar().setTitle("");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        mUserProvider=new UserDatabaseProvider();
        mChatsProvider=new ChatsProvider();
        mAuth= new AuthProviders();
        mMessageProvider=new MessageProvider();

        editTextMessageToSend=findViewById(R.id.editTextMessageChat);
        btnSendMessage=findViewById(R.id.btnSendMessageChat);
        photoUserToToolbar=findViewById(R.id.photoUserToToolbar);
        nameUserToToolbar=findViewById(R.id.nameUserToToolbar);
        statusUserToToolbar=findViewById(R.id.statusUserToToolbar);

        mRecyclerViewMessage=findViewById(R.id.recyclerViewMessage);
        linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessage.setLayoutManager(linearLayoutManager);

        ///getintetns
        idUserToChat=getIntent().getStringExtra("idUserToChat");
        idCurrentChat=getIntent().getStringExtra("idChat");

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idCurrentChat!=null && !editTextMessageToSend.getText().toString().isEmpty()){
                    final Message msg=new Message();
                    msg.setIdChat(idCurrentChat);
                    msg.setIdsUserFrom(mAuth.getIdCurrentUser());
                    msg.setIdUserTo(idUserToChat);
                    msg.setTimestamp(new Date().getTime());
                    msg.setViewed(false);
                    msg.setMessage(editTextMessageToSend.getText().toString());
                    mMessageProvider.create(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                sendNotification(msg);
                            }else{
                                Toast.makeText(ChatActivity.this, "Fallo al subir mensaje", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    ///diremos que est es el ultimo mensaje del chat
                    mChatsProvider.updateLastMessageOnChat(idCurrentChat,msg.getId(),ChatActivity.this);

                    Toast.makeText(ChatActivity.this, "Se envio el mensaje", Toast.LENGTH_SHORT).show();
                    editTextMessageToSend.setText("");
                    mAdapterMessage.notifyDataSetChanged();
                    //enviar notifiacion



                }else{
                    Toast.makeText(ChatActivity.this, "Escriba un mensaje antes de enviar o idchat", Toast.LENGTH_SHORT).show();
                }

            }
        });

        loadDataToolbar();


        if(idCurrentChat==null){
            checkifChatExistAndCreate();
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        getAllMessages();
    }

    private void getAllMessages() {
        Query query = mMessageProvider.getMessageByChat(idCurrentChat);
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();
        mAdapterMessage = new MessageAdapter(options, ChatActivity.this);
        mRecyclerViewMessage.setAdapter(mAdapterMessage);
        mAdapterMessage.startListening();
        mAdapterMessage.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();

                int numberMessages=mAdapterMessage.getItemCount();
                int lastMessagePosition=linearLayoutManager.findLastVisibleItemPosition();
                if(lastMessagePosition==-1||(positionStart>=(numberMessages-1) && lastMessagePosition ==(positionStart-1))){
                    mRecyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapterMessage.stopListening();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(escuchaConstanteCambioStatusConnectionUser!=null){
            escuchaConstanteCambioStatusConnectionUser.remove();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void loadDataToolbar() {
        mUserProvider.getUser(idUserToChat).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("nombre")) {
                        nameUserToToolbar.setText(documentSnapshot.get("nombre").toString());
                    }
                    if (documentSnapshot.contains("online") && documentSnapshot.contains("lastConnection")) {
                escuchaConstanteCambioStatusConnectionUser =  mUserProvider.getIsOnlineUser(idUserToChat).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                           @Override
                           public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                               if(value.getBoolean("online")){
                                   statusUserToToolbar.setText("Conectado");
                               }else{
                                   statusUserToToolbar.setText(RelativeTime.getTimeAgo(value.getLong("lastConnection")));
                               }
                           }
                       });
                    }
                    if (documentSnapshot.contains("imageProfile")) {
                        String urlImage = documentSnapshot.getString("imageProfile");
                        if (urlImage != null) {
                            Picasso.with(ChatActivity.this).load(urlImage).into(photoUserToToolbar);
                        }
                    }

                } else {
                    Toast.makeText(ChatActivity.this, "fallo al conseguir user toolbar", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "Ha fallado la busqieda del usiario", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendNotification(Message model){
        if(idUserToChat==null){
            return;
        }
        Util.sendNotificationMessage(this,idUserToChat,model,model.getIdsUserFrom().substring(model.getIdsUserFrom().length()-3));
    }

    private void checkifChatExistAndCreate() {
        mChatsProvider.getChatFromUserToAndUserFrom(idUserToChat,mAuth.getIdCurrentUser()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()==0){
                    ///no existe un chat con las comibanciones de los id entonces la creamos
                    createChat();
                    onPause();
                    onStart();

                }else{
                    //existe el chat
                    System.out.println("este el id del chat que existe:::" +queryDocumentSnapshots.getDocuments().get(0).getString("idChat"));
                    idCurrentChat=queryDocumentSnapshots.getDocuments().get(0).getString("idChat");
                    onPause();
                    onStart();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "Error al entrar al chat", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    public void updateViewed(){
        mMessageProvider.getMessageByChatAndSender(idCurrentChat,idUserToChat).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                System.out.println(idCurrentChat+"---"+idUserToChat);
                System.out.println("total mensajes "+queryDocumentSnapshots.size());
                for(DocumentSnapshot i:queryDocumentSnapshots.getDocuments()){
                   String idDocumentIterated= i.getId();
                    System.out.println("id mensaje iterado "+idDocumentIterated);
                   mMessageProvider.updateViewd(idDocumentIterated);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "Fallo al marcar el visto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChat() {
        String idChat=mAuth.getIdCurrentUser()+idUserToChat;
        idCurrentChat=idChat;

        Chat chat = new Chat();
        chat.setIdChat(idChat);
        chat.setIdUserTo(idUserToChat);
        chat.setIdUserFrom(mAuth.getIdCurrentUser());
        chat.setWritting(false);
        chat.setTimestamp(new Date().getTime());
        String [] idChats=new String[]{chat.getIdUserFrom(),chat.getIdUserTo()};
        chat.setIdsChats(Arrays.asList(idChats));

        mChatsProvider.create(chat);
    }
}