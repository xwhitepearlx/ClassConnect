package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classconnect.data.DatabaseHelper;

public class JoinSessionActivity extends AppCompatActivity {

    private int sessionId;
    private String studentEmail;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_session);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);

        // Get session details from intent
        Intent intent = getIntent();
        sessionId = intent.getIntExtra("session_id", -1);
        int courseId = intent.getIntExtra("course_id", -1);
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        int duration = intent.getIntExtra("duration", 0);
        String location = intent.getStringExtra("location");
        int maxParticipant = intent.getIntExtra("max_participant", 0);
        String description = intent.getStringExtra("description");
        studentEmail = intent.getStringExtra("student_email");

        if (sessionId == -1 || studentEmail == null) {
            Toast.makeText(this, "Error loading session details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find views
        TextView tvSessionId = findViewById(R.id.SessionID);
        TextView tvStartTime = findViewById(R.id.sessionStartTime);
        TextView tvDuration = findViewById(R.id.sessionDuration);
        TextView tvLocation = findViewById(R.id.sessionLocation);
        TextView tvDescription = findViewById(R.id.sessionDescription);

        RadioGroup rsvpGroup = findViewById(R.id.roleRegisterGroup);
        RadioButton radioYes = findViewById(R.id.radio_yes);
        RadioButton radioNo = findViewById(R.id.radio_no);
        RadioButton radioMaybe = findViewById(R.id.radio_maybe);

        EditText inputNotes = findViewById(R.id.inputOptionalNotes);
        Button btnSubmitRSVP = findViewById(R.id.btnSubmitRSVP);
        Button btnJoinSession = findViewById(R.id.btnJoinSession);
        TextView tvBack = findViewById(R.id.tvBack);

        // Set session details
        tvSessionId.setText(String.valueOf(sessionId));
        tvStartTime.setText(date + " at " + time);
        tvDuration.setText(duration + " minutes");
        tvLocation.setText(location);
        tvDescription.setText(description);

        // Submit RSVP button (Optional feature - stores RSVP response)
        btnSubmitRSVP.setOnClickListener(v -> {
            int selectedId = rsvpGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an RSVP option", Toast.LENGTH_SHORT).show();
                return;
            }

            String rsvpResponse = "";
            if (selectedId == R.id.radio_yes) {
                rsvpResponse = "Yes";
            } else if (selectedId == R.id.radio_no) {
                rsvpResponse = "No";
            } else if (selectedId == R.id.radio_maybe) {
                rsvpResponse = "Maybe";
            }

            String notes = inputNotes.getText().toString().trim();

            // TODO: Store RSVP response and notes in database (future feature)
            Toast.makeText(this, "RSVP submitted: " + rsvpResponse, Toast.LENGTH_SHORT).show();
        });

        // Join Session button - Actually enrolls student in session
        btnJoinSession.setOnClickListener(v -> {
            boolean joined = db.joinSession(studentEmail, sessionId);

            if (joined) {
                Toast.makeText(this, "Successfully joined session!", Toast.LENGTH_LONG).show();
                finish(); // Go back to BrowseSessionsActivity
            } else {
                Toast.makeText(this, "You've already joined this session", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        tvBack.setOnClickListener(v -> finish());
    }
}