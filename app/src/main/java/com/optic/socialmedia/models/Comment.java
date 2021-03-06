package com.optic.socialmedia.models;

public class Comment {
    String id;
    String comment;
    String idUser;
    String idPost;
    long timestamp;

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Comment(String id, String comment, String idUser, String idPost, long timestap) {
        this.id = id;
        this.comment = comment;
        this.idUser = idUser;
        this.idPost = idPost;
        this.timestamp = timestap;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", comment='" + comment + '\'' +
                ", idUser='" + idUser + '\'' +
                ", idPost='" + idPost + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
