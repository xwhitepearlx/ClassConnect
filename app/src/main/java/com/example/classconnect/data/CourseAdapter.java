package com.example.classconnect.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.classconnect.Course;
import com.example.classconnect.R;

import java.util.List;

public class CourseAdapter extends ArrayAdapter<Course>
{
    public CourseAdapter(Context context, List<Course> courses)
    {
        super(context, 0, courses);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.course_item, parent, false);
        }

        Course course = getItem(position);

        TextView name = convertView.findViewById(R.id.CourseName);
        TextView code = convertView.findViewById(R.id.CourseID);

        name.setText(course.getName());
        code.setText(course.getCsis() + " " + course.getCode());

        return convertView;
    }
}
