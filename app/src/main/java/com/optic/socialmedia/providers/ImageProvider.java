package com.optic.socialmedia.providers;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

    public UploadTask save(Context c, File file){
        if(isFirstUse){
            isFirstUse=false;
        }else{
            this.mStorage=this.mStorage.getParent();
        }
        byte[]imageByte= CompressorBitmapImage.getImage(c,file.getPath(),500,500);
        StorageReference storage=mStorage.child(new Date()+".jpg"   );
        mStorage=storage;
        UploadTask task=storage.putBytes(imageByte);
        return task;
    }

    public StorageReference getStorage(){
        return mStorage;
    }
}
