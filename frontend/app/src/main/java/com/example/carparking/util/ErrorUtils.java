package com.example.carparking.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

public class ErrorUtils {
    public static String getErrorFromApi(ResponseBody messageJson) {
        String defaultErrorMessage = "An unknown error occurred.";
        if (messageJson == null) {
            return defaultErrorMessage;
        }

        String errorMessage;
        try {
            errorMessage = messageJson.string();
        } catch (IOException e) {
            Log.e("ErrorUtils", "Failed to read error message", e);
            return defaultErrorMessage;
        }

        try {
            JSONObject jsonObject = new JSONObject(errorMessage);
            return jsonObject.optString("message", defaultErrorMessage);
        } catch (JSONException e) {
            Log.e("ErrorUtils", "Failed to parse error message JSON", e);
            return defaultErrorMessage;
        }
    }
}
