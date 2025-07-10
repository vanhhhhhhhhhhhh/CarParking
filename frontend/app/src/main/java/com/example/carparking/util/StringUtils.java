package com.example.carparking.util;

import java.util.Locale;

public class StringUtils {
    public static String moneyFormat(int amount) {
        return String.format(Locale.getDefault(), "%,d", amount);
    }

    public static String formatDistance(double distance) {
        if (distance < 1000) {
            return String.format(Locale.getDefault(), "%.1f m", distance);
        } else {
            return String.format(Locale.getDefault(), "%.1f km", distance / 1000);
        }
    }
}
