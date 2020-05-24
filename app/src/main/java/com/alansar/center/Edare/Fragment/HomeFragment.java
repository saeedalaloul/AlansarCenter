package com.alansar.center.Edare.Fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alansar.center.Common.Common;
import com.alansar.center.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private TextView count_students_all_stage,
            count_mohafzeen_all_stage, count_exams_month_stage,
            count_exams_all_stage;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edare_home, container, false);
        initial(view);
        if (Common.currentPerson != null
                && Common.currentSTAGE != null) {
            getDataStageFromDB();
        } else {
            Common.currentSTAGE = Paper.book().read(Common.STAGE);
            Common.currentPerson = Paper.book().read(Common.PERSON);
            Common.currentPermission = Paper.book().read(Common.PERMISSION);
            getDataStageFromDB();
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void getDataStageFromDB() {
        db.collection("Student")
                .whereEqualTo("stage", Common.currentSTAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_students_all_stage.setText("" + queryDocumentSnapshots.size());
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));

        db.collection("Mohafez")
                .whereEqualTo("stage", Common.currentSTAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_mohafzeen_all_stage.setText("" + queryDocumentSnapshots.size());
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));

        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                .whereEqualTo("stage", Common.currentSTAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_month_stage.setText("" + queryDocumentSnapshots.size());
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));

        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("stage", Common.currentSTAGE)
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_all_stage.setText("" + queryDocumentSnapshots.size());
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    private void initial(View view) {
        count_students_all_stage = view.findViewById(R.id.count_students_all_stage);
        count_mohafzeen_all_stage = view.findViewById(R.id.count_mohafzeen_all_stage);
        count_exams_month_stage = view.findViewById(R.id.count_exams_month_stage);
        count_exams_all_stage = view.findViewById(R.id.count_exams_all_stage);

        db = FirebaseFirestore.getInstance();
    }


}
