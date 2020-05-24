package com.alansar.center.Models;

import java.util.ArrayList;

public class Person {
    private String id;
    private String fname;
    private String mname;
    private String lname;
    private String phone;
    private String dob;
    private String image;
    private ArrayList<String> permissions;
    private String identificationNumber;
    private boolean isEnableAccount;

    public Person() {
    }

    public Person(String fname, String mname, String lname, String phone, String dob, String image, ArrayList<String> permissions, String identificationNumber, String id) {
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.phone = phone;
        this.dob = dob;
        this.image = image;
        this.permissions = permissions;
        this.identificationNumber = identificationNumber;
        this.id = id;
        this.isEnableAccount = true;
    }


    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnableAccount() {
        return isEnableAccount;
    }

    public void setEnableAccount(boolean enableAccount) {
        isEnableAccount = enableAccount;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }
}
