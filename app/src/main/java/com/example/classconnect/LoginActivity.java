package com.example.classconnect;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.classconnect.data.schema.UserTable;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView forgotPasswordText, signUpText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Find views
        emailInput = findViewById(R.id.uCurrentPassword);
        passwordInput = findViewById(R.id.uNewPassword);
        loginButton = findViewById(R.id.loginLoginButton);
        forgotPasswordText = findViewById(R.id.tvForgotPasswordLogin);
        signUpText = findViewById(R.id.tvLogInNow);

        // Navigation button
        loginButton.setOnClickListener(v -> handleLogin());
        forgotPasswordText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
        signUpText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user data including name
        String[] userData = checkUserCredentials(email, password);

        if (userData != null) {
            String userName = userData[0];
            String userEmail = userData[1];
            String userRole = userData[2];

            // Save ALL user data to SharedPreferences
            SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("logged_name", userName);    // ← FIXED: Now saving name!
            editor.putString("logged_email", userEmail);
            editor.putString("logged_role", userRole);
            editor.commit(); // Use commit() for immediate save

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, CoursesActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private String[] checkUserCredentials(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT " + UserTable.COL_NAME + ", " +
                UserTable.COL_EMAIL + ", " +
                UserTable.COL_USER_TYPE +
                " FROM " + UserTable.TABLE_NAME +
                " WHERE " + UserTable.COL_EMAIL + "=? AND " +
                UserTable.COL_PASSWORD + "=?";

        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        String[] userData = null;

        if (cursor.moveToFirst()) {
            userData = new String[3];
            userData[0] = cursor.getString(0); // Name
            userData[1] = cursor.getString(1); // Email
            userData[2] = cursor.getString(2); // UserType (Role)
        }

        cursor.close();
        db.close();

        return userData;
    }
}