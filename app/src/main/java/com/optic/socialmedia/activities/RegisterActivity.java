package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.optic.socialmedia.R;
import com.optic.socialmedia.models.User;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.optic.socialmedia.utils.MyAppCompactActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends MyAppCompactActivity {
    CircleImageView btnBack;
    TextInputEditText txtEmail;
    TextInputEditText txtPass;
    TextInputEditText txtConfirmPass;
    Button btnRegister;
    private AuthProviders mAuth;
    private UserDatabaseProvider mUserProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=new AuthProviders();
        mUserProvider= new UserDatabaseProvider();


        btnBack=findViewById(R.id.btnBack);
        txtConfirmPass=findViewById(R.id.passConfirmRegister);
        txtEmail=findViewById(R.id.emailRegister);
        txtPass=findViewById(R.id.passRegister);
        btnRegister=findViewById(R.id.btnRegisterRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=txtEmail.getText().toString();
                String pass=txtPass.getText().toString();
                String confirmPass=txtConfirmPass.getText().toString();
                final User mUser= new User(email,pass);
                Intent i= new Intent(RegisterActivity.this, CompleteProfileActivity.class);
                i.putExtra("usuario",mUser);
                startActivity(i);
                //createUser();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void createUser() {
        String email=txtEmail.getText().toString();
        String pass=txtPass.getText().toString();
        String confirmPass=txtConfirmPass.getText().toString();
        final User mUser= new User(email,pass);
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
                                Toast.makeText(RegisterActivity.this, "Registrado database normal ", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(RegisterActivity.this, "Ha habido un error database "+task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Toast.makeText(RegisterActivity.this, "Usuario registrado Authentication", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "Ha habido un error AUTHENTICATION "+task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}