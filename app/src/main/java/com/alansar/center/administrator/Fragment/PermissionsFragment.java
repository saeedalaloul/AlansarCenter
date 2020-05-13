package com.alansar.center.administrator.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.alansar.center.R;
import com.alansar.center.administrator.Model.PermissionsUsers;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends Fragment {
    private MaterialCheckBox ch_moshref_add_mohafez, ch_moshref_update_mohafez, ch_moshref_disable_account,
            ch_moshref_add_halaka, ch_moshref_update_halaka, ch_moshref_add_student, ch_moshref_update_student,
            ch_edare_add_mohafez, ch_edare_update_mohafez, ch_edare_disable_account,
            ch_edare_add_halaka, ch_edare_update_halaka, ch_edare_add_student, ch_edare_update_student,
            ch_mohafez_add_student, ch_mohafez_update_student;
    private FirebaseFirestore db;

    public PermissionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_permissions, container, false);
        initialized(view);
        getData();

        ch_moshref_add_mohafez.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.addMohafez", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.addMohafez", false);
            }
        });

        ch_moshref_update_mohafez.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.updateMohafez", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.updateMohafez", false);
            }
        });

        ch_moshref_disable_account.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.disableAccountMohafez", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.disableAccountMohafez", false);
            }
        });

        ch_moshref_add_halaka.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.addHalaka", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.addHalaka", false);
            }
        });

        ch_moshref_update_halaka.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.updateHalaka", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.updateHalaka", false);
            }
        });

        ch_moshref_add_student.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.addStudent", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.addStudent", false);
            }
        });

        ch_moshref_update_student.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.updateStudent", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMoshref.updateStudent", false);
            }
        });

        ///////////////////

        ch_edare_add_mohafez.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.addMohafez", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.addMohafez", false);
            }
        });

        ch_edare_update_mohafez.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.updateMohafez", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.updateMohafez", false);
            }
        });

        ch_edare_disable_account.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.disableAccountMohafez", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.disableAccountMohafez", false);
            }
        });

        ch_edare_add_halaka.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.addHalaka", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.addHalaka", false);
            }
        });

        ch_edare_update_halaka.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.updateHalaka", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.updateHalaka", false);
            }
        });

        ch_edare_add_student.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.addStudent", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.addStudent", false);
            }
        });

        ch_edare_update_student.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.updateStudent", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsEdare.updateStudent", false);
            }
        });

        ////////////////////////////

        ch_mohafez_add_student.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMohafez.addStudent", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMohafez.addStudent", false);
            }
        });

        ch_mohafez_update_student.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMohafez.updateStudent", true);
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").update("permissionsMohafez.updateStudent", false);
            }
        });

        return view;
    }

    private void getData() {
        db.collection("PermissionsUsers").document("permissionsUsers")
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                setDataOfFailds(documentSnapshot.toObject(PermissionsUsers.class));
            } else {
                db.collection("PermissionsUsers").document("permissionsUsers").set(new PermissionsUsers());
            }
        });
    }

    private void setDataOfFailds(PermissionsUsers permissionsUsers) {
        if (permissionsUsers != null
                && permissionsUsers.getPermissionsMoshref() != null
                && !permissionsUsers.getPermissionsMoshref().isEmpty()
                && permissionsUsers.getPermissionsEdare() != null
                && !permissionsUsers.getPermissionsEdare().isEmpty()
                && permissionsUsers.getPermissionsMohafez() != null
                && !permissionsUsers.getPermissionsMohafez().isEmpty())
        {
            if (permissionsUsers.getPermissionsMoshref().get("addMohafez"))
            {
                ch_moshref_add_mohafez.setChecked(true);
            }else
            {
                ch_moshref_add_mohafez.setChecked(false);
            }

            if (permissionsUsers.getPermissionsMoshref().get("updateMohafez"))
            {
                ch_moshref_update_mohafez.setChecked(true);
            }else
            {
                ch_moshref_update_mohafez.setChecked(false);
            }

            if (permissionsUsers.getPermissionsMoshref().get("disableAccountMohafez"))
            {
                ch_moshref_disable_account.setChecked(true);
            }else
            {
                ch_moshref_disable_account.setChecked(false);
            }

            if (permissionsUsers.getPermissionsMoshref().get("addHalaka"))
            {
                ch_moshref_add_halaka.setChecked(true);
            }else
            {
                ch_moshref_add_halaka.setChecked(false);
            }

            if (permissionsUsers.getPermissionsMoshref().get("updateHalaka"))
            {
                ch_moshref_update_halaka.setChecked(true);
            }else
            {
                ch_moshref_update_halaka.setChecked(false);
            }

            if (permissionsUsers.getPermissionsMoshref().get("addStudent"))
            {
                ch_moshref_add_student.setChecked(true);
            }else
            {
                ch_moshref_add_student.setChecked(false);
            }

            if (permissionsUsers.getPermissionsMoshref().get("updateStudent"))
            {
                ch_moshref_update_student.setChecked(true);
            }else
            {
                ch_moshref_update_student.setChecked(false);
            }

            ///////////////////////////////////////

            if (permissionsUsers.getPermissionsEdare().get("addMohafez"))
            {
                ch_edare_add_mohafez.setChecked(true);
            }else
            {
                ch_edare_add_mohafez.setChecked(false);
            }

            if (permissionsUsers.getPermissionsEdare().get("updateMohafez"))
            {
                ch_edare_update_mohafez.setChecked(true);
            }else
            {
                ch_edare_update_mohafez.setChecked(false);
            }

            if (permissionsUsers.getPermissionsEdare().get("disableAccountMohafez"))
            {
                ch_edare_disable_account.setChecked(true);
            }else
            {
                ch_edare_disable_account.setChecked(false);
            }

            if (permissionsUsers.getPermissionsEdare().get("addHalaka"))
            {
                ch_edare_add_halaka.setChecked(true);
            }else
            {
                ch_edare_add_halaka.setChecked(false);
            }

            if (permissionsUsers.getPermissionsEdare().get("updateHalaka"))
            {
                ch_edare_update_halaka.setChecked(true);
            }else
            {
                ch_edare_update_halaka.setChecked(false);
            }

            if (permissionsUsers.getPermissionsEdare().get("addStudent"))
            {
                ch_edare_add_student.setChecked(true);
            }else
            {
                ch_edare_add_student.setChecked(false);
            }

            if (permissionsUsers.getPermissionsEdare().get("updateStudent"))
            {
                ch_edare_update_student.setChecked(true);
            }else
            {
                ch_edare_update_student.setChecked(false);
            }

            //////////////////////////////////

            if (permissionsUsers.getPermissionsMohafez().get("addStudent"))
            {
                ch_mohafez_add_student.setChecked(true);
            }else
            {
                ch_mohafez_add_student.setChecked(false);
            }

            if (permissionsUsers.getPermissionsMohafez().get("updateStudent"))
            {
                ch_mohafez_update_student.setChecked(true);
            }else
            {
                ch_mohafez_update_student.setChecked(false);
            }

        }
    }

    private void initialized(View view) {
        ch_moshref_add_mohafez = view.findViewById(R.id.checkbox_admin_permissions_moshref_add_mohafez);
        ch_moshref_update_mohafez = view.findViewById(R.id.checkbox_admin_permissions_moshref_update_mohafez);
        ch_moshref_disable_account = view.findViewById(R.id.checkbox_admin_permissions_moshref_disable_account);
        ch_moshref_add_halaka = view.findViewById(R.id.checkbox_admin_permissions_moshref_add_halaka);
        ch_moshref_update_halaka = view.findViewById(R.id.checkbox_admin_permissions_moshref_update_halaka);
        ch_moshref_add_student = view.findViewById(R.id.checkbox_admin_permissions_moshref_add_student);
        ch_moshref_update_student = view.findViewById(R.id.checkbox_admin_permissions_moshref_update_student);

        ch_edare_add_mohafez = view.findViewById(R.id.checkbox_admin_permissions_edare_add_mohafez);
        ch_edare_update_mohafez = view.findViewById(R.id.checkbox_admin_permissions_edare_update_mohafez);
        ch_edare_disable_account = view.findViewById(R.id.checkbox_admin_permissions_edare_disable_account);
        ch_edare_add_halaka = view.findViewById(R.id.checkbox_admin_permissions_edare_add_halaka);
        ch_edare_update_halaka = view.findViewById(R.id.checkbox_admin_permissions_edare_update_halaka);
        ch_edare_add_student = view.findViewById(R.id.checkbox_admin_permissions_edare_add_student);
        ch_edare_update_student = view.findViewById(R.id.checkbox_admin_permissions_edare_update_student);

        ch_mohafez_add_student = view.findViewById(R.id.checkbox_admin_permissions_mohafez_add_student);
        ch_mohafez_update_student = view.findViewById(R.id.checkbox_admin_permissions_mohafez_update_student);

        db = FirebaseFirestore.getInstance();
    }

}
