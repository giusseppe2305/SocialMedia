package com.optic.socialmedia.models;

import java.io.File;

public class Photo {
    private File mFilePhotoSelected;
    private String mAbsolutePhotoPath;
    private int imageDrawable;
    private boolean isFromXMLDrawable;
    private String urlFirebase;
    private String previousAbsolutePath;
    private byte[] photoBytes;

    public void setPhotoBytes(byte[] photoBytes) {
        this.photoBytes = photoBytes;
    }

    public byte[] getPhotoBytes(){
        return photoBytes;
    }
    private boolean isNecessaryUploadPhotoToServer;

    public boolean isNecessaryUploadPhotoToServer() {
        return isNecessaryUploadPhotoToServer;
    }

    public void setIsNecessaryUploadPhotoToServer(boolean aNull) {
        isNecessaryUploadPhotoToServer = aNull;
        if(!aNull){
            isFromXMLDrawable=false;
            mAbsolutePhotoPath="";
        }
    }


    public Photo() {
        isNecessaryUploadPhotoToServer =false;
        isFromXMLDrawable=false;
        mAbsolutePhotoPath="";
        previousAbsolutePath=null;

    }

    public String getUrlFirebase() {
        return urlFirebase;
    }

    public void setUrlFirebase(String urlFirebase) {
        this.urlFirebase = urlFirebase;
    }

    public boolean isFromXMLDrawable() {
        return isFromXMLDrawable;
    }

    public void setFromXMLDrawable(boolean fromXMLDrawable) {
        isFromXMLDrawable = fromXMLDrawable;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(int imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public File getFilePhotoSelected() {
        return mFilePhotoSelected;
    }

    public void setFilePhotoSelected(File mFilePhotoSelected) {
        this.mFilePhotoSelected = mFilePhotoSelected;
        setAbsolutePhotoPath(mFilePhotoSelected.getAbsolutePath());
    }

    public String getAbsolutePhotoPath() {
        return mAbsolutePhotoPath;
    }

    public void setAbsolutePhotoPath(String mAbsolutePhotoPath) {
        this.mAbsolutePhotoPath = mAbsolutePhotoPath;
        photoBytes=null;
    }

    public void setAbsolutePhotoPathRecovered(String absolutePath) {
        previousAbsolutePath=mAbsolutePhotoPath;
        mAbsolutePhotoPath=absolutePath;
    }

    public String getBackupAbsolutePath() {
        return previousAbsolutePath;
    }


    public void useBuckup() {
        mAbsolutePhotoPath=previousAbsolutePath;
        previousAbsolutePath=null;
    }
}

