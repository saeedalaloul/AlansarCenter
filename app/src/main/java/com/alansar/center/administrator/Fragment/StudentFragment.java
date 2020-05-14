package com.alansar.center.administrator.Fragment;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.alansar.center.supervisor_exams.Model.Exam;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
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
    private TextView tv_name_student, tv_part_exam,
            tv_date_exam, tv_name_mohafez,
            tv_mark_exam, tv_notes_exam,
            tv_stage_exam;
    private LinearLayout li_question_exam_ed;

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
        } else if (item.getTitle().equals(Common.VIEW_THE_LATEST_EXAM)) {
            ViewLatestExamOfStudent(item.getOrder());
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

    private void ViewLatestExamOfStudent(int order) {
        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("idStudent", students.get(order).getId())
                .whereEqualTo("stage", students.get(order).getStage())
                .whereEqualTo("statusAcceptance", 3)
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .orderBy("month", Query.Direction.DESCENDING)
                .orderBy("day", Query.Direction.DESCENDING)
                .limit(1)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                showDialogMoreDetails(queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class),order);
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    @SuppressLint("SetTextI18n")
    private void showDialogMoreDetails(Exam exam,int order) {
        if (getContext() != null && exam != null) {
            LayoutInflater factory = LayoutInflater.from(getActivity());
            @SuppressLint("InflateParams") final View moreDetailsDialogView
                    = factory.inflate(R.layout.more_details_exam_dialog, null);
            AlertDialog dialogMoreDetails = new AlertDialog.Builder(getContext())
                    .setView(moreDetailsDialogView).create();
            dialogMoreDetails.show();
            Objects.requireNonNull(dialogMoreDetails.getWindow()).setBackgroundDrawableResource(R.color.fbutton_color_transparent);
            InitializationDialog(dialogMoreDetails);

            tv_name_student.setText(exam.getNotes());
            tv_part_exam.setText(exam.getExamPart());
            tv_stage_exam.setText(exam.getStage());
            tv_date_exam.setText(exam.getDay() + "/" + exam.getMonth() + "/" + exam.getYear());
            getNameStudentFromDB(exam.getIdStudent(), tv_name_student);
            getNameMohafezFromDB(students.get(order).getGroupId(), tv_name_mohafez);
            tv_notes_exam.setText(exam.getNotes());
            calcMarksExam(exam.getMarksExamQuestions(), tv_mark_exam);
            if (exam.getMarksExamQuestions() != null) {
                for (int i = 1; i <= exam.getMarksExamQuestions().size(); i++) {
                    MaterialEditText ed_questions = new MaterialEditText(getContext());
                    LinearLayout.LayoutParams lp_ed = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    ed_questions.setLayoutParams(lp_ed);
                    ed_questions.setMetTextColor(Color.BLACK);
                    ed_questions.setTextSize(20);
                    ed_questions.setTextColor(Color.BLACK);
                    ed_questions.setFocusable(false);
                    ed_questions.setHelperText("س :" + i);
                    ed_questions.setHelperTextAlwaysShown(true);
                    ed_questions.setHelperTextColor(Color.BLACK);
                    ed_questions.setText("" + exam.getMarksExamQuestions().get("" + i));
                    li_question_exam_ed.addView(ed_questions);
                }
            }
        }
    }

    private void InitializationDialog(AlertDialog dialogMoreDetails) {
        tv_notes_exam = dialogMoreDetails.findViewById(R.id.tv_notes_more_details_exam);
        tv_name_mohafez = dialogMoreDetails.findViewById(R.id.tv_name_mohafez);
        tv_name_student = dialogMoreDetails.findViewById(R.id.tv_name_student);
        tv_part_exam = dialogMoreDetails.findViewById(R.id.tv_part_exam);
        tv_date_exam = dialogMoreDetails.findViewById(R.id.tv_date_exam);
        tv_mark_exam = dialogMoreDetails.findViewById(R.id.tv_mark_exam);
        tv_stage_exam = dialogMoreDetails.findViewById(R.id.tv_stage_more_details_exam);
        li_question_exam_ed = dialogMoreDetails.findViewById(R.id.linear_layout_questions_exam_ed);
    }

    private void getNameStudentFromDB(String id, TextView et_name) {
        if (id != null && !id.isEmpty()) {
            db.collection("Student").document(id)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (et_name != null) {
                        et_name.setText(documentSnapshot.getString("name"));
                    }
                }
            });
        }
    }

    private void getNameMohafezFromDB(String id, TextView et_name) {
        if (id != null && !id.isEmpty()) {
            db.collection("Mohafez")
                    .whereEqualTo("groupId", id)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && !documentSnapshot.isEmpty()) {
                    if (et_name != null) {
                        et_name.setText(documentSnapshot.getDocuments().get(0).getString("name"));
                    }
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void calcMarksExam(HashMap<String, Double> map, TextView tv_mark_exam) {
        if (map != null) {
            double mark = 0;
            if (!map.isEmpty()) {
                for (int i = 1; i <= map.size(); i++) {
                    if (map.get("" + i) != null) {
                        mark += map.get("" + i);
                    }
                }
            }
            double result = round(mark / map.size());
            if (result >= 80.00) {
                tv_mark_exam.setText("" + result);
                tv_mark_exam.setTextColor(Color.WHITE);
                tv_mark_exam.setBackgroundColor(Color.GREEN);

            } else if (result < 80.00) {
                tv_mark_exam.setText("" + result);
                tv_mark_exam.setTextColor(Color.WHITE);
                tv_mark_exam.setBackgroundColor(Color.RED);
            }
        }
    }

    private double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
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