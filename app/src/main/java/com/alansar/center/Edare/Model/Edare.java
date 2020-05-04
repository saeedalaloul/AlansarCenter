package com.alansar.center.Edare.Model;

public class Edare {
    private String stage;
    private String Id;
    private String Name;

    public Edare() {

    }

    public Edare(String stage, String id, String name) {
        this.stage = stage;
        Id = id;
        Name = name;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
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
}
