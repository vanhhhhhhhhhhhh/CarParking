package com.example.carparking.api;

import com.example.carparking.model.BookingListing;
import com.example.carparking.model.ResponseWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookingApiService {
    @GET("booking/list")
    Call<ResponseWrapper<List<BookingListing>>> getMyBookings(
            @Query("startDate") long startTimeUnixMs,
            @Query("endDate") long endTimeUnixMs
    );
}
