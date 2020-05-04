package com.alansar.center.Mohafez.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;

public class ReportsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_daily_report_day, tv_daily_report_status, tv_daily_report_end_sura,
            tv_daily_report_end_aya, tv_daily_report_date, tv_daily_report_evaluation, tv_daily_report_start_aya, tv_daily_report_start_sura;
    private ItemClickListener itemClickListener;


    public ReportsViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_daily_report_day = itemView.findViewById(R.id.tv_daily_report_day);
        tv_daily_report_status = itemView.findViewById(R.id.tv_daily_report_status);
        tv_daily_report_end_sura = itemView.findViewById(R.id.tv_daily_report_end_sura);
        tv_daily_report_end_aya = itemView.findViewById(R.id.tv_daily_report_end_aya);
        tv_daily_report_date = itemView.findViewById(R.id.tv_daily_report_date);
        tv_daily_report_evaluation = itemView.findViewById(R.id.tv_daily_report_evaluation);
        tv_daily_report_start_aya = itemView.findViewById(R.id.tv_daily_report_start_aya);
        tv_daily_report_start_sura = itemView.findViewById(R.id.tv_daily_report_start_sura);
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
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE_DAILY_RECITATIONS);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE_DAILY_RECITATIONS);
    }
}
