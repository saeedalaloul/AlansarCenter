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
import androidx.fragment.app.Fragment;

import com.alansar.center.Common.Common;
import com.alansar.center.FButton;
import com.alansar.center.Models.Group;
import com.alansar.center.Models.GroupMembers;
import com.alansar.center.Models.Person;
import com.alansar.center.Mohafez.Model.Mohafez;
import com.alansar.center.Moshref.Model.Moshref;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.students.Model.Student;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class Personal_Information__Fragment extends Fragment {
    private MaterialEditText edtDOB, edtFName, edtMName, edtLName, edtPhone, edtIdentificationNumber;
    private DatePickerDialog.OnDateSetListener setListener;
    private int year;
    private int month;
    private int day;
    private CircleImageView img_profile;
    private Uri imgUri;
    private FButton btn_add;
    private FirebaseFirestore db;
    private Person person;
    private StorageReference storageReference;
    private ArrayList<String> ListPermissions;
    private SearchableSpinner sp_stage, sp_group;
    private String stage;
    private String permissions;
    private SweetAlertDialog_ sweetAlertDialog_;
    private ArrayList<Group> groups;
    private ArrayAdapter<Group> adapter;
    private String groupId;

    public Personal_Information__Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal__information__, container, false);
        if (getActivity() != null && getContext() != null) {
            initialized(view);
            showDialogPickerDate();
            if (getArguments() != null && getArguments().getString("permissions") != null) {
                permissions = getArguments().getString("permissions");
            } else {
                Objects.requireNonNull(getActivity()).finish();
                Toast.makeText(getContext(), "No permission was found to be able to update the data. See Permissions for users", Toast.LENGTH_SHORT).show();
            }
            btn_add.setOnClickListener(view1 -> {
                if (validateInputs() && validateSpinner()) {
                    add_Person_To_Database(Objects.requireNonNull(edtFName.getText()).toString(),
                            Objects.requireNonNull(edtMName.getText()).toString(),
                            Objects.requireNonNull(edtLName.getText()).toString(),
                            Objects.requireNonNull(edtPhone.getText()).toString(),
                            Objects.requireNonNull(edtDOB.getText()).toString(),
                            Objects.requireNonNull(edtIdentificationNumber.getText()).toString());
                }
            });
            if (permissions.equals(Common.PERMISSIONS_STUDENTN)) {
                sp_stage.setVisibility(View.GONE);
                sp_group.setVisibility(View.VISIBLE);
            }
            img_profile.setOnClickListener(view12 -> chooseImage());
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
            }
        }
        return view;
    }


    private void add_Person_To_Database(String FName, String MName, String LName, String Phone, String DOB, String IdentificationNumber) {
        db.collection("Person")
                .whereEqualTo("fname", FName)
                .whereEqualTo("mname", MName)
                .whereEqualTo("lname", LName)
                .limit(1)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                sweetAlertDialog_.showDialogError("الإسم المدخل موجود مسبقا ..");
            } else {
                db.collection("Person")
                        .whereEqualTo("phone", Phone)
                        .limit(1)
                        .get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                    if (queryDocumentSnapshots1 != null && !queryDocumentSnapshots1.isEmpty()) {
                        sweetAlertDialog_.showDialogError("رقم الهاتف المدخل موجود مسبقا ..");
                    } else {
                        stage = sp_stage.getSelectedItem().toString();
                        ListPermissions.add(permissions);
                        String PushId = db.collection("Person").document().getId();
                        if (imgUri != null) {
                            person = new Person(FName, MName, LName, Phone, DOB, "", ListPermissions, IdentificationNumber, PushId);
                            db.collection("Person").document(PushId).set(person).addOnFailureListener(e -> sweetAlertDialog_.showDialogError(e.getMessage()));
                            getTypepermissions(PushId);
                            uploadImage(PushId);
                        } else {
                            person = new Person(FName, MName, LName, Phone, DOB, "", ListPermissions, IdentificationNumber, PushId);
                            Log.d("sss", PushId);
                            db.collection("Person").document(PushId).set(person)
                                    .addOnFailureListener(e -> sweetAlertDialog_.showDialogError(e.getMessage()));
                            getTypepermissions(PushId);
                            sweetAlertDialog_.showDialogSuccess("OK", "تم اضافة البيانات بنجاح !")
                                    .setConfirmButton("OK", sweetAlertDialog1 -> {
                                        sweetAlertDialog1.dismissWithAnimation();
                                        clearInputs();
                                        Objects.requireNonNull(getActivity()).finish();
                                    });
                        }
                    }
                });
            }
        });
    }

    private void chooseImage() {
        startActivityForResult(Intent.createChooser(new Intent().setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT), "Select Picture"), Common.PICK_IMAGE_REQUEST);
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

        db = FirebaseFirestore.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        ListPermissions = new ArrayList<>();
        groups = new ArrayList<>();
        sweetAlertDialog_ = new SweetAlertDialog_(getContext());
        sp_stage.setTitle("اختر المرحلة");

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
    }


    private void getHalakasfromDatebase() {
        db.collection("Group").whereEqualTo("stage", Common.currentSTAGE).get().addOnSuccessListener(queryDocumentSnapshots -> {
            groups.clear();
            groups.add(new Group("اختر الحلقة", "", "", ""));
            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                groups.add(queryDocumentSnapshots.toObjects(Group.class).get(i));
                adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_spinner_item, groups);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_group.setAdapter(adapter);
            }
        });
    }

    private void uploadImage(String id) {
        if (imgUri != null) {
            sweetAlertDialog_.showdialogProgress();
            final StorageReference imageFolder = storageReference.child("images/" + id);
            imageFolder.putFile(imgUri).addOnSuccessListener(taskSnapshot -> {
                sweetAlertDialog_.cancelDialog();
                imageFolder.getDownloadUrl().addOnSuccessListener(uri -> db.collection("Person").document(id).update("image", uri.toString()).addOnSuccessListener(aVoid -> {
                    clearInputs();
                    sweetAlertDialog_.showDialogSuccess("OK", "تم اضافة البيانات بنجاح !")
                            .setConfirmButton("OK", sweetAlertDialog1 -> {
                                clearInputs();
                                Objects.requireNonNull(getActivity()).finish();
                            });
                }));
            })
                    .addOnFailureListener(e -> {
                        sweetAlertDialog_.cancelDialog();
                        sweetAlertDialog_.showDialogError(e.getMessage());
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        sweetAlertDialog_.sweetAlertDialog.setTitleText("Uploaded " + progress + "%");
                    });
        }
    }

    private void getTypepermissions(String id) {
        String fullName = person.getFname() + " " + person.getMname() + " " + person.getLname();
        if (permissions != null) {
            switch (permissions) {
                case Common.PERMISSIONS_MOHAFEZ:
                    db.collection("Mohafez").document(id).set(new Mohafez(stage, "", id, fullName));
                    break;
                case Common.PERMISSIONS_SUPER_VISOR:
                    db.collection("Moshref").document(id).set(new Moshref(stage, id, fullName));
                    break;
                case Common.PERMISSIONS_EDARE:
                    db.collection("Edare").document(id).set(new Moshref(stage, id, fullName));
                    break;
                case Common.PERMISSIONS_STUDENTN:
                    setStudentOfDB(id, fullName);
                    break;
            }
        }
    }

    private void setStudentOfDB(String id, String fullName) {
        ArrayList<String> groupMembers = new ArrayList<>();
        db.collection("Moshref").document(Common.currentPerson.getId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                groupMembers.add(id);
                db.collection("Student").document(id).set(new Student(id, fullName, groupId,
                        Objects.requireNonNull(documentSnapshot.get("stage")).toString()));
                db.collection("GroupMembers").document(groupId).get().addOnSuccessListener(documentSnapshot1 -> {
                    assert documentSnapshot1 != null;
                    if (documentSnapshot1.exists()) {
                        db.collection("GroupMembers").document(groupId).update("groupMembers", FieldValue.arrayUnion(id));
                    } else {
                        db.collection("GroupMembers").document(groupId).set(new GroupMembers(groupId, groupMembers));
                    }
                });

            }
        });
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
                sweetAlertDialog_.showDialogError("يجب اختيار الحلقة ..");
                return false;
            }
        } else {
            Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
            if (sp_stage.getSelectedItemPosition() != 0) {
                Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                sweetAlertDialog_.showDialogError("يجب اختيار نوع المرحلة ..");
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
                (edtIdentificationNumber.getText().toString().trim().length() == 9)) {
            return true;
        } else if (edtFName.getText().toString().trim().isEmpty()) {
            sweetAlertDialog_.showDialogError("حقل الإسم الأول فارغ !");
            edtFName.setFocusable(true);
            edtFName.setError("");
        } else if (Objects.requireNonNull(edtMName.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog_.showDialogError("حقل اسم الأب فارغ !");
            edtMName.setFocusable(true);
            edtMName.setError("");
        } else if (Objects.requireNonNull(edtLName.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog_.showDialogError("حقل الإسم الأخير فارغ !");
            edtLName.setFocusable(true);
            edtLName.setError("");
        } else if (Objects.requireNonNull(edtPhone.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog_.showDialogError("حقل رقم الجوال فارغ !");
            edtPhone.setFocusable(true);
            edtPhone.setError("");
        } else if (edtPhone.getText().toString().trim().length() != 10) {
            sweetAlertDialog_.showDialogError("يجب أن لا يقل حقل الجوال عن 10 أرقام");
            edtPhone.setFocusable(true);
            edtPhone.setError("");
        } else if (Objects.requireNonNull(edtDOB.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog_.showDialogError("حقل تاريخ الميلاد فارغ !");
            edtDOB.setError("");
        } else if (Objects.requireNonNull(edtIdentificationNumber.getText()).toString().trim().isEmpty()) {
            sweetAlertDialog_.showDialogError("حقل رقم الهوية فارغ !");
            edtIdentificationNumber.setFocusable(true);
            edtIdentificationNumber.setError("");
        } else if (edtIdentificationNumber.getText().toString().trim().length() != 9) {
            sweetAlertDialog_.showDialogError("يجب أن لا يقل حقل رقم الهوية عن 9 أرقام");
            edtIdentificationNumber.setFocusable(true);
            edtIdentificationNumber.setError("");
        }
        return false;
    }
}