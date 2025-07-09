package com.example.carparking.model;

import com.google.gson.annotations.SerializedName;

public class BookingListing {
    private String id;
    private int totalPrice;
    private String parkingName;
    private String address;
    public enum Status {
        @SerializedName("completed")
        COMPLETED("Đã hoàn thành"),
        @SerializedName("pending")
        PENDING("Đang chờ xác nhận"),
        @SerializedName("confirmed")
        CONFIRMED("Đã xác nhận"),
        @SerializedName("cancelled")
        CANCELLED("Đã hủy");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    private Status status;
    private String startTime;
    private String endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}