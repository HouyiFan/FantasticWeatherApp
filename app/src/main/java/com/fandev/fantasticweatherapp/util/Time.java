package com.fandev.fantasticweatherapp.util;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Time {

    private static final String TAG = "Time";

    public String parseDate(String time, String inputPattern, String outputPattern) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String getDateFromUTC(long timestamp, String desiredFormat) {
        String date = null;
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone timeZone = calendar.getTimeZone();
            Log.d("Time zone: ", timeZone.getDisplayName());
            String relTime = DateUtils.getRelativeTimeSpanString(
                    timestamp * 1000,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS).toString();
//
//            int timeDiff = Integer.parseInt(relTime.substring(0, relTime.indexOf(" ")));
            Log.d("readTime: ", relTime.toString());
//            int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
//            long now = System.currentTimeMillis() + offset;
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTimeInMillis(timestamp * 1000L);
            date = DateFormat.format(desiredFormat, cal.getTimeInMillis()).toString();

            SimpleDateFormat formatter = new SimpleDateFormat(desiredFormat, Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(date);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(desiredFormat, Locale.getDefault());
            dateFormatter.setTimeZone(cal.getTimeZone());
            date = dateFormatter.format(value);

            Log.d(TAG, "getDateFromUTC: " + date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
