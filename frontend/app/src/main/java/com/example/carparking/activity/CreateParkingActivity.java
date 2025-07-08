package com.example.carparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carparking.R;

public class CreateParkingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_parking);

        Button btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(CreateParkingActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Kết thúc Activity hiện tại
        });
    }
}
