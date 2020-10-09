package com.optic.socialmedia.providers;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.optic.socialmedia.models.Photo;
import com.optic.socialmedia.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class ImageProvider {
    StorageReference mStorage;
    boolean isFirstUse;
    public ImageProvider() {
        mStorage= FirebaseStorage.getInstance().getReference();
        isFirstUse=true;
    }

    public UploadTask save(Context c, Photo mPhoto){
        if(isFirstUse){
            isFirstUse=false;
        }else{
            this.mStorage=this.mStorage.getParent();
        }
        byte[]imageByte;
        String nombre;
        if(mPhoto.getPhotoBytes()==null){
            File file=mPhoto.getFilePhotoSelected();
            imageByte= CompressorBitmapImage.getImage(c,file.getPath(),500,500);
            nombre=file.getName();
        }else{
            imageByte=mPhoto.getPhotoBytes();
            nombre="google";
        }


        StorageReference storage=mStorage.child(nombre+"_"+new Date()+".jpg"   );
        mStorage=storage;
        UploadTask task=storage.putBytes(imageByte);
        return task;
    }

    public StorageReference getStorage(){
        return mStorage;
    }
}
