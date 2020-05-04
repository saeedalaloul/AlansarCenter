package com.alansar.center.testers.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.R;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.alansar.center.testers.ViewHolder.OrdersExamsViewHolder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrdersExamsAdapter extends RecyclerView.Adapter<OrdersExamsViewHolder> {

    private List<Exam> exams;
    private FirebaseFirestore db;

    public OrdersExamsAdapter(List<Exam> exams) {
        this.exams = exams;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrdersExamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_exam_tester_layout, parent, false);
        return new OrdersExamsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrdersExamsViewHolder holder, int position) {
        holder.imgbtn_more.setOnClickListener(View::showContextMenu);
        holder.tv_part_order_exam.setText(exams.get(position).getExamPart());
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });

        if (exams.get(position) != null) {
            if (exams.get(position).getIdStudent() != null) {
                db.collection("Student").document(exams.get(position).getIdStudent())
                        .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        holder.tv_student_name_order_exam.setText(documentSnapshot.getString("name"));
                    }
                });
            }

            if (exams.get(position).getIdMohafez() != null) {
                db.collection("Mohafez").document(exams.get(position).getIdMohafez())
                        .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        holder.tv_name_mohafez.setText(documentSnapshot.getString("name"));
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