package com.alansar.center.testers.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.alansar.center.testers.Activitys.PlaceExamActivity;
import com.alansar.center.testers.Adapter.TodayTestsAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayTestsFragment extends Fragment {
    private FirebaseFirestore db;
    private TodayTestsAdapter adapter;
    private ArrayList<Exam> exams;
    private View view;
    private RecyclerView recyclerView;
    private AlertDialog alertDialog;
    private Spinner sp_number_questions;
    private List<String> questionsExamNumber;
    private ListenerRegistration registration;
    private String StudentName;

    public TodayTestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tester_today_tests, container, false);
        recyclerView = view.findViewById(R.id.exams_orders_rv);
        db = FirebaseFirestore.getInstance();
        exams = new ArrayList<>();
        adapter = new TodayTestsAdapter(exams);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);
        questionsExamNumber = new ArrayList<>();
        questionsExamNumber.add("اختر عدد أسئلة الإختبار");
        getDetailsQuestionsExam();
        return view;
    }

    private void getDetailsQuestionsExam() {
        db.collection("ExamsSettings")
                .document("examsSettings")
                .get().addOnSuccessListener(documentSnapshot -> {
            String maxQuestions = Objects.requireNonNull(documentSnapshot.get("maxQuestionsExam")).toString();
            String minQuestions = Objects.requireNonNull(documentSnapshot.get("minQuestionsExam")).toString();
            if (Integer.parseInt(maxQuestions) == Integer.parseInt(minQuestions)) {
                questionsExamNumber.add("" + maxQuestions);
            } else {
                for (int i = Integer.parseInt(minQuestions); i <= Integer.parseInt(maxQuestions); i++) {
                    questionsExamNumber.add("" + i);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        exams.clear();
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    private void loadData() {
        if (Common.currentPerson != null && Common.currentPerson.getId() != null) {
            Calendar calendar = Calendar.getInstance();
            registration = db.collection("Exam")
                    .whereEqualTo("day", calendar.get(Calendar.DAY_OF_MONTH))
                    .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                    .whereEqualTo("year", calendar.get(Calendar.YEAR))
                    .whereEqualTo("statusAcceptance", 2)
                    .whereEqualTo("idTester", Common.currentPerson.getId())
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
                                                if (doc.getBoolean("isSeenExam.isSeenTester") != null
                                                        && !doc.getBoolean("isSeenExam.isSeenTester")) {
                                                    doc.getReference().update("isSeenExam.isSeenTester", true);
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
                                                if (doc.getBoolean("isSeenExam.isSeenTester") != null &&
                                                        !doc.getBoolean("isSeenExam.isSeenTester")) {
                                                    doc.getReference().update("isSeenExam.isSeenTester", true);
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
                                    view.findViewById(R.id.tv_check_exams_today).setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    view.findViewById(R.id.tv_check_exams_today).setVisibility(View.GONE);
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
        if (item.getTitle().equals(Common.START_THE_EXAM)) {
            getNameStudentFromDB(item.getOrder(), null);
            if (exams.get(item.getOrder()).getExamPart().equals(Common.PART_OF_AMA)) {
                if (StudentName != null && !StudentName.isEmpty()) {
                    db.collection("ExamsSettings")
                            .document("examsSettings")
                            .get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            startActivity(new Intent(getContext(), PlaceExamActivity.class)
                                    .putExtra("questionsNumberExam", Objects.requireNonNull(documentSnapshot.get("numberQuestionsPart")).toString())
                                    .putExtra("idExam", exams.get(item.getOrder()).getId())
                                    .putExtra("StudentName", StudentName));
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "اسم الطالب غير متوفر", Toast.LENGTH_SHORT).show();
                }
            } else {
                startExamOfDB(item.getOrder());
            }
        }
        return super.onContextItemSelected(item);
    }

    private void startExamOfDB(int order) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View addTesterDialogView = factory.inflate(R.layout.start_exam_dialog, null);
        alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity())).create();
        alertDialog.setView(addTesterDialogView);
        alertDialog.show();
        sp_number_questions = alertDialog.findViewById(R.id.start_exam_spinner_number_questions);
        assert sp_number_questions != null;
        sp_number_questions.setAdapter(new ArrayAdapter<>(Objects.requireNonNull(getContext())
                , android.R.layout.simple_spinner_item,
                questionsExamNumber));
        Button btn_add = alertDialog.findViewById(R.id.start_exam_add_btn_save);
        EditText et_name = alertDialog.findViewById(R.id.start_exam_et_name);
        getNameStudentFromDB(order, et_name);
        assert btn_add != null;
        btn_add.setOnClickListener(view -> {
            if (validateInputs()) {
                alertDialog.dismiss();
                if (et_name != null) {
                    startActivity(new Intent(getContext(), PlaceExamActivity.class)
                            .putExtra("questionsNumberExam", sp_number_questions.getSelectedItem().toString())
                            .putExtra("idExam", exams.get(order).getId())
                            .putExtra("StudentName", et_name.getText().toString()));
                }
            }
        });
        ImageButton imbtn_close = alertDialog.findViewById(R.id.start_exam_imgbtn_close);
        assert imbtn_close != null;
        imbtn_close.setOnClickListener(view121 -> alertDialog.dismiss());
    }

    private void getNameStudentFromDB(int order, EditText et_name) {
        if (exams.get(order) != null && exams.get(order).getIdStudent() != null) {
            db.collection("Student").document(exams.get(order).getIdStudent())
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (et_name != null) {
                        et_name.setText(documentSnapshot.getString("name"));
                    } else {
                        StudentName = documentSnapshot.getString("name");
                    }
                }
            });
        }
    }

    private boolean validateInputs() {
        if (sp_number_questions.getSelectedItemPosition() != 0) {
            return true;
        } else {
            new SweetAlertDialog_(getContext()).showDialogError("يجب اختيار عدد أسئلة الإخنبار !");
            return false;
        }
    }
}
