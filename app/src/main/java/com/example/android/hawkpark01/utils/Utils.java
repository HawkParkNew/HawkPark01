package com.example.android.hawkpark01.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.R.attr.format;
import static com.example.android.hawkpark01.SessionManager.KEY_NAME;

/**
 * Created by priya on 4/22/2017.
 */

public class Utils {

    //key for home Activity/ list view/ selected lot's name=========================================
    public static final String LOT_KEY = "lot_name";
    public static final String ID_KEY = "firebase_id";
    public static final String EMAIL_KEY = "email_id";
    public static final String FB_KEY = "feedback_id";
    public static final String CONNECT_KEY = "connect_firebase_id";
    public static final String CONNECT_NAME_KEY = "connect_name";
    public static final String DB_KEY = "db_name";
    //r2p functionality=============================================================================





    /**
     * Gets current time in HH(hours 0-23):mm:ss format=============================================
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, K:mm a", Locale.US);
        return sdf.format(Calendar.getInstance().getTime());
    }
    /**
     * Gets current date and time in the format "d MMM yy HH:mm:ss"=================================
     * Hours(0-23), dd(day in the month), MM(months (1-12)),MMM- month names apr,jun
     * Option: "d MMM yyyy HH:mm:ss SSS" time in millis
     * Option: "d MMM yyyy K:mm a" time am/pm
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yy HH:mm", Locale.US);
        return sdf.format(Calendar.getInstance().getTime());
    }
    /**
     * Sets time in "d MMM, HH:mm" format===========================================================
     * i.e. 4 Apr, 13:20
     * Generates the date-time displayed in NP and NR listviews, from user's time picker selection
     */
    public static String setReqTime(String HHmm){
        String format = "d MMM, "+HHmm;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.US);
        if(HHmm.substring(0,1).equals("00")){//check if date need to be changed
            if(!(getCurrentDateTime().substring(9,10).equals("00"))){
                c.add(Calendar.DATE, 1);
            }
        }
        return sdf.format(c.getTime());
    }
    /**
     * Gets time in millisecs=======================================================================
     * Used to sort NP and NR requests in db
     * Takes input in the form "dd-MM-yyyy HH:mm:ss"
     */
    public static long getTimeinMillis(String reqDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.US);
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(reqDate);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    /**
     * Creates a date string("dd-MM-yyyy HH:mm:ss") from the time picker input======================
     * Takes input in the form HH:mm
     */
    public static String createReqDateString(String HHmm){
        String format = "dd-MM-yyyy "+HHmm+":0";
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.US);
        Calendar c = Calendar.getInstance();
        if(HHmm.substring(0,1).equals("00")){//check if date need to be changed
            if(!(getCurrentDateTime().substring(9,10).equals("00"))){
                c.add(Calendar.DATE, 1);
            }
        }
        return sdf.format(c.getTime());
    }

}
