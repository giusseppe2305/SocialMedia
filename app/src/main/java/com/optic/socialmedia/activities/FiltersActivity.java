package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.optic.socialmedia.R;
import com.optic.socialmedia.adapters.PostsAdapter;
import com.optic.socialmedia.models.Post;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.PostDatabaseProvider;
import com.optic.socialmedia.utils.MyAppCompactActivity;

public class FiltersActivity extends MyAppCompactActivity {
    String optionFilterSelected;

    AuthProviders mAuthProvider;
    RecyclerView mRecyclerView;
    PostDatabaseProvider mPostProvider;
    PostsAdapter mPostsAdapter;

    TextView mTextViewNumberFilter;
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters_activity);

        mRecyclerView = findViewById(R.id.recyclerViewFilter);
        mToolbar = findViewById(R.id.TOOLBAR);
        mTextViewNumberFilter = findViewById(R.id.textViewNumberFilter);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(FiltersActivity.this, 2));

        optionFilterSelected = getIntent().getStringExtra("category");

        mAuthProvider = new AuthProviders();
        mPostProvider = new PostDatabaseProvider();

    }
    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByCategoryAndTimestamp(optionFilterSelected);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostsAdapter = new PostsAdapter(options, FiltersActivity.this, mTextViewNumberFilter);
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}