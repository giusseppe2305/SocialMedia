package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.optic.socialmedia.R;
import com.optic.socialmedia.fragments.ChatFragment;
import com.optic.socialmedia.fragments.FiltersFragment;
import com.optic.socialmedia.fragments.HomeFragment;
import com.optic.socialmedia.fragments.ProfileFragment;
import com.optic.socialmedia.models.Comment;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.TokenProvider;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.optic.socialmedia.utils.MyAppCompactActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends MyAppCompactActivity {
    BottomNavigationView mBottomNavigationView;
    TokenProvider mTokenProvider;
    AuthProviders mAuth;
    UserDatabaseProvider mUserProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mUserProvider=new UserDatabaseProvider();
        mTokenProvider=new TokenProvider();
        mAuth=new AuthProviders();
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        mBottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());
        createToken();
       // prueba();
        prueba2();
    }

    private void prueba2() {
        DocumentReference collection= FirebaseFirestore.getInstance().collection("prueba").document("comentarios");
        collection.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<Comment> users = documentSnapshot.toObject(WrapperComments.class).listaComentarios;
                for(Comment i:users){
                    System.out.println(i);
                }
            }
        });

    }

    private void prueba() {
        Comment primero= new Comment();
        primero.setComment("primer comentario");
        primero.setId("1");
        primero.setIdPost("idpost1");
        primero.setIdUser("iduser 1");
        primero.setTimestamp(new Date().getTime());
        Comment segundo= new Comment();
        segundo.setComment("segundo comentario");
        segundo.setId("2");
        segundo.setIdPost("idpost 2");
        segundo.setIdUser("iduser 2");
        segundo.setTimestamp(new Date().getTime());
        Comment tercero= new Comment();
        tercero.setComment("tercero comentario");
        tercero.setId("3");
        tercero.setIdPost("idpost 3");
        tercero.setIdUser("iduser 3");
        tercero.setTimestamp(new Date().getTime());
        ArrayList<Comment> misComments= new ArrayList<>();
        misComments.add(primero);
        misComments.add(segundo);
        misComments.add(tercero);

       CollectionReference collection= FirebaseFirestore.getInstance().collection("prueba");
       WrapperComments entrada= new WrapperComments();
       entrada.setListaComentarios(misComments);
       entrada.setMisTextos(Arrays.asList(new String[]{"hola","adios","que tal"}));
       collection.document("comentarios").set(entrada);
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