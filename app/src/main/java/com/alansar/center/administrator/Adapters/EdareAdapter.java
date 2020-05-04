package com.alansar.center.administrator.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Edare.Model.Edare;
import com.alansar.center.R;
import com.alansar.center.administrator.ViewHolder.EdareViewHolder;

import java.util.ArrayList;

public class EdareAdapter extends RecyclerView.Adapter<EdareViewHolder> implements Filterable {
    public static ArrayList<Edare> edares;
    private ArrayList<Edare> edareArrayList;
    private Filter Filtet = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String charString = constraint.toString();

            if (charString.isEmpty()) {
                edares = edareArrayList;
            } else {

                ArrayList<Edare> filterList = new ArrayList<>();

                for (Edare data : edareArrayList) {

                    if (data.getName().toLowerCase().contains(charString)) {
                        filterList.add(data);
                    }
                }

                edares = filterList;

            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = edares;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            edares = (ArrayList<Edare>) results.values;
            notifyDataSetChanged();
        }
    };

    public EdareAdapter(ArrayList<Edare> edares) {
        EdareAdapter.edares = edares;
        this.edareArrayList = edares;
    }

    @NonNull
    @Override
    public EdareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edare_layout, parent, false);
        return new EdareViewHolder(v, edares);
    }

    @Override
    public void onBindViewHolder(@NonNull EdareViewHolder holder, int position) {
        holder.imgbtn_more.setOnClickListener(View::showContextMenu);
        holder.tv_Edare_name.setText(edares.get(position).getName());
        holder.tv_Edare_stage.setText(edares.get(position).getStage());
        holder.setItemClickListener((view, position1, isLongClick) -> {
        });
    }

    @Override
    public int getItemCount() {
        return edares.size();
    }

    @Override
    public Filter getFilter() {
        return Filtet;
    }
}
