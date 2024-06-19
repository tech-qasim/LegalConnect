package com.example.blacksuits.Notifications;

public class TokenSingletonClass {
    private static TokenSingletonClass instance;
    private String receiverUid;
    private String senderUid;

    private TokenSingletonClass() {}

    public static TokenSingletonClass getInstance() {
        if (instance == null) {
            instance = new TokenSingletonClass();
        }
        return instance;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
}
