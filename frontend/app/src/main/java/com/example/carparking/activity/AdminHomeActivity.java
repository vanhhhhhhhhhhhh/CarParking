package com.example.carparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carparking.R;
import com.example.carparking.adapters.ParkingRequestAdapter;
import com.example.carparking.api.ApiClient;
import com.example.carparking.api.ParkingApiService;
import com.example.carparking.model.Parking;
import com.example.carparking.model.ResponseWrapper;
import com.example.carparking.util.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        TextView tvHeader = navigationView.getHeaderView(0).findViewById(R.id.tvHeader);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            String name = SharedPrefManager.getInstance(this).getFullName();
            tvHeader.setText("Xin chào " + name);
        } else {
            tvHeader.setText("Xin chào khách");
        }

        rvParkingRequests.setLayoutManager(new LinearLayoutManager(this));
        loadPendingRequests();
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
        }
        return false;
    }

    private void loadPendingRequests() {
        String token = SharedPrefManager.getInstance(this).getToken();
        ParkingApiService apiService = ApiClient.getClient(token).create(ParkingApiService.class);

        Call<ResponseWrapper<List<Parking>>> call = apiService.getParkingList(
                null, null, null, null
        );

        call.enqueue(new Callback<ResponseWrapper<List<Parking>>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<List<Parking>>> call, Response<ResponseWrapper<List<Parking>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Parking> parkingList = response.body().data;
                    ParkingRequestAdapter adapter = new ParkingRequestAdapter(AdminHomeActivity.this, parkingList);
                    rvParkingRequests.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminHomeActivity.this, "Không thể tải danh sách bãi đỗ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<List<Parking>>> call, Throwable t) {
                Toast.makeText(AdminHomeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
