package com.alansar.center.Models;

public class AccountItem {
    private String image, permission, name;

    public AccountItem() {
    }

    public AccountItem(String image, String permission, String name) {
        this.image = image;
        this.permission = permission;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
