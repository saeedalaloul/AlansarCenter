package com.alansar.center.Mohafez.Fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alansar.center.Common.Common;
import com.alansar.center.Models.GroupMembers;
import com.alansar.center.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private TextView count_students_all_group, count_exams_month_group,
            count_exams_all_group;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mohafez_home, container, false);
        initial(view);
        if (Common.currentGroupId != null) {
            getDataGroupFromDB();
        } else {
            Common.currentSTAGE = Paper.book().read(Common.STAGE);
            Common.currentPerson = Paper.book().read(Common.PERSON);
            Common.currentGroupId = Paper.book().read(Common.GROUPID);
            Common.currentPermission = Paper.book().read(Common.PERMISSION);
            getDataGroupFromDB();
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void getDataGroupFromDB() {
        if (Common.currentGroupId != null) {
            db.collection("GroupMembers")
                    .document(Common.currentGroupId)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    ArrayList<String> groupMembers = Objects.requireNonNull(documentSnapshot.toObject(GroupMembers.class)).getGroupMembers();
                    if (groupMembers != null && !groupMembers.isEmpty()) {
                        count_students_all_group.setText("" + groupMembers.size());
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }

        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                .whereEqualTo("stage", Common.currentSTAGE)
                .whereEqualTo("idMohafez", Common.currentPerson.getId())
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_month_group.setText("" + queryDocumentSnapshots.size());
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));

        db.collection("Exam")
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("stage", Common.currentSTAGE)
                .whereEqualTo("idMohafez", Common.currentPerson.getId())
                .whereEqualTo("statusAcceptance", 3)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                count_exams_all_group.setText("" + queryDocumentSnapshots.size());
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));

    }

    private void initial(View view) {
        count_students_all_group = view.findViewById(R.id.count_students_all_group);
        count_exams_month_group = view.findViewById(R.id.count_exams_month_group);
        count_exams_all_group = view.findViewById(R.id.count_exams_all_group);
        db = FirebaseFirestore.getInstance();
    }

}
