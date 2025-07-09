package com.example.carparking.util;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }
    
    public static String formatDateTime(Date date) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        return dateTimeFormat.format(date);
    }
}
