package com.example.carparking.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carparking.R;
import com.example.carparking.dialogs.AddLicensePlateDialog;
import com.example.carparking.fragments.BookingFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingActivity extends AppCompatActivity implements BookingFragment.OnBookingActionListener, AddLicensePlateDialog.OnLicensePlateAddedListener {

    private BookingFragment bookingFragment;
    private List<String> licensePlates;

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

        bookingFragment = new BookingFragment();
        bookingFragment.setBookingListener(this);
        bookingFragment.setAllowBooking(false);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.bookingFragmentContainerView, bookingFragment)
                .commit();
    }

    @Override
    public void onBookingClicked(String licensePlate, Date startTime, Date endTime) {
        Log.d("BookingActivity", "Booking clicked: " + licensePlate + " from " + startTime + " to " + endTime);
    }

    @Override
    public void onBookingChanged(String licensePlate, Date startTime, Date endTime) {
        Log.d("BookingActivity", "Booking changed: " + licensePlate + " from " + startTime + " to " + endTime);
    }

    @Override
    public void onAddNewLicensePlateClicked() {
        AddLicensePlateDialog dialog = new AddLicensePlateDialog();
        dialog.setOnLicensePlateAddedListener(this);
        dialog.show(getSupportFragmentManager(), "AddLicensePlateDialog");
    }

    @Override
    public void onLicensePlateAdded(String licensePlate) {
        if (licensePlates == null) {
            licensePlates = new ArrayList<>();
        }

        if (!licensePlates.contains(licensePlate)) {
            licensePlates.add(licensePlate);
            if (bookingFragment != null) {
                bookingFragment.setLicensePlates(licensePlates);
            }
            Log.d("BookingActivity", "Added new license plate: " + licensePlate);
        } else {
            Log.d("BookingActivity", "License plate already exists: " + licensePlate);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}