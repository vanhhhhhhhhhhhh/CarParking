package com.example.carparking.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carparking.R;
import com.example.carparking.adapters.BookingAdapter;
import com.example.carparking.model.BookingDemo;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity implements ChipGroup.OnCheckedStateChangeListener {

    private ChipGroup chipGroupFilters;
    private RecyclerView recyclerViewBookings;
    private BookingAdapter bookingAdapter;
    private List<BookingDemo> bookingList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar appbar = getSupportActionBar();
        if (appbar != null) {
            appbar.setDisplayHomeAsUpEnabled(true);
            appbar.setTitle(R.string.history_title);
        }

        initViews();
        setupRecyclerView();
        loadSampleData();
    }

    private void initViews() {
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);

        chipGroupFilters.setOnCheckedStateChangeListener(this);
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);
        
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookings.setAdapter(bookingAdapter);
        
        bookingAdapter.setOnBookingClickListener(booking -> {
            Toast.makeText(this, "Viewing details for: " + booking.getAddress(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSampleData() {
        bookingList.clear();
        
        bookingList.add(new BookingDemo("1", "Địa chỉ 1", "6:00 - 12:00", "200.000 VND", BookingDemo.Status.COMPLETED));
        bookingList.add(new BookingDemo("2", "Vincom Center", "14:00 - 18:00", "150.000 VND", BookingDemo.Status.PENDING));
        bookingList.add(new BookingDemo("3", "Lotte Center", "8:00 - 17:00", "300.000 VND", BookingDemo.Status.CONFIRMED));
        bookingList.add(new BookingDemo("4", "Times City", "10:00 - 15:00", "180.000 VND", BookingDemo.Status.CANCELLED));
        bookingList.add(new BookingDemo("5", "Royal City", "9:00 - 16:00", "250.000 VND", BookingDemo.Status.COMPLETED));
        bookingList.add(new BookingDemo("6", "Indochina Plaza", "7:00 - 19:00", "400.000 VND", BookingDemo.Status.CONFIRMED));
        
        bookingAdapter.notifyDataSetChanged();
    }

    private void filterBookings(int checkedChipId) {
        if (checkedChipId == R.id.chipLast7Days) {
            Toast.makeText(this, "Filtering bookings for the last 7 days", Toast.LENGTH_SHORT).show();
        } else if (checkedChipId == R.id.chipLast1Month) {
            Toast.makeText(this, "Filtering bookings for the last 1 month", Toast.LENGTH_SHORT).show();
        } else if (checkedChipId == R.id.chipLast6Months) {
            Toast.makeText(this, "Filtering bookings for the last 6 months", Toast.LENGTH_SHORT).show();
        } else if (checkedChipId == R.id.chipLast1Year) {
            Toast.makeText(this, "Filtering bookings for the last 1 year", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
        filterBookings(group.getCheckedChipId());
    }
}