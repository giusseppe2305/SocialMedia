package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.optic.socialmedia.R;
import com.optic.socialmedia.fragments.ChatFragment;
import com.optic.socialmedia.fragments.FiltersFragment;
import com.optic.socialmedia.fragments.HomeFragment;
import com.optic.socialmedia.fragments.ProfileFragment;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.TokenProvider;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView mBottomNavigationView;
    TokenProvider mTokenProvider;
    AuthProviders mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTokenProvider=new TokenProvider();
        mAuth=new AuthProviders();
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        mBottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());
        createToken();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.page_1:
                            openFragment(new HomeFragment());
                            return true;
                        case R.id.page_2:
                            openFragment(new ChatFragment());
                            return true;
                        case R.id.page_3:
                            openFragment(new FiltersFragment());
                            return true;
                        case R.id.page_4:
                            openFragment(new ProfileFragment());
                            return true;

                    }
                    return false;
                }
            };

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void createToken(){
        mTokenProvider.create(mAuth.getIdCurrentUser());
    }
}