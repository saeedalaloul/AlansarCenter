package com.alansar.center.administrator.Fragment;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Models.Group;
import com.alansar.center.Models.GroupMembers;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.administrator.Adapters.StudentAdapter;
import com.alansar.center.students.Model.Student;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentFragment extends Fragment {
    private FirebaseFirestore db;
    private List<Student> students;
    private StudentAdapter adapter;
    private AlertDialog alertDialog;
    private Spinner sp_halaka, sp_stage;
    private ArrayList<Group> groups;
    private ArrayAdapter<Group> adapterGroups;
    private List<String> stage_list;
    private SweetAlertDialog_ sweetAlertDialog;
    private String groupId;
    private String retgroupId;
    private String retstage;
    private RecyclerView rv;
    private View view;
    private ListenerRegistration registration;

    public StudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_student, container, false);
        initialized(view);
        rv.setAdapter(adapter);
        return view;
    }

    private void initialized(View view) {
        db = FirebaseFirestore.getInstance();
        sweetAlertDialog = new SweetAlertDialog_(getContext());
        stage_list = new ArrayList<>();
        stage_list.add("اختر المرحلة");
        stage_list.add(Common.SUP_STAGE);
        stage_list.add(Common.FOUNDATION_STAGE);
        stage_list.add(Common.INTERMEDIATE_STAGE);
        stage_list.add(Common.THE_UPPER_STAGE);
        groups = new ArrayList<>();
        rv = view.findViewById(R.id.student_rv);
        students = new ArrayList<>();
        adapter = new StudentAdapter(students);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    private void LoadData() {
        registration = db.collection("Student")
                .orderBy("name", Query.Direction.ASCENDING)
                .limit(10)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }
                    assert queryDocumentSnapshots != null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        students.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            students.add(snapshots.toObject(Student.class));
                        }
                        adapter.notifyDataSetChanged();
                        if (students.isEmpty()) {
                            view.findViewById(R.id.tv_check_students).setVisibility(View.VISIBLE);
                            rv.setVisibility(View.GONE);
                        } else {
                            view.findViewById(R.id.tv_check_students).setVisibility(View.GONE);
                            rv.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showDialogUpdateStudent(item.getOrder());
        } else if (item.getTitle().equals(Common.ISDISABLEACCOUNT)) {
            updateIsEnabledAccount(StudentAdapter.students.get(item.getOrder()).getId(), false);
        } else if (item.getTitle().equals(Common.ISENABLEACCOUNT)) {
            updateIsEnabledAccount(StudentAdapter.students.get(item.getOrder()).getId(), true);
        }
        return super.onContextItemSelected(item);
    }

    private void updateIsEnabledAccount(String id, boolean isEnable) {
        final SweetAlertDialog BtnDialog = new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.NORMAL_TYPE)
                .setContentText("هل أنت متأكد من ذلك ؟")
                .setConfirmText("تأكيد")
                .setCancelText("إلغاء");

        if (isEnable)
            BtnDialog.setTitleText("تمكين الحساب");
        else
            BtnDialog.setTitleText("تعطيل الحساب");

        BtnDialog.setOnShowListener(dialog -> {
            BtnDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setOnClickListener(view -> {
                db.collection("Person").document(id).update("enableAccount", isEnable);
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("تمت");
                if (isEnable)
                    sweetAlertDialog.setTitleText("تم تمكين الحساب بنجاح !");
                else
                    sweetAlertDialog.setTitleText("تم تعطيل الحساب بنجاح !");
                dialog.dismiss();
                sweetAlertDialog.show();
            });

            BtnDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setOnClickListener(view -> dialog.dismiss());
        });
        BtnDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fg_menu, menu);
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // adapter.getFilter().filter(query);
                SearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                SearchData(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showDialogUpdateStudent(int position) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View updateStudentDialogView = factory.inflate(R.layout.update_student_from_admin, null);
        alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity())).create();
        alertDialog.setView(updateStudentDialogView);
        alertDialog.show();
        sp_stage = alertDialog.findViewById(R.id.update_student_spinner_stage);
        sp_halaka = alertDialog.findViewById(R.id.update_student_spinner_halakas);
        EditText et_name = alertDialog.findViewById(R.id.update_student_et_name);
        assert sp_halaka != null;
        sp_halaka.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                groupId = groups.get(i).getGroupId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sp_stage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View v,
                                       int arg2, long arg3) {
                String selectedVal = getResources().getStringArray(R.array.stage_array)[spinner.getSelectedItemPosition()];
                sp_halaka.setSelection(0);
                getGroupsfromDatebase(selectedVal, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });

        Button btn_add = alertDialog.findViewById(R.id.update_student_add_btn_save);
        assert btn_add != null;
        btn_add.setOnClickListener(view -> {
            if (validateInputs()) {
                updateStudentFromDB(position);
            }
        });
        ImageButton imbtn_close = alertDialog.findViewById(R.id.update_student_imgbtn_close);
        assert imbtn_close != null;
        imbtn_close.setOnClickListener(view121 -> alertDialog.dismiss());
        sp_stage.setSelection(stage_list.indexOf(students.get(position).getStage()));
        retstage = students.get(position).getStage();
        retgroupId = students.get(position).getGroupId();
        if (et_name != null) {
            et_name.setText(students.get(position).getName());
        }
    }

    private void updateStudentFromDB(int position) {
        HashMap<String, Object> map = new HashMap<>();
        if (!sp_stage.getSelectedItem().toString().equals(retstage)) {
            map.put("stage", sp_stage.getSelectedItem().toString());
        }
        if (!groupId.equals(retgroupId)) {
            map.put("groupId", groupId);
            updateGroupMember(groupId, retgroupId, students.get(position).getId());
        }
        if (!map.isEmpty()) {
            db.collection("Student").document(students.get(position).getId()).update(map);
        }
        sweetAlertDialog.showDialogSuccess("OK", "تم اضافة البيانات بنجاح !");
        alertDialog.dismiss();
    }

    private void updateGroupMember(String groupId, String retGroupId, String UID) {
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

    private void getGroupsfromDatebase(String selectedVal, int position) {
        db.collection("Group").whereEqualTo("stage", selectedVal).get().addOnSuccessListener(queryDocumentSnapshots -> {
            groups.clear();
            groups.add(new Group("اختر الحلقة", "", "", ""));
            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                groups.add(queryDocumentSnapshots.toObjects(Group.class).get(i));
                adapterGroups = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_spinner_item, groups);
                adapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_halaka.setAdapter(adapterGroups);
            }
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).getGroupId().equals(students.get(position).getGroupId())) {
                    sp_halaka.setSelection(i);
                }
            }
        });
    }

    private boolean validateInputs() {
        if (
                sp_stage.getSelectedItemPosition() != 0 &&
                        sp_halaka.getSelectedItemPosition() != 0 &&
                        groupId != null && !groupId.isEmpty()
        ) {
            return true;
        } else {
            if (sp_stage.getSelectedItemPosition() == 0) {
                sweetAlertDialog.showDialogError("يجب إختيار مرحلة ..");
            } else if (sp_halaka.getSelectedItemPosition() == 0 && groupId == null || groupId.isEmpty()) {
                sweetAlertDialog.showDialogError("يجب إختيار حلقة ..");
            }
        }
        return false;
    }

    private void SearchData(String newText) {
        CollectionReference Ref = db.collection("Student");
        if (newText.isEmpty()) {
            students.clear();
            LoadData();
            adapter.notifyDataSetChanged();
        } else {
            Query query = Ref.orderBy("name").startAt(newText).endAt(newText + "\uf8ff");
            query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        students.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            students.add(snapshots.toObject(Student.class));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        students.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}