package com.example.carparking.api;

import com.example.carparking.model.LicensePlate;
import com.example.carparking.model.LicensePlateBody;
import com.example.carparking.model.ResponseWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LicensePlateApiService {
    @GET("license-plate/list")
    public Call<ResponseWrapper<List<LicensePlate>>> getLicensePlates();

    @POST("license-plate/create")
    public Call<ResponseWrapper<LicensePlate>> createLicensePlate(@Body LicensePlateBody licensePlate);
}
