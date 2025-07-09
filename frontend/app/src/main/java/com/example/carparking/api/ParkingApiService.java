package com.example.carparking.api;

import com.example.carparking.model.Parking;
import com.example.carparking.model.ResponseWrapper;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ParkingApiService {
    @Multipart
    @POST("parking/create")
    Call<ResponseBody> createParking(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("address") RequestBody address,
            @Part("coordinates") RequestBody coordinates,
            @Part("totalSlots") RequestBody totalSlots,
            @Part("availableSlots") RequestBody availableSlots,
            @Part("pricePerHour") RequestBody pricePerHour,
            @Part("openTime") RequestBody openTime,
            @Part("closeTime") RequestBody closeTime
    );

    @GET("parking/list")
    Call<ResponseWrapper<List<Parking>>> getParkingList(
            @Query("name") String name,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("distance") Double distance
    );
}
