package com.alansar.center.Moshref.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;

public class ExamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_student_name_exam, tv_part_exam, tv_mark_exam;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;


    public ExamsViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_student_name_exam = itemView.findViewById(R.id.tv_student_name_exam);
        tv_part_exam = itemView.findViewById(R.id.tv_part_exam);
        tv_mark_exam = itemView.findViewById(R.id.tv_mark_exam);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_exam);
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
        contextMenu.add(0, 0, getAdapterPosition(), Common.MORE_DETAILS);
    }
}