package com.example.android.hawkpark01.models;

import com.example.android.hawkpark01.utils.Utils;

/**
 * Created by priya on 4/27/2017.
 */

public class NeedParkingDB {

    String userId, arriveTime, lotPref1,lotPref2, lotPref3, numSeats, requestTime;

    public NeedParkingDB() {

    }

    //time in the format mmddyyyy hhmmss
    public NeedParkingDB(String userId, String arriveTime,
                         String lotPref1, String lotPref2, String lotPref3,
                         String numSeats) {
        this.userId = userId;
        this.arriveTime = arriveTime;
        this.lotPref1 = lotPref1;
        this.lotPref2 = lotPref2;
        this.lotPref3 = lotPref3;
        this.numSeats = numSeats;
        requestTime = Utils.getCurrentDateTime();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getLotPref1() {
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

    public String getLotPref3() {
        return lotPref3;
    }

    public void setLotPref3(String lotPref3) {
        this.lotPref3 = lotPref3;
    }

    public String getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(String numSeats) {
        this.numSeats = numSeats;
    }
}