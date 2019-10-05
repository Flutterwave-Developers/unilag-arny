package com.yorubadev.arny.utilities;

import android.content.Context;

import java.util.Calendar;

public class DateTimeUtils {

    /**
     * Returns a date string in the format specified, which shows an abbreviated date without a
     * year.
     *
     * @param context      Used by DateTimeUtils to format the date in the current locale
     * @param timeInMillis Time in milliseconds since the epoch (local time)
     * @return The formatted date string
     */
    public static String getReadableDateString(Context context, long timeInMillis) {
        int flags = android.text.format.DateUtils.FORMAT_SHOW_DATE
                | android.text.format.DateUtils.FORMAT_SHOW_YEAR
                | android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY;

        return android.text.format.DateUtils.formatDateTime(context, timeInMillis, flags);
    }

    /**
     * This method is used to get the properly-formatted String for displaying the time an event
     * occurred/will occur in Talkspace. This is just the hour and minute it's happening/happened
     * delimited by a colon.
     *
     * @param milliseconds the Epoch time of the event
     * @return the formatted String representation of the event
     */
    public static String getCorrectDisplayTime(long milliseconds) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(milliseconds);  //here your time in miliseconds
        int hour = cl.get(Calendar.HOUR_OF_DAY), minute = cl.get(Calendar.MINUTE);
        String strHour, strMinute;
        if (hour < 10) strHour = "0" + hour;
        else strHour = String.valueOf(hour);
        if (minute < 10) strMinute = "0" + minute;
        else strMinute = String.valueOf(minute);
        return strHour + ":" + strMinute;
    }

    public static String getDigitalTime(long milliSeconds) {
        long seconds = milliSeconds / 1000;
        String secondsString = seconds == 60 ? "00" : seconds < 10 ? "0" + seconds : String.valueOf(seconds);

        long minutes = seconds / 60;
        String minutesString = minutes < 10 ? "0" + minutes : String.valueOf(minutes);

        long hours = minutes / 60;
        String hoursString = hours < 10 ? "0" + hours : String.valueOf(hours);

        String minuteTime = String.format("%s:%s", minutesString, secondsString);

        return hours > 0 ? String.format("%s:%s", hoursString, minuteTime) : minuteTime;
    }
}
