package com.example.appmovil;

public class Notification {
    private String title;
    private String message;

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

}
