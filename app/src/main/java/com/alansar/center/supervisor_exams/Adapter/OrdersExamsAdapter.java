package com.alansar.center.supervisor_exams.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.R;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.alansar.center.supervisor_exams.ViewHolder.OrdersExamsViewHolder;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_exam_layout, parent, false);
        return new OrdersExamsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrdersExamsViewHolder holder, int position) {
        holder.imgbtn_more.setOnClickListener(View::showContextMenu);
        holder.tv_part_order_exam.setText(exams.get(position).getExamPart());
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });
        getNameStudentFromDB(position, holder.tv_student_name_order_exam);
        getTypeStatusOrder(exams.get(position).getStatusAcceptance(), holder.tv_status_order_exam);
    }

    @Override
    public int getItemCount() {
        return exams.size();
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

    private void getTypeStatusOrder(int statusAcceptance, TextView tv_status_order_exam) {
        if (statusAcceptance == 0) {
            tv_status_order_exam.setText("قيد الطلب حاليا");
        } else if (statusAcceptance == 1) {
            tv_status_order_exam.setText("تم قبول الطلب من قبل المشرف");
        } else if (statusAcceptance == 2) {
            tv_status_order_exam.setText("لقد أجريت الموافقة على طلب الإختبار");
        } else if (statusAcceptance == 3) {
            tv_status_order_exam.setText("لقد أجرى الطالب الإختبار");
        } else if (statusAcceptance == -1) {
            tv_status_order_exam.setText("تم رفض الطلب من قبل المشرف");
        } else if (statusAcceptance == -2) {
            tv_status_order_exam.setText("لقد أجريت الرفض على طلب الإختبار");
        } else if (statusAcceptance == -3) {
            tv_status_order_exam.setText("الطالب لم يجري الإختبار");
        }
    }

}