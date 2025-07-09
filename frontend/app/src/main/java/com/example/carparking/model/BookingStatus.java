package com.example.carparking.model;

import com.google.gson.annotations.SerializedName;

public enum BookingStatus {
    @SerializedName("completed")
    COMPLETED("Đã hoàn thành"),
    @SerializedName("pending")
    PENDING("Đang chờ xác nhận"),
    @SerializedName("confirmed")
    CONFIRMED("Đã xác nhận"),
    @SerializedName("cancelled")
    CANCELLED("Đã hủy");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
