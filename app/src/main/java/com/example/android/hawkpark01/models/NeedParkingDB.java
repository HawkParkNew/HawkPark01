package com.example.android.hawkpark01.models;

import com.example.android.hawkpark01.utils.Utils;

/**
 * Created by priya on 4/27/2017.
 */

public class NeedParkingDB {

    private String userId;
    private String arriveTime;
    private long reqTimeMillis;
    private String lotPref1;
    private String lotPref2;
    private String numSeats;
    private String name;
    private String status;

    public NeedParkingDB() {
    }

    //status = "1" for active
    //status = "0" for timed out
    //status = "2" for cancelled
    //status = "3" for connected
    public NeedParkingDB(String userId, String name,
                         String arriveTime, long reqTimeMillis,
                         String lotPref1, String lotPref2,
                         String numSeats) {
        this.userId = userId;
        this.name = name;
        this.arriveTime = arriveTime;
        this.reqTimeMillis = reqTimeMillis;
        this.lotPref1 = lotPref1;
        this.lotPref2 = lotPref2;
        this.numSeats = numSeats;
        status = "1"; //Default is active
        String requestTime = Utils.getCurrentDateTime();
    }

    public long getReqTimeMillis() {
        return reqTimeMillis;
    }

    public void setReqTimeMillis(long reqTimeMillis) {
        this.reqTimeMillis = reqTimeMillis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(String numSeats) {
        this.numSeats = numSeats;
    }
}