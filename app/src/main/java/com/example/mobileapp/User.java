package com.example.mobileapp;

public class User {

    private String email;
    private String name;
    private String lastMessage;
    private long lastMessageTimestamp;
    private String avatarId;

    public User() {} // required for Firestore

    public User(String email, String name, String lastMessage, long lastMessageTimestamp) {
        this.email = email;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarId() { return avatarId; }
    public void setAvatarId(String avatarId) { this.avatarId = avatarId; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name != null ? name : email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}
