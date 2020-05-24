package com.alansar.center.administrator.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alansar.center.Common.Common;
import com.alansar.center.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class HomeFragment extends Fragment {
    private TextView count_students_all, count_mohafzeen_all,
            count_exams_month, count_exams_all, count_students_all_stage_1,
            count_mohafzeen_all_stage_1, count_exams_month_stage_1,
            count_exams_all_stage_1, count_students_all_stage_2,
            count_mohafzeen_all_stage_2, count_exams_month_stage_2,
            count_exams_all_stage_2, count_students_all_stage_3,
            count_mohafzeen_all_stage_3, count_exams_month_stage_3,
            count_exams_all_stage_3, count_students_all_stage_4,
            count_mohafzeen_all_stage_4, count_exams_month_stage_4,
            count_exams_all_stage_4;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        initial(view);
        getDataAllFromDB();
        getDataStage1FromDB();
        getDataStage2FromDB();
        getDataStage3FromDB();
        getDataStage4FromDB();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void getDataStage4FromDB() {
        db.collection("Student")
                .whereEqualTo("stage", Common.SUP_STAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_students_all_stage_4.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Mohafez")
                .whereEqualTo("stage", Common.SUP_STAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_mohafzeen_all_stage_4.setText("" + queryDocumentSnapshots.size());
            }
        });

        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                .whereEqualTo("stage", Common.SUP_STAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_month_stage_4.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("stage", Common.SUP_STAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_all_stage_4.setText("" + queryDocumentSnapshots.size());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getDataStage3FromDB() {
        db.collection("Student")
                .whereEqualTo("stage", Common.FOUNDATION_STAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_students_all_stage_3.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Mohafez")
                .whereEqualTo("stage", Common.FOUNDATION_STAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_mohafzeen_all_stage_3.setText("" + queryDocumentSnapshots.size());
            }
        });

        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                .whereEqualTo("stage", Common.FOUNDATION_STAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_month_stage_3.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("stage", Common.FOUNDATION_STAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_all_stage_3.setText("" + queryDocumentSnapshots.size());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getDataStage2FromDB() {
        db.collection("Student")
                .whereEqualTo("stage", Common.INTERMEDIATE_STAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_students_all_stage_2.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Mohafez")
                .whereEqualTo("stage", Common.INTERMEDIATE_STAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_mohafzeen_all_stage_2.setText("" + queryDocumentSnapshots.size());
            }
        });

        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                .whereEqualTo("stage", Common.INTERMEDIATE_STAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_month_stage_2.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("stage", Common.INTERMEDIATE_STAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_all_stage_2.setText("" + queryDocumentSnapshots.size());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getDataAllFromDB() {
        db.collection("Student")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_students_all.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Mohafez")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_mohafzeen_all.setText("" + queryDocumentSnapshots.size());
            }
        });

        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_month.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_all.setText("" + queryDocumentSnapshots.size());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getDataStage1FromDB() {
        db.collection("Student")
                .whereEqualTo("stage", Common.THE_UPPER_STAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_students_all_stage_1.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Mohafez")
                .whereEqualTo("stage", Common.THE_UPPER_STAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_mohafzeen_all_stage_1.setText("" + queryDocumentSnapshots.size());
            }
        });

        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                .whereEqualTo("stage", Common.THE_UPPER_STAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_month_stage_1.setText("" + queryDocumentSnapshots.size());
            }
        });

        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("stage", Common.THE_UPPER_STAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_all_stage_1.setText("" + queryDocumentSnapshots.size());
            }
        });
    }

    private void initial(View view) {
        count_students_all = view.findViewById(R.id.count_students_all);
        count_mohafzeen_all = view.findViewById(R.id.count_mohafzeen_all);
        count_exams_month = view.findViewById(R.id.count_exams_month);
        count_exams_all = view.findViewById(R.id.count_exams_all);
        count_students_all_stage_1 = view.findViewById(R.id.count_students_all_stage_1);
        count_mohafzeen_all_stage_1 = view.findViewById(R.id.count_mohafzeen_all_stage_1);
        count_exams_month_stage_1 = view.findViewById(R.id.count_exams_month_stage_1);
        count_exams_all_stage_1 = view.findViewById(R.id.count_exams_all_stage_1);
        count_students_all_stage_2 = view.findViewById(R.id.count_students_all_stage_2);
        count_mohafzeen_all_stage_2 = view.findViewById(R.id.count_mohafzeen_all_stage_2);
        count_exams_month_stage_2 = view.findViewById(R.id.count_exams_month_stage_2);
        count_exams_all_stage_2 = view.findViewById(R.id.count_exams_all_stage_2);
        count_students_all_stage_3 = view.findViewById(R.id.count_students_all_stage_3);
        count_mohafzeen_all_stage_3 = view.findViewById(R.id.count_mohafzeen_all_stage_3);
        count_exams_month_stage_3 = view.findViewById(R.id.count_exams_month_stage_3);
        count_exams_all_stage_3 = view.findViewById(R.id.count_exams_all_stage_3);
        count_students_all_stage_4 = view.findViewById(R.id.count_students_all_stage_4);
        count_mohafzeen_all_stage_4 = view.findViewById(R.id.count_mohafzeen_all_stage_4);
        count_exams_month_stage_4 = view.findViewById(R.id.count_exams_month_stage_4);
        count_exams_all_stage_4 = view.findViewById(R.id.count_exams_all_stage_4);

        db = FirebaseFirestore.getInstance();
    }
}
