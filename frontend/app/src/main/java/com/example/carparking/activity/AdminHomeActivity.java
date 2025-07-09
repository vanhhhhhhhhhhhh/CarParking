package com.example.carparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carparking.R;
import com.example.carparking.util.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

public class AdminHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView rvParkingRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        rvParkingRequests = findViewById(R.id.rvParkingRequests);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        rvParkingRequests.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Load danh sách request từ API
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_profile) {
            Toast.makeText(this, "Xem hồ sơ", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.nav_logout) {
            SharedPrefManager.getInstance(this).clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else {
            return false;
        }

    }
}
