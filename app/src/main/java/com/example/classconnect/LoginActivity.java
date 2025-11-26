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

        if (checkUserCredentials(email, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, CoursesActivity.class);
            startActivity(intent);
            String role = dbHelper.getUserRole(email);

            // 🔥 Save email + role in SharedPreferences
            SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("logged_email", email);
            editor.putString("logged_role", role);
            editor.apply();
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //Log.d("LOGIN_TEST", "Trying login with email: '" + email + "', password: '" + password + "'");
        String query = "SELECT * FROM " + UserTable.TABLE_NAME +
                " WHERE " + UserTable.COL_EMAIL + "=? AND " + UserTable.COL_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean valid = cursor.moveToFirst();
        //Log.d("LOGIN_TEST", "Cursor count: " + cursor.getCount());
        cursor.close();
        db.close();
        return valid;
    }
}