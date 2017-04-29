package com.example.android.hawkpark01.models;

import com.example.android.hawkpark01.utils.Utils;

/**
 * Created by priya on 4/27/2017.
 */

public class NeedRideDB {

    private String userId, name, leaveTime,parkedLot,numRiders,pickUp, requestTime;

    public NeedRideDB(){

    }
    public NeedRideDB(String userId, String name, String leaveTime,String parkedLot, String pickUp, String numRiders){
        this.userId = userId;
        this.name = name;
        this.leaveTime = leaveTime;
        this.parkedLot = parkedLot;
        this.numRiders = numRiders;
        this.pickUp = pickUp;
        requestTime = Utils.getCurrentDateTime();
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

    String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    String getParkedLot() {
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
