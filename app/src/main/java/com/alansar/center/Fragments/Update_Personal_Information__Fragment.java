package com.alansar.center.Fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.alansar.center.Common.Common;
import com.alansar.center.Edare.Model.Edare;
import com.alansar.center.FButton;
import com.alansar.center.Models.Group;
import com.alansar.center.Models.GroupMembers;
import com.alansar.center.Models.Person;
import com.alansar.center.Mohafez.Model.Mohafez;
import com.alansar.center.Moshref.Model.Moshref;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.students.Model.Student;
import com.alansar.center.supervisor_exams.Model.SuperVisorExams;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Update_Personal_Information__Fragment extends Fragment {

    private MaterialEditText edtDOB, edtFName, edtMName, edtLName, edtPhone, edtIdentificationNumber;
    private DatePickerDialog.OnDateSetListener setListener;
    private String UID;
    private int year;
    private int month;
    private int day;
    private CircleImageView img_profile;
    private Uri imgUri;
    private FButton btn_add, btn_select_permissions;
    private FirebaseFirestore db;
    private Person person;
    private ArrayList<String> ListPermissions;
    private SearchableSpinner sp_stage, sp_group;
    private String newstage;
    private String retStage = "";
    private List<String> stage_list;
    private List<String> groups_Names;
    private String ImageUrl;
    private StorageReference storageReference;
    private String permissions;
    private SweetAlertDialog_ sweetAlertDialog;
    private String groupId;
    private ArrayList<Group> groups;
    private ArrayAdapter<Group> adapter;
    private String retGroupId;
    private String retGroupName;


    public Update_Personal_Information__Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update__personal__information__, container, false);
        initialized(view);
        showDialogPickerDate();
        if (this.getArguments() != null && this.getArguments().getString("UID") != null
                && getArguments().getString("permissions") != null) {
            UID = this.getArguments().getString("UID");
            permissions = getArguments().getString("permissions");
            btn_add.setOnClickListener(view1 -> {
                if (validateInputs() && validateSpinner()) {
                    update_Person_To_Database(Objects.requireNonNull(edtFName.getText()).toString(),
                            Objects.requireNonNull(edtMName.getText()).toString(),
                            Objects.requireNonNull(edtLName.getText()).toString(),
                            Objects.requireNonNull(edtPhone.getText()).toString(),
                            Objects.requireNonNull(edtDOB.getText()).toString(),
                            Objects.requireNonNull(edtIdentificationNumber.getText()).toString());
                }
            });

            img_profile.setOnClickListener(view12 -> chooseImage());
            btn_select_permissions.setOnClickListener(view13 -> showDialogSelectPermissions());
            getPersonData();
        } else {
            Objects.requireNonNull(getActivity()).finish();
            Toast.makeText(getContext(), "No permission was found to be able to update the data. See Permissions for users", Toast.LENGTH_SHORT).show();
        }

        if (permissions.equals(Common.PERMISSIONS_STUDENTN)) {
            view.findViewById(R.id.liner_layout_sp_groups).setVisibility(View.VISIBLE);
            view.findViewById(R.id.liner_layout_sp_stage).setVisibility(View.GONE);
        }

        sp_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!groups.isEmpty()) {
                    groupId = groups.get(i).getGroupId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (Common.currentPerson != null && Common.currentSTAGE != null) {
            getHalakasfromDatebase();

            if (Common.currentPermission.equals(Common.PERMISSIONS_SUPER_VISOR) ||
                    Common.currentPermission.equals(Common.PERMISSIONS_EDARE)) {
                view.findViewById(R.id.liner_layout_sp_stage).setVisibility(View.GONE);
            }
        }
        return view;
    }


    private void getHalakasfromDatebase() {
        if (getActivity() != null) {
            db.collection("Group")
                    .whereEqualTo("stage", Common.currentSTAGE)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    groups.clear();
                    groups_Names.clear();
                    groups.add(new Group("اختر الحلقة", "", "", ""));
                    groups_Names.add("اختر الحلقة");
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        groups_Names.add(queryDocumentSnapshots.toObjects(Group.class).get(i).getName());
                    }

                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        groups.add(queryDocumentSnapshots.toObjects(Group.class).get(i));
                        if (queryDocumentSnapshots.getDocuments().get(i).getId().equals(retGroupId)) {
                            retGroupName = queryDocumentSnapshots.getDocuments().get(i).getString("name");
                        }
                    }
                    adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, groups);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_group.setAdapter(adapter);
                    sp_group.setSelection(groups_Names.indexOf(retGroupName));
                }
            });
        }
    }

    private void updatepermissions(ArrayList<String> listPermissions) {
        HashMap<String, Object> map = new HashMap<>();
        String fullNameUpdate = Objects.requireNonNull(edtFName.getText()).toString() + " " +
                Objects.requireNonNull(edtMName.getText()).toString() + " " +
                Objects.requireNonNull(edtLName.getText()).toString();
        String retFullName = person.getFname() + " " + person.getMname() + " " + person.getLname();
        if (!retFullName.equals(fullNameUpdate)) {
            map.put("name", fullNameUpdate);
        }

        if (retStage != null && newstage != null && !retStage.equals(newstage)) {
            updateStageofDB(newstage);
        }

        if (groupId != null && retGroupId != null && !groupId.equals(retGroupId)) {
            map.put("groupId", groupId);
            updateGroupMember(groupId, retGroupId);
        }

        for (int i = 0; i < listPermissions.size(); i++) {
            if (listPermissions.get(i).equals(Common.PERMISSIONS_EDARE)) {
                if (!map.isEmpty()) {
                    db.collection("Edare").document(UID).update(map);
                }
            }

            if (listPermissions.get(i).equals(Common.PERMISSIONS_SUPER_VISOR)) {
                if (!map.isEmpty()) {
                    db.collection("Moshref").document(UID).update(map);
                }
            }
            if (listPermissions.get(i).equals(Common.PERMISSIONS_MOHAFEZ)) {
                if (!map.isEmpty()) {
                    db.collection("Mohafez").document(UID).update(map);
                }
            }
            if (listPermissions.get(i).equals(Common.PERMISSIONS_STUDENTN)) {
                if (!map.isEmpty()) {
                    db.collection("Student").document(UID).update(map);
                }
            }
            if (listPermissions.get(i).equals(Common.PERMISSIONS_SUPER_VISOR_EXAMS)) {
                if (!map.isEmpty()) {
                    db.collection("SuperVisorExams").document(UID).update(map);
                }
            }
        }
    }

    private void updateGroupMember(String groupId, String retGroupId) {
        if (groupId != null && retGroupId != null) {
            db.collection("GroupMembers").document(groupId).get().addOnSuccessListener(documentSnapshot1 -> {
                assert documentSnapshot1 != null;
                if (documentSnapshot1.exists()) {
                    db.collection("GroupMembers").document(groupId).update("groupMembers", FieldValue.arrayUnion(UID));
                } else {
                    ArrayList<String> strings = new ArrayList<>();
                    strings.add(UID);
                    db.collection("GroupMembers").document(groupId).set(new GroupMembers(groupId, strings));
                }
                db.collection("GroupMembers").document(retGroupId).update("groupMembers", FieldValue.arrayRemove(UID));
            });
        }
    }

    private void updateStageofDB(String updateStage) {
        switch (permissions) {
            case Common.PERMISSIONS_MOHAFEZ:
                db.collection("Mohafez").document(UID).update("stage", updateStage);
                break;
            case Common.PERMISSIONS_EDARE:
                db.collection("Edare").document(UID).update("stage", updateStage);
                break;
            case Common.PERMISSIONS_SUPER_VISOR:
                db.collection("Moshref").document(UID).update("stage", updateStage);
                break;
            case Common.PERMISSIONS_STUDENTN:
                db.collection("Student").document(UID).update("stage", updateStage);
                break;
        }
    }

    private void update_Person_To_Database(String FName, String MName, String LName, String Phone, String DOB, String IdentificationNumber) {
        if (person.getFname().equals(FName)
                && person.getMname().equals(MName)
                && person.getLname().equals(LName)) {
            if (person.getPhone().equals(Phone)) {
                setUpdateData(FName, MName, LName, Phone, DOB, IdentificationNumber);
            } else {
                validatePhoneFromDB(Phone, FName, MName, LName, DOB, IdentificationNumber);
            }
        } else {
            db.collection("Person")
                    .whereEqualTo("fname", FName)
                    .whereEqualTo("mname", MName)
                    .whereEqualTo("lname", LName)
                    .limit(1)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    sweetAlertDialog.showDialogError("الإسم المدخل موجود مسبقا ..");
                } else {
                    if (person.getPhone().equals(Phone)) {
                        setUpdateData(FName, MName, LName, Phone, DOB, IdentificationNumber);
                    } else {
                        validatePhoneFromDB(Phone, FName, MName, LName, DOB, IdentificationNumber);
                    }
                }
            });
        }
    }

    private void validatePhoneFromDB(String Phone, String FName, String MName, String LName, String DOB, String IdentificationNumber) {
        db.collection("Person")
                .whereEqualTo("phone", Phone)
                .limit(1)
                .get().addOnSuccessListener(queryDocumentSnapshots1 -> {
            if (queryDocumentSnapshots1 != null && !queryDocumentSnapshots1.isEmpty()) {
                sweetAlertDialog.showDialogError("رقم الهاتف المدخل موجود مسبقا ..");
            } else {
                setUpdateData(FName, MName, LName, Phone, DOB, IdentificationNumber);
            }
        });
    }

    private void setUpdateData(String FName, String MName, String LName, String Phone, String DOB, String IdentificationNumber) {
        newstage = sp_stage.getSelectedItem().toString();
        if (imgUri != null) {
            Person personUpdate = new Person(FName, MName, LName, Phone, DOB, "", ListPermissions, IdentificationNumber, UID);
            updatePerson(personUpdate);
            uploadImage(UID);
        } else {
            Person personUpdate = new Person(FName, MName, LName, Phone, DOB, ImageUrl, ListPermissions, IdentificationNumber, UID);
            updatePerson(personUpdate);
            sweetAlertDialog.showDialogSuccess("OK", "تم تحديث البيانات بنجاح !")
                    .setConfirmButton("OK", sweetAlertDialog1 -> {
                        clearInputs();
                        sweetAlertDialog1.dismissWithAnimation();
                        sweetAlertDialog.cancelDialog();
                        Objects.requireNonNull(getActivity()).finish();
                    });
        }
    }


    private void updatePerson(Person personUpdate) {
        HashMap<String, Object> personHashMap = new HashMap<>();
        if (!personUpdate.getFname().equals(person.getFname())) {
            personHashMap.put("fname", personUpdate.getFname());
        } else if (!personUpdate.getMname().equals(person.getMname())) {
            personHashMap.put("mname", personUpdate.getMname());
        } else if (!personUpdate.getLname().equals(person.getLname())) {
            personHashMap.put("lname", personUpdate.getLname());
        } else if (!personUpdate.getPhone().equals(person.getPhone())) {
            personHashMap.put("phone", personUpdate.getPhone());
        } else if (!personUpdate.getDob().equals(person.getDob())) {
            personHashMap.put("dob", personUpdate.getDob());
        } else if (!personUpdate.getPermissions().equals(person.getPermissions())) {
            personHashMap.put("permissions", personUpdate.getPermissions());
        } else if (!personUpdate.getIdentificationNumber().equals(person.getIdentificationNumber())) {
            personHashMap.put("identificationNumber", personUpdate.getIdentificationNumber());
        } else if (!personUpdate.getImage().equals(person.getImage())) {
            personHashMap.put("image", personUpdate.getImage());
        }

        if (!personHashMap.isEmpty()) {
            db.collection("Person").document(UID).update(personHashMap);
            Log.d("sss", "update");
        }
        String Name = personUpdate.getFname() + " " + personUpdate.getMname() + " " + personUpdate.getLname();
        setPermissionsOfDB(personUpdate.getPermissions(), person.getPermissions(), Name);
    }

    private void setPermissionsOfDB(ArrayList<String> permissions, ArrayList<String> permissions1, String Name) {
        removepermissions(permissions);
        permissions.removeAll(permissions1);
        updatepermissions(permissions1);
        for (int i = 0; i < permissions.size(); i++) {
            if (permissions.get(i).equals(Common.PERMISSIONS_EDARE)) {
                db.collection("Edare").document(UID).set(new Edare(newstage, UID, Name));
            }
            if (permissions.get(i).equals(Common.PERMISSIONS_MOHAFEZ)) {
                db.collection("Mohafez").document(UID).set(new Mohafez(newstage, "", UID, Name));
            }
            if (permissions.get(i).equals(Common.PERMISSIONS_SUPER_VISOR)) {
                db.collection("Moshref").document(UID).set(new Moshref(newstage, UID, Name));
            }
            if (permissions.get(i).equals(Common.PERMISSIONS_STUDENTN)) {
                db.collection("Student").document(UID).set(new Student(UID, Name, groupId, newstage));
            }
            if (permissions.get(i).equals(Common.PERMISSIONS_SUPER_VISOR_EXAMS)) {
                db.collection("SuperVisorExams").document(UID).set(new SuperVisorExams(UID, Name));
            }
        }
    }

    private void removepermissions(ArrayList<String> permissions1) {
        if (!permissions1.contains(Common.PERMISSIONS_EDARE)) {
            db.collection("Edare").document(UID).delete();
        }
        if (!permissions1.contains(Common.PERMISSIONS_MOHAFEZ)) {
            db.collection("Mohafez").document(UID).delete();
        }
        if (!permissions1.contains(Common.PERMISSIONS_SUPER_VISOR)) {
            db.collection("Moshref").document(UID).delete();
        }
        if (!permissions1.contains(Common.PERMISSIONS_SUPER_VISOR_EXAMS)) {
            db.collection("SuperVisorExams").document(UID).delete();
        }
    }

    private void getPersonData() {
        if (permissions != null) {
            switch (permissions) {
                case Common.PERMISSIONS_MOHAFEZ:
                    db.collection("Mohafez").document(UID).get().addOnSuccessListener(documentSnapshot -> {
                        Mohafez mohafez = documentSnapshot.toObject(Mohafez.class);
                        if (mohafez != null) {
                            sp_stage.setSelection(stage_list.indexOf(mohafez.getStage()));
                            groupId = mohafez.getGroupId();
                            retStage = mohafez.getStage();
                        }
                    });
                    break;
                case Common.PERMISSIONS_SUPER_VISOR:
                    db.collection("Moshref").document(UID).get().addOnSuccessListener(documentSnapshot -> {
                        Moshref moshref = documentSnapshot.toObject(Moshref.class);
                        assert moshref != null;
                        sp_stage.setSelection(stage_list.indexOf(moshref.getStage()));
                        retStage = moshref.getStage();
                    });
                    break;
                case Common.PERMISSIONS_EDARE:
                    db.collection("Edare").document(UID).get().addOnSuccessListener(documentSnapshot -> {
                        Edare edare = documentSnapshot.toObject(Edare.class);
                        assert edare != null;
                        sp_stage.setSelection(stage_list.indexOf(edare.getStage()));
                        retStage = edare.getStage();
                    });
                    break;
                case Common.PERMISSIONS_STUDENTN:
                    db.collection("Student").document(UID).get().addOnSuccessListener(documentSnapshot -> {
                        Student student = documentSnapshot.toObject(Student.class);
                        assert student != null;
                        sp_stage.setSelection(stage_list.indexOf(student.getStage()));
                        retStage = student.getStage();
                        retGroupId = student.getGroupId();
                    });
                    break;
            }
        }

        db.collection("Person").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                person = documentSnapshot.toObject(Person.class);
                assert person != null;
                edtFName.setText(person.getFname());
                edtMName.setText(person.getMname());
                edtLName.setText(person.getLname());
                edtPhone.setText(person.getPhone());
                edtDOB.setText(person.getDob());
                edtIdentificationNumber.setText(person.getIdentificationNumber());
                if (!person.getImage().equals("")) {
                    Picasso.get().load(person.getImage()).into(img_profile);
                }
                ImageUrl = person.getImage();
                ListPermissions.addAll(person.getPermissions());
            }
        });
    }

    private void showDialogSelectPermissions() {
// Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("اختيار بعض الصلاحيات");

// Add a checkbox list
        String[] PERMISSIONS = {Common.ConvertPermissionToNameArabic(Common.PERMISSIONS_ADMIN),
                Common.ConvertPermissionToNameArabic(Common.PERMISSIONS_MOHAFEZ),
                Common.ConvertPermissionToNameArabic(Common.PERMISSIONS_SUPER_VISOR),
                Common.ConvertPermissionToNameArabic(Common.PERMISSIONS_SUPER_VISOR_EXAMS),
                Common.ConvertPermissionToNameArabic(Common.PERMISSIONS_TESTER),
                Common.ConvertPermissionToNameArabic(Common.PERMISSIONS_EDARE)};
        boolean[] checkedItems = {false, false, false, false, false, false};

        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (ListPermissions.contains(Common.ConvertPermissionToNameEnglish(PERMISSIONS[i]))) {
                checkedItems[i] = true;
            }
        }
        builder.setMultiChoiceItems(PERMISSIONS, checkedItems, (dialog, which, isChecked) -> {
            // The user checked or unchecked a box
            if (isChecked) {
                // If the user checked the item, add it to the selected items
                ListPermissions.add(Common.ConvertPermissionToNameEnglish(PERMISSIONS[which]));
            } else {
                // Else, if the item is already in the array, remove it
                ListPermissions.remove(Common.ConvertPermissionToNameEnglish(PERMISSIONS[which]));
            }

        });

