package com.optic.socialmedia.models;

public class Like {
    long timestamp;
    String idUser;
    String idPost;
    String id;

    public Like() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Like(long timestamp, String idUser, String idPost) {
        this.timestamp = timestamp;
        this.idUser = idUser;
        this.idPost = idPost;
    }
}
