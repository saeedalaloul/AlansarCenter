package com.alansar.center.Edare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Edare.ViewHolder.StudentViewHolder;
import com.alansar.center.R;
import com.alansar.center.students.Model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentViewHolder> implements Filterable {

    public static List<Student> students;
    private List<Student> studentsArrayList;
    private Filter mohafezeenFiltet = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String charString = constraint.toString();

            if (charString.isEmpty()) {
                students = studentsArrayList;
            } else {
                ArrayList<Student> filterList = new ArrayList<>();
                for (Student data : studentsArrayList) {
                    if (data.getName().toLowerCase().contains(charString)) {
                        filterList.add(data);
                    }
                }
                students = filterList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = students;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            students = (List<Student>) results.values;
            notifyDataSetChanged();
        }
    };

    public StudentAdapter(List<Student> students) {
        StudentAdapter.students = students;
        this.studentsArrayList = students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_layout, parent, false);
        return new StudentViewHolder(v, students);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.imgbtn_more.setOnClickListener(View::showContextMenu);
        holder.tv_student_name.setText(students.get(position).getName());
        holder.tv_student_stage.setText(students.get(position).getStage());
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    @Override
    public Filter getFilter() {
        return mohafezeenFiltet;
    }
}
