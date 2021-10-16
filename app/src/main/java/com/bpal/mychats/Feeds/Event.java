package com.bpal.mychats.Feeds;

public class Event {
    private String channel;
    private String event;
    private String data;

    public  Event(){}

    public Event(String channel, String event, String data) {
        this.channel = channel;
        this.event = event;
        this.data = data;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
