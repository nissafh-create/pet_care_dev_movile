package com.example.petcareproject.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static long calculateDays(String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = sdf.parse(startDateStr);
            Date end = sdf.parse(endDateStr);
            if (start != null && end != null) {
                long diff = end.getTime() - start.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                return Math.max(days, 1);
            }
        } catch (ParseException ignored) {
        }
        return 1;
    }
}
