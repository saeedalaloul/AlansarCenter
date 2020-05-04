package com.alansar.center.Mohafez.Model;

public class Mohafez {
    private String stage;
    private String groupId;
    private String Id;
    private String Name;

    public Mohafez() {

    }

    public Mohafez(String stage, String groupId, String id, String name) {
        this.stage = stage;
        if (groupId == null) {
            this.groupId = "";
        } else {
            this.groupId = groupId;
        }
        Id = id;
        Name = name;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return Name;
    }
}
