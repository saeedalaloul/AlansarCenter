package com.alansar.center.Moshref.Adapter;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Models.Group;
import com.alansar.center.Models.GroupMembers;
import com.alansar.center.Moshref.ViewHolder.HalakaViewHolder;
import com.alansar.center.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;


public class HalakaAdapter extends RecyclerView.Adapter<HalakaViewHolder> implements Filterable {
    public static ArrayList<Group> halakat;
    private FirebaseFirestore db;
    private ArrayList<Group> groupArrayList;
    private Filter Filtet = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String charString = constraint.toString();

            if (charString.isEmpty()) {
                halakat = groupArrayList;
            } else {

                ArrayList<Group> filterList = new ArrayList<>();

                for (Group data : groupArrayList) {

                    if (data.getName().toLowerCase().contains(charString)) {
                        filterList.add(data);
                    }
                }

                halakat = filterList;

            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = halakat;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            halakat = (ArrayList<Group>) results.values;
            notifyDataSetChanged();
        }
    };

    public HalakaAdapter(ArrayList<Group> halakat) {
        HalakaAdapter.halakat = halakat;
        this.groupArrayList = halakat;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public HalakaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.halaka_layout, parent, false);
        return new HalakaViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HalakaViewHolder holder, int position) {
        holder.tv_halaka_name.setText(halakat.get(position).getName());
        holder.tv_halaka_stage.setText(halakat.get(position).getStage());
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });
        holder.imgbtn_more.setOnClickListener(View::showContextMenu);
        if (!halakat.get(position).getMohafezId().isEmpty()) {
            db.collection("Mohafez").document(halakat.get(position).getMohafezId()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    holder.tv_mohafez_name.setText(Objects.requireNonNull(documentSnapshot.get("name")).toString());
                }
            });

            if (!halakat.get(position).getMohafezId().isEmpty()) {
                getMohafezName(position, holder);
                getCountMembersGroup(position, holder);
            }
        }
    }

    private void getMohafezName(int position, HalakaViewHolder holder) {
        db.collection("Mohafez").document(halakat.get(position).getMohafezId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                holder.tv_mohafez_name.setText(Objects.requireNonNull(documentSnapshot.get("name")).toString());
            }
        });
    }

    private void getCountMembersGroup(int position, HalakaViewHolder holder) {
        if (halakat.get(position) != null &&
                halakat.get(position).getGroupId() != null &&
                !halakat.get(position).getGroupId().isEmpty()) {
            db.collection("GroupMembers").document(halakat.get(position).getGroupId())
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    holder.tv_student_num.setText("" + Objects.requireNonNull(documentSnapshot.toObject(GroupMembers.class)).getGroupMembers().size());
                } else {
                    holder.tv_student_num.setText("0");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return halakat.size();
    }

    @Override
    public Filter getFilter() {
        return Filtet;
    }
}