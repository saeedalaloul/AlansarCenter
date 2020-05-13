package com.alansar.center.administrator.Model;

import java.util.HashMap;

public class PermissionsUsers {
    private HashMap<String, Boolean> permissionsMoshref, permissionsMohafez, permissionsEdare;


    public PermissionsUsers() {
        permissionsMoshref = new HashMap<>();
        permissionsEdare = new HashMap<>();
        permissionsMohafez = new HashMap<>();

        this.permissionsMoshref.put("addMohafez", false);
        this.permissionsMoshref.put("updateMohafez", false);
        this.permissionsMoshref.put("disableAccountMohafez", false);
        this.permissionsMoshref.put("addHalaka", false);
        this.permissionsMoshref.put("updateHalaka", false);
        this.permissionsMoshref.put("addStudent", false);
        this.permissionsMoshref.put("updateStudent", false);

        this.permissionsEdare.put("addMohafez", false);
        this.permissionsEdare.put("updateMohafez", false);
        this.permissionsEdare.put("disableAccountMohafez", false);
        this.permissionsEdare.put("addHalaka", false);
        this.permissionsEdare.put("updateHalaka", false);
        this.permissionsEdare.put("addStudent", false);
        this.permissionsEdare.put("updateStudent", false);

        this.permissionsMohafez.put("addStudent", false);
        this.permissionsMohafez.put("updateStudent", false);

    }

    public HashMap<String, Boolean> getPermissionsMoshref() {
        return permissionsMoshref;
    }

    public void setPermissionsMoshref(HashMap<String, Boolean> permissionsMoshref) {
        this.permissionsMoshref = permissionsMoshref;
    }

    public HashMap<String, Boolean> getPermissionsMohafez() {
        return permissionsMohafez;
    }

    public void setPermissionsMohafez(HashMap<String, Boolean> permissionsMohafez) {
        this.permissionsMohafez = permissionsMohafez;
    }

    public HashMap<String, Boolean> getPermissionsEdare() {
        return permissionsEdare;
    }

    public void setPermissionsEdare(HashMap<String, Boolean> permissionsEdare) {
        this.permissionsEdare = permissionsEdare;
    }
}
