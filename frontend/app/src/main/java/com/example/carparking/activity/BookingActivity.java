package com.example.carparking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.example.carparking.R;
import com.example.carparking.api.ApiClient;
import com.example.carparking.api.BookingApiService;
import com.example.carparking.api.LicensePlateApiService;
import com.example.carparking.api.ParkingApiService;
import com.example.carparking.dialogs.AddLicensePlateDialog;
import com.example.carparking.fragments.BookingFragment;
import com.example.carparking.model.Booking;
import com.example.carparking.model.BookingBody;
import com.example.carparking.model.LicensePlate;
import com.example.carparking.model.LicensePlateBody;
import com.example.carparking.model.Parking;
import com.example.carparking.model.ResponseWrapper;
import com.example.carparking.util.ErrorUtils;
import com.example.carparking.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity implements BookingFragment.OnBookingActionListener, AddLicensePlateDialog.OnLicensePlateAddedListener {

    private BookingFragment bookingFragment;
    private List<String> licensePlates;
    private LicensePlateApiService licensePlateApiService;
    private BookingApiService bookingApiService;
    private ParkingApiService parkingApiService;
    private String parkingId;
    private ProgressBar circularProgressBar;
    private FragmentContainerView bookingFragmentContainerView;
    int loadedCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar appBar = getSupportActionBar();
        if (appBar != null) {
            appBar.setDisplayHomeAsUpEnabled(true);
            appBar.setTitle(R.string.booking_title);
        }

        SharedPrefManager manager = SharedPrefManager.getInstance(this);
        licensePlateApiService = ApiClient.getClient(manager.getToken()).create(LicensePlateApiService.class);
        bookingApiService = ApiClient.getClient(manager.getToken()).create(BookingApiService.class);
        parkingApiService = ApiClient.getClient(manager.getToken()).create(ParkingApiService.class);

        bookingFragment = new BookingFragment();
        bookingFragment.setBookingListener(this);
        bookingFragment.setAllowBooking(false);

        circularProgressBar = findViewById(R.id.progressBar);

        bookingFragmentContainerView  = findViewById(R.id.bookingFragmentContainerView);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.bookingFragmentContainerView, bookingFragment)
                .commit();

        loadParkingData();
        loadLicensePlates();
    }

    private void loadParkingData() {
        Intent intent = getIntent();
        if (intent == null) {
            Log.e("BookingActivity", "Intent is null, cannot load parking data.");
            return;
        }

        parkingId = intent.getStringExtra("parkingId");
        if (parkingId == null) {
            Log.e("BookingActivity", "Parking ID is null, cannot load parking data.");
            return;
        }

        parkingApiService.getParkingById(parkingId).enqueue(new Callback<ResponseWrapper<Parking>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Parking>> call, Response<ResponseWrapper<Parking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Parking parking = response.body().data;
                    if (parking != null) {
                        boolean hasAvailableSpots = parking.getAvailableSlots() > 0;
                        String statusText;
                        if (hasAvailableSpots) {
                            statusText = getString(R.string.parking_available, parking.getAvailableSlots());
                        } else {
                            statusText = getString(R.string.parking_full);
                        }
                        bookingFragment.setParkingData(
                                parking.getName(),
                                statusText,
                                parking.getPricePerHour()
                        );
                        bookingFragment.setParkingImage(parking.getImageUrl());
                        bookingFragment.setAllowBooking(hasAvailableSpots);

                        showEverything();

                        Log.d("BookingActivity", "Parking data loaded successfully: " + parking.getName());
                    } else {
                        Log.e("BookingActivity", "Parking data is null.");
                    }
                } else {
                    Log.e("BookingActivity", "Failed to load parking data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Parking>> call, Throwable t) {
                Log.e("BookingActivity", "Error loading parking data: " + t.getMessage());
            }
        });
    }

    private void showEverything() {
        if (++loadedCount < 2) {
            return;
        }
        circularProgressBar.setVisibility(View.GONE);
        bookingFragmentContainerView.setVisibility(View.VISIBLE);
    }

    private void loadLicensePlates() {
        licensePlateApiService.getLicensePlates().enqueue(new Callback<ResponseWrapper<List<LicensePlate>>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<List<LicensePlate>>> call, Response<ResponseWrapper<List<LicensePlate>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LicensePlate> licensePlateList = response.body().data;
                    if (licensePlateList != null && bookingFragment != null) {
                        bookingFragment.setLicensePlates(licensePlateList);
                        showEverything();
                    }
                } else {
                    Log.e("BookingActivity", "Failed to load license plates: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<List<LicensePlate>>> call, Throwable t) {
                Log.e("BookingActivity", "Error loading license plates: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBookingClicked(LicensePlate licensePlate, Date startTime, Date endTime) {
        Log.d("BookingActivity", "Booking clicked: " + licensePlate + " from " + startTime + " to " + endTime);

        BookingBody bookingBody = new BookingBody(
                parkingId,
                licensePlate.getId(),
                startTime.getTime(),
                endTime.getTime()
        );

        bookingApiService.createBooking(bookingBody).enqueue(new Callback<ResponseWrapper<Booking>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Booking>> call, Response<ResponseWrapper<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("BookingActivity", "Booking created successfully");
                    showBookingSuccessDialog();
                } else {
                    Log.e("BookingActivity", "Failed to create booking: " + response.message());
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                    builder.setTitle(R.string.error_creating_booking)
                            .setMessage(ErrorUtils.getErrorFromApi(response.errorBody()))
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Booking>> call, Throwable t) {
                Log.e("BookingActivity", "Error creating booking: " + t.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                builder.setTitle(R.string.error_creating_booking)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void showBookingSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.booking_success_title)
                .setMessage(R.string.booking_success_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    Intent intent = new Intent(BookingActivity.this, BookingHistoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBookingChanged(LicensePlate licensePlate, Date startTime, Date endTime) {
        BookingBody bookingBody = new BookingBody(
                parkingId,
                null,
                startTime.getTime(),
                endTime.getTime()
        );

        bookingApiService.calculatePrice(bookingBody).enqueue(new Callback<ResponseWrapper<Integer>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Integer>> call, Response<ResponseWrapper<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer price = response.body().data;
                    bookingFragment.updateTotalPrice(price);
                    Log.d("BookingActivity", "Booking price calculated: " + price);
                } else {
                    Log.e("BookingActivity", "Failed to calculate booking price: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Integer>> call, Throwable t) {
                Log.e("BookingActivity", "Error calculating booking price: " + t.getMessage());
            }
        });
    }

    @Override
    public void onAddNewLicensePlateClicked() {
        AddLicensePlateDialog dialog = new AddLicensePlateDialog();
        dialog.setOnLicensePlateAddedListener(this);
        dialog.show(getSupportFragmentManager(), "AddLicensePlateDialog");
    }

    @Override
    public void onLicensePlateAdded(String licensePlate) {
        LicensePlateBody licensePlateBody = new LicensePlateBody(licensePlate);
        licensePlateApiService.createLicensePlate(licensePlateBody).enqueue(new Callback<ResponseWrapper<LicensePlate>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<LicensePlate>> call, Response<ResponseWrapper<LicensePlate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("BookingActivity", "License plate created successfully: " + response.body().data.getLicensePlate());
                    loadLicensePlates();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                    builder.setTitle(R.string.error_creating_license_plate)
                            .setMessage(ErrorUtils.getErrorFromApi(response.errorBody()))
                            .setPositiveButton("OK", null)
                            .show();
                    Log.e("BookingActivity", "Failed to create license plate: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<LicensePlate>> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                builder.setTitle(R.string.error_creating_license_plate)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
                Log.e("BookingActivity", "Error creating license plate: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}