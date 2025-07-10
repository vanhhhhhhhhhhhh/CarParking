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
import com.example.carparking.adapters.OwnerBookingAdapter;
import com.example.carparking.api.ApiClient;
import com.example.carparking.api.OwnerBookingApiService;
import com.example.carparking.model.BookingListing;
import com.example.carparking.model.ResponseWrapper;
import com.example.carparking.util.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private RecyclerView rvBookingRequests;
    private OwnerBookingApiService ownerBookingApi;
    private List<BookingListing> bookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        rvBookingRequests = findViewById(R.id.rvBookingRequests);
        rvBookingRequests.setLayoutManager(new LinearLayoutManager(this));


        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
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

        loadBookingRequests();

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadBookingRequests() {
        ownerBookingApi = ApiClient.getClient(SharedPrefManager.getInstance(this).getToken())
                .create(OwnerBookingApiService.class);

        ownerBookingApi.getAllBookingRequests().enqueue(new Callback<ResponseWrapper<List<BookingListing>>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<List<BookingListing>>> call, Response<ResponseWrapper<List<BookingListing>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookings = response.body().data;
                    OwnerBookingAdapter adapter = new OwnerBookingAdapter(
                            OwnerHomeActivity.this,
                            bookings,
                            ownerBookingApi,
                            OwnerHomeActivity.this::loadBookingRequests
                    );
                    rvBookingRequests.setAdapter(adapter);
                } else {
                    Toast.makeText(OwnerHomeActivity.this, "Không thể tải danh sách", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<List<BookingListing>>> call, Throwable t) {
                Toast.makeText(OwnerHomeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
