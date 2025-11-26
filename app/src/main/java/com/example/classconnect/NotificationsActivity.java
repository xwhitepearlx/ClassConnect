package com.example.classconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.classconnect.data.DatabaseHelper;
import com.example.classconnect.data.NotificationAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private DatabaseHelper db;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        db = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update navigation header with logged-in user's info
        updateNavigationHeader(navigationView);

        listView = findViewById(R.id.listview);

        // Load notifications for the logged-in student
        loadNotifications();

        // Click on notification to view details
        listView.setOnItemClickListener((parent, view, position, id) -> {
            NotificationData notification = (NotificationData) parent.getItemAtPosition(position);

            Intent intent = new Intent(NotificationsActivity.this, NotificationActivity.class);
            intent.putExtra("sessionID", notification.getSessionID());
            intent.putExtra("sessionStartTime", notification.getScheduleTime());
            intent.putExtra("message", notification.getMessage());
            intent.putExtra("timestamp", notification.getTimestamp());
            startActivity(intent);
        });
    }

    private void loadNotifications() {
        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sp.getString("logged_email", "");

        if (email.isEmpty()) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        List<NotificationData> notifications = db.getNotificationsForStudent(email);

        if (notifications.isEmpty()) {
            Toast.makeText(this, "No notifications yet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, notifications.size() + " notification(s)", Toast.LENGTH_SHORT).show();
        }

        NotificationAdapter adapter = new NotificationAdapter(this, notifications);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_sessions) {
            startActivity(new Intent(this, CoursesActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_notifications) {
            drawerLayout.closeDrawers();
        } else if (id == R.id.nav_sign_out) {
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
}