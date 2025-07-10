package com.example.carparking.model;

public class ResponseWrapper<T> {
    public boolean success;
    public String message;
    public String accessToken;

    public T data;
    public boolean isSuccess() {
        return success;
    }
}