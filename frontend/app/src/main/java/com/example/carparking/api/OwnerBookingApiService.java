package com.example.carparking.api;

import com.example.carparking.model.BookingListing;
import com.example.carparking.model.ResponseWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OwnerBookingApiService {

    @GET("booking/listByOwner")
    Call<ResponseWrapper<List<BookingListing>>> getAllBookingRequests();

    @PUT("booking/confirm/{id}")
    Call<ResponseWrapper<Object>> confirmBooking(@Path("id") String bookingId);

    @PUT("booking/cancel/{id}")
    Call<ResponseWrapper<Object>> cancelBooking(@Path("id") String bookingId);
}
