package com.example.carparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carparking.R;
import com.example.carparking.api.ApiClient;
import com.example.carparking.api.AuthApiService;
import com.example.carparking.model.ResponseWrapper;
import com.example.carparking.model.User;
import com.example.carparking.util.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etPhone, etPassword;
    Button btnLogin;
    TextView tvRegister;
    AuthApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        api = ApiClient.getClient(null).create(AuthApiService.class);

        btnLogin.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ số điện thoại và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.phone = phone;
            user.password = password;

            api.login(user).enqueue(new Callback<ResponseWrapper<User>>() {
                @Override
                public void onResponse(Call<ResponseWrapper<User>> call, Response<ResponseWrapper<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().success) {
                        String token = response.body().accessToken;
                        User loggedInUser = response.body().data;

                        if (loggedInUser != null) {
                            String fullName = loggedInUser.fullName;
                            String role = loggedInUser.role;

                            SharedPrefManager pref = SharedPrefManager.getInstance(LoginActivity.this);
                            pref.saveToken(token);
                            pref.saveFullName(fullName);
                            pref.saveRole(role);

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                            if ("admin".equals(role)) {
                                startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                            } else if ("owner".equals(role)) {
                                Toast.makeText(LoginActivity.this, "Chào chủ bãi " + fullName, Toast.LENGTH_LONG).show();
                            } else if ("user".equals(role)) {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Vai trò không xác định", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    } else if (response.body() != null) {
                        Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseWrapper<User>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
