package com.alansar.center.testers.Model;

public class Tester {
    private String UID, Name, Stage;

    public Tester() {
    }

    public Tester(String UID, String name, String stage) {
        this.UID = UID;
        Name = name;
        this.Stage = stage;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStage() {
        return Stage;
    }

    public void setStage(String stage) {
        Stage = stage;
    }

    @Override
    public String toString() {
        return Name;
    }
}
