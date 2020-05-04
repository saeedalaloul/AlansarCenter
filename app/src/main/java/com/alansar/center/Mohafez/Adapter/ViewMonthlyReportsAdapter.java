package com.alansar.center.Mohafez.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Mohafez.Model.DailyReport;
import com.alansar.center.Mohafez.ViewHolder.ReportsViewHolder;
import com.alansar.center.R;

import java.util.ArrayList;
import java.util.List;

public class ViewMonthlyReportsAdapter extends RecyclerView.Adapter<ReportsViewHolder> implements Filterable {

    public static List<DailyReport> dailyReports;
    private List<DailyReport> dailyReportList;
    private Filter mohafezeenFiltet = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String charString = constraint.toString();

            if (charString.isEmpty()) {
                dailyReports = dailyReportList;
            } else {
                ArrayList<DailyReport> filterList = new ArrayList<>();
                for (DailyReport data : dailyReportList) {
                    if (data.getDayOfWeek().toLowerCase().contains(charString)) {
                        filterList.add(data);
                    }
                }
                dailyReports = filterList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = dailyReports;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dailyReports = (List<DailyReport>) results.values;
            notifyDataSetChanged();
        }
    };

    public ViewMonthlyReportsAdapter(List<DailyReport> students) {
        ViewMonthlyReportsAdapter.dailyReports = students;
        this.dailyReportList = students;
    }

    @NonNull
    @Override
    public ReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_report, parent, false);
        return new ReportsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsViewHolder holder, int position) {
        DailyReport dailyReport = dailyReportList.get(position);
        holder.tv_daily_report_date.setText(dailyReport.getDay() + "/" + dailyReport.getMonth() + "/" + dailyReport.getYear());
        holder.tv_daily_report_day.setText(dailyReport.getDayOfWeek());
        holder.tv_daily_report_start_sura.setText(dailyReport.getSuratStart());
        holder.tv_daily_report_end_sura.setText(dailyReport.getSuratEnd());
        holder.tv_daily_report_start_aya.setText(dailyReport.getAyaStart() + "");
        holder.tv_daily_report_end_aya.setText(dailyReport.getAyaEnd() + "");
        holder.tv_daily_report_evaluation.setText(dailyReport.getEvaluationStudent());
        holder.tv_daily_report_status.setText(dailyReport.getStatusStudent());

        holder.setItemClickListener((view, position1, isLongClick) -> {

        });
    }

    @Override
    public int getItemCount() {
        return dailyReports.size();
    }

    @Override
    public Filter getFilter() {
        return mohafezeenFiltet;
    }
}
