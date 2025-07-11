package com.example.carparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.example.carparking.R;
import com.example.carparking.api.ApiClient;
import com.example.carparking.api.BookingApiService;
import com.example.carparking.api.ParkingApiService;
import com.example.carparking.fragments.TimeRangeFragment;
import com.example.carparking.model.Booking;
import com.example.carparking.model.BookingStatus;
import com.example.carparking.model.Parking;
import com.example.carparking.model.ResponseWrapper;
import com.example.carparking.util.ErrorUtils;
import com.example.carparking.util.SharedPrefManager;
import com.example.carparking.util.StringUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailActivity extends AppCompatActivity {

    private ImageView parkingImage;
    private TextView parkingName;
    private TextView pricePerHour;
    private Chip chipSelectedLicensePlate;
    private TextView totalPrice;
    private MaterialButton bookButton;

    private TimeRangeFragment timeRangeFragment;
    private String bookingId;
    private BookingApiService bookingApiService;
    private ParkingApiService parkingApiService;
    private ProgressBar bookingDetailProgress;
    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar appbar = getSupportActionBar();
        if (appbar != null) {
            appbar.setDisplayHomeAsUpEnabled(true);
            appbar.setTitle(R.string.booking_detail_title);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("bookingId")) {
            bookingId = intent.getStringExtra("bookingId");
        } else {
            Log.e("BookingDetailActivity", "No bookingId provided in intent extras");
            setResult(RESULT_CANCELED, null);
            finish();
        }

        SharedPrefManager manager = SharedPrefManager.getInstance(this);
        bookingApiService = ApiClient.getClient(manager.getToken()).create(BookingApiService.class);
        parkingApiService = ApiClient.getClient(manager.getToken()).create(ParkingApiService.class);

        setupViews();
        setupListeners();
        loadBookingData();
    }

    private void setupViews() {
        parkingImage = findViewById(R.id.parkingImage);
        parkingName = findViewById(R.id.parkingName);
        pricePerHour = findViewById(R.id.pricePerHour);
        chipSelectedLicensePlate = findViewById(R.id.chipSelectedLicensePlate);
        totalPrice = findViewById(R.id.totalPrice);
        bookButton = findViewById(R.id.bookButton);
        bookButton.setVisibility(View.GONE);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        bookingDetailProgress = findViewById(R.id.bookingDetailProgress);

        timeRangeFragment = new TimeRangeFragment();
        timeRangeFragment.setEnabled(false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.timeRangeFragmentContainer, timeRangeFragment)
                .commit();
    }

    private void setupListeners() {
        bookButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailActivity.this);
            builder
                    .setTitle(R.string.confirm_cancel_booking_title)
                    .setMessage(R.string.confirm_cancel_booking_message)
                    .setPositiveButton("OK", (dialog, which) -> {
                        cancelBooking();
                    })
                    .setNegativeButton(R.string.cancel, null);

            builder.show();
        });
    }

    private void showEverything() {
        bookingDetailProgress.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
    }

    private void loadParkingData(String parkingId) {
        parkingApiService.getParkingById(parkingId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Parking>> call, Response<ResponseWrapper<Parking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Parking parking = response.body().data;
                    if (parking != null) {
                        parkingName.setText(parking.getName());
                        pricePerHour.setText(StringUtils.moneyFormat(parking.getPricePerHour()));
                        Glide.with(BookingDetailActivity.this)
                                .load(parking.getImageUrl())
                                .placeholder(R.drawable.ic_parking)
                                .error(R.drawable.ic_parking)
                                .into(parkingImage);

                        showEverything();
                    } else {
                        Log.e("BookingDetailActivity", "Parking data is null");
                    }
                } else {
                    Log.e("BookingDetailActivity", "Failed to load parking details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Parking>> call, Throwable t) {
                Log.e("BookingDetailActivity", "Failed to load parking details: " + t.getMessage());
            }
        });
    }

    private void loadBookingData() {
        bookingApiService.getBookingDetails(bookingId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Booking>> call, Response<ResponseWrapper<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Booking booking = response.body().data;
                    if (booking != null) {
                        loadParkingData(booking.getParkingId());
                        chipSelectedLicensePlate.setText(booking.getVehicleNumber());

                        timeRangeFragment.setStartTime(booking.getStartTime());
                        timeRangeFragment.setEndTime(booking.getEndTime());

                        if (booking.getStatus() == BookingStatus.PENDING) {
                            bookButton.setVisibility(View.VISIBLE);
                        }

                        String formattedPrice = String.format(Locale.getDefault(), "%s VND", StringUtils.moneyFormat(booking.getTotalPriceInt()));
                        totalPrice.setText(formattedPrice);
                    } else {
                        Log.e("BookingDetailActivity", "Booking data is null");
                    }
                } else {
                    Log.e("BookingDetailActivity", "Failed to load booking details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Booking>> call, Throwable t) {
                Log.e("BookingDetailActivity", "Failed to load booking details: " + t.getMessage());
            }
        });
    }

    private void cancelBooking() {
        bookingApiService.cancelBooking(bookingId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Void>> call, Response<ResponseWrapper<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(BookingDetailActivity.this, R.string.cancel_booking_success, Toast.LENGTH_LONG).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("shouldReload", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailActivity.this);
                    builder.setTitle(R.string.error_cancelling_booking)
                            .setMessage(ErrorUtils.getErrorFromApi(response.errorBody()))
                            .setPositiveButton("OK", null)
                            .show();
                    Log.e("BookingDetailActivity", "Failed to cancel booking: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Void>> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailActivity.this);
                builder.setTitle(R.string.error_cancelling_booking)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
                Log.e("BookingDetailActivity", "Failed to cancel booking: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}