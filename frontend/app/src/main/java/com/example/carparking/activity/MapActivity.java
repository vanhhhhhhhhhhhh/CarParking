package com.example.carparking.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carparking.R;
import com.example.carparking.fragments.MapsFragment;
import com.example.carparking.fragments.SearchFragment;
import com.example.carparking.model.SearchResult;

import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity {

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
        SearchFragment searchFragment = new SearchFragment();

        searchFragment.setOnSearchItemSelectedCallback(searchResult -> {
            Toast.makeText(MapActivity.this,
                    "Selected: " + searchResult.getTitle(),
                    Toast.LENGTH_SHORT).show();

            handleSearchResultSelection(searchResult);
        });

        List<SearchResult> sampleResults = createSampleSearchResults();
        searchFragment.setSearchResults(sampleResults);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.overlayFragmentContainer, searchFragment)
                .commit();
    }

    private List<SearchResult> createSampleSearchResults() {
        return Arrays.asList(
                new SearchResult("1", "Parking Location 1", "123 Main Street, District 1", SearchResult.SearchResultType.PARKING_LOCATION),
                new SearchResult("2", "Building 1", "456 Office Building, District 2", SearchResult.SearchResultType.BUILDING),
                new SearchResult("3", "Shopping Mall Parking", "789 Shopping Center, District 3", SearchResult.SearchResultType.PARKING_LOCATION),
                new SearchResult("4", "City Center Building", "321 Business District, District 1", SearchResult.SearchResultType.BUILDING)
        );
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




    private void openParkingDetails(SearchResult result) {
    }

    private void openBuildingDetails(SearchResult result) {
    }

}