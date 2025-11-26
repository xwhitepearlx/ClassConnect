package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;
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

public class BrowseSessionsActivity extends AppCompatActivity {

    private int courseId;
    private String studentEmail;
    private DatabaseHelper db;
    private ListView listView;
    private List<Session> availableSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_sessions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cardViewSessions), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        courseId = intent.getIntExtra("course_id", -1);
        String courseName = intent.getStringExtra("course_name");
        studentEmail = intent.getStringExtra("student_email");

        if (courseId == -1 || studentEmail == null) {
            Toast.makeText(this, "Error loading sessions", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tvBrowseTitle);
        tvTitle.setText("Available Sessions - " + courseName);

        listView = findViewById(R.id.listViewAvailableSessions);
        Button btnBack = findViewById(R.id.btnBack);

        db = new DatabaseHelper(this);

        loadAvailableSessions();

        // UPDATED: Navigate to JoinSessionActivity instead of joining directly
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Session selectedSession = availableSessions.get(position);

            Intent detailIntent = new Intent(BrowseSessionsActivity.this, JoinSessionActivity.class);
            detailIntent.putExtra("session_id", selectedSession.getId());
            detailIntent.putExtra("course_id", courseId);
            detailIntent.putExtra("date", selectedSession.getDate());
            detailIntent.putExtra("time", selectedSession.getStartTime());
            detailIntent.putExtra("duration", selectedSession.getDuration());
            detailIntent.putExtra("location", selectedSession.getLocation());
            detailIntent.putExtra("max_participant", selectedSession.getMaxParticipant());
            detailIntent.putExtra("description", selectedSession.getDescription());
            detailIntent.putExtra("student_email", studentEmail);
            startActivity(detailIntent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // UPDATED: Refresh list when returning from JoinSessionActivity
        loadAvailableSessions();
    }

    private void loadAvailableSessions() {
        availableSessions = db.getAvailableSessionsForCourse(studentEmail, courseId);

        if (availableSessions.isEmpty()) {
            Toast.makeText(this, "No sessions available to join", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, availableSessions.size() + " session(s) available", Toast.LENGTH_SHORT).show();
        }

        SessionAdapter adapter = new SessionAdapter(this, availableSessions);
        listView.setAdapter(adapter);
    }
}