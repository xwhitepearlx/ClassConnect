package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classconnect.data.DatabaseHelper;

import java.util.List;

public class SessionDetailTutor extends AppCompatActivity {

    private DatabaseHelper db;
    private int sessionId;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_session_detail_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        TextView tvSessionId = findViewById(R.id.SessionID);
        TextView tvStartTime = findViewById(R.id.sessionStartTime);
        TextView tvLocation = findViewById(R.id.sessionLocation);
        TextView tvDescription = findViewById(R.id.sessionDescription);
        TextView tvParticipation = findViewById(R.id.sessionParticipation);
        ListView lvParticipants = findViewById(R.id.lvParticipants);
        Button btnEditSession = findViewById(R.id.btnEditSession);
        Button btnCancelSession = findViewById(R.id.btnCancelSession);
        Button btnSendReminders = findViewById(R.id.btnSendRemainders);
        TextView tvBack = findViewById(R.id.tvBack);

        tvSessionId.setText(String.valueOf(sessionId));
        tvStartTime.setText(date + " at " + time);
        tvLocation.setText(location);
        tvDescription.setText(duration + " minutes");

        int participantCount = db.getSessionParticipantCount(sessionId);
        tvParticipation.setText(participantCount + "/" + maxParticipant);

        List<String> participants = db.getSessionParticipants(sessionId);
        if (!participants.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_list_item_1, participants);
            lvParticipants.setAdapter(adapter);
        }

        btnEditSession.setOnClickListener(v -> {
            Intent editIntent = new Intent(SessionDetailTutor.this, EditSessionActivity.class);
            editIntent.putExtra("session_id", sessionId);
            editIntent.putExtra("course_id", courseId);
            editIntent.putExtra("date", date);
            editIntent.putExtra("time", time);
            editIntent.putExtra("duration", duration);
            editIntent.putExtra("location", location);
            editIntent.putExtra("max_participant", maxParticipant);
            editIntent.putExtra("description", description);
            startActivityForResult(editIntent, 100);
        });

        btnCancelSession.setOnClickListener(v -> {
            new AlertDialog.Builder(SessionDetailTutor.this)
                    .setTitle("Cancel Session")
                    .setMessage("Are you sure you want to cancel this session? This cannot be undone.")
                    .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                        // Send notification to all participants before deleting
                        String notificationMessage = "Session #" + sessionId +
                                " scheduled for " + date + " at " + time +
                                " has been cancelled by the tutor.";

                        db.notifySessionParticipants(sessionId, "SESSION_CANCELLED", notificationMessage);

                        // Now delete the session
                        boolean deleted = db.deleteSession(sessionId);
                        if (deleted) {
                            Toast.makeText(SessionDetailTutor.this,
                                    "Session cancelled and participants notified", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SessionDetailTutor.this,
                                    "Failed to cancel session", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        btnSendReminders.setOnClickListener(v -> {
            int count = db.getSessionParticipantCount(sessionId);

            if (count == 0) {
                Toast.makeText(SessionDetailTutor.this,
                        "No participants to send reminders to", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send reminder notification to all participants
            String reminderMessage = "Reminder: You have an upcoming session #" + sessionId +
                    " scheduled for " + date + " at " + time +
                    " at " + location + ". Duration: " + duration + " minutes.";

            db.notifySessionParticipants(sessionId, "SESSION_REMINDER", reminderMessage);

            Toast.makeText(SessionDetailTutor.this,
                    "Reminders sent to " + count + " participant(s)", Toast.LENGTH_SHORT).show();
        });

        tvBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Refresh the activity when returning from Edit Session
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Reload the activity to show updated information
            finish();
            startActivity(getIntent());
        }
    }
}