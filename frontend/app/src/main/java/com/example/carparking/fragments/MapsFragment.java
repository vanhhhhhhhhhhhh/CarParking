package com.example.carparking.fragments;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carparking.R;
import com.example.carparking.model.Parking;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsFragment extends Fragment {

    public interface OnParkingClickListener {
        void onParkingClick(Parking parking);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private GoogleMap googleMap;
    private OnParkingClickListener onParkingClickListener;
    private List<Parking> parkingList;

    public void setOnParkingClickListener(OnParkingClickListener listener) {
        this.onParkingClickListener = listener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        googleMap.setMyLocationEnabled(true);
                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle(R.string.permission_denied)
                                .setMessage(R.string.show_location_permission_message)
                                .setPositiveButton("OK", null)
                                .create();

                        dialog.show();
                    }
                });
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsFragment.this.googleMap = googleMap;

            Context context = getContext();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                googleMap.setMyLocationEnabled(true);
            }

            UiSettings settings = googleMap.getUiSettings();
            settings.setZoomControlsEnabled(true);
            settings.setCompassEnabled(true);
            settings.setMyLocationButtonEnabled(false);

            focusOnMyLocation();

            if (parkingList != null) {
                displayParkingMarkers();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        view.findViewById(R.id.floatingActionButton).setOnClickListener(v -> focusOnMyLocation());
    }

    @SuppressWarnings("MissingPermission")
    public void focusOnMyLocation() {
        Context context = getContext();
        if (context == null || googleMap == null) return;

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                    }
                });
    }

    public void focusOnLocation(double latitude, double longitude) {
        if (googleMap != null) {
            LatLng location = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
        }
    }

    public void setParkings(List<Parking> parkings) {
        this.parkingList = parkings;
        if (googleMap != null && parkings != null) {
            displayParkingMarkers();
        }
    }

    private void displayParkingMarkers() {
        if (googleMap == null || parkingList == null) return;

        googleMap.clear();

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                if (onParkingClickListener != null) {
                    Parking parking = (Parking) marker.getTag();
                    if (parking != null) {
                        onParkingClickListener.onParkingClick(parking);
                    }
                }
            }
        });

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                TextView tvParkingName = infoWindow.findViewById(R.id.tv_parking_name);

                Parking parking = (Parking) marker.getTag();
                if (parking != null) {
                    tvParkingName.setText(parking.getName());
                }

                return infoWindow;
            }
        });

        for (Parking parking : parkingList) {
            if (parking.getLocation() != null &&
                    parking.getLocation().coordinates != null &&
                    parking.getLocation().coordinates.size() >= 2) {

                double longitude = parking.getLocation().coordinates.get(0);
                double latitude = parking.getLocation().coordinates.get(1);

                LatLng position = new LatLng(latitude, longitude);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(position)
                        .title(parking.getName())
                        .snippet(parking.getAvailableSlots() + "/" + parking.getTotalSlots());

                Marker marker = googleMap.addMarker(markerOptions);
                if (marker != null) {
                    marker.setTag(parking);
                }
            }
        }
    }
}