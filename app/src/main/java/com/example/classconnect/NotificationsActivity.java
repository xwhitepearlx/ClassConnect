package com.example.classconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.classconnect.databinding.ActivityNotificationsBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    ActivityNotificationsBinding binding;
    ListAdapter listAdapter;
    ArrayList<NotificationData> dataArrayList = new ArrayList<>();
    NotificationData listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        String[] sessionIDList = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] sessionStartTimeList = {"10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30"};

        for (int i = 0; i < sessionIDList.length; i++){
            listData = new NotificationData(
                    sessionStartTimeList[i],
                    sessionIDList[i]
            );
            dataArrayList.add(listData);
        }

        listAdapter = new ListAdapter(NotificationsActivity.this, dataArrayList);
        binding.listview.setAdapter(listAdapter);
        binding.listview.setClickable(true);
        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotificationsActivity.this, NotificationActivity.class);
                intent.putExtra("sessionID", sessionIDList[position]);
                intent.putExtra("sessionStartTime", sessionStartTimeList[position]);
                startActivity(intent);
            }
        });

// update the navigation drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView navName = headerView.findViewById(R.id.navUserName);
        TextView navEmail = headerView.findViewById(R.id.navUserEmail);

        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);

        navName.setText(sp.getString("logged_name", "User"));
        navEmail.setText(sp.getString("logged_email", "user@email.com"));

        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // --- Toolbar ---
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");

        // --- Drawer ---
        drawerLayout = binding.drawerLayout;
        NavigationView navigationView1 = binding.navView;
        navigationView1.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_notifications) {
            drawerLayout.closeDrawers();
        } else if (id == R.id.nav_sessions) {
            startActivity(new Intent(this, CoursesActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_sign_out) {
            Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
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
}