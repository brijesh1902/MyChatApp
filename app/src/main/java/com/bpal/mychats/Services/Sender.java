package com.bpal.mychats.Services;

public class Sender {
    public String to;
    public Notifications notifications;

    public Sender() {
    }

    public Sender(String to, Notifications notifications) {
        this.to = to;
        this.notifications = notifications;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notifications getNotification() {
        return notifications;
    }

    public void setNotification(Notifications notifications) {
        this.notifications = notifications;
    }
}
