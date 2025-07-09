package com.example.carparking.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carparking.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingFragment extends Fragment implements TimeRangeFragment.OnTimeRangeSelectedListener {

    public interface OnBookingActionListener {
        void onBookingClicked(String licensePlate, Date startTime, Date endTime);
        void onBookingChanged(String licensePlate, Date startTime, Date endTime);
        void onAddNewLicensePlateClicked();
    }

    private ImageView parkingImage;
    private TextView parkingName;
    private TextView parkingStatus;
    private TextView operatingHours;
    private TextView pricePerHour;
    private ChipGroup chipGroupLicensePlates;
    private ChipGroup chipGroupTimeSlots;
    private TextView totalPrice;
    private MaterialButton bookButton;
    private Button addLicensePlateButton;
    private TimeRangeFragment timeRangeFragment;

    private OnBookingActionListener bookingListener;
    private String selectedLicensePlate;
    private String selectedTimeSlot;
    private Date selectedStartTime;
    private Date selectedEndTime;
    private List<String> licensePlates;
    private boolean allowBooking = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBookingActionListener) {
            bookingListener = (OnBookingActionListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupListeners();
    }

    private void initializeViews(View view) {
        parkingImage = view.findViewById(R.id.parkingImage);
        parkingName = view.findViewById(R.id.parkingName);
        parkingStatus = view.findViewById(R.id.parkingStatus);
        operatingHours = view.findViewById(R.id.operatingHours);
        pricePerHour = view.findViewById(R.id.pricePerHour);
        chipGroupLicensePlates = view.findViewById(R.id.chipGroupLicensePlates);
        chipGroupTimeSlots = view.findViewById(R.id.chipGroupTimeSlots);
        totalPrice = view.findViewById(R.id.totalPrice);
        bookButton = view.findViewById(R.id.bookButton);
        addLicensePlateButton = view.findViewById(R.id.btnAddNewPlate);
        timeRangeFragment = new TimeRangeFragment();
        timeRangeFragment.setTimeRangeListener(this);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.timeRangeFragmentContainer, timeRangeFragment)
                .commit();

        if (licensePlates != null) {
            setLicensePlates(licensePlates);
        }
        bookButton.setEnabled(allowBooking);
    }

    private void setupListeners() {
        chipGroupTimeSlots.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int id = chipGroupTimeSlots.getCheckedChipId();
            int minutes = getMinutesFromTimeSlot(id);
            timeRangeFragment.setDuration(minutes);
        });

        chipGroupLicensePlates.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int id = chipGroupLicensePlates.getCheckedChipId();
            Chip chip = group.findViewById(id);
            if (chip != null) {
                selectedLicensePlate = chip.getText().toString();
                if (bookingListener != null) {
                    bookingListener.onBookingChanged(selectedLicensePlate, selectedStartTime, selectedEndTime);
                }
            }
        });

        bookButton.setOnClickListener(v -> {
            if (bookingListener != null && isValidBooking()) {
                bookingListener.onBookingClicked(
                    selectedLicensePlate, 
                    selectedStartTime,
                    selectedEndTime
                );
            }
        });

        addLicensePlateButton.setOnClickListener(v -> {;
            if (bookingListener != null) {
                bookingListener.onAddNewLicensePlateClicked();
            }
        });
    }

    public void setAllowBooking(boolean allow) {
        allowBooking = allow;
        if (bookButton != null) {
            bookButton.setEnabled(allowBooking);
        }
    }

    public void setParkingData(String name, String status, String hours, int price) {
        if (parkingName != null) parkingName.setText(name);
        if (parkingStatus != null) parkingStatus.setText(status);
        if (operatingHours != null) operatingHours.setText(hours);
        if (pricePerHour != null) pricePerHour.setText(price);
    }

    public void setParkingImage(int imageResId) {
        if (parkingImage != null) {
            parkingImage.setImageResource(imageResId);
        }
    }

    public void setLicensePlates(List<String> plates) {
        if (chipGroupLicensePlates != null) {
            chipGroupLicensePlates.removeAllViews();
            
            for (int i = 0; i < plates.size(); i++) {
                Chip chip = new Chip(getContext());
                chip.setText(plates.get(i));
                chip.setId(View.generateViewId());
                chip.setCheckable(true);

                if (i == 0) {
                    chip.setChecked(true);
                    selectedLicensePlate = plates.get(i);
                }
                
                chipGroupLicensePlates.addView(chip);
            }
        } else {
            licensePlates = plates;
        }
    }

    @Override
    public void onTimeRangeSelected(Date startTime, Date endTime) {
        selectedStartTime = startTime;
        selectedEndTime = endTime;
        if (bookingListener != null) {
            bookingListener.onBookingChanged(selectedLicensePlate, startTime, endTime);
        }
    }

    private int getMinutesFromTimeSlot(int id) {
        if (id == R.id.chip15Minutes) {
            return 15;
        } else if (id == R.id.chip30Minutes) {
            return 30;
        } else if (id == R.id.chip1Hour) {
            return 60;
        } else if (id == R.id.chip2Hours) {
            return 120;
        } else if (id == R.id.chip5Hours) {
            return 300;
        } else if (id == R.id.chip8Hours) {
            return 480;
        } else {
            return 0;
        }
    }

    public void updateTotalPrice(int price) {
        String formattedPrice = String.format(Locale.getDefault(), "%d VND", price);
        totalPrice.setText(formattedPrice);
    }

    private boolean isValidBooking() {
        return selectedLicensePlate != null && 
               selectedStartTime != null &&
                selectedEndTime != null;
    }

    public void setBookingListener(OnBookingActionListener listener) {
        this.bookingListener = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bookingListener = null;
    }
} 