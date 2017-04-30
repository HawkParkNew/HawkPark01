package com.example.android.hawkpark01.models;

import com.example.android.hawkpark01.Connect;

/**
 * Created by priya on 4/30/2017.
 */

public class ConnectionsDB {
    String riderId, parkerId, meetSpot, destination, status;
    long meetTime;

    public ConnectionsDB(){}
    //status = "1" for active
    //status = "0" for timed out
    //status = "2" for cancelled
    public ConnectionsDB(String riderId, String parkerId,
                         String meetSpot, String destination,
                         long meetTime){
        this.riderId = riderId;
        this.parkerId = parkerId;
        this.meetSpot = meetSpot;
        this.destination = destination;
        this.meetTime = meetTime;
        status = "1";//Default "1" for active- when item is created
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getParkerId() {
        return parkerId;
    }

    public void setParkerId(String parkerId) {
        this.parkerId = parkerId;
    }

    public String getPickupLocation() {
        return meetSpot;
    }

    public void setPickupLocation(String pickupLocation) {
        this.meetSpot = pickupLocation;
    }

    public String getParkingLot() {
        return destination;
    }

    public void setParkingLot(String parkingLot) {
        this.destination = parkingLot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getMeetingTime() {
        return meetTime;
    }

    public void setMeetingTime(long meetingTime) {
        this.meetTime = meetingTime;
    }
}
