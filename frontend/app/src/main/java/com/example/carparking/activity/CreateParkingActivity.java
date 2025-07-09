package com.example.carparking.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carparking.R;

public class CreateParkingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parking);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Đăng ký trở thành chủ bãi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
