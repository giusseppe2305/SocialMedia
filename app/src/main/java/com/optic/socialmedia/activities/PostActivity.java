package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.optic.socialmedia.R;
import com.optic.socialmedia.models.Post;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.ImageProvider;
import com.optic.socialmedia.providers.PostDatabaseProvider;
import com.optic.socialmedia.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.security.AuthProvider;
import java.util.Date;

import javax.xml.transform.Result;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_INTENT_GALLERY = 1;
    private static final int REQUEST_CODE_INTENT_GALLERY2 = 2;
    private static final int REQUEST_CODE_INTENT_TAKE_PHOTO = 3;
    private static final int REQUEST_CODE_INTENT_TAKE_PHOTO2 = 4;
    private int SELECTED_PHOTO_GALLERY;

    ImageView mUploadImage1, mUploadImage2, ivPS4, ivPC, ivXBOX;
    private File mImageFile;
    private File mImageFile2;

    Button btnPublish;
    ImageProvider mImageProvider;
    TextInputEditText txtTitleGame;
    TextInputEditText txtDescription;
    TextView txtCategorySelected;
    private PostDatabaseProvider mPostProvider;
    private AuthProviders mAuth;
    AlertDialog mDialog;
    MaterialAlertDialogBuilder mDialogSelectFromPicture;
    CircleImageView btnBack;
    private String mPhotoPath;
    private String mAbsoultePhotoPath;
    View.OnClickListener eventImagenViews = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            btnPublish.setEnabled(true);
            if (view.getId() == R.id.ivPCPost) {
                txtCategorySelected.setText("Computadora");
            } else if (view.getId() == R.id.ivPS4Post) {
                txtCategorySelected.setText("PS4");
            } else {
                txtCategorySelected.setText("XBOX");
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider = new ImageProvider();
        mPostProvider = new PostDatabaseProvider();
        mAuth = new AuthProviders();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Subiendo imagen 0/2.")
                .setCancelable(false).build();
        mDialogSelectFromPicture=new MaterialAlertDialogBuilder(this).
                setTitle("Elige una opcion")
                .setItems(new String[]{"Elegir de la galeria", "Tomar FOTO AHORA"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            openGallery();
                        }else{
                            takePhoto();
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(PostActivity.this, "Cancelaste la opcion de elegir donde sacar imagen", Toast.LENGTH_SHORT).show();
                    }
                });

        btnBack=findViewById(R.id.btnBack);
        mUploadImage1 = findViewById(R.id.uploadImage1Post);
        mUploadImage2 = findViewById(R.id.uploadImage2Post);
        btnPublish = findViewById(R.id.btnPublishPost);
        ivPC = findViewById(R.id.ivPCPost);
        ivPS4 = findViewById(R.id.ivPS4Post);
        ivXBOX = findViewById(R.id.ivXBOXPost);
        txtTitleGame = findViewById(R.id.titleGamePost);
        txtDescription = findViewById(R.id.descripcionPost);
        txtCategorySelected = findViewById(R.id.categorySelectedPost);

        ivPC.setOnClickListener(eventImagenViews);
        ivPS4.setOnClickListener(eventImagenViews);
        ivXBOX.setOnClickListener(eventImagenViews);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });
        mUploadImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SELECTED_PHOTO_GALLERY=REQUEST_CODE_INTENT_GALLERY;
                mDialogSelectFromPicture.show();
            }
        });
        mUploadImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SELECTED_PHOTO_GALLERY=REQUEST_CODE_INTENT_GALLERY2;
                mDialogSelectFromPicture.show();
            }
        });


    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile=null;
            try {
                photoFile=createPhotoFile();
            }catch (Exception e){
                Toast.makeText(PostActivity.this, "Hubo un error con el archivo", Toast.LENGTH_SHORT).show();
            }

            if(photoFile!=null){
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this,"com.optic.socialmedia",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent,REQUEST_CODE_INTENT_TAKE_PHOTO);
            }
        }
    }

    private File createPhotoFile() throws IOException {
        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile=File.createTempFile(
                new Date()+"_photo",
                ".jpg",
                storageDir
        );
        mPhotoPath="file:"+photoFile.getAbsolutePath();
        mAbsoultePhotoPath=photoFile.getAbsolutePath();
        return photoFile;
    }

    private void clickPost() {
        if (mImageFile == null) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
        } else {
            mDialog.show();
            mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        mDialog.setMessage("Subiendo imagen 1/2.");
                        Toast.makeText(PostActivity.this, "Se almaceno correctamente la imagen 1 en el servidor", Toast.LENGTH_LONG).show();

                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override
                            public void onSuccess(Uri uri) {
                                final String urlImagen1 = uri.toString();

                                mImageProvider.save(PostActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            mDialog.setMessage("Subiendo imagen 2/2.");
                                            savePostDatabase(urlImagen1);
                                            Toast.makeText(PostActivity.this, "Se almaceno correctamente la imagen 2 en el servidor", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(PostActivity.this, "Error al almacenar la imagen 2 " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });


                    } else {
                        Toast.makeText(PostActivity.this, "Error al almacenar la imagen 1 " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void savePostDatabase(final String urlImagen1) {
        final String title = txtTitleGame.getText().toString();
        final String description = txtDescription.getText().toString();
        final String category = txtCategorySelected.getText().toString();
        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url2 = uri.toString();
                Post mPost = new Post();
                mPost.setTitle(title);
                mPost.setImage1(urlImagen1);
                mPost.setImage2(url2);
                mPost.setDescription(description);
                mPost.setCategory(category);
                mPost.setIdUser(mAuth.getIdCurrentUser());
                mPostProvider.save(mPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Se subio el post correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PostActivity.this, "Fallo al subir el post", Toast.LENGTH_SHORT).show();
                            //borrar la imagen
                            mImageProvider.getStorage().delete();
                        }
                    }
                });
            }
        });
    }


    private void openGallery() {
        Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
        intentGallery.setType("image/*");
        startActivityForResult(Intent.createChooser(intentGallery, "Select Picture"), SELECTED_PHOTO_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTENT_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    mImageFile = FileUtil.from(this, data.getData());
                    mUploadImage1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                } catch (Exception e) {
                    Log.e("error", "eroor: " + e.getMessage());
                    Toast.makeText(this, "Fallo al coger imagen " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Cancelaste seleccion de foto", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_INTENT_GALLERY2) {
            if (resultCode == RESULT_OK) {
                try {
                    mImageFile2 = FileUtil.from(this, data.getData());
                    mUploadImage2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
                } catch (Exception e) {
                    Log.e("error", "eroor: " + e.getMessage());
                    Toast.makeText(this, "Fallo al coger imagen 2" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Cancelaste seleccion de foto 2", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode==REQUEST_CODE_INTENT_TAKE_PHOTO){
            if(resultCode==RESULT_OK){
                Picasso.with(PostActivity.this).load(mPhotoPath).into(mUploadImage1);
            }else{
                Toast.makeText(this, "Fallo al traer la photo del intent camera 1", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode==REQUEST_CODE_INTENT_TAKE_PHOTO2){
            if(resultCode==RESULT_OK){
                Picasso.with(PostActivity.this).load(mPhotoPath).into(mUploadImage2);
            }else{
                Toast.makeText(this, "Fallo al traer la photo del intent camera 2", Toast.LENGTH_SHORT).show();
            }
        }
    }
}