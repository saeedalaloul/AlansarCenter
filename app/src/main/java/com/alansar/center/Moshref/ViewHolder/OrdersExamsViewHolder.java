package com.alansar.center.Moshref.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;

public class OrdersExamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_student_name_order_exam, tv_part_order_exam, tv_status_order_exam
            , tv_notes_order_exam, tv_date_order_exam;
    public ImageButton imgbtn_more;
    public LinearLayout linearLayout,linearLayout_date;
    private ItemClickListener itemClickListener;


    public OrdersExamsViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_student_name_order_exam = itemView.findViewById(R.id.tv_student_name_order_exam);
        tv_part_order_exam = itemView.findViewById(R.id.tv_part_order_exam);
        tv_status_order_exam = itemView.findViewById(R.id.tv_status_order_exam);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_tester);
        tv_notes_order_exam = itemView.findViewById(R.id.tv_notes_order_exam);
        tv_date_order_exam = itemView.findViewById(R.id.tv_date_order_exam);
        linearLayout = itemView.findViewById(R.id.linear2);
        linearLayout_date = itemView.findViewById(R.id.linear_date);
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
