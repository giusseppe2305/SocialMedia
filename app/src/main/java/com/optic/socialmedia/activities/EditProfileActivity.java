package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.optic.socialmedia.R;
import com.optic.socialmedia.models.Photo;
import com.optic.socialmedia.models.User;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.ImageProvider;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.optic.socialmedia.utils.FileUtil;
import com.optic.socialmedia.utils.Util;
import com.optic.socialmedia.utils.MyAppCompactActivity;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends MyAppCompactActivity {
    private static final int REQUEST_CODE_INTENT_GALLERY = 1;
    private static final int REQUEST_CODE_INTENT_TAKE_PHOTO = 2;
    TextInputEditText nombre;
    TextInputEditText apellidos;
    TextInputEditText edad;
    Button btnUpdateProfile;
    CircleImageView btnEditPhotoProfile;
    CircleImageView ivPhotoProfile;
    CircleImageView btntBack;

    AuthProviders mAuth;
    UserDatabaseProvider mUserProvider;
    ImageProvider mImageProvider;

    MaterialAlertDialogBuilder mDialogSelectFromPicture;
    Photo currentPhotoProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = new AuthProviders();
        mUserProvider = new UserDatabaseProvider();
        mImageProvider = new ImageProvider();
        currentPhotoProfile=new Photo();

        nombre = findViewById(R.id.nombreUpdateProfile);
        apellidos = findViewById(R.id.apellidosUpdateProfile);
        edad = findViewById(R.id.edadUpdateProfile);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnEditPhotoProfile = findViewById(R.id.editImageProfileUpdateProfile);
        ivPhotoProfile = findViewById(R.id.imageProfileUpdateProfile);
        btntBack = findViewById(R.id.btnBack);


        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
        btntBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnEditPhotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogSelectFromPicture.show();
            }
        });
        mDialogSelectFromPicture = new MaterialAlertDialogBuilder(this).
                setTitle("Elige una opcion")
                .setItems(new String[]{"Elegir de la galeria", "Tomar FOTO AHORA"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Util.openGallery(EditProfileActivity.this,REQUEST_CODE_INTENT_GALLERY);
                        } else {

                            Util.takePhoto(EditProfileActivity.this,currentPhotoProfile,REQUEST_CODE_INTENT_TAKE_PHOTO);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(EditProfileActivity.this, "Cancelaste la opcion de elegir donde sacar imagen", Toast.LENGTH_SHORT).show();
                    }
                });

        cargarDatos();
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
                    Picasso.with(this).load(currentPhotoProfile.getFilePhotoSelected()).into(ivPhotoProfile);

                } catch (Exception e) {
                    Log.e("error", "eroor: " + e.getMessage());
                    Toast.makeText(this, "Fallo al coger imagen sola " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        } else if (requestCode == REQUEST_CODE_INTENT_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                currentPhotoProfile.setFilePhotoSelected(new File(currentPhotoProfile.getAbsolutePhotoPath()));

                Picasso.with(this).load(currentPhotoProfile.getFilePhotoSelected()).into(ivPhotoProfile);
            } else {
                System.out.println("***************************RESULTADOS****");
                if(currentPhotoProfile.getBackupAbsolutePath().isEmpty()){
                    System.out.println("PRIMERA VEZ QUE ENTRAMOS DIRECTO A CAMARA Y NO QUEREMOS SUBIR FOTO A SERVER ASI QUE PONEMOS NULL");
                    currentPhotoProfile.setIsNecessaryUploadPhotoToServer(false);

                }else if(new File(currentPhotoProfile.getBackupAbsolutePath()).exists()){
                    System.out.println("TENIAMOS UNA IMAGEN SELECCIONADA DE GALERIA Y ESA ES LA QUE QUERMOS SUBIR ASI QUE SUSTITUIMOS EL BACLK UP");
                    currentPhotoProfile.useBuckup();
                }else{
                    System.out.println("NI PUTA IDEA QUE HA PASADOOOOOOO");
                }

                Toast.makeText(this, "Fallo al traer la photo del intent camera 1", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void editProfile() {
        String name = this.nombre.getText().toString();
        String age = edad.getText().toString();
        String lastName = apellidos.getText().toString();
        final User mUser = new User(name, lastName, age);
        if (currentPhotoProfile.isNecessaryUploadPhotoToServer()) {
            mImageProvider.save(this, currentPhotoProfile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mUser.setImageProfile(uri.toString());
                                mUserProvider.updateUser(mAuth.getIdCurrentUser(), mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditProfileActivity.this, "Se actualizo correcamente tu perfil", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(EditProfileActivity.this, "Fallo", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfileActivity.this, "Fallo al obtener la url de la iagen del perfil", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Toast.makeText(EditProfileActivity.this, "Ha habido un error al subir la imagen del  perfil.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            mUserProvider.updateUser(mAuth.getIdCurrentUser(), mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "Se actualizo correcamente tu perfil", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Fallo", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

    public void cargarDatos() {
        Task<DocumentSnapshot> task = mUserProvider.getUser(mAuth.getIdCurrentUser()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nombre.setText(documentSnapshot.get("nombre").toString());
                apellidos.setText(documentSnapshot.get("apellidos").toString());
                edad.setText(documentSnapshot.get("edad").toString());
                Object url = documentSnapshot.get("imageProfile");
                if (url != null) {
                    Picasso.with(EditProfileActivity.this).load(url.toString()).into(ivPhotoProfile);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Algo ha fallodo", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }





}