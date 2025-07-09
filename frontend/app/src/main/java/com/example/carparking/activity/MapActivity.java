package com.example.carparking.activity;

import android.content.Intent;
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
import com.example.carparking.api.ApiClient;
import com.example.carparking.api.ParkingApiService;
import com.example.carparking.api.PlacesApiService;
import com.example.carparking.fragments.MapsFragment;
import com.example.carparking.fragments.SearchFragment;
import com.example.carparking.model.Parking;
import com.example.carparking.model.ResponseWrapper;
import com.example.carparking.model.SearchResult;
import com.example.carparking.util.Debouncer;
import com.example.carparking.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements SearchFragment.SearchQueryListener, MapsFragment.OnParkingClickListener {

    private static final String TAG = "MapActivity";
    private PlacesApiService placesApiService;
    private ParkingApiService parkingApiService;
    private SearchFragment searchFragment;
    private MapsFragment mapsFragment;
    List<SearchResult> searchResults = new ArrayList<>();


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
        loadParkings();
    }

    private void loadParkings() {
        parkingApiService.getParkingList(null, null, null, null).enqueue(new Callback<ResponseWrapper<List<Parking>>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<List<Parking>>> call, Response<ResponseWrapper<List<Parking>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Parking> parkingList = response.body().data;
                    if (parkingList != null && !parkingList.isEmpty()) {
                        mapsFragment.setParkings(parkingList);
                    } else {
                        Log.d(TAG, "No parking results found");
                    }
                } else {
                    Log.e(TAG, "Failed to fetch parking results: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<List<Parking>>> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpMapFragment() {
        mapsFragment = new MapsFragment();
        mapsFragment.setOnParkingClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mapsFragmentContainer2, mapsFragment)
                .commit();
    }


    private void setupSearchFragment() {
        searchFragment = new SearchFragment();

        searchFragment.setOnSearchItemSelectedCallback(this::handleSearchResultSelection);

        searchFragment.setSearchQueryListener(this);

        SharedPrefManager manager = SharedPrefManager.getInstance(this);
        placesApiService = new PlacesApiService(this);
        parkingApiService = ApiClient.getClient(manager.getToken()).create(ParkingApiService.class);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.overlayFragmentContainer, searchFragment)
                .commit();
    }

    private final Debouncer<String> debouncer = new Debouncer<>(400, query -> {
        performSearch(query);
        performParkingSearch(query);
    });

    @Override
    public void doSearch(String query) {
        debouncer.consume(query);
    }

    private void performParkingSearch(String query) {
        parkingApiService.getParkingList(query, null, null, null).enqueue(new Callback<ResponseWrapper<List<Parking>>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<List<Parking>>> call, Response<ResponseWrapper<List<Parking>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Parking> parkingList = response.body().data;
                    searchResults.removeIf(searchResult -> searchResult.getType() == SearchResult.SearchResultType.PARKING_LOCATION);

                    if (parkingList != null && !parkingList.isEmpty()) {
                        for (Parking parking : parkingList) {
                            SearchResult searchResult = new SearchResult(
                                    parking.getId(),
                                    parking.getName(),
                                    parking.getAddress(),
                                    SearchResult.SearchResultType.PARKING_LOCATION
                            );
                            searchResult.setLatitude(parking.getLocation().coordinates.get(1));
                            searchResult.setLongitude(parking.getLocation().coordinates.get(0));

                            searchResults.add(0, searchResult);
                        }
                    } else {
                        Log.d(TAG, "No parking results found");
                    }
                } else {
                    Log.e(TAG, "Failed to fetch parking results: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<List<Parking>>> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String query) {
        placesApiService.searchBuildings(query, new PlacesApiService.PlacesCallback() {
            @Override
            public void onSuccess(List<SearchResult> results) {
                runOnUiThread(() -> {
                    if (searchFragment != null) {
                        searchResults.removeIf(searchResult -> searchResult.getType() == SearchResult.SearchResultType.BUILDING);
                        searchResults.addAll(results);
                        searchFragment.setSearchResults(searchResults);
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
        if (mapsFragment != null) {
            Log.d("MapActivity", "Focusing on parking location: " + result.getLatitude() + ", " + result.getLongitude());
            mapsFragment.focusOnLocation(result.getLatitude(), result.getLongitude());
        }
    }

    private void openBuildingDetails(SearchResult result) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onParkingClick(Parking parking) {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("parkingId", parking.getId());

        startActivity(intent);
    }
}