package com.alansar.center.students.Model;

public class Student {
    private String id;
    private String name;
    private String groupId;
    private String stage;

    public Student() {
    }

    public Student(String id, String name, String groupId, String stage) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.stage = stage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}

