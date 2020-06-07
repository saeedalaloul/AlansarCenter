package com.alansar.center.testers.Fragments;

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
    private TextView count_exams_all, count_exams_month, count_exams_all_year;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tester_home, container, false);
        initial(view);
        if (Common.currentPerson != null) {
            getDataExamsFromDB();
        } else {
            Common.currentPerson = Paper.book().read(Common.PERSON);
            Common.currentPermission = Paper.book().read(Common.PERMISSION);
            getDataExamsFromDB();
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void getDataExamsFromDB() {
        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("idTester", Common.currentPerson.getId())
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_all.setText("" + queryDocumentSnapshots.size());
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                .whereEqualTo("idTester", Common.currentPerson.getId())
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_month.setText("" + queryDocumentSnapshots.size());
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));

        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("statusAcceptance", 3)
                .whereEqualTo("idTester", Common.currentPerson.getId())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_all_year.setText("" + queryDocumentSnapshots.size());
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    private void initial(View view) {
        count_exams_all = view.findViewById(R.id.count_exams_all);
        count_exams_month = view.findViewById(R.id.count_exams_month);
        count_exams_all_year = view.findViewById(R.id.count_exams_all_year);
        db = FirebaseFirestore.getInstance();
    }
}
