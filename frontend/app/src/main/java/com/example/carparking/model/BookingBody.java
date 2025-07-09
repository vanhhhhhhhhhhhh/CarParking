package com.example.carparking.model;

import com.google.gson.annotations.SerializedName;

public class BookingBody {
    private String parkingId;
    private String licensePlateId;
    @SerializedName("startTime")
    private long startTimeUnixMs;

    @SerializedName("endTime")
    private long endTimeUnixMs;

    public BookingBody(String parkingId, String licensePlateId, long startTimeUnixMs, long endTimeUnixMs) {
        this.parkingId = parkingId;
        this.licensePlateId = licensePlateId;
        this.startTimeUnixMs = startTimeUnixMs;
        this.endTimeUnixMs = endTimeUnixMs;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getLicensePlateId() {
        return licensePlateId;
    }

    public void setLicensePlateId(String licensePlateId) {
        this.licensePlateId = licensePlateId;
    }

    public long getStartTimeUnixMs() {
        return startTimeUnixMs;
    }

    public void setStartTimeUnixMs(long startTimeUnixMs) {
        this.startTimeUnixMs = startTimeUnixMs;
    }

    public long getEndTimeUnixMs() {
        return endTimeUnixMs;
    }

    public void setEndTimeUnixMs(long endTimeUnixMs) {
        this.endTimeUnixMs = endTimeUnixMs;
    }
}
