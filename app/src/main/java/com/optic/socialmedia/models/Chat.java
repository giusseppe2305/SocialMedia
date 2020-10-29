package com.optic.socialmedia.models;

import java.util.List;

public class Chat {
    private String idChat;
    private List<String> idsChats;
    private boolean isWritting;
    private long timestamp;
    private String idUserTo;
    private String idLastMessage;

    public String getIdLastMessage() {
        return idLastMessage;
    }

    public void setIdLastMessage(String idLastMessage) {
        this.idLastMessage = idLastMessage;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public List<String> getIdsChats() {
        return idsChats;
    }

    public void setIdsChats(List<String> idsChats) {
        this.idsChats = idsChats;
    }

    public Chat(String idChat, List<String> idsChats, boolean isWritting, long timestamp, String idUserTo, String idUserFrom) {
        this.idChat = idChat;
        this.idsChats = idsChats;
        this.isWritting = isWritting;
        this.timestamp = timestamp;
        this.idUserTo = idUserTo;
        this.idUserFrom = idUserFrom;
    }

    public String getIdUserTo() {
        return idUserTo;
    }

    public void setIdUserTo(String idUserTo) {
        this.idUserTo = idUserTo;
    }

    public String getIdUserFrom() {
        return idUserFrom;
    }

    public void setIdUserFrom(String idUserFrom) {
        this.idUserFrom = idUserFrom;
    }

    private String idUserFrom;
    public boolean isWritting() {
        return isWritting;
    }

    public void setWritting(boolean writting) {
        isWritting = writting;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Chat() {
    }
}
