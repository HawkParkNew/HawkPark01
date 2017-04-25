package com.example.android.hawkpark01.utils;

import android.graphics.SweepGradient;
import android.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by priya on 4/24/2017.
 */

public class SpaceCalculator {

    //Lot status - based on assumptions- used in the absence of user feedback- customized by lot
    private String[] cpWeekday={"1","2","3","2","1","1"};
    private String[] cpHoliday={"1","1","1","2","2","2"};
    private String[] lot24Weekday={"1","2","3","1","1","1"};
    private String[] lot24Holiday={"1","1","1","2","1","1"};
    private String[] lot60Weekday={"1","2","2","1","1","1"};
    private String[] lot60Holiday={"1","1","1","1","1","1"};
    //Time intervals for each status
    private String[] time ={"00:00:00","07:30:00","08:30:00","13:00:00","17:00:00", "23:59:59" };
    private String[] currentCalendar;
    private String currentTimeString;
    private String status;

    public SpaceCalculator(String lotname) {
        currentCalendar = getCalendar(lotname);
        currentTimeString = getCurrentTime();
        status = getLotStatus(currentCalendar,currentTimeString);
    }

    public SpaceCalculator(String lotname, String currentStatus, String feedback){
        currentCalendar = getCalendar(lotname);
        currentTimeString = getCurrentTime();
        status = getLotStatus(currentCalendar,currentTimeString);

        //TODO-- use current status and feedback to compute a new value for the lot status

    }

    public String[] getCurrentCalendar() {
        return currentCalendar;
    }

    public void setCurrentCalendar(String[] currentCalendar) {
        this.currentCalendar = currentCalendar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Sets the availability calendar to be used based on lot name
    //returns the appropriate calendar to be used based on
    //- whether the day is a weekday or a weekend
    //- the lot name
    private String[] getCalendar(String lotname) {
        String[] currCal = new String[time.length];
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        //getTime() returns the current date in default time zone
        Date date = calendar.getTime();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (lotname){
            case "Carparc Diem":
                if(dayOfWeek==1||dayOfWeek==7)
                    currCal = cpWeekday;
                else
                    currCal = cpHoliday;
                break;
            case "Lot 24":
                if(dayOfWeek==1||dayOfWeek==7)
                    currCal = lot24Weekday;
                else
                    currCal = lot24Holiday;
                break;
            case "Lot 60":
                if(dayOfWeek==1||dayOfWeek==7)
                    currCal = lot60Weekday;
                else
                    currCal = lot60Holiday;
                break;
        }
        return currCal;
    }
    //Gets current time in HH(hours 0-23):mm:ss format
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());

    }
    //Gets the status of the lot specified based on the current time from the
    //assumptions calendar
    private String getLotStatus(String[] currentCalendar, String currentTime){

        int i = 0;
        int n = time.length;
        while((i<n) && (time[i].compareTo(currentTimeString)<0)){
            i++;
        }
        return currentCalendar[i];
    }
}