package com.alansar.center.Mohafez.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;

public class MonthlyReportsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_month_report, tv_year_report;
    public ImageButton imgbtn_download_report;
    private ItemClickListener itemClickListener;


    public MonthlyReportsViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_month_report = itemView.findViewById(R.id.tv_month_report);
        tv_year_report = itemView.findViewById(R.id.tv_year_report);
        imgbtn_download_report = itemView.findViewById(R.id.imgbtn_download_report);
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