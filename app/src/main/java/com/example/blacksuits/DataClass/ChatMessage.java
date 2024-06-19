package com.example.blacksuits.DataClass;

import java.util.Date;

public class ChatMessage {
    private String text;

    private String senderUsername;

    private String receiverUsername;
    private String receiverId;
    private String senderId;
    private String timestamp;



    private String currentUserType;

    public ChatMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatMessage.class)
    }

    public ChatMessage(String text, String senderUsername, String receiverUsername, String receiverId, String senderId, String timestamp) {
        this.text = text;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;

    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }



    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername){
        this.receiverUsername = receiverUsername;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}