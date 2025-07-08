package com.example.carparking.api;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {
    public static Retrofit getClient(String token) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if (token != null && !token.isEmpty()) {
            clientBuilder.addInterceptor(chain -> {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            });
        }

        return new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9999/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();
    }
}
