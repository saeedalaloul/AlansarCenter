package com.alansar.center.Models;

public class Group {
    private String name;
    private String mohafezId;
    private String stage;
    private String GroupId;

    public Group() {
    }

    public Group(String name, String mohafezId, String stage, String groupId) {
        this.name = name;
        this.mohafezId = mohafezId;
        this.stage = stage;
        GroupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMohafezId() {
        return mohafezId;
    }

    public void setMohafezId(String mohafezId) {
        this.mohafezId = mohafezId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    @Override
    public String toString() {
        return name;
    }
}
