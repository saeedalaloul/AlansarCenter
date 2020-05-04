package com.alansar.center.administrator.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Moshref.Model.Moshref;
import com.alansar.center.R;
import com.alansar.center.administrator.ViewHolder.MoshrefViewHolder;

import java.util.ArrayList;

public class MoshrefAdapter extends RecyclerView.Adapter<MoshrefViewHolder> implements Filterable {
    public static ArrayList<Moshref> moshrefs;
    private ArrayList<Moshref> moshrefArrayList;
    private Filter Filtet = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String charString = constraint.toString();

            if (charString.isEmpty()) {
                moshrefs = moshrefArrayList;
            } else {

                ArrayList<Moshref> filterList = new ArrayList<>();

                for (Moshref data : moshrefArrayList) {

                    if (data.getName().toLowerCase().contains(charString)) {
                        filterList.add(data);
                    }
                }

                moshrefs = filterList;

            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = moshrefs;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            moshrefs = (ArrayList<Moshref>) results.values;
            notifyDataSetChanged();
        }
    };


    public MoshrefAdapter(ArrayList<Moshref> moshrefs) {
        MoshrefAdapter.moshrefs = moshrefs;
        this.moshrefArrayList = moshrefs;
    }

    @NonNull
    @Override
    public MoshrefViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.moshref_layout, parent, false);
        return new MoshrefViewHolder(v, moshrefs);
    }

    @Override
    public void onBindViewHolder(@NonNull MoshrefViewHolder holder, int position) {
        holder.imgbtn_more.setOnClickListener(View::showContextMenu);
        holder.tv_moshref_name.setText(moshrefs.get(position).getName());
        holder.tv_moshref_stage.setText(moshrefs.get(position).getStage());
        holder.setItemClickListener((view, position1, isLongClick) -> {
        });
    }

    @Override
    public int getItemCount() {
        return moshrefs.size();
    }

    @Override
    public Filter getFilter() {
        return Filtet;
    }
}