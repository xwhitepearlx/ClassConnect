package com.example.classconnect;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailInput;
    private Button recoverButton;
    private TextView passwordResult;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Find views
        emailInput = findViewById(R.id.emailForgotInput);
        recoverButton = findViewById(R.id.recoverButton);
        passwordResult = findViewById(R.id.passwordResult);

        dbHelper = new DatabaseHelper(this);
        recoverButton.setOnClickListener(v -> recoverPassword());
    }

    private void recoverPassword() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Password FROM USERS WHERE Email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            String password = cursor.getString(0);
            passwordResult.setText("Your password is: " + password);
        } else {
            passwordResult.setText("");
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }
}