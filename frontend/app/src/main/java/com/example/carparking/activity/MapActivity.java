package com.example.carparking.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carparking.R;
import com.example.carparking.api.PlacesApiService;
import com.example.carparking.fragments.MapsFragment;
import com.example.carparking.fragments.SearchFragment;
import com.example.carparking.model.SearchResult;
import com.example.carparking.util.Debouncer;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements SearchFragment.SearchQueryListener {

    private static final String TAG = "MapActivity";
    private PlacesApiService placesApiService;
    private SearchFragment searchFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.parking_title);
        }

        setupSearchFragment();
        setUpMapFragment();
    }

    private void setUpMapFragment() {
        MapsFragment mapsFragment = new MapsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mapsFragmentContainer2, mapsFragment)
                .commit();
    }


    private void setupSearchFragment() {
        searchFragment = new SearchFragment();

        searchFragment.setOnSearchItemSelectedCallback(searchResult -> {
            Toast.makeText(MapActivity.this,
                    "Selected: " + searchResult.getTitle(),
                    Toast.LENGTH_SHORT).show();

            handleSearchResultSelection(searchResult);
        });

        searchFragment.setSearchQueryListener(this);

        placesApiService = new PlacesApiService(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.overlayFragmentContainer, searchFragment)
                .commit();
    }

    private final Debouncer<String> debouncer = new Debouncer<>(400, query -> {
        performSearch(query);
    });

    @Override
    public void doSearch(String query) {
        debouncer.consume(query);
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (searchFragment != null) {
                searchFragment.setSearchResults(new ArrayList<>());
            }
            return;
        }

        placesApiService.searchBuildings(query, new PlacesApiService.PlacesCallback() {
            @Override
            public void onSuccess(List<SearchResult> results) {
                runOnUiThread(() -> {
                    if (searchFragment != null) {
                        searchFragment.setSearchResults(results);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Places search error: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(MapActivity.this,
                        "Search error: " + error,
                        Toast.LENGTH_SHORT).show();
                    if (searchFragment != null) {
                        searchFragment.setSearchResults(new ArrayList<>());
                    }
                });
            }
        });
    }

    private void handleSearchResultSelection(SearchResult searchResult) {
        switch (searchResult.getType()) {
            case PARKING_LOCATION:
                openParkingDetails(searchResult);
                break;
            case BUILDING:
                openBuildingDetails(searchResult);
                break;
        }
    }

    private void openParkingDetails(SearchResult result) {

    }

    private void openBuildingDetails(SearchResult result) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}