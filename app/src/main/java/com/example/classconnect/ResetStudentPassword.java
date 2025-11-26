package com.example.classconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class ResetStudentPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_student_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // change pass word coding
        // getting the current email here by shared preference

        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sp.getString("logged_email", null);

        EditText oldPassword = findViewById(R.id.uCurrentPassword);
        EditText newPassword = findViewById(R.id.uNewPassword);
        EditText confirmNewPassword = findViewById(R.id.uNewPasswordConfirmed);

        Button passwordConfirm = findViewById(R.id.btnConfirm);

        passwordConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                // Read the text from EditTexts
                String oldPass = oldPassword.getText().toString();
                String newPass = newPassword.getText().toString();
                String confirmNewPass = confirmNewPassword.getText().toString();

                // Validate fields
                if (oldPass.isEmpty() || newPass.isEmpty() || confirmNewPass.isEmpty()) {
                    Toast.makeText(ResetStudentPassword.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check new passwords match
                if (!newPass.equals(confirmNewPass)) {
                    Toast.makeText(ResetStudentPassword.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                //  Update password using DB
                DatabaseHelper db = new DatabaseHelper(ResetStudentPassword.this);
                boolean success = db.changePassword(email, oldPass, newPass);

                // 6️⃣ Show result
                if (success) {
                    Toast.makeText(ResetStudentPassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // close current screen
                } else {
                    Toast.makeText(ResetStudentPassword.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //Button for go back
        TextView tvBack = findViewById(R.id.tvBack);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetStudentPassword.this, ProfileActivity.class));
            }
        });
    }
}