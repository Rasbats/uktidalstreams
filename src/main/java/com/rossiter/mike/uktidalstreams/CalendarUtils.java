package com.rossiter.mike.uktidalstreams;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mike on 30/03/18.
 */
public class CalendarUtils {

    public static String dateFormat = "E dd/MM/yyyy HH:mm";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public static String ConvertMilliSecondsToFormattedDate(String milliSeconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return simpleDateFormat.format(calendar.getTime());
    }
}