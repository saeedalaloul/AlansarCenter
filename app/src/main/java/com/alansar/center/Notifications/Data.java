package com.alansar.center.Notifications;

public class Data {
    private String user, body, title, sent, typeActivity, typeFragment;

    public Data(String user, String body, String title, String sent, String typeActivity, String typeFragment) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.typeActivity = typeActivity;
        this.typeFragment = typeFragment;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getTypeActivity() {
        return typeActivity;
    }

    public void setTypeActivity(String typeActivity) {
        this.typeActivity = typeActivity;
    }

    public String getTypeFragment() {
        return typeFragment;
    }

    public void setTypeFragment(String typeFragment) {
        this.typeFragment = typeFragment;
    }
}
