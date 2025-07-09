package com.example.carparking.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.carparking.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddLicensePlateDialog extends DialogFragment {

    public interface OnLicensePlateAddedListener {
        void onLicensePlateAdded(String licensePlate);
    }

    private OnLicensePlateAddedListener listener;
    private TextInputEditText etLicensePlate;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLicensePlateAddedListener) {
            listener = (OnLicensePlateAddedListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_license_plate, null);

        etLicensePlate = view.findViewById(R.id.etLicensePlate);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);

        btnCancel.setOnClickListener(v -> dismiss());

        btnAdd.setOnClickListener(v -> {
            String licensePlate = etLicensePlate.getText().toString().trim().toUpperCase();

            if (TextUtils.isEmpty(licensePlate)) {
                Toast.makeText(getContext(), "Please enter a license plate number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidLicensePlate(licensePlate)) {
                Toast.makeText(getContext(), "Please enter a valid license plate number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                listener.onLicensePlateAdded(licensePlate);
            }
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }

    private boolean isValidLicensePlate(String licensePlate) {
        return !licensePlate.isEmpty();
    }

    public void setOnLicensePlateAddedListener(OnLicensePlateAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
