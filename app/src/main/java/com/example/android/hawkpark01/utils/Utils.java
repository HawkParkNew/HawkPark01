package com.example.android.hawkpark01.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

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

    //r2p functionality=============================================================================
    private String[] lotNames = {"Car Parc Diem", "Lot 24", "Lot 60"};
    private String[] pickupLocation = {"Rec. Center", "Opposite Carparc"};




    /**
     * Gets current time in HH(hours 0-23):mm:ss format=============================================
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, K:mm a", Locale.US);
        return sdf.format(Calendar.getInstance().getTime());
    }
    /**
     * Gets current date and time in the format dd:MM:yyyy HH:mm:ss=================================
     * Hours(0-23), dd(day in the month), MM(months (1-12))
     * Option: "d MMM yyyy HH:mm:ss SSS" time in millis
     * Option: "d MMM yyyy K:mm a" time am/pm
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        return sdf.format(Calendar.getInstance().getTime());
    }

    public String[] getPickupLocation(){
        return pickupLocation;
    }
    public String[] getLotNames(){
        return lotNames;
    }
}
