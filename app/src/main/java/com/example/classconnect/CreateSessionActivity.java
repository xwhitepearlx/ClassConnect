package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classconnect.data.DatabaseHelper;

public class CreateSessionActivity extends AppCompatActivity {

    private static final String TAG = "CreateSession";
    private int courseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_session);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent receivedIntent = getIntent();
        courseId = receivedIntent.getIntExtra("course_id", -1);

        if (courseId == -1) {
            Toast.makeText(this, "Error: No course selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        EditText SessionDate = findViewById(R.id.inputSessionDate);
        EditText Time = findViewById(R.id.inputTime);
        EditText Duration = findViewById(R.id.inputDuration);
        EditText Location = findViewById(R.id.inputLocation);
        EditText MaxParticipant = findViewById(R.id.inputMaxParticipant);
        EditText Description = findViewById(R.id.inputDescription);
        Button CreateSession = findViewById(R.id.btnCreateSession);
        TextView tvBack = findViewById(R.id.tvBack);

        DatabaseHelper db = new DatabaseHelper(this);

        CreateSession.setOnClickListener(v -> {
            String date = SessionDate.getText().toString().trim();
            String time = Time.getText().toString().trim();
            String duration = Duration.getText().toString().trim();
            String location = Location.getText().toString().trim();
            String maxParticipant = MaxParticipant.getText().toString().trim();
            String description = Description.getText().toString().trim();

            if (date.isEmpty() || time.isEmpty() || duration.isEmpty() ||
                    location.isEmpty() || maxParticipant.isEmpty()) {
                Toast.makeText(CreateSessionActivity.this,
                        "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int durationInt = Integer.parseInt(duration);
                int maxParticipantInt = Integer.parseInt(maxParticipant);

                boolean inserted = db.insertSession(courseId, date, time, durationInt,
                        location, maxParticipantInt, description);

                if (inserted) {
                    Toast.makeText(CreateSessionActivity.this,
                            "Session Created Successfully!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(CreateSessionActivity.this,
                            "Failed to Create Session", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(CreateSessionActivity.this,
                        "Invalid number format", Toast.LENGTH_SHORT).show();
            }
        });

        tvBack.setOnClickListener(v -> finish());
    }
}