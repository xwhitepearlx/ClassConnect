package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.classconnect.data.DatabaseHelper;

public class EditSessionActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private int sessionId;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_session);

        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        sessionId = intent.getIntExtra("session_id", -1);
        courseId = intent.getIntExtra("course_id", -1);
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        int duration = intent.getIntExtra("duration", 0);
        String location = intent.getStringExtra("location");
        int maxParticipant = intent.getIntExtra("max_participant", 0);
        String description = intent.getStringExtra("description");

        EditText etDate = findViewById(R.id.inputSessionDate);
        EditText etTime = findViewById(R.id.inputTime);
        EditText etDuration = findViewById(R.id.inputDuration);
        EditText etLocation = findViewById(R.id.inputLocation);
        EditText etMaxParticipant = findViewById(R.id.inputMaxParticipant);
        EditText etDescription = findViewById(R.id.inputDescription);
        Button btnSaveChanges = findViewById(R.id.btnCreateSession);
        TextView tvBack = findViewById(R.id.tvBack);

        // Pre-fill with existing data
        etDate.setText(date);
        etTime.setText(time);
        etDuration.setText(String.valueOf(duration));
        etLocation.setText(location);
        etMaxParticipant.setText(String.valueOf(maxParticipant));
        etDescription.setText(description);

        btnSaveChanges.setOnClickListener(v -> {
            String newDate = etDate.getText().toString().trim();
            String newTime = etTime.getText().toString().trim();
            String newDurationStr = etDuration.getText().toString().trim();
            String newLocation = etLocation.getText().toString().trim();
            String newMaxPartStr = etMaxParticipant.getText().toString().trim();
            String newDescription = etDescription.getText().toString().trim();

            if (newDate.isEmpty() || newTime.isEmpty() || newDurationStr.isEmpty() ||
                    newLocation.isEmpty() || newMaxPartStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int newDuration = Integer.parseInt(newDurationStr);
                int newMaxPart = Integer.parseInt(newMaxPartStr);

                // Check if current participants exceed new max
                int currentParticipants = db.getSessionParticipantCount(sessionId);
                if (currentParticipants > newMaxPart) {
                    Toast.makeText(this,
                            "Cannot reduce max participants below current count (" + currentParticipants + ")",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                boolean updated = db.updateSession(sessionId, newDate, newTime, newDuration,
                        newLocation, newMaxPart, newDescription);

                if (updated) {
                    // Send notification to all participants about the update
                    String notificationMessage = "Session #" + sessionId + " has been updated. " +
                            "New schedule: " + newDate + " at " + newTime +
                            ". Location: " + newLocation +
                            ". Duration: " + newDuration + " minutes.";

                    db.notifySessionParticipants(sessionId, "SESSION_UPDATED", notificationMessage);

                    Toast.makeText(this, "Session updated and participants notified",
                            Toast.LENGTH_SHORT).show();

                    // Return to previous activity with success result
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update session", Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            }
        });

        tvBack.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}