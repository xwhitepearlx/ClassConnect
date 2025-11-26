package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classconnect.data.DatabaseHelper;

public class StudentSessionDetailActivity extends AppCompatActivity {

    private int sessionId;
    private String studentEmail;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_session_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);

        // Get session details from intent
        Intent intent = getIntent();
        sessionId = intent.getIntExtra("session_id", -1);
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        int duration = intent.getIntExtra("duration", 0);
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        studentEmail = intent.getStringExtra("student_email");

        if (sessionId == -1 || studentEmail == null) {
            Toast.makeText(this, "Error loading session details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find views
        TextView tvSessionId = findViewById(R.id.tvSessionId);
        TextView tvStartTime = findViewById(R.id.tvStartTime);
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvDescription = findViewById(R.id.tvDescription);
        Spinner spinnerDropReason = findViewById(R.id.spinnerDropReason);
        Button btnDropSession = findViewById(R.id.btnDropSession);
        TextView tvBack = findViewById(R.id.tvBack);

        // Set session details
        tvSessionId.setText("Session ID: " + sessionId);
        tvStartTime.setText("Date & Time: " + date + " at " + time);
        tvDuration.setText("Duration: " + duration + " minutes");
        tvLocation.setText("Location: " + location);
        tvDescription.setText("Description: " + description);

        // Setup dropdown for drop reasons
        String[] dropReasons = {
                "Select a reason",
                "Schedule conflict",
                "Personal emergency",
                "Health issues",
                "No longer interested",
                "Found alternative session",
                "Travel plans",
                "Work commitment",
                "Family obligation",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dropReasons
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDropReason.setAdapter(adapter);

        // Drop Session button
        btnDropSession.setOnClickListener(v -> {
            String selectedReason = spinnerDropReason.getSelectedItem().toString();

            if (selectedReason.equals("Select a reason")) {
                Toast.makeText(this, "Please select a reason for dropping", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Drop Session")
                    .setMessage("Are you sure you want to drop this session?\n\nReason: " + selectedReason)
                    .setPositiveButton("Yes, Drop", (dialog, which) -> {
                        boolean dropped = db.dropSession(studentEmail, sessionId);

                        if (dropped) {
                            Toast.makeText(this, "Successfully dropped session", Toast.LENGTH_LONG).show();
                            finish(); // Go back to CourseActivity
                        } else {
                            Toast.makeText(this, "Failed to drop session", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Back button
        tvBack.setOnClickListener(v -> finish());
    }
}