package com.example.mobileapp;

import java.util.Date;

public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String senderName;
    private String senderAvatarId;
    private String message;
    private long timestamp;

    public ChatMessage() {} // required by Firestore

    public ChatMessage(String senderId, String receiverId,
                       String senderName, String senderAvatarId,
                       String message, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderName = senderName;
        this.senderAvatarId = senderAvatarId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // getters and setters for all fields
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatarId() { return senderAvatarId; }
    public void setSenderAvatarId(String senderAvatarId) { this.senderAvatarId = senderAvatarId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
