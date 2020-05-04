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


public class TestersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_tester_name, tv_tester_stage;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;


    public TestersViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_tester_name = itemView.findViewById(R.id.tv_tester_name);
        tv_tester_stage = itemView.findViewById(R.id.tv_tester_stage);
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
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
