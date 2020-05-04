package com.alansar.center.Mohafez.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Mohafez.Adapter.TodayTestsAdapter;
import com.alansar.center.R;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayTestsFragment extends Fragment {
    private FirebaseFirestore db;
    private TodayTestsAdapter adapter;
    private ArrayList<Exam> exams;
    private RecyclerView recyclerView;
    private View view;
    private ListenerRegistration registration;


    public TodayTestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mohafez_today_tests, container, false);

        recyclerView = view.findViewById(R.id.exams_orders_rv);
        db = FirebaseFirestore.getInstance();
        exams = new ArrayList<>();
        adapter = new TodayTestsAdapter(exams);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);
        return view;
    }

    private void loadData() {
        if (Common.currentPerson != null && Common.currentPerson.getId() != null) {
            Calendar calendar = Calendar.getInstance();
            registration = db.collection("Exam")
                    .whereEqualTo("day", calendar.get(Calendar.DAY_OF_MONTH))
                    .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                    .whereEqualTo("year", calendar.get(Calendar.YEAR))
                    .whereEqualTo("statusAcceptance", 2)
                    .whereEqualTo("idMohafez", Common.currentPerson.getId())
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
                                                if (!doc.getBoolean("isSeenExam.isSeenMohafez")) {
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
                            }
                            if (exams.isEmpty()) {
                                view.findViewById(R.id.tv_check_exams_today).setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                view.findViewById(R.id.tv_check_exams_today).setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            Common.currentPerson = Paper.book().read(Common.PERSON);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }
}
