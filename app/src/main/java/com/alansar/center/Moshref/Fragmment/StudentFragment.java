package com.alansar.center.Moshref.Fragmment;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Activitys.HostingActivity;
import com.alansar.center.Common.Common;
import com.alansar.center.Moshref.Adapter.StudentAdapter;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.students.Model.Student;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.concurrent.atomic.AtomicBoolean;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentFragment extends Fragment {

    private FirebaseFirestore db;
    private List<Student> students;
    private StudentAdapter adapter;
    private RecyclerView rv;
    private View view;
    private ListenerRegistration registration;
    private TextView tv_name_student, tv_part_exam,
            tv_date_exam, tv_name_mohafez,
            tv_mark_exam, tv_notes_exam,
            tv_name_tester;
    private LinearLayout li_question_exam_ed;

    public StudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_moshref_students, container, false);

        db = FirebaseFirestore.getInstance();

        rv = view.findViewById(R.id.student_rv);
        students = new ArrayList<>();
        FloatingActionButton btnaddMohafez = view.findViewById(R.id.btn_add_student);
        btnaddMohafez.setOnClickListener(view1 -> addStudent());
        adapter = new StudentAdapter(students);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);
        setHasOptionsMenu(true);
        rv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.currentPerson != null && Common.currentSTAGE != null) {
            LoadData();
        }
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
                .whereEqualTo("stage", Common.currentSTAGE)
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
                            if (!snapshots.getId().equals(Common.currentPerson.getId()))
                                students.add(snapshots.toObject(Student.class));
                            adapter.notifyDataSetChanged();
                        }
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
                showDialogMoreDetails(queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class), order);
            } else {
                new SweetAlertDialog_(getContext()).showDialogError("عذرا لم يتم العثور على أية إختبارات لهذا الطالب");
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    @SuppressLint("SetTextI18n")
    private void showDialogMoreDetails(Exam exam, int order) {
        if (getContext() != null && exam != null) {
            LayoutInflater factory = LayoutInflater.from(getActivity());
            @SuppressLint("InflateParams") final View moreDetailsDialogView
                    = factory.inflate(R.layout.more_details_exam_dialog_custom_super_visor, null);
            AlertDialog dialogMoreDetails = new AlertDialog.Builder(getContext())
                    .setView(moreDetailsDialogView).create();
            dialogMoreDetails.show();
            Objects.requireNonNull(dialogMoreDetails.getWindow()).setBackgroundDrawableResource(R.color.fbutton_color_transparent);
            InitializationDialog(dialogMoreDetails);

            tv_name_student.setText(exam.getNotes());
            tv_part_exam.setText(exam.getExamPart());
            tv_date_exam.setText(exam.getDay() + "/" + exam.getMonth() + "/" + exam.getYear());
            getNameTesterFromDB(exam.getIdTester(), tv_name_tester);
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

    private void getNameTesterFromDB(String idTester, TextView tv_name_tester) {
        if (idTester != null) {
            db.collection("Tester").document(idTester)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (tv_name_tester != null) {
                        tv_name_tester.setText(documentSnapshot.getString("name"));
                    }
                }
            });
        }
    }

    private void InitializationDialog(AlertDialog dialogMoreDetails) {
        tv_notes_exam = dialogMoreDetails.findViewById(R.id.tv_notes_more_details_exam);
        tv_name_mohafez = dialogMoreDetails.findViewById(R.id.tv_name_mohafez);
        tv_name_student = dialogMoreDetails.findViewById(R.id.tv_name_student);
        tv_part_exam = dialogMoreDetails.findViewById(R.id.tv_part_exam);
        tv_date_exam = dialogMoreDetails.findViewById(R.id.tv_date_exam);
        tv_mark_exam = dialogMoreDetails.findViewById(R.id.tv_mark_exam);
        tv_name_tester = dialogMoreDetails.findViewById(R.id.tv_name_tester);
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
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            db.collection("PermissionsUsers")
                    .document("permissionsUsers")
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.get("permissionsMoshref.updateStudent", Boolean.TYPE)) {
                        startActivity(new Intent(getActivity(), HostingActivity.class)
                                .putExtra("fragmentType", "Update_Personal_Information__Fragment")
                                .putExtra("UID", StudentAdapter.students.get(item.getOrder()).getId())
                                .putExtra("permissions", Common.PERMISSIONS_STUDENTN)
                        );
                    } else {
                        new SweetAlertDialog_(getContext()).showDialogError("لم يتم منحك صلاحية تحديث طالب يرجى مراجعة مسؤول المركز");
                    }
                }
            });
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
                //adapter.getFilter().filter(query);
                SearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // adapter.getFilter().filter(newText);
                SearchData(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void SearchData(String newText) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        CollectionReference Ref = db.collection("Student");
        Query query = Ref.whereEqualTo("stage", Common.currentSTAGE);
        if (newText.isEmpty()) {
            students.clear();
            adapter.notifyDataSetChanged();
            LoadData();
        } else {
            students.clear();
            query = query.orderBy("name").startAt(newText).endAt(newText + "\uf8ff");
            query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        isFound.set(true);
                        students.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            students.add(snapshots.toObject(Student.class));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                        students.clear();
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    adapter.notifyDataSetChanged();
                    if (!isFound.get()) {
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addStudent() {
        db.collection("PermissionsUsers")
                .document("permissionsUsers")
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.get("permissionsMoshref.addStudent", Boolean.TYPE)) {
                    startActivity(new Intent(getActivity(), HostingActivity.class)
                            .putExtra("fragmentType", "Personal_Information__Fragment")
                            .putExtra("permissions", Common.PERMISSIONS_STUDENTN));
                } else {
                    new SweetAlertDialog_(getContext()).showDialogError("لم يتم منحك صلاحية إضافة طالب يرجى مراجعة مسؤول المركز");
                }
            }
        });
    }
}