// Add OK and Cancel buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            // The user clicked OK
        });
        builder.setNegativeButton("Cancel", null);

// Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void chooseImage() {
        startActivityForResult(Intent.createChooser(new Intent().setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT), "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage(String id) {
        if (imgUri != null) {
            sweetAlertDialog.showdialogProgress();

            final StorageReference imageFolder = storageReference.child("images/" + id);
            imageFolder.putFile(imgUri).addOnSuccessListener(taskSnapshot -> {
                sweetAlertDialog.cancelDialog();
                imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                    db.collection("Person").document(id).update("image", uri.toString());
                    sweetAlertDialog.showDialogSuccess("OK", "تم تحديث البيانات بنجاح !")
                            .setConfirmButton("OK", sweetAlertDialog1 -> {
                                clearInputs();
                                sweetAlertDialog1.dismissWithAnimation();
                                sweetAlertDialog.cancelDialog();
                                Objects.requireNonNull(getActivity()).finish();
                            });
                });
            })
                    .addOnFailureListener(e -> {
                        sweetAlertDialog.cancelDialog();
                        sweetAlertDialog.showDialogError(e.getMessage());
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        sweetAlertDialog.sweetAlertDialog.setTitleText("Uploaded " + progress + "%");
                    });

        }
    }

    private void initialized(View view) {
        edtDOB = view.findViewById(R.id.edtDOB);
        edtFName = view.findViewById(R.id.edt_F_name);
        edtMName = view.findViewById(R.id.edt_M_name);
        edtLName = view.findViewById(R.id.edt_L_name);
        edtPhone = view.findViewById(R.id.edtPhone);
        sp_stage = view.findViewById(R.id.sp_stage);
        sp_group = view.findViewById(R.id.sp_groups);
        edtIdentificationNumber = view.findViewById(R.id.edtIdentificationNumber);
        img_profile = view.findViewById(R.id.img_profile);
        btn_add = view.findViewById(R.id.btn_add_mohafez);
        btn_select_permissions = view.findViewById(R.id.btn_select_permissions);

        db = FirebaseFirestore.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        ListPermissions = new ArrayList<>();
        stage_list = new ArrayList<>();
        groups_Names = new ArrayList<>();
        groups = new ArrayList<>();
        stage_list.add("اختر المرحلة");
        stage_list.add(Common.SUP_STAGE);
        stage_list.add(Common.FOUNDATION_STAGE);
        stage_list.add(Common.INTERMEDIATE_STAGE);
        stage_list.add(Common.THE_UPPER_STAGE);

        sweetAlertDialog = new SweetAlertDialog_(getContext());
    }

    private void clearInputs() {
        edtFName.setText("");
        edtMName.setText("");
        edtLName.setText("");
        edtPhone.setText("");
        edtDOB.setText("");
        edtIdentificationNumber.setText("");
        imgUri = null;
        img_profile.setImageResource(R.drawable.profile_image);
        UID = "";
    }

    @SuppressLint("SetTextI18n")
    private void showDialogPickerDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        edtDOB.setText(day + "/" + month + "/" + year);
        edtDOB.setOnClickListener(view1 -> {
            DatePickerDialog pickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                    setListener, year, month, day);
            pickerDialog.show();
        });

        setListener = (datePicker, year1, month1, day1) -> {
            month1 = month1 + 1;
            String date = day1 + "/" + month1 + "/" + year1;
            edtDOB.setText(date);
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imgUri = data.getData();
            img_profile.setImageURI(imgUri);
        }
    }

    private boolean validateSpinner() {
        if (permissions.equals(Common.PERMISSIONS_STUDENTN)) {
            if (sp_group.getSelectedItemPosition() != 0 && groupId != null) {
                return true;
            } else {
                sweetAlertDialog.showDialogError("يجب اختيار الحلقة ..");
                return false;
            }
        } else {
            if (sp_stage.getSelectedItemPosition() != 0) {
                return true;
            } else {
                sweetAlertDialog.showDialogError("يجب اختيار نوع المرحلة ..");
                return false;
            }
        }
    }

    private boolean validateInputs() {
        if (!Objects.requireNonNull(edtFName.getText()).toString().trim().isEmpty() &&
                !Objects.requireNonNull(edtMName.getText()).toString().trim().isEmpty() &&
                !Objects.requireNonNull(edtLName.getText()).toString().trim().isEmpty() &&
                !Objects.requireNonNull(edtDOB.getText()).toString().trim().isEmpty() &&
                !Objects.requireNonNull(edtPhone.getText()).toString().trim().isEmpty() &&
                (edtPhone.getText().toString().trim().length() == 10) &&
                !Objects.requireNonNull(edtIdentificationNumber.getText()).toString().trim().isEmpty() &&
                (edtIdentificationNumber.getText().toString().trim().length() == 9) &&
                !ListPermissions.isEmpty()
        ) {
            return true;
        } else if (edtFName.getText().toString().trim().isEmpty()) {
            sweetAlertDialog.showDialogError("حقل الإسم الأول فارغ !");
            edtFName.setFocusable(true);
            edtFName.setError("");
        } else if (Objects.requireNonNull(edtMName.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog.showDialogError("حقل اسم الأب فارغ !");
            edtMName.setFocusable(true);
            edtMName.setError("");
        } else if (Objects.requireNonNull(edtLName.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog.showDialogError("حقل الإسم الأخير فارغ !");
            edtLName.setFocusable(true);
            edtLName.setError("");
        } else if (Objects.requireNonNull(edtPhone.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog.showDialogError("حقل رقم الجوال فارغ !");
            edtPhone.setFocusable(true);
            edtPhone.setError("");
        } else if (edtPhone.getText().toString().trim().length() != 10) {
            sweetAlertDialog.showDialogError("يجب أن لا يقل حقل الجوال عن 10 أرقام");
            edtPhone.setFocusable(true);
            edtPhone.setError("");
        } else if (Objects.requireNonNull(edtDOB.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog.showDialogError("حقل تاريخ الميلاد فارغ !");
            edtDOB.setError("");
        } else if (Objects.requireNonNull(edtIdentificationNumber.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog.showDialogError("حقل رقم الهوية فارغ !");
            edtIdentificationNumber.setFocusable(true);
            edtIdentificationNumber.setError("");
        } else if (edtIdentificationNumber.getText().toString().trim().length() != 9) {
            sweetAlertDialog.showDialogError("يجب أن لا يقل حقل رقم الهوية عن 9 أرقام");
            edtIdentificationNumber.setFocusable(true);
            edtIdentificationNumber.setError("");
        } else if (ListPermissions.isEmpty()) {
            sweetAlertDialog.showDialogError("يجب اعتماد صلاحية واحدة على الأقل ..");
        }
        return false;
    }
}