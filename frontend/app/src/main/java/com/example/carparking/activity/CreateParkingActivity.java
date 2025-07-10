package com.example.carparking.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carparking.R;
import com.example.carparking.api.ApiClient;
import com.example.carparking.api.ParkingApiService;
import com.example.carparking.util.InputStreamRequestBody;
import com.example.carparking.util.SharedPrefManager;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateParkingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;

    private EditText etName, etAddress, etLatitude, etLongitude, etTotalSlots,
            etAvailableSlots, etPricePerHour, etOpenTime, etCloseTime;
    private Button btnSelectImage, btnSubmit;
    private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parking);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        etTotalSlots = findViewById(R.id.etTotalSlots);
        etAvailableSlots = findViewById(R.id.etAvailableSlots);
        etPricePerHour = findViewById(R.id.etPricePerHour);
        etOpenTime = findViewById(R.id.etOpenTime);
        etCloseTime = findViewById(R.id.etCloseTime);

        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPreview = findViewById(R.id.imgPreview);

        btnSelectImage.setOnClickListener(view -> openImagePicker());
        btnSubmit.setOnClickListener(view -> submitCreateParking());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgPreview.setImageURI(selectedImageUri);
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = "image.jpg";
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        } else {
            String path = uri.getPath();
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                result = path.substring(cut + 1);
            }
        }
        return result;
    }

    private void submitCreateParking() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String latitude = etLatitude.getText().toString().trim();
        String longitude = etLongitude.getText().toString().trim();
        String totalSlots = etTotalSlots.getText().toString().trim();
        String availableSlots = etAvailableSlots.getText().toString().trim();
        String pricePerHour = etPricePerHour.getText().toString().trim();
        String openTime = etOpenTime.getText().toString().trim();
        String closeTime = etCloseTime.getText().toString().trim();

        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh bãi đỗ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            String fileName = getFileName(selectedImageUri);

            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), address);
            RequestBody coordinatesBody = RequestBody.create(MediaType.parse("text/plain"), "[" + longitude + "," + latitude + "]");
            RequestBody totalSlotsBody = RequestBody.create(MediaType.parse("text/plain"), totalSlots);
            RequestBody availableSlotsBody = RequestBody.create(MediaType.parse("text/plain"), availableSlots);
            RequestBody pricePerHourBody = RequestBody.create(MediaType.parse("text/plain"), pricePerHour);
            RequestBody openTimeBody = RequestBody.create(MediaType.parse("text/plain"), openTime);
            RequestBody closeTimeBody = RequestBody.create(MediaType.parse("text/plain"), closeTime);

            RequestBody requestFile = new InputStreamRequestBody("image/*", inputStream);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", fileName, requestFile);

            String token = SharedPrefManager.getInstance(this).getToken();
            ParkingApiService apiService = ApiClient.getClient(token).create(ParkingApiService.class);

            Call<ResponseBody> call = apiService.createParking(
                    imagePart,
                    nameBody,
                    addressBody,
                    coordinatesBody,
                    totalSlotsBody,
                    availableSlotsBody,
                    pricePerHourBody,
                    openTimeBody,
                    closeTimeBody
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CreateParkingActivity.this, "Tạo bãi đỗ thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateParkingActivity.this, "Thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CreateParkingActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Không thể đọc ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
