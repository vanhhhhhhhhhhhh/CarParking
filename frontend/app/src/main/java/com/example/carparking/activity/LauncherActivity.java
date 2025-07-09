package com.example.carparking.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carparking.util.SharedPrefManager;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefManager pref = SharedPrefManager.getInstance(this);

        if (!pref.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            String role = pref.getRole();

            if ("admin".equals(role)) {
                startActivity(new Intent(this, AdminHomeActivity.class));
            } else if ("owner".equals(role)) {
                startActivity(new Intent(this, HomeActivity.class));
            } else {
                startActivity(new Intent(this, HomeActivity.class));
            }
        }

        finish();
    }
}
