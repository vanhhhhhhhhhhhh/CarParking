package com.example.carparking.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.carparking.R;
import com.example.carparking.util.DateUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class DateRangeFragment extends Fragment {

    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(Date startDate, Date endDate);
    }

    private TextInputEditText startDateInput;
    private TextInputEditText endDateInput;
    private Date selectedStartDate;
    private Date selectedEndDate;
    private OnDateRangeSelectedListener dateRangeListener;

    public static DateRangeFragment newInstance() {
        return new DateRangeFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDateRangeSelectedListener) {
            dateRangeListener = (OnDateRangeSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDateRangeSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_range, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startDateInput = view.findViewById(R.id.etStartDateEditText);
        endDateInput = view.findViewById(R.id.etEndDateEditText);

        startDateInput.setOnClickListener(this::showDatePicker);
        endDateInput.setOnClickListener(this::showDatePicker);
    }

    private void showDatePicker(View view) {
        Pair<Long, Long> selection = new Pair<>(
            MaterialDatePicker.thisMonthInUtcMilliseconds(),
            MaterialDatePicker.todayInUtcMilliseconds()
        );

        MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(selection)
                .setTitleText(R.string.date_range_label)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection1 -> {
            selectedStartDate = new Date(selection1.first);
            selectedEndDate = new Date(selection1.second);
            updateDateInputs();

            if (dateRangeListener != null) {
                dateRangeListener.onDateRangeSelected(selectedStartDate, selectedEndDate);
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void updateDateInputs() {
        if (selectedStartDate != null) {
            startDateInput.setText(DateUtils.formatDate(selectedStartDate));
        } else {
            startDateInput.setText("");
        }

        if (selectedEndDate != null) {
            endDateInput.setText(DateUtils.formatDate(selectedEndDate));
        } else {
            endDateInput.setText("");
        }
    }

    public void setDateRangeListener(OnDateRangeSelectedListener listener) {
        this.dateRangeListener = listener;
    }

    public Date getSelectedStartDate() {
        return selectedStartDate;
    }

    public Date getSelectedEndDate() {
        return selectedEndDate;
    }

    public void setSelectedDates(Date startDate, Date endDate) {
        this.selectedStartDate = startDate;
        this.selectedEndDate = endDate;
        updateDateInputs();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dateRangeListener = null;
    }
}
