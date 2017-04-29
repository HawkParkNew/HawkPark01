package com.example.android.hawkpark01.models;

import com.example.android.hawkpark01.utils.Utils;

/**
 * Created by priya on 4/27/2017.
 */

public class NeedParkingDB {

    private String userId;
    private String arriveTime;
    private String lotPref1;
    private String lotPref2;
    private String numSeats;
    private String name;

    public NeedParkingDB() {
    }

    //time in the format mmddyyyy hhmmss
    public NeedParkingDB(String userId, String name, String arriveTime,
                         String lotPref1, String lotPref2,
                         String numSeats) {
        this.userId = userId;
        this.name = name;
        this.arriveTime = arriveTime;
        this.lotPref1 = lotPref1;
        this.lotPref2 = lotPref2;
        this.numSeats = numSeats;
        String requestTime = Utils.getCurrentDateTime();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    String getLotPref1() {
        return lotPref1;
    }

    public void setLotPref1(String lotPref1) {
        this.lotPref1 = lotPref1;
    }

    public String getLotPref2() {
        return lotPref2;
    }

    public void setLotPref2(String lotPref2) {
        this.lotPref2 = lotPref2;
    }

    public String getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(String numSeats) {
        this.numSeats = numSeats;
    }
}