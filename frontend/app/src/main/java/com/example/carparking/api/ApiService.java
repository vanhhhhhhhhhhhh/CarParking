package com.example.carparking.api;

import com.example.carparking.model.User;
import com.example.carparking.model.ResponseWrapper;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("register")
    Call<ResponseWrapper<Void>> register(@Body User user);

    @POST("login")
    Call<ResponseWrapper<User>> login(@Body User user);
}

