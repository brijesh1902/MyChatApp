package com.bpal.mychats.Services;

public class Notification {

    public String body, title;

    public Notification() {

    }

    public Notification(String title, String body) {
        this.body = body;
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
