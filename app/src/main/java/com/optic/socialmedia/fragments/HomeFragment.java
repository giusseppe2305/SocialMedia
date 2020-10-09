package com.optic.socialmedia.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.optic.socialmedia.R;
import com.optic.socialmedia.activities.MainActivity;
import com.optic.socialmedia.activities.PostActivity;
import com.optic.socialmedia.adapters.PostsAdapter;
import com.optic.socialmedia.models.Post;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.PostDatabaseProvider;


public class HomeFragment extends Fragment {
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    View mView;
    FloatingActionButton mFab;
    private AuthProviders mAuthProvider;
    PostDatabaseProvider mPostProvider;
    PostsAdapter mPostAdapter;
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuthProvider=new AuthProviders();
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mFab=mView.findViewById(R.id.fabHomeFragment);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPost();
            }
        });
        mToolbar=mView.findViewById(R.id.TOOLBAR);
        mPostProvider=new PostDatabaseProvider();


        mRecyclerView=mView.findViewById(R.id.recyclerViewHomreFragment);
        LinearLayoutManager linear= new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linear);


        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).setTitle("Publicaciones");
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query=mPostProvider.getAll();///comprobar no sea nulo
        if(query!=null){
            FirestoreRecyclerOptions<Post> options= new FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post.class).build();
            mPostAdapter= new PostsAdapter(getContext(),options);
            mRecyclerView.setAdapter(mPostAdapter);
            mPostAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mPostAdapter!=null){
            mPostAdapter.stopListening();
        }
    }

    private void goToPost() {
        Intent i= new Intent(getContext(), PostActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemLogOutMenu:mAuthProvider.logOut(getContext());
                Intent intent= new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}