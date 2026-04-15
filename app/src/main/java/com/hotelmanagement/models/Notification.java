package com.hotelmanagement.models;

public class Notification {
    private String title;
    private String time;
    private String iconEmoji;
    private boolean isRead;

    public Notification(String title, String time, String iconEmoji, boolean isRead) {
        this.title = title;
        this.time = time;
        this.iconEmoji = iconEmoji;
        this.isRead = isRead;
    }

    public String getTitle() { return title; }
    public String getTime() { return time; }
    public String getIconEmoji() { return iconEmoji; }
    public boolean isRead() { return isRead; }
}
