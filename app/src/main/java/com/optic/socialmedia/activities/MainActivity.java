package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.optic.socialmedia.R;
import com.optic.socialmedia.models.User;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.UserDatabaseProvider;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_GOOGLE = 1;
    Button btnLogin;
    TextView txtRegisterNow;
    TextInputEditText txtEmail;
    TextInputEditText txtPass;
    private AuthProviders mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnSignInGoogle;
    private UserDatabaseProvider mUserProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=new AuthProviders();
        mUserProvider=new UserDatabaseProvider();


        btnLogin=findViewById(R.id.btnLoginMain);
        txtRegisterNow=findViewById(R.id.txtRegisterNowMain);
        txtEmail=findViewById(R.id.emailMain);
        txtPass=findViewById(R.id.passlMain);
        btnSignInGoogle=findViewById(R.id.btnSignInGoogle);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=txtEmail.getText().toString();
                String pass=txtPass.getText().toString();
                mAuth.logIn(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login correcto!!!!", Toast.LENGTH_SHORT).show();

                            Intent i= new Intent(MainActivity.this,HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }else{
                            Toast.makeText(MainActivity.this, "Login fallido!!!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        txtRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.existSession()){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {

              mAuth.logInGoogle(idToken).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUserExist(task.getResult().getUser().getUid());
                        } else {
                            Toast.makeText(MainActivity.this, "No se pudo iniciar con google "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserExist(final String id) {
        mUserProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    Intent i= new Intent(MainActivity.this,HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }else{
                    User mUser= new User();
                    mUser.setEmail(mAuth.getFirebaseAuth().getCurrentUser().getEmail());
                    mUser.setId(id);
                    mUserProvider.createUser(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Intent i= new Intent(MainActivity.this, CompleteProfileActivity.class);
                                startActivity(i);
                                Toast.makeText(MainActivity.this, "Registrado database normal ", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Ha habido un error database "+task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE);
    }
}