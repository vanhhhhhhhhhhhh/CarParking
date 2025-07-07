package com.example.carparking.model;

public class BookingDemo {
    public enum Status {
        COMPLETED("Đã hoàn thành"),
        PENDING("Đang chờ xác nhận"),
        CONFIRMED("Đã xác nhận"),
        CANCELLED("Đã hủy");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private String id;
    private String address;
    private String time;
    private String price;
    private Status status;

    public BookingDemo() {
    }

    public BookingDemo(String id, String address, String time, String price, Status status) {
        this.id = id;
        this.address = address;
        this.time = time;
        this.price = price;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getTime() {
        return time;
    }

    public String getPrice() {
        return price;
    }

    public Status getStatus() {
        return status;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
} 