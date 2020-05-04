package com.alansar.center.Mohafez.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;

public class TodayTestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_student_name_order_exam, tv_part_order_exam, tv_tester_name;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;


    public TodayTestsViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_student_name_order_exam = itemView.findViewById(R.id.tv_student_name_order_exam_mohafez);
        tv_part_order_exam = itemView.findViewById(R.id.tv_part_order_exam_mohafez);
        tv_tester_name = itemView.findViewById(R.id.tv_name_tester_order_exam_mohafez);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_mohafez);
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
    }
}