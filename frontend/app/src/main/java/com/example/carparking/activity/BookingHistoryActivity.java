package com.example.carparking.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class BookingHistoryActivity extends AppCompatActivity {

    private ChipGroup chipGroupFilters;
    private Chip chipLast7Days;
    private Chip chipLast1Month;
    private Chip chipLast6Months;
    private Chip chipLast1Year;
    
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
        setupClickListeners();
        loadSampleData();
    }

    private void initViews() {
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        chipLast7Days = findViewById(R.id.chipLast7Days);
        chipLast1Month = findViewById(R.id.chipLast1Month);
        chipLast6Months = findViewById(R.id.chipLast6Months);
        chipLast1Year = findViewById(R.id.chipLast1Year);
        
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
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

    private void setupClickListeners() {
        chipLast7Days.setOnClickListener(v -> {
            filterBookings("7_days");
        });
        
        chipLast1Month.setOnClickListener(v -> {
            filterBookings("1_month");
        });
        
        chipLast6Months.setOnClickListener(v -> {
            filterBookings("6_months");
        });
        
        chipLast1Year.setOnClickListener(v -> {
            filterBookings("1_year");
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

    private void filterBookings(String period) {
        resetChipSelection();
        
        switch (period) {
            case "7_days":
                chipLast7Days.setChecked(true);
                break;
            case "1_month":
                chipLast1Month.setChecked(true);
                break;
            case "6_months":
                chipLast6Months.setChecked(true);
                break;
            case "1_year":
                chipLast1Year.setChecked(true);
                break;
        }
    }

    private void resetChipSelection() {
        chipLast7Days.setChecked(false);
        chipLast1Month.setChecked(false);
        chipLast6Months.setChecked(false);
        chipLast1Year.setChecked(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 