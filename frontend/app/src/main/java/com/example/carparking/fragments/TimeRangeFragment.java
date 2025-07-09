package com.example.carparking.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carparking.R;
import com.example.carparking.util.DateUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeRangeFragment extends Fragment {

    public interface OnTimeRangeSelectedListener {
        void onTimeRangeSelected(Date startTime, Date endTime);
    }

    private TextInputLayout startTimeInputLayout;
    private TextInputLayout endTimeInputLayout;
    private TextInputEditText startTimeEditText;
    private TextInputEditText endTimeEditText;
    
    private Date selectedStartTime;
    private Date selectedEndTime;
    private OnTimeRangeSelectedListener timeRangeListener;
    
    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTimeRangeSelectedListener) {
            timeRangeListener = (OnTimeRangeSelectedListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_range, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupListeners();
        setDefaultTimes();
    }

    private void initializeViews(View view) {
        startTimeInputLayout = view.findViewById(R.id.startTimeInputLayout);
        endTimeInputLayout = view.findViewById(R.id.endTimeInputLayout);
        startTimeEditText = view.findViewById(R.id.startTimeEditText);
        endTimeEditText = view.findViewById(R.id.endTimeEditText);
    }

    private void setupListeners() {
        startTimeEditText.setOnClickListener(v -> showDateTimePicker(true));
        endTimeEditText.setOnClickListener(v -> showDateTimePicker(false));
        
        startTimeInputLayout.setEndIconOnClickListener(v -> showDateTimePicker(true));
        endTimeInputLayout.setEndIconOnClickListener(v -> showDateTimePicker(false));
    }

    private void setDefaultTimes() {
        startCalendar.setTime(new Date());
        
        endCalendar.setTime(new Date());
        endCalendar.add(Calendar.HOUR_OF_DAY, 1);
        
        updateTimeDisplays();
        updateSelectedTimes();
        notifyTimeRangeChanged();
    }

    private void showDateTimePicker(boolean isStartTime) {
        Calendar calendar = isStartTime ? startCalendar : endCalendar;
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                showTimePicker(isStartTime, calendar);
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker(boolean isStartTime, Calendar calendar) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            requireContext(),
            (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                
                if (isStartTime) {
                    if (endCalendar.getTimeInMillis() <= calendar.getTimeInMillis()) {
                        endCalendar.setTimeInMillis(calendar.getTimeInMillis());
                    }
                } else {
                    if (calendar.getTimeInMillis() <= startCalendar.getTimeInMillis()) {
                        startCalendar.setTimeInMillis(calendar.getTimeInMillis());
                    }
                }
                
                updateTimeDisplays();
                updateSelectedTimes();
                notifyTimeRangeChanged();
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        );
        
        timePickerDialog.show();
    }

    private void updateTimeDisplays() {
        if (startTimeEditText != null) {
            String startTimeText = formatDateTime(startCalendar.getTime());
            startTimeEditText.setText(startTimeText);
        }
        
        if (endTimeEditText != null) {
            String endTimeText = formatDateTime(endCalendar.getTime());
            endTimeEditText.setText(endTimeText);
        }
    }

    private void updateSelectedTimes() {
        selectedStartTime = startCalendar.getTime();
        selectedEndTime = endCalendar.getTime();
    }

    private void notifyTimeRangeChanged() {
        if (timeRangeListener != null) {
            timeRangeListener.onTimeRangeSelected(selectedStartTime, selectedEndTime);
        }
    }

    private String formatDateTime(Date date) {
        return DateUtils.formatDateTime(date);
    }

    public void setStartTime(Date startTime) {
        if (startTime != null) {
            startCalendar.setTime(startTime);
            updateTimeDisplays();
            updateSelectedTimes();
            notifyTimeRangeChanged();
        }
    }

    public void setEndTime(Date endTime) {
        if (endTime != null) {
            endCalendar.setTime(endTime);
            updateTimeDisplays();
            updateSelectedTimes();
            notifyTimeRangeChanged();
        }
    }

    public void setDuration(int minutes) {
        endCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
        endCalendar.add(Calendar.MINUTE, minutes);
        updateTimeDisplays();
        updateSelectedTimes();
        notifyTimeRangeChanged();
    }

    public Date getSelectedStartTime() {
        return selectedStartTime;
    }

    public Date getSelectedEndTime() {
        return selectedEndTime;
    }

    public long getDurationInMinutes() {
        if (selectedStartTime != null && selectedEndTime != null) {
            return (selectedEndTime.getTime() - selectedStartTime.getTime()) / (1000 * 60);
        }
        return 0;
    }

    public void setTimeRangeListener(OnTimeRangeSelectedListener listener) {
        this.timeRangeListener = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timeRangeListener = null;
    }
} 