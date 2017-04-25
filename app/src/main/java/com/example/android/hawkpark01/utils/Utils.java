package com.example.android.hawkpark01.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by priya on 4/22/2017.
 */

public class Utils {
    //key for home Activity/ list view/ selected lot's name
    public static final String LOT_KEY = "lot_name";
    public static final String ID_KEY = "firebase_id";
    public static final String EMAIL_KEY = "email_id";
    public static final String FB_KEY = "feedback_id";

    //Gets current time in HH(hours 0-23):mm:ss format
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());

    }
}
