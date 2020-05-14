package com.alansar.center.Edare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Mohafez.Model.Mohafez;
import com.alansar.center.R;
import com.alansar.center.Edare.ViewHolder.MohafezViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MohafezAdapter extends RecyclerView.Adapter<MohafezViewHolder> implements Filterable {
    public static List<Mohafez> mohafezeen;
    private List<Mohafez> mohafezArrayList;
    private Filter mohafezeenFiltet = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String charString = constraint.toString();

            if (charString.isEmpty()) {
                mohafezeen = mohafezArrayList;
            } else {
                ArrayList<Mohafez> filterList = new ArrayList<>();
                for (Mohafez data : mohafezArrayList) {
                    if (data.getName().toLowerCase().contains(charString)) {
                        filterList.add(data);
                    }
                }
                mohafezeen = filterList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = mohafezeen;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mohafezeen = (List<Mohafez>) results.values;
            notifyDataSetChanged();
        }
    };

    public MohafezAdapter(ArrayList<Mohafez> mohafezeen) {
        MohafezAdapter.mohafezeen = mohafezeen;
        this.mohafezArrayList = mohafezeen;
    }

    @NonNull
    @Override
    public MohafezViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mohafez_layout, parent, false);
        return new MohafezViewHolder(v, mohafezeen);
    }

    @Override
    public void onBindViewHolder(@NonNull MohafezViewHolder holder, int position) {
        holder.imgbtn_more.setOnClickListener(View::showContextMenu);
        holder.tv_mohafez_name.setText(mohafezeen.get(position).getName());
        holder.tv_mohafez_stage.setText(mohafezeen.get(position).getStage());
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });
    }

    @Override
    public int getItemCount() {
        return mohafezeen.size();
    }

    @Override
    public Filter getFilter() {
        return mohafezeenFiltet;
    }
}