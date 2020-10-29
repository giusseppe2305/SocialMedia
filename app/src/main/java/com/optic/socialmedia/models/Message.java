package com.optic.socialmedia.models;

public class Message {
    String id;
    String idUserTo;
    String idsUserFrom;
    String idChat;
    String message;
    long timestamp;
    boolean viewed;

    public Message() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUserTo() {
        return idUserTo;
    }

    public void setIdUserTo(String idUserTo) {
        this.idUserTo = idUserTo;
    }

    public String getIdsUserFrom() {
        return idsUserFrom;
    }

    public void setIdsUserFrom(String idsUserFrom) {
        this.idsUserFrom = idsUserFrom;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public Message(String id, String idUserTo, String idsUserFrom, String idChat, String message, long timestamp, boolean viewed) {
        this.id = id;
        this.idUserTo = idUserTo;
        this.idsUserFrom = idsUserFrom;
        this.idChat = idChat;
        this.message = message;
        this.timestamp = timestamp;
        this.viewed = viewed;
    }
}
