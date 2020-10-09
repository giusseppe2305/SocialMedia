package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.optic.socialmedia.R;
import com.optic.socialmedia.models.Photo;
import com.optic.socialmedia.models.User;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.ImageProvider;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.optic.socialmedia.utils.FileUtil;
import com.optic.socialmedia.utils.Intents;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_INTENT_GALLERY = 1;
    private static final int REQUEST_CODE_INTENT_TAKE_PHOTO = 2;
    CircleImageView btnEditPhotoProfile;
    CircleImageView imageViewProfile;
    MaterialAlertDialogBuilder mDialogSelectFromPicture;

    TextInputEditText txtNombre;
    TextInputEditText txtApellidos;
    TextInputEditText txtEdad;
    Button btnRegister;
    private AuthProviders mAuth;
    private UserDatabaseProvider mUserProvider;
    private User mUser;
    private Photo currentPhotoProfile;
    private ImageProvider mImageProvider;
    private boolean isGoogleLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complet_profile);
        isGoogleLogin = false;
        mAuth = new AuthProviders();
        mUserProvider = new UserDatabaseProvider();
        currentPhotoProfile = new Photo();
        mImageProvider = new ImageProvider();

        btnEditPhotoProfile = findViewById(R.id.editImageProfileCompleteProfile);
        txtNombre = findViewById(R.id.nombreCompleteProfile);
        txtApellidos = findViewById(R.id.apellidosCompleteProfile);
        txtEdad = findViewById(R.id.edadCompleteProfile);
        btnRegister = findViewById(R.id.btnRegisterCompleteProfile);
        imageViewProfile = findViewById(R.id.imageProfileCompleteProfile);


        mDialogSelectFromPicture = new MaterialAlertDialogBuilder(this).
                setTitle("Elige una opcion")
                .setItems(new String[]{"Elegir de la galeria", "Tomar FOTO AHORA"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intents.openGallery(CompleteProfileActivity.this, REQUEST_CODE_INTENT_GALLERY);
                        } else {
                            Intents.takePhoto(CompleteProfileActivity.this, currentPhotoProfile, REQUEST_CODE_INTENT_TAKE_PHOTO);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CompleteProfileActivity.this, "Cancelaste la opcion de elegir donde sacar imagen", Toast.LENGTH_SHORT).show();
                    }
                });

        btnEditPhotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogSelectFromPicture.show();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCreateUser();
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            mUser = new User();
            mUser.setId(mAuth.getIdCurrentUser());
            mUser.setEmail(mAuth.getFirebaseAuth().getCurrentUser().getEmail());
            isGoogleLogin = true;
            //////ponemos la foto en cola,
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
            ImageLoader.getInstance().loadImage(acct.getPhotoUrl().toString(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Bitmap bitmap = loadedImage;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    currentPhotoProfile.setIsNecessaryUploadPhotoToServer(true);
                    currentPhotoProfile.setPhotoBytes(data);
                }
            });
            cargarDatosGoogle(acct);
        } else {
            mUser = (User) getIntent().getSerializableExtra("usuario");
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTENT_GALLERY) {
            if (data.getData() != null) {
                try {
                    Toast.makeText(this, "Cogio imagen sola bien", Toast.LENGTH_LONG).show();
                    currentPhotoProfile.setIsNecessaryUploadPhotoToServer(true);
                    currentPhotoProfile.setFilePhotoSelected(FileUtil.from(this, data.getData()));
                    Picasso.with(this).load(currentPhotoProfile.getFilePhotoSelected()).into(imageViewProfile);


                } catch (Exception e) {
                    Log.e("error", "eroor: " + e.getMessage());
                    Toast.makeText(this, "Fallo al coger imagen sola " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        } else if (requestCode == REQUEST_CODE_INTENT_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                currentPhotoProfile.setFilePhotoSelected(new File(currentPhotoProfile.getAbsolutePhotoPath()));

                Picasso.with(this).load(currentPhotoProfile.getFilePhotoSelected()).into(imageViewProfile);
            } else {
                System.out.println("***************************RESULTADOS****");
                if (currentPhotoProfile.getBackupAbsolutePath().isEmpty()) {
                    System.out.println("PRIMERA VEZ QUE ENTRAMOS DIRECTO A CAMARA Y NO QUEREMOS SUBIR FOTO A SERVER ASI QUE PONEMOS NULL");
                    currentPhotoProfile.setIsNecessaryUploadPhotoToServer(false);

                } else if (new File(currentPhotoProfile.getBackupAbsolutePath()).exists()) {
                    System.out.println("TENIAMOS UNA IMAGEN SELECCIONADA DE GALERIA Y ESA ES LA QUE QUERMOS SUBIR ASI QUE SUSTITUIMOS EL BACLK UP");
                    currentPhotoProfile.useBuckup();
                } else {
                    System.out.println("NI PUTA IDEA QUE HA PASADOOOOOOO");
                }

                Toast.makeText(this, "Fallo al traer la photo del intent camera 1", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void cargarDatosGoogle(GoogleSignInAccount acct) {

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

    private void clickCreateUser() {

        final String nombre = txtNombre.getText().toString();
        final String apelldios = txtApellidos.getText().toString();
        final String edad = txtEdad.getText().toString();
        mUser.setNombre(nombre);
        mUser.setEdad(edad);
        mUser.setApellidos(apelldios);

        if (isGoogleLogin) {
            //ingreso con google
            //final User mUser = new User();

//
            mUser.setTimestamp(new Date().getTime());
            mUserProvider.createUser(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        subirFoto();
                        Intent i = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        Toast.makeText(CompleteProfileActivity.this, "Registrado database normal ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CompleteProfileActivity.this, "Ha habido un error database " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            //final User mUser= this.mUser;
//            mUser.setNombre(nombre);
//            mUser.setEdad(edad);
//            mUser.setApellidos(apelldios);
            mUser.setTimestamp(new Date().getTime());
            mAuth.createAuthentication(mUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        subirFoto();
                        mUser.setId(mAuth.getIdCurrentUser());
                        mUserProvider.createUser(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    Toast.makeText(CompleteProfileActivity.this, "Registrado database normal ", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CompleteProfileActivity.this, "Ha habido un error database " + task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        Toast.makeText(CompleteProfileActivity.this, "Usuario registrado Authentication", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CompleteProfileActivity.this, "Ha habido un error AUTHENTICATION " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }



    }

    private void subirFoto() {
        if (currentPhotoProfile.isNecessaryUploadPhotoToServer()) {
            mImageProvider.save(this, currentPhotoProfile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mUser.setImageProfile(uri.toString());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CompleteProfileActivity.this, "No se ha podido obtener el enlace de la foto subida", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CompleteProfileActivity.this, "Fallo al subir la imagen al servidor", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private ImageLoadingListener actualizarPhotoFromGoogleImage = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            Bitmap bitmap = loadedImage;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            currentPhotoProfile.setPhotoBytes(data);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    };
}