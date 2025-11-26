package com.example.classconnect.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classconnect.Course;
import com.example.classconnect.R;

import java.util.ArrayList;
import java.util.List;

public class CourseCheckboxAdapter extends RecyclerView.Adapter<CourseCheckboxAdapter.ViewHolder>
{
    private Context context;
    private List<Course> courseList;

    private List<Integer> selectedIds = new ArrayList<>();

    public CourseCheckboxAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkCourse;
        TextView txtCourseCode, txtCourseName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkCourse = itemView.findViewById(R.id.checkCourse);
            txtCourseCode = itemView.findViewById(R.id.txtCourseCode);
            txtCourseName = itemView.findViewById(R.id.txtCourseName);
        }
    }

    // create to create the new row in the recyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_course_checkbox, parent, false);

        return new ViewHolder(view);
    }

    // bind the data to the row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Course course = courseList.get(position);

        holder.txtCourseCode.setText(course.getCsis() + " " + course.getCode());

        holder.txtCourseName.setText(course.getName());

        // Checkbox selection logic
        holder.checkCourse.setOnCheckedChangeListener(null);

        holder.checkCourse.setChecked(selectedIds.contains(course.getId()));

        holder.checkCourse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedIds.contains(course.getId())) {
                    selectedIds.add(course.getId());
                }
            } else {
                selectedIds.remove(Integer.valueOf(course.getId()));
            }
        });
    }


    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public List<Integer> getSelectedIds() {
        return selectedIds;
    }

    // ------------ UPDATE LIST AFTER SAVE ------------
    public void updateList(List<Course> newList) {
        this.courseList = newList;
        selectedIds.clear();
        notifyDataSetChanged();
    }
}
