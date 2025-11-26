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

public class EditStudentProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_student_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TextView tvBack = findViewById(R.id.tvBack);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditStudentProfile.this, ProfileActivity.class));
            }
        });
        Button btnConfirmEdit = findViewById(R.id.btnConfirmEditProfile);
        EditText name = findViewById(R.id.uEditName);
        EditText phoneNumber = findViewById(R.id.uEditPhone);
        EditText interest = findViewById(R.id.uEditInterest);
        EditText program = findViewById(R.id.uEditProgram);

        btnConfirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = name.getText().toString();
                String newPhone = phoneNumber.getText().toString();
                String newInterest = interest.getText().toString();
                String newProgram = program.getText().toString();

                DatabaseHelper db = new DatabaseHelper(EditStudentProfile.this);

                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                String email = sp.getString("logged_email", null);

                boolean success = db.updateAllInfo(email, newName, newPhone, newProgram, newInterest);

                if (success) {
                    Toast.makeText(EditStudentProfile.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditStudentProfile.this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

