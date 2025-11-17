package com.example.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.classconnect.databinding.ActivityNotificationBinding;

public class NotificationActivity extends AppCompatActivity {

    ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = this.getIntent();
        if (intent != null) {
            String sessionID = intent.getStringExtra("sessionID");
            String sessionStartTime = intent.getStringExtra("sessionStartTime");

            binding.SessionID.setText(sessionID);
            binding.sessionStartTime.setText(sessionStartTime);
        }
    }
}
