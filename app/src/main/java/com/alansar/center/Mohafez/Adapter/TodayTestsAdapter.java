package com.alansar.center.Mohafez.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Mohafez.ViewHolder.TodayTestsViewHolder;
import com.alansar.center.R;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TodayTestsAdapter extends RecyclerView.Adapter<TodayTestsViewHolder> {

    private List<Exam> exams;
    private FirebaseFirestore db;

    public TodayTestsAdapter(List<Exam> exams) {
        this.exams = exams;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public TodayTestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_exam_mohafez_layout, parent, false);
        return new TodayTestsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TodayTestsViewHolder holder, int position) {
        holder.imgbtn_more.setOnClickListener(View::showContextMenu);
        holder.tv_part_order_exam.setText("" + exams.get(position).getExamPart());
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });


        if (exams.get(position) != null) {
            if (exams.get(position).getIdStudent() != null &&
                    exams.get(position).getIdTester() != null) {
                getNameStudentAndTester(exams.get(position).getIdTester(),
                        exams.get(position).getIdStudent(),
                        holder.tv_tester_name,
                        holder.tv_student_name_order_exam);
            }
        }

    }

    private void getNameStudentAndTester(String idTester, String idStudent, TextView tv_tester_name, TextView tv_student_name_order_exam) {
        db.collection("Student").document(idStudent)
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                tv_student_name_order_exam.setText(documentSnapshot.getString("name"));
            }
        });

        db.collection("Tester").document(idTester)
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                tv_tester_name.setText(documentSnapshot.getString("name"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

}