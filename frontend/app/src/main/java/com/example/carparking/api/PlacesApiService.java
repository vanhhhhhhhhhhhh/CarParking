package com.example.carparking.api;

import android.content.Context;
import android.util.Log;

import com.example.carparking.model.SearchResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class PlacesApiService {
    private static final String TAG = "PlacesApiService";
    private PlacesClient placesClient;
    private AutocompleteSessionToken token;

    public interface PlacesCallback {
        void onSuccess(List<SearchResult> results);
        void onError(String error);
    }

    public interface PlaceDetailsCallback {
        void onSuccess(LatLng result);
        void onError(String error);
    }

    public PlacesApiService(Context context) {
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(com.example.carparking.R.string.google_maps_key));
        }
        placesClient = Places.createClient(context);
        token = AutocompleteSessionToken.newInstance();
    }

    public void searchBuildings(String query, PlacesCallback callback) {
        if (query == null || query.trim().isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypesFilter(List.of(PlaceTypes.ESTABLISHMENT))
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            List<SearchResult> searchResults = new ArrayList<>();

            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                prediction.getSecondaryText(null);
                SearchResult result = new SearchResult(
                    null,
                    prediction.getPrimaryText(null).toString(),
                        prediction.getSecondaryText(null).toString(),
                    SearchResult.SearchResultType.BUILDING
                );
                result.setPlaceId(prediction.getPlaceId());
                searchResults.add(result);
            }

            callback.onSuccess(searchResults);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                callback.onError("Failed to search places: " + apiException.getMessage());
            } else {
                Log.e(TAG, "Error searching places", exception);
                callback.onError("Error searching places: " + exception.getMessage());
            }
        });
    }

    public void getPlaceLocation(String placeId, PlaceDetailsCallback callback) {
        if (placeId == null || placeId.trim().isEmpty()) {
            callback.onError("Invalid Place ID");
            return;
        }

        List<Place.Field> placeFields = List.of(Place.Field.LOCATION);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            LatLng latLng = place.getLocation();
            if (latLng != null) {
                callback.onSuccess(latLng);
            } else {
                callback.onError("Location not found for the place");
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                callback.onError("Failed to fetch place: " + apiException.getMessage());
            } else {
                Log.e(TAG, "Error fetching place", exception);
                callback.onError("Error fetching place: " + exception.getMessage());
            }
        });
    }
}
