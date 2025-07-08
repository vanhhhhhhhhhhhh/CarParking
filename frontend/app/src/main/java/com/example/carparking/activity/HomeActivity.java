package com.example.carparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.carparking.R;
import com.example.carparking.util.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    LinearLayout btnMap, btnHistory, btnOwner, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        btnMap = findViewById(R.id.btnMap);
        btnHistory = findViewById(R.id.btnHistory);
        btnOwner = findViewById(R.id.btnOwner);
        btnSettings = findViewById(R.id.btnSettings);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_menu_logged_in);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_menu_logged_out);
        }

        navigationView.setNavigationItemSelectedListener(this);

        TextView tvHeader = navigationView.getHeaderView(0).findViewById(R.id.tvHeader);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            String name = SharedPrefManager.getInstance(this).getFullName();
            tvHeader.setText("Xin chào " + name);
        } else {
            tvHeader.setText("Xin chào khách");
        }

        btnMap.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, BookingHistoryActivity.class)));
        btnOwner.setOnClickListener(v -> startActivity(new Intent(this, CreateParkingActivity.class)));
        btnSettings.setOnClickListener(v -> Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Toast.makeText(this, "Hồ sơ cá nhân", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            SharedPrefManager.getInstance(this).clear();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        drawerLayout.closeDrawers();
        return true;
    }
}
