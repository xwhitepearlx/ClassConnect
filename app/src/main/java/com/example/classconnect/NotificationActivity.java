package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView tvSessionID = findViewById(R.id.SessionID);
        TextView tvStartTime = findViewById(R.id.sessionStartTime);
        TextView tvMessage = findViewById(R.id.notificationMessage);
        TextView tvSentTime = findViewById(R.id.notificationTime);
        TextView tvBack = findViewById(R.id.tvBack);

        Intent intent = getIntent();
        if (intent != null) {
            String sessionID = intent.getStringExtra("sessionID");
            String sessionStartTime = intent.getStringExtra("sessionStartTime");
            String message = intent.getStringExtra("message");
            String timestamp = intent.getStringExtra("timestamp");

            tvSessionID.setText(sessionID != null ? sessionID : "N/A");
            tvStartTime.setText(sessionStartTime != null ? sessionStartTime : "N/A");
            tvMessage.setText(message != null ? message : "No additional details available.");

            if (timestamp != null) {
                tvSentTime.setText("Sent: " + formatTimestamp(timestamp));
            } else {
                tvSentTime.setText("Sent: Recently");
            }
        }

        tvBack.setOnClickListener(v -> finish());
    }

    private String formatTimestamp(String timestamp) {
        try {
            if (timestamp.contains(" ")) {
                String[] parts = timestamp.split(" ");
                if (parts.length == 2) {
                    return parts[0] + " at " + parts[1];
                }
            }
            return timestamp;
        } catch (Exception e) {
            return timestamp;
        }
    }
}