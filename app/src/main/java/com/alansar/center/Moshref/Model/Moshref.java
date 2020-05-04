package com.alansar.center.Moshref.Model;

public class Moshref {
    private String stage;
    private String Id;
    private String Name;

    public Moshref() {
    }

    public Moshref(String stage, String id, String name) {
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
