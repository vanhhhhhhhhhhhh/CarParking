package com.example.carparking.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
}
