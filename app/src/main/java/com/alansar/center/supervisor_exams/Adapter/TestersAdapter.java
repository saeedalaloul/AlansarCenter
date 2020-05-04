package com.alansar.center.supervisor_exams.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.R;
import com.alansar.center.supervisor_exams.ViewHolder.TestersViewHolder;
import com.alansar.center.testers.Model.Tester;

import java.util.List;

public class TestersAdapter extends RecyclerView.Adapter<TestersViewHolder> {

    public List<Tester> testers;

    public TestersAdapter(List<Tester> testers) {
        this.testers = testers;
    }

    @NonNull
    @Override
    public TestersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tester_layout, parent, false);
        return new TestersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TestersViewHolder holder, int position) {
        holder.imgbtn_more.setOnClickListener(View::showContextMenu);
        holder.tv_tester_name.setText(testers.get(position).getName());
        holder.tv_tester_stage.setText(testers.get(position).getStage());
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });
    }

    @Override
    public int getItemCount() {
        return testers.size();
    }
}
