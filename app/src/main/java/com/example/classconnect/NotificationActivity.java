package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classconnect.databinding.ActivityNotificationBinding;

public class NotificationActivity extends AppCompatActivity {

    ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        Intent intent = this.getIntent();
        if (intent != null) {
            String SessionID = intent.getStringExtra("SessionID");
            String SessionStartTime = intent.getStringExtra("SessionStartTime");

            binding.SessionID.setText(SessionID);
            binding.sessionStartTime.setText(SessionStartTime);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}