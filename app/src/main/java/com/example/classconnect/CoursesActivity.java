package com.example.classconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.classconnect.data.CourseAdapter;
import com.example.classconnect.data.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class CoursesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);

        String email = sp.getString("logged_email", null);
        String role  = sp.getString("logged_role", null);

        Button nextPage = findViewById(R.id.coursepage);

        // --- Load Courses in ListView ---
        ListView listView = findViewById(R.id.listview);
        List<Course> courseList;

        if(role.equals("Student"))
        {
            nextPage.setText("Join Course");
            nextPage.setOnClickListener(v -> {
                startActivity(new Intent(CoursesActivity.this, JoinCourseActivity.class));
            });

            // Show ONLY Joined Courses
            courseList = db.getJoinedCourses(email);

            // If no joined courses
            if (courseList.isEmpty()) {
                Toast.makeText(this, "You have not joined any courses yet!", Toast.LENGTH_SHORT).show();
            }
        }
        else  // TEACHER
        {
            nextPage.setText("Create Session");

            nextPage.setOnClickListener(v -> {
                startActivity(new Intent(CoursesActivity.this, CreateCourseActivity.class));
            });

            courseList = db.getAllCourses();
        }

        // Create adapter
        CourseAdapter adapter = new CourseAdapter(CoursesActivity.this, courseList);
        listView.setAdapter(adapter);

        // When user taps a course
        listView.setOnItemClickListener((parent, view, position, id) -> {

            Course selected = courseList.get(position);

            Intent i = new Intent(CoursesActivity.this, CourseActivity.class);
            i.putExtra("id", selected.getId());
            i.putExtra("name", selected.getName());
            i.putExtra("csis", selected.getCsis());
            i.putExtra("code", selected.getCode());

            startActivity(i);
        });


        // --- Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sessions");


        // --- Drawer ---
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // UPDATE NAV HEADER with logged-in user's info
        updateNavigationHeader(navigationView);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_sessions) {
            drawerLayout.closeDrawers();

        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));

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
        String savedName = sp.getString("logged_name", "User Name");
        String savedEmail = sp.getString("logged_email", "user@email.com");

        // Load header view
        android.view.View headerView = navigationView.getHeaderView(0);

        // Find header textviews
        android.widget.TextView navName = headerView.findViewById(R.id.navUserName);
        android.widget.TextView navEmail = headerView.findViewById(R.id.navUserEmail);

        // Set data
        if (navName != null) {
            navName.setText(savedName);
        }
        if (navEmail != null) {
            navEmail.setText(savedEmail);
        }
    }
}