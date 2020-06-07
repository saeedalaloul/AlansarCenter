package com.alansar.center.Mohafez.Fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Mohafez.Adapter.ExamsAdapter;
import com.alansar.center.R;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;

import io.paperdb.Paper;

public class ExamsFragment extends Fragment {

    private ArrayList<Exam> exams;
    private ExamsAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private View view;
    private TextView tv_name_student, tv_part_exam,
            tv_date_exam, tv_name_mohafez,
            tv_mark_exam, tv_notes_exam,
            tv_name_tester;
    private LinearLayout li_question_exam_ed;
    private ListenerRegistration registration;


    public ExamsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mohafez_exams, container, false);
        return view;
    }

    private void Initialization(View view) {
        db = FirebaseFirestore.getInstance();
        exams = new ArrayList<>();
        adapter = new ExamsAdapter(exams);
        recyclerView = view.findViewById(R.id.exams_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);
        loadData();
    }

    private void loadData() {
        if (Common.currentPerson != null) {
            registration = db.collection("Exam")
                    .whereEqualTo("statusAcceptance", 3)
                    .whereEqualTo("idMohafez", Common.currentPerson.getId())
                    .orderBy("year", Query.Direction.DESCENDING)
                    .orderBy("month", Query.Direction.DESCENDING)
                    .orderBy("day", Query.Direction.DESCENDING)
                    .limit(20)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.w("sss", "listen:error" + e.getLocalizedMessage());
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        Log.d("sss", "New exam: " + dc.getDocument().getData());
                                        exams.clear();
                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            if (doc.exists()) {
                                                if (doc.getBoolean("isSeenExam.isSeenMohafez") != null
                                                        && !doc.getBoolean("isSeenExam.isSeenMohafez")) {
                                                    doc.getReference().update("isSeenExam.isSeenMohafez", true);
                                                }
                                                Exam exam = doc.toObject(Exam.class);
                                                exams.add(exam);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        break;
                                    case MODIFIED:
                                        Log.d("sss", "Modified exam: " + dc.getDocument().getData());
                                        exams.clear();
                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            if (doc.exists()) {
                                                if (!doc.getBoolean("isSeenExam.isSeenMohafez")) {
                                                    doc.getReference().update("isSeenExam.isSeenMohafez", true);
                                                }
                                                Exam exam = doc.toObject(Exam.class);
                                                exams.add(exam);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        break;
                                    case REMOVED:
                                        Log.d("sss", "Removed exam: " + dc.getDocument().getData());
                                        exams.remove(dc.getOldIndex());
                                        adapter.notifyDataSetChanged();
                                        break;
                                }
                                if (exams.isEmpty()) {
                                    view.findViewById(R.id.tv_check_exams).setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    view.findViewById(R.id.tv_check_exams).setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
        } else {
            Common.currentPerson = Paper.book().read(Common.PERSON);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.MORE_DETAILS)) {
            showDialogMoreDetails(item.getOrder());
        }
        return super.onContextItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void showDialogMoreDetails(int order) {
        if (getContext() != null) {
            LayoutInflater factory = LayoutInflater.from(getActivity());
            @SuppressLint("InflateParams") final View moreDetailsDialogView
                    = factory.inflate(R.layout.more_details_exam_dialog_custom_super_visor, null);
            AlertDialog dialogMoreDetails = new AlertDialog.Builder(getContext())
                    .setView(moreDetailsDialogView).create();
            dialogMoreDetails.show();
            Objects.requireNonNull(dialogMoreDetails.getWindow()).setBackgroundDrawableResource(R.color.fbutton_color_transparent);
            InitializationDialog(dialogMoreDetails);

            Exam exam = exams.get(order);
            tv_name_student.setText(exam.getNotes());
            tv_part_exam.setText(exam.getExamPart());
            tv_date_exam.setText(exam.getDay() + "/" + exam.getMonth() + "/" + exam.getYear());
            getNameStudentFromDB(order, tv_name_student);
            getNameMohafezFromDB(order, tv_name_mohafez);
            getNameTesterFromDB(order, tv_name_tester);
            tv_notes_exam.setText(exam.getNotes());
            calcMarksExam(order, tv_mark_exam);
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

    private void getNameTesterFromDB(int order, TextView tv_name_tester) {
        if (exams.get(order) != null && exams.get(order).getIdTester() != null) {
            db.collection("Tester").document(exams.get(order).getIdTester())
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (tv_name_tester != null) {
                        tv_name_tester.setText(documentSnapshot.getString("name"));
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }
    }

    private void getNameStudentFromDB(int order, TextView et_name) {
        if (exams.get(order) != null && exams.get(order).getIdStudent() != null) {
            db.collection("Student").document(exams.get(order).getIdStudent())
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (et_name != null) {
                        et_name.setText(documentSnapshot.getString("name"));
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));

        }
    }

    private void getNameMohafezFromDB(int order, TextView et_name) {
        if (exams.get(order) != null && exams.get(order).getIdMohafez() != null) {
            db.collection("Mohafez").document(exams.get(order).getIdMohafez())
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (et_name != null) {
                        et_name.setText(documentSnapshot.getString("name"));
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }
    }

    @SuppressLint("SetTextI18n")
    private void calcMarksExam(int position, TextView tv_mark_exam) {
        if (exams.get(position) != null) {
            double mark = 0;
            if (exams.get(position).getMarksExamQuestions() != null) {
                for (int i = 1; i <= exams.get(position).getMarksExamQuestions().size(); i++) {
                    if (exams.get(position).getMarksExamQuestions().get("" + i) != null) {
                        mark += exams.get(position).getMarksExamQuestions().get("" + i);
                    }
                }
            }
            double result = round(mark / exams.get(position).getMarksExamQuestions().size());
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
    public void onResume() {
        super.onResume();
        Initialization(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }
}
