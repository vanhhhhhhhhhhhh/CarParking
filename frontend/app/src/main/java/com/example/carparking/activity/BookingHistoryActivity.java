package com.example.carparking.activity;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.carparking.api.ApiClient;
import com.example.carparking.api.BookingApiService;
import com.example.carparking.fragments.DateRangeFragment;
import com.example.carparking.model.BookingListing;
import com.example.carparking.model.ResponseWrapper;
import com.example.carparking.util.SharedPrefManager;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryActivity extends AppCompatActivity
        implements ChipGroup.OnCheckedStateChangeListener, DateRangeFragment.OnDateRangeSelectedListener {

    private ChipGroup chipGroupFilters;
    private RecyclerView recyclerViewBookings;
    private BookingAdapter bookingAdapter;
    private List<BookingListing> bookingList;
    private ProgressBar historyProgressBar;
    private LinearLayout emptyStateView;
    private Long startDateUnixMs;
    private Long endDateUnixMs;
    private BookingApiService bookingApiService;
    private DateRangeFragment dateRangeFragment;
    private ActivityResultLauncher<Intent> launchDetailActivity;

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

        SharedPrefManager manager = SharedPrefManager.getInstance(this);
        bookingApiService = ApiClient.getClient(manager.getToken()).create(BookingApiService.class);

        ActionBar appbar = getSupportActionBar();
        if (appbar != null) {
            appbar.setDisplayHomeAsUpEnabled(true);
            appbar.setTitle(R.string.history_title);
        }

        initViews();
        setUpResultLauncher();
        setupRecyclerView();
    }

    private void setUpResultLauncher() {
        launchDetailActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() != RESULT_OK) return;
                    if (data == null) return;

                    boolean shouldReload = data.getBooleanExtra("shouldReload", false);
                    if (shouldReload) {
                        loadData();
                    }
                });
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate) {
        startDateUnixMs = startDate.getTime();
        endDateUnixMs = endDate.getTime();

        loadData();
    }

    private void initViews() {
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        emptyStateView = findViewById(R.id.emptyStateView);

        chipGroupFilters.setOnCheckedStateChangeListener(this);

        historyProgressBar = findViewById(R.id.historyLoadingProgress);

        dateRangeFragment = new DateRangeFragment();
        dateRangeFragment.setDateRangeListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dateRangeFragmentContainer, dateRangeFragment)
                .commit();
        setTime(7);
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);

        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookings.setAdapter(bookingAdapter);

        bookingAdapter.setOnBookingClickListener(booking -> {
            Intent intent = new Intent(this, BookingDetailActivity.class);
            intent.putExtra("bookingId", booking.getId());
            launchDetailActivity.launch(intent);
        });
    }

    private void loadData() {
        historyProgressBar.setVisibility(VISIBLE);
        recyclerViewBookings.setVisibility(INVISIBLE);
        emptyStateView.setVisibility(GONE);

        bookingApiService.getMyBookings(startDateUnixMs, endDateUnixMs).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseWrapper<List<BookingListing>>> call, Response<ResponseWrapper<List<BookingListing>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookingList.clear();
                    bookingList.addAll(response.body().data);
                    bookingAdapter.notifyDataSetChanged();
                    historyProgressBar.setVisibility(INVISIBLE);
                    recyclerViewBookings.setVisibility(VISIBLE);

                    if (bookingList.isEmpty()) {
                        emptyStateView.setVisibility(VISIBLE);
                    } else {
                        emptyStateView.setVisibility(GONE);
                    }
                } else {
                    Toast.makeText(BookingHistoryActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<List<BookingListing>>> call, Throwable t) {
                Toast.makeText(BookingHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                historyProgressBar.setVisibility(INVISIBLE);
                recyclerViewBookings.setVisibility(VISIBLE);
            }
        });
    }

    private void setTime(int lastNDays) {
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DAY_OF_YEAR, -lastNDays);
        Calendar to = Calendar.getInstance();

        dateRangeFragment.setSelectedDates(from.getTime(), to.getTime());
    }

    private void filterBookings(int checkedChipId) {
        if (checkedChipId == R.id.chipLast7Days) {
            setTime(7);
        } else if (checkedChipId == R.id.chipLast1Month) {
            setTime(30);
        } else if (checkedChipId == R.id.chipLast6Months) {
            setTime(180);
        } else if (checkedChipId == R.id.chipLast1Year) {
            setTime(365);
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