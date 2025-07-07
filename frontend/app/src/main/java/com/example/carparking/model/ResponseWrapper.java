package com.example.carparking.model;

public class ResponseWrapper<T> {
    public boolean success;
    public String message;
    public T data;
}
