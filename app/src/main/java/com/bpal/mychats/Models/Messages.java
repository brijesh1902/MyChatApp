package com.bpal.mychats.Models;

public class Messages {

    private String message, time, receiverID, senderID, id;

    public Messages(){}

    public Messages(String message, String time, String receiverID, String senderID, String id) {
        this.message = message;
        this.time = time;
        this.receiverID = receiverID;
        this.senderID = senderID;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
