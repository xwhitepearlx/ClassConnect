package com.example.classconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.classconnect.data.DatabaseHelper;
import com.example.classconnect.data.schema.UserTable;
import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    TextView uName1, uName2, uEmail1, uEmail2, uCourse, uPhoneNumber;

    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");

        // --- Drawer ---
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update navigation header with logged-in user's info
        updateNavigationHeader(navigationView);

        // --- Profile buttons ---
        TextView tvBack = findViewById(R.id.tvBack);
        TextView tvChangePass = findViewById(R.id.tvChangePass);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        tvBack.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, CoursesActivity.class)));

        tvChangePass.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ResetStudentPassword.class)));

        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditStudentProfile.class)));


        //data shown in the student from table
        uName1 = findViewById(R.id.uFullName1);
        uName2 = findViewById(R.id.uName2);
        uEmail1 = findViewById(R.id.uEmail1);
        uEmail2 = findViewById(R.id.uEmail2);
        uCourse = findViewById(R.id.uProgram);
        uPhoneNumber = findViewById(R.id.uPhoneNo);

        db = new DatabaseHelper(this);
        SharedPreferences sp1 = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sp1.getString("logged_email", null);

        loadStudentData(email);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            drawerLayout.closeDrawers();
        } else if (id == R.id.nav_sessions) {
            startActivity(new Intent(this, CoursesActivity.class));
        } else if (id == R.id.nav_notifications) {
            startActivity(new Intent(this, NotificationsActivity.class));
        } else if (id == R.id.nav_sign_out) {
            // FIXED: Proper sign-out implementation
            SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private void updateNavigationHeader(NavigationView navigationView) {
        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        String savedName = sp.getString("logged_name", "User");
        String savedEmail = sp.getString("logged_email", "user@email.com");

        View headerView = navigationView.getHeaderView(0);

        TextView navName = headerView.findViewById(R.id.navUserName);
        TextView navEmail = headerView.findViewById(R.id.navUserEmail);

        if (navName != null) {
            navName.setText(savedName);
        }
        if (navEmail != null) {
            navEmail.setText(savedEmail);
        }
    }

    private void loadStudentData(String email) {
        Cursor cursor = db.getUserByEmail(email);

        try {
            if (cursor != null && cursor.moveToFirst()) {

                String name = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_NAME));
                String emailAddress = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_EMAIL));
                String course = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.Col_Program));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.Col_Phone_No));

                uName1.setText(name);
                uName2.setText(name);
                uEmail1.setText(emailAddress);
                uEmail2.setText(emailAddress);
                uCourse.setText(course);
                uPhoneNumber.setText(phone);

                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("logged_name", name);
                editor.putString("logged_email", emailAddress);
                editor.apply();

            } else {
                Toast.makeText(this, "No data found for: " + email, Toast.LENGTH_SHORT).show();
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}