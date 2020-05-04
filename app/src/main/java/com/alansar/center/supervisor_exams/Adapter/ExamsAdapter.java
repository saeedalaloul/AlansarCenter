package com.alansar.center.supervisor_exams.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.R;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.alansar.center.supervisor_exams.ViewHolder.ExamsViewHolder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsViewHolder> {
    private ArrayList<Exam> exams;
    private FirebaseFirestore db;

    public ExamsAdapter(ArrayList<Exam> exams) {
        this.exams = exams;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ExamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_layout, parent, false);
        return new ExamsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamsViewHolder holder, int position) {
        Exam exam = exams.get(position);
        holder.tv_part_exam.setText(exam.getExamPart());
        getNameStudentFromDB(position, holder.tv_student_name_exam);
        calcMarksExam(position, holder.tv_mark_exam);
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });

    }

    @SuppressLint("SetTextI18n")
    private void calcMarksExam(int position, TextView tv_mark_exam) {
        if (exams.get(position) != null && exams.get(position).getMarksExamQuestions() != null) {
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

    private void getNameStudentFromDB(int position, TextView tv_name) {
        if (exams.get(position) != null) {
            if (exams.get(position).getIdStudent() != null) {
                db.collection("Student").document(exams.get(position).getIdStudent())
                        .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tv_name.setText(documentSnapshot.getString("name"));
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }
}
