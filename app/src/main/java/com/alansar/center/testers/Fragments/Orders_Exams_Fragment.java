package com.alansar.center.testers.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.alansar.center.testers.Activitys.PlaceExamActivity;
import com.alansar.center.testers.Adapter.OrdersExamsAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Orders_Exams_Fragment extends Fragment {
    private FirebaseFirestore db;
    private OrdersExamsAdapter adapter;
    private ArrayList<Exam> exams;
    private View view;
    private RecyclerView recyclerView;
    private AlertDialog alertDialog;
    private Spinner sp_number_questions;
    private List<String> questionsExamNumber;
    private ListenerRegistration registration;
    private String StudentName, IdMoshrefExams;

    public Orders_Exams_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tester_orders__exams, container, false);
        recyclerView = view.findViewById(R.id.exams_orders_rv);
        db = FirebaseFirestore.getInstance();
        exams = new ArrayList<>();
        adapter = new OrdersExamsAdapter(exams);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);
        questionsExamNumber = new ArrayList<>();
        questionsExamNumber.add("اختر عدد أسئلة الإختبار");
        getIdMoshrefExams();
        return view;
    }

    private void getDetailsQuestionsExam() {
        if (IdMoshrefExams != null && !IdMoshrefExams.isEmpty()) {
            db.collection("ExamsSettings")
                    .document(IdMoshrefExams)
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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadData() {
        if (Common.currentPerson != null && Common.currentPerson.getId() != null) {
            registration = db.collection("Exam")
                    .whereEqualTo("statusAcceptance", 2)
                    .whereEqualTo("idTester", Common.currentPerson.getId())
                    .orderBy("year", Query.Direction.DESCENDING)
                    .orderBy("month", Query.Direction.DESCENDING)
                    .orderBy("day", Query.Direction.DESCENDING)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.w("sss", "listen:error" + e.getLocalizedMessage());
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            SimpleDateFormat dateformat = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.getDefault());
                            }
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        Log.d("sss", "New exam: " + dc.getDocument().getData());
                                        exams.clear();
                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            if (doc.exists()) {
                                                if (!doc.getBoolean("isSeenExam.isSeenTester")) {
                                                    doc.getReference().update("isSeenExam.isSeenTester", true);
                                                }
                                                Exam exam = doc.toObject(Exam.class);
                                                if (dateformat != null) {
                                                    String date = dateformat.format(Timestamp.now().toDate().getTime());
                                                    try {
                                                        if (exam.getDate() != null && !exam.getDate().trim().isEmpty()) {
                                                            getDifferenceDate(dateformat.parse(exam.getDate()), dateformat.parse(date), doc);
                                                        }
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
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
                                                if (!doc.getBoolean("isSeenExam.isSeenTester")) {
                                                    doc.getReference().update("isSeenExam.isSeenTester", true);
                                                }
                                                Exam exam = doc.toObject(Exam.class);
                                                if (dateformat != null) {
                                                    String date = dateformat.format(Timestamp.now().toDate().getTime());
                                                    try {
                                                        if (exam.getDate() != null && !exam.getDate().trim().isEmpty()) {
                                                            getDifferenceDate(dateformat.parse(exam.getDate()), dateformat.parse(date), doc);
                                                        }
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
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
                                if (view != null) {
                                    if (exams.isEmpty()) {
                                        view.findViewById(R.id.tv_check_exams_today).setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    } else {
                                        view.findViewById(R.id.tv_check_exams).setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.START_THE_EXAM)) {
            Calendar calendar = Calendar.getInstance();
            if (exams.get(item.getOrder()).getDay() == calendar.get(Calendar.DAY_OF_MONTH)
                    && exams.get(item.getOrder()).getMonth() == calendar.get(Calendar.MONTH) + 1
                    && exams.get(item.getOrder()).getYear() == calendar.get(Calendar.YEAR)) {
                getNameStudentFromDB(item.getOrder(), null);
                if (exams.get(item.getOrder()).getExamPart().equals(Common.PART_OF_AMA)) {
                    if (StudentName != null && !StudentName.isEmpty()) {
                        if (IdMoshrefExams != null && !IdMoshrefExams.isEmpty()) {
                            db.collection("ExamsSettings")
                                    .document(IdMoshrefExams)
                                    .get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot != null && documentSnapshot.exists()) {
                                    startActivity(new Intent(getContext(), PlaceExamActivity.class)
                                            .putExtra("questionsNumberExam", Objects.requireNonNull(documentSnapshot.get("numberQuestionsPart")).toString())
                                            .putExtra("idExam", exams.get(item.getOrder()).getId())
                                            .putExtra("StudentName", StudentName));
                                }
                            });
                        }
                    }
                } else {
                    startExamOfDB(item.getOrder());
                }
            } else {
                new SweetAlertDialog_(getContext()).showDialogError("عذرا يجب إجراء الإختبار في هذا التاريخ المحدد : " +
                        exams.get(item.getOrder()).getDay() + "/"
                        + exams.get(item.getOrder()).getMonth() + "/"
                        + exams.get(item.getOrder()).getYear());
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

    private void getIdMoshrefExams() {
        db.collection("SuperVisorExams")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                IdMoshrefExams = queryDocumentSnapshots.getDocuments().get(0).getId();
                getDetailsQuestionsExam();
            }
        });
    }

    private void getDifferenceDate(Date startDate, Date endDate, QueryDocumentSnapshot doc) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;
        int status = Integer.parseInt(Objects.requireNonNull(doc.get("statusAcceptance")).toString());

        new Thread(() -> {
            if (!Common.hasActiveInternetConnection(getContext())) {
                Log.d("sss", "لا يوجد اتصال بالإنترنت ..");
            } else {
                Log.d("sss", "متصل بالإنترنت ..");
                if (status == 2) {
                    Log.d("sss", "" + status);
                    if ((int) elapsedDays >= 1) {
                        doc.getReference().update("statusAcceptance", -3);
                    }
                }
            }
        }).start();

        Log.d("sss", elapsedDays + " " + elapsedHours + " " + elapsedMinutes + " " + elapsedSeconds);
    }
}