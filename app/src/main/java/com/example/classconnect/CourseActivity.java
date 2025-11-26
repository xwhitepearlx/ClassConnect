package com.example.classconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classconnect.data.DatabaseHelper;
import com.example.classconnect.data.SessionAdapter;

import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private int courseId;
    private String courseName;
    private String courseCode;
    private DatabaseHelper db;
    private ListView listView;
    private SessionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvCourseName = findViewById(R.id.CourseNameText);
        TextView tvCourseID = findViewById(R.id.CourseID);
        TextView tvCourseCode = findViewById(R.id.CourseCode);
        listView = findViewById(R.id.listView);

        db = new DatabaseHelper(this);

        Intent i = getIntent();
        courseName = i.getStringExtra("name");
        String csis = i.getStringExtra("csis");
        int code = i.getIntExtra("code", -1);
        courseId = i.getIntExtra("id", -1);

        tvCourseName.setText(courseName);
        tvCourseID.setText(String.valueOf(courseId));
        courseCode = csis + " " + code;
        tvCourseCode.setText(courseCode);

        loadSessions();

        Button addCourse = findViewById(R.id.addCourse);
        Button tvBack = findViewById(R.id.BackButton);

        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        String role = sp.getString("logged_role", "");
        String email = sp.getString("logged_email", "");

        if (role.equals("Student")) {
            addCourse.setText("Join Session");
            addCourse.setOnClickListener(v -> {
                Intent intent = new Intent(CourseActivity.this, BrowseSessionsActivity.class);
                intent.putExtra("course_id", courseId);
                intent.putExtra("course_name", courseName);
                intent.putExtra("student_email", email);
                startActivity(intent);
            });

            // Students can click on joined sessions to view details and drop
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Session selectedSession = (Session) parent.getItemAtPosition(position);
                Intent intent = new Intent(CourseActivity.this, StudentSessionDetailActivity.class);
                intent.putExtra("session_id", selectedSession.getId());
                intent.putExtra("date", selectedSession.getDate());
                intent.putExtra("time", selectedSession.getStartTime());
                intent.putExtra("duration", selectedSession.getDuration());
                intent.putExtra("location", selectedSession.getLocation());
                intent.putExtra("description", selectedSession.getDescription());
                intent.putExtra("student_email", email);
                startActivity(intent);
            });

        } else {
            addCourse.setText("Add Session");
            addCourse.setOnClickListener(v -> {
                Intent intent = new Intent(CourseActivity.this, CreateSessionActivity.class);
                intent.putExtra("course_id", courseId);
                intent.putExtra("course_name", courseName);
                intent.putExtra("course_code", courseCode);
                startActivity(intent);
            });

            // Teachers can click sessions to view details and manage
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Session selectedSession = (Session) parent.getItemAtPosition(position);
                Intent intent = new Intent(CourseActivity.this, SessionDetailTutor.class);
                intent.putExtra("session_id", selectedSession.getId());
                intent.putExtra("course_id", courseId);
                intent.putExtra("date", selectedSession.getDate());
                intent.putExtra("time", selectedSession.getStartTime());
                intent.putExtra("duration", selectedSession.getDuration());
                intent.putExtra("location", selectedSession.getLocation());
                intent.putExtra("max_participant", selectedSession.getMaxParticipant());
                intent.putExtra("description", selectedSession.getDescription());
                startActivity(intent);
            });
        }

        tvBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSessions();
    }

    private void loadSessions() {
        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        String role = sp.getString("logged_role", "");
        String email = sp.getString("logged_email", "");

        List<Session> sessionList;

        if (role.equals("Student")) {
            sessionList = db.getJoinedSessionsForCourse(email, courseId);
        } else {
            sessionList = db.getSessionsForCourse(courseId);
        }

        if (sessionList.isEmpty()) {
            if (role.equals("Student")) {
                Toast.makeText(this, "You haven't joined any sessions yet", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No sessions available for this course", Toast.LENGTH_SHORT).show();
            }
        }

        adapter = new SessionAdapter(this, sessionList);
        listView.setAdapter(adapter);
    }
}