package com.optic.socialmedia.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.optic.socialmedia.models.Photo;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Intents {

    public static void takePhoto(Activity parent,Photo mPhoto,int REQUEST_CODE) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(parent.getPackageManager()) != null) {
            System.out.println("ENTRO A LA CAMARAAAAA");
            File photoFile = null;
            try {
                photoFile = createPhotoFile(parent);
            } catch (Exception e) {
                Toast.makeText(parent, "Hubo un error con el archivo", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                mPhoto.setIsNecessaryUploadPhotoToServer(true);
                System.out.println("instancion de nuevo la current photo");
                mPhoto.setAbsolutePhotoPathRecovered(photoFile.getAbsolutePath());
                Uri photoUri = FileProvider.getUriForFile(parent, "com.optic.socialmedia", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                parent.startActivityForResult(takePictureIntent, REQUEST_CODE);


            }

        }else {
            System.out.println("OTRA OPCIONNASDNASJDAS");
        }
    }

    private static File createPhotoFile(Activity parent) throws IOException {
        File storageDir = parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );

        return photoFile;
    }

    public static void openGallery(Activity parent,int REQUEST_CODE) {
        Intent intentGallery = new Intent();
        intentGallery.setType("image/*");
        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
        parent.startActivityForResult(Intent.createChooser(intentGallery, "Titulooooo guay"), REQUEST_CODE);
    }
}
