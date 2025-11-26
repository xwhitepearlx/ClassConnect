package com.example.classconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classconnect.data.CourseCheckboxAdapter;
import com.example.classconnect.data.DatabaseHelper;

import java.util.List;

public class JoinCourseActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CourseCheckboxAdapter adapter;
    private DatabaseHelper db;
    private String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        studentEmail = prefs.getString("logged_email", "");
        db = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerCourses);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //we load the remaining courses in the list with the help of this
        List<Course> remainingCourses = db.getRemainingCourses(studentEmail);

        adapter = new CourseCheckboxAdapter(this, remainingCourses);
        recyclerView.setAdapter(adapter);


        Button btnSave = findViewById(R.id.btnSaveCourses);

        btnSave.setOnClickListener(v -> {

            List<Integer> selectedIds = adapter.getSelectedIds();

            if (selectedIds.isEmpty()) {
                Toast.makeText(this, "Please select at least one course!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save each selected course
            for (int courseId : selectedIds) {
                db.insertStudentCourse(studentEmail, courseId);
            }

            Toast.makeText(this, "Courses saved successfully!", Toast.LENGTH_SHORT).show();

            // Refresh list after saving
            List<Course> newRemaining = db.getRemainingCourses(studentEmail);
            adapter.updateList(newRemaining);
        });

        TextView goBack = findViewById(R.id.tvBack);
        goBack.setOnClickListener(v -> {
            startActivity(new Intent(JoinCourseActivity.this, CoursesActivity.class));
        });
    }
}
