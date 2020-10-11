package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialmedia.R;
import com.optic.socialmedia.adapters.CommentsAdapter;
import com.optic.socialmedia.adapters.PostsAdapter;
import com.optic.socialmedia.models.Comment;
import com.optic.socialmedia.models.Post;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.CommentDatabaseProvider;
import com.optic.socialmedia.providers.PostDatabaseProvider;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.squareup.picasso.Picasso;

import java.text.BreakIterator;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetailActivity extends AppCompatActivity {
    TextView textName,textEmail,textAge,textCountPosts;
    CircleImageView ivPhotoProfile,btnBack;
    UserDatabaseProvider mUserProvider;
    PostDatabaseProvider mPostProvider;
    private String idUserToSee;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        idUserToSee=getIntent().getStringExtra("idUserToSee");

        mUserProvider=new UserDatabaseProvider();
        mPostProvider=new PostDatabaseProvider();




        textName=findViewById(R.id.nameProfileDetail);
        textEmail=findViewById(R.id.emailProfileDetail);
        textAge=findViewById(R.id.ageProfileDetail);
        textCountPosts=findViewById(R.id.countPostProfileDetail);
        ivPhotoProfile=findViewById(R.id.ivPhotoProfileDetail);
        btnBack=findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        cargarDatosFromUser();
        loadDataFromPosts();


    }



    private void loadDataFromPosts() {
        mPostProvider.getPostFromUser(idUserToSee).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    textCountPosts.setText(task.getResult().size()+"");
                }else{
                    Toast.makeText(ProfileDetailActivity.this, "Error al traer post", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarDatosFromUser() {
        mUserProvider.getUser(idUserToSee).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot selectedProfile=task.getResult();
                    String name= selectedProfile.get("nombre").toString();
                    String apellidos= selectedProfile.get("apellidos").toString();
                    String edad= selectedProfile.get("edad").toString();
                    String imageProfile= selectedProfile.get("imageProfile").toString();
                    String email= selectedProfile.get("email").toString();

                    textAge.setText(edad);
                    textName.setText(name+" "+apellidos);
                    textEmail.setText(email);
                    Picasso.with(ProfileDetailActivity.this).load(imageProfile).into(ivPhotoProfile);
                }else{
                    Toast.makeText(ProfileDetailActivity.this, "Ha habido un error al cargar los datos del perfil seleccionado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}