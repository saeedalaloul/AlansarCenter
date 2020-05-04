package com.alansar.center.supervisor_exams.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;

public class TodayTestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_student_name_order_exam, tv_part_order_exam, tv_status_order_exam, tv_tester_name, tv_mohafez_name;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;


    public TodayTestsViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_student_name_order_exam = itemView.findViewById(R.id.tv_student_name_order_exam);
        tv_part_order_exam = itemView.findViewById(R.id.tv_part_order_exam);
        tv_status_order_exam = itemView.findViewById(R.id.tv_status_order_exam);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_tester);
        tv_tester_name = itemView.findViewById(R.id.tv_name_tester_order_exam_today);
        tv_mohafez_name = itemView.findViewById(R.id.tv_name_mohafez_order_exam_today);
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
        contextMenu.add(0, 0, getAdapterPosition(), Common.PLACE_AN_ORDER);
    }
}
