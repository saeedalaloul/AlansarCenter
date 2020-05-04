package com.alansar.center.Mohafez.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;

public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_student_name, tv_student_stage;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;


    public StudentViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_student_name = itemView.findViewById(R.id.tv_student_name);
        tv_student_stage = itemView.findViewById(R.id.tv_student_stage);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_student);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
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
        contextMenu.add(0, 0, getAdapterPosition(), Common.ADD_DAILY_RECITATIONS);
        contextMenu.add(0, 1, getAdapterPosition(), Common.VIEW_THE_LATEST_MONTHLY_REPORT);
        contextMenu.add(0, 1, getAdapterPosition(), Common.VIEW_THE_LATEST_EXAM);
        contextMenu.add(0, 2, getAdapterPosition(), Common.REQUEST_A_TEST);
    }
}
