package com.example.carparking.activity;

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
import com.example.carparking.util.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullName, etPhone, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvBackToLogin;
    AuthApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        api = ApiClient.getClient(null).create(AuthApiService.class);

        tvBackToLogin.setOnClickListener(v -> {
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (fullName.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.fullName = fullName;
            user.phone = phone;
            user.password = password;
            user.confirmPassword = confirmPassword;
            btnRegister.setEnabled(false);
            btnRegister.setText("Đang đăng ký...");

            api.register(user).enqueue(new Callback<ResponseWrapper<Void>>() {
                @Override
                public void onResponse(Call<ResponseWrapper<Void>> call, Response<ResponseWrapper<Void>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, ErrorUtils.getErrorFromApi(response.errorBody()), Toast.LENGTH_SHORT).show();
                    }

                    btnRegister.setEnabled(true);
                    btnRegister.setText("Đăng ký");
                }

                @Override
                public void onFailure(Call<ResponseWrapper<Void>> call, Throwable t) {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Đăng ký");
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
