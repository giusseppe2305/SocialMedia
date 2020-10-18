package com.optic.socialmedia.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialmedia.R;
import com.optic.socialmedia.adapters.ChatsAdapter;
import com.optic.socialmedia.adapters.CommentsAdapter;
import com.optic.socialmedia.models.Chat;
import com.optic.socialmedia.models.Comment;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.ChatsProvider;


public class ChatFragment extends Fragment {
    AuthProviders mAuth;
    ChatsAdapter mChatAdapter;
    RecyclerView mRecyclerView;
    ChatsProvider mChatProvider;
    View mView;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_chat, container, false);
        mChatProvider=new ChatsProvider();
        mAuth=new AuthProviders();
        mRecyclerView=mView.findViewById(R.id.chatsRecyclerViewChatFragment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        return mView;
    }
    @Override
    public  void onStart() {
        super.onStart();
        final Query query=mChatProvider.getAllChatsFromUser(mAuth.getIdCurrentUser());///comprobar no sea nulo
        if(query!=null){
            FirestoreRecyclerOptions<Chat> options= new FirestoreRecyclerOptions.Builder<Chat>().setQuery(query,Chat.class).build();
            mChatAdapter=new ChatsAdapter(options,getContext());
            mRecyclerView.setAdapter(mChatAdapter);
            mChatAdapter.startListening();
        }
    }
    public void onStop() {
        super.onStop();
        if(mChatAdapter!=null){
            mChatAdapter.stopListening();
        }
    }
}