package com.example.blacksuits.Notifications;

public class Room {
    private String receiver;
    private String sender;

    private String message;

    // Constructor
    public Room(String receiver, String sender, String message) {
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;

        System.out.println(this.sender + this.receiver);
    }

    public Room() {

    }

    // Getters and setters
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "receiver='" + receiver + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
