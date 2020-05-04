package com.alansar.center.Models;

import java.util.ArrayList;

public class GroupMembers {
    private String idGroup;
    private ArrayList<String> groupMembers;

    public GroupMembers() {
    }

    public GroupMembers(String idGroup, ArrayList<String> groupMembers) {
        this.idGroup = idGroup;
        this.groupMembers = groupMembers;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<String> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
