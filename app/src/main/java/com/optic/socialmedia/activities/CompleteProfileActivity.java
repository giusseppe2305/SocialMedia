package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.optic.socialmedia.R;
import com.optic.socialmedia.models.User;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteProfileActivity extends AppCompatActivity {
    CircleImageView imageViewProfile;
    TextInputEditText txtNombre;
    TextInputEditText txtApellidos;
    TextInputEditText txtEdad;
    Button btnRegister;
    private AuthProviders mAuth;
    private UserDatabaseProvider mUserProvider;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complet_profile);

        mAuth = new AuthProviders();
        mUserProvider = new UserDatabaseProvider();


        txtNombre = findViewById(R.id.nombreCompleteProfile);
        txtApellidos = findViewById(R.id.apellidosCompleteProfile);
        txtEdad = findViewById(R.id.edadCompleteProfile);
        btnRegister = findViewById(R.id.btnRegisterCompleteProfile);
        imageViewProfile = findViewById(R.id.imageProfileCompleteProfile);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createUser();
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            mUser=null;
            cargarDatosGoogle(acct);
        }else{
           mUser=(User)getIntent().getSerializableExtra("usuario");
        }

    }

    private void cargarDatosGoogle(GoogleSignInAccount acct ){

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            System.out.println("///////////**");
            System.out.println(personName);
            System.out.println(personGivenName);
            System.out.println(personFamilyName);
            System.out.println(personEmail);
            System.out.println(personId);
            System.out.println(personPhoto);

            txtNombre.setText(personGivenName);
            txtApellidos.setText(personFamilyName);

        Picasso.with(CompleteProfileActivity.this).load(personPhoto).into(imageViewProfile);
    }
    private void createUser() {
        String nombre = txtNombre.getText().toString();
        String apelldios = txtApellidos.getText().toString();
        String edad = txtEdad.getText().toString();

        if(mUser==null){
            //ingreso con google
            final User mUser = new User();
            mUser.setId(mAuth.getIdCurrentUser());
            mUser.setEmail(mAuth.getFirebaseAuth().getCurrentUser().getEmail());
            mUser.setNombre(nombre);
            mUser.setEdad(edad);
            mUser.setApellidos(apelldios);
            mUserProvider.createUser(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CompleteProfileActivity.this, "Registrado database normal ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CompleteProfileActivity.this, "Ha habido un error database " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            final User mUser= this.mUser;
            mUser.setNombre(nombre);
            mUser.setEdad(edad);
            mUser.setApellidos(apelldios);
            mAuth.createAuthentication(mUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        mUser.setId(mAuth.getIdCurrentUser());
                        mUserProvider.createUser(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Intent i= new Intent(CompleteProfileActivity.this,HomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    Toast.makeText(CompleteProfileActivity.this, "Registrado database normal ", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(CompleteProfileActivity.this, "Ha habido un error database "+task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        Toast.makeText(CompleteProfileActivity.this, "Usuario registrado Authentication", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(CompleteProfileActivity.this, "Ha habido un error AUTHENTICATION "+task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }
}