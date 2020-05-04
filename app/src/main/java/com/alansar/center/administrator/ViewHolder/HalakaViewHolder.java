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

public class HalakaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_halaka_name, tv_mohafez_name, tv_student_num, tv_halaka_stage;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;

    public HalakaViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_halaka_name = itemView.findViewById(R.id.tv_halaka_name);
        tv_mohafez_name = itemView.findViewById(R.id.tv_mohafez_name);
        tv_halaka_stage = itemView.findViewById(R.id.tv_halaka_stage);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_halaka);
        tv_student_num = itemView.findViewById(R.id.tv_student_num);

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

        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
