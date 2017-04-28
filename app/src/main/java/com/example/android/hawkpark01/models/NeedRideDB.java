package com.example.android.hawkpark01.models;

import com.example.android.hawkpark01.utils.Utils;

/**
 * Created by priya on 4/27/2017.
 */

public class NeedRideDB {

    private String userId,leaveTime,parkedLot,numRiders,pickUp, requestTime;

    public NeedRideDB(){

    }
    public NeedRideDB(String userId, String leaveTime,String parkedLot, String numRiders, String pickUp){
        this.userId = userId;
        this.leaveTime = leaveTime;
        this.parkedLot = parkedLot;
        this.numRiders = numRiders;
        this.pickUp = pickUp;
        requestTime = Utils.getCurrentDateTime();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getParkedLot() {
        return parkedLot;
    }

    public void setParkedLot(String parkedLot) {
        this.parkedLot = parkedLot;
    }

    public String getNumRiders() {
        return numRiders;
    }

    public void setNumRiders(String numRiders) {
        this.numRiders = numRiders;
    }

    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp(String pickUp) {
        this.pickUp = pickUp;
    }

    public String getCurrentTime() {
        return requestTime;
    }

    public void setCurrentTime(String currentTime) {
        this.requestTime = currentTime;
    }
}
