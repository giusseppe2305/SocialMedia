package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.optic.socialmedia.R;
import com.optic.socialmedia.models.Photo;
import com.optic.socialmedia.models.Post;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.ImageProvider;
import com.optic.socialmedia.providers.PostDatabaseProvider;
import com.optic.socialmedia.recyclerview.MainAdapter;
import com.optic.socialmedia.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_INTENT_GALLERY = 1;
    private static final int REQUEST_CODE_INTENT_TAKE_PHOTO = 2;
    ImageView ivPS4, ivPC, ivXBOX;

    Button btnPublish;
    ImageProvider mImageProvider;
    TextInputEditText txtTitleGame;
    TextInputEditText txtDescription;
    TextView txtCategorySelected;
    ImageView btnAddImage;
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

    RecyclerView mRecycler;
    private MainAdapter adapterImagesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider = new ImageProvider();
        mPostProvider = new PostDatabaseProvider();
        mAuth = new AuthProviders();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Subiendo imagen.")
                .setCancelable(false).build();
        mDialogSelectFromPicture = new MaterialAlertDialogBuilder(this).
                setTitle("Elige una opcion")
                .setItems(new String[]{"Elegir de la galeria", "Tomar FOTO AHORA"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            openGallery();
                        } else {

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

        btnBack = findViewById(R.id.btnBack);
        btnAddImage = findViewById(R.id.btnAddImagePost);
        btnPublish = findViewById(R.id.btnPublishPost);
        ivPC = findViewById(R.id.ivPCPost);
        ivPS4 = findViewById(R.id.ivPS4Post);
        ivXBOX = findViewById(R.id.ivXBOXPost);
        txtTitleGame = findViewById(R.id.titleGamePost);
        txtDescription = findViewById(R.id.descripcionPost);
        txtCategorySelected = findViewById(R.id.categorySelectedPost);
        mRecycler = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(20);
        mRecycler.setDrawingCacheEnabled(true);
        mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        adapterImagesRecycler = new MainAdapter(this);
        mRecycler.setAdapter(adapterImagesRecycler);


        ivPC.setOnClickListener(eventImagenViews);
        ivPS4.setOnClickListener(eventImagenViews);
        ivXBOX.setOnClickListener(eventImagenViews);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogSelectFromPicture.show();
            }
        });
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });

    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile();
            } catch (Exception e) {
                Toast.makeText(PostActivity.this, "Hubo un error con el archivo", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Photo obj = new Photo();
                obj.setAbsolutePhotoPath(photoFile.getAbsolutePath());
                adapterImagesRecycler.getList().add(obj);

                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.optic.socialmedia", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_CODE_INTENT_TAKE_PHOTO);


            }
        }
    }

    private File createPhotoFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );

        return photoFile;
    }

    private void clickPost() {
        if (adapterImagesRecycler.getList().isEmpty()) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
        } else {
            mDialog.show();
            subirImagenes(0);
        }
    }

    private void subirImagenes(final int posicion) {
        if (posicion < adapterImagesRecycler.getItemCount()) {
            final Photo objActual = adapterImagesRecycler.getList().get(posicion);
            final int posicionActual = posicion;
            System.out.println("iteraccion subir " + posicionActual);

            mImageProvider.save(PostActivity.this, objActual).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    System.out.println("iteraccion subir SUBIDOOOOOOO " + posicionActual);
                    mDialog.setMessage("Subiendo imagen " + posicionActual + "/" + adapterImagesRecycler.getItemCount() + ".");
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.println("iteraccion subir " + posicionActual + " obtenido url: " + uri.toString());
                            objActual.setUrlFirebase(uri.toString());
                            subirImagenes(posicion + 1);
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "Fallo loa subida del archivo " + posicion, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            System.out.println("Termino la recursividad");
            mDialog.setMessage("ultimando detalles");
            savePostDatabase();


        }


    }

    private void savePostDatabase() {
        final String title = txtTitleGame.getText().toString();
        final String description = txtDescription.getText().toString();
        final String category = txtCategorySelected.getText().toString();
        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Post mPost = new Post();
                mPost.setTitle(title);
                mPost.setDescription(description);
                mPost.setCategory(category);
                mPost.setIdUser(mAuth.getIdCurrentUser());
                mPost.setImages(getListUrlsImagesPost());
                mPost.setTimestamp(new Date().getTime());
                mPostProvider.save(mPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finish();
                            Toast.makeText(PostActivity.this, "Se subio el post correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PostActivity.this, "Fallo al subir el post", Toast.LENGTH_SHORT).show();
                            //borrarImagenes la imagen
                          //  mImageProvider.getStorage().delete();
                        } mDialog.dismiss();
                    }
                });
            }
        });
    }

    private List<String> getListUrlsImagesPost() {
        ArrayList<String> dev=new ArrayList<>();
        for(Photo it:adapterImagesRecycler.getList())
        {
            dev.add(it.getUrlFirebase());
        }
        return dev;
    }


    private void openGallery() {
        Intent intentGallery = new Intent();
        intentGallery.setType("image/*");
        intentGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentGallery, "Titulooooo guay"), REQUEST_CODE_INTENT_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_INTENT_GALLERY) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    System.out.println("----multiple----");
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        System.out.println(imageUri.getPath());

                        try {
                            Photo p = new Photo();

                            p.setFilePhotoSelected(FileUtil.from(this, data.getClipData().getItemAt(i).getUri()));
                            adapterImagesRecycler.getList().add(p);
                        } catch (Exception e) {
                            Log.e("error", "eroor: " + e.getMessage());
                            Toast.makeText(this, "Fallo al coger imagen sola " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    adapterImagesRecycler.notifyItemRangeInserted(adapterImagesRecycler.getItemCount() - count - 1, adapterImagesRecycler.getItemCount() - 1);
                } else if (data.getData() != null) {
                    String imagePath = data.getData().getPath();
                    System.out.println("solo----");
                    System.out.println(imagePath);
                    try {
                        Toast.makeText(this, "Cogio imagen sola bien", Toast.LENGTH_LONG).show();
                        //mImageFile = FileUtil.from(this, data.getData());
                        Photo p = new Photo();

                        p.setFilePhotoSelected(FileUtil.from(this, data.getData()));

                        adapterImagesRecycler.getList().add(p);
                        adapterImagesRecycler.notifyItemInserted(adapterImagesRecycler.getItemCount() - 1);


                    } catch (Exception e) {
                        Log.e("error", "eroor: " + e.getMessage());
                        Toast.makeText(this, "Fallo al coger imagen sola " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

        } else if (requestCode == REQUEST_CODE_INTENT_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                adapterImagesRecycler.getLastItem().setFilePhotoSelected(new File(adapterImagesRecycler.getLastItem().getAbsolutePhotoPath()));
                adapterImagesRecycler.notifyItemInserted(adapterImagesRecycler.getItemCount() - 1);
            } else {
                Toast.makeText(this, "Fallo al traer la photo del intent camera 1", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
