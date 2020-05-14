package com.alansar.center.administrator.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;
import com.alansar.center.students.Model.Student;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_student_name, tv_student_stage;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;
    private List<Student> students;
    private FirebaseFirestore db;


    public StudentViewHolder(@NonNull View itemView, List<Student> students) {
        super(itemView);
        tv_student_name = itemView.findViewById(R.id.tv_student_name);
        tv_student_stage = itemView.findViewById(R.id.tv_student_stage);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_student);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        this.students = students;
        db = FirebaseFirestore.getInstance();
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the Action");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.VIEW_THE_LATEST_EXAM);
        db.collection("Person").document(students.get(getAdapterPosition()).getId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.getBoolean("enableAccount")) {
                    contextMenu.add(0, 2, getAdapterPosition(), Common.ISDISABLEACCOUNT);
                } else {
                    contextMenu.add(0, 2, getAdapterPosition(), Common.ISENABLEACCOUNT);
                }
            }
        });
    }
}
