package com.alansar.center.Mohafez.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Mohafez.Model.Report;
import com.alansar.center.Mohafez.ViewHolder.MonthlyReportsViewHolder;
import com.alansar.center.R;
import com.alansar.center.Services.DownloadReportService;

import java.util.ArrayList;

public class MonthlyReportsAdapter extends RecyclerView.Adapter<MonthlyReportsViewHolder> {

    private ArrayList<Report> reports;
    private Context context;

    public MonthlyReportsAdapter(ArrayList<Report> reports, Context context) {
        this.reports = reports;
        this.context = context;
    }

    @NonNull
    @Override
    public MonthlyReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monthly_report, parent, false);
        return new MonthlyReportsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MonthlyReportsViewHolder holder, int position) {
        holder.tv_month_report.setText("" + reports.get(position).getMoth());
        holder.tv_year_report.setText("" + reports.get(position).getYear());
        holder.imgbtn_download_report.setOnClickListener(view -> {
            if (DownloadReportService.isRunning) {
                Toast.makeText(context, "عذرا يتم تحميل التقرير الشهري في الخلفية ..", Toast.LENGTH_SHORT).show();
            } else{
                context.startService(new Intent(context, DownloadReportService.class)
                        .putExtra("month", reports.get(position).getMoth())
                        .putExtra("year", reports.get(position).getYear()));
            }
        });
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }
}
