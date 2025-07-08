package com.example.carparking.api;

import com.example.carparking.model.User;
import com.example.carparking.model.ResponseWrapper;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/register")
    Call<ResponseWrapper<Void>> register(@Body User user);

    @POST("auth/login")
    Call<ResponseWrapper<User>> login(@Body User user);
}
