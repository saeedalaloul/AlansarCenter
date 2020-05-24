package com.alansar.center.testers.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;

public class OrdersExamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_student_name_order_exam, tv_part_order_exam, tv_name_mohafez,
            tv_stage_order_exam,tv_date_order_exam;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;


    public OrdersExamsViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_student_name_order_exam = itemView.findViewById(R.id.tv_student_name_order_exam_tester);
        tv_part_order_exam = itemView.findViewById(R.id.tv_part_order_exam_tester);
        tv_name_mohafez = itemView.findViewById(R.id.tv_name_mohafez_order_exam_tester);
        tv_stage_order_exam = itemView.findViewById(R.id.tv_stage_order_exam_tester);
        tv_date_order_exam = itemView.findViewById(R.id.tv_date_order_exam_tester);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_tester);
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
        contextMenu.add(0, 0, getAdapterPosition(), Common.START_THE_EXAM);
    }
}
