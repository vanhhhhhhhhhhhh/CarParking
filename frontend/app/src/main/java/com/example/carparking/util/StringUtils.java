package com.example.carparking.util;

import java.util.Locale;

public class StringUtils {
    public static String moneyFormat(int amount) {
        return String.format(Locale.getDefault(), "%,d", amount);
    }
}
