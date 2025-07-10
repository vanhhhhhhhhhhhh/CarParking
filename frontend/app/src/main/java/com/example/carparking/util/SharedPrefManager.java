package com.example.carparking.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "car_parking_prefs";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_ROLE = "user_role";

    private static SharedPrefManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void clear() {
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_FULL_NAME);
        editor.remove(KEY_ROLE);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void saveFullName(String name) {
        editor.putString(KEY_FULL_NAME, name);
        editor.apply();
    }

    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, "");
    }

    public void saveRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "");
    }
}
