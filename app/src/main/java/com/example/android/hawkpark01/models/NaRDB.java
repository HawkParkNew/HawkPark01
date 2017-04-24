package com.example.android.hawkpark01.models;

/**
 * Created by catalinamorales on 4/23/17.
 */

    public class NaRDB {
    private String uID,leavingIn,parkingLot,pickupLocation;
    private Integer numOfPassengers;

    public NaRDB(){
    }

    public NaRDB(String uID,String leavingIn,String parkingLot, String pickupLocation, Integer numOfPassengers){
        this.uID = uID;
        this.leavingIn = leavingIn;
        this.parkingLot = parkingLot;
        this.pickupLocation = pickupLocation;
        this.numOfPassengers = numOfPassengers;

    }

    public String getuID(){return uID;}

    public void setuID(String uID) {this.uID=uID;}

    public String getLeavingIn(){return leavingIn;}

    public void setLeavingIn(String leavingIn) {this.leavingIn= leavingIn;}

    public String getParkingLot(){return parkingLot;}

    public void setParkingLot(String parkingLot){this.parkingLot = parkingLot;}

    public String getPickupLocation(){return pickupLocation;}

    public void setPickupLocation(String pickupLocation) {this.pickupLocation = pickupLocation;}

    public Integer getNumOfPassengers(){return numOfPassengers;}

    public void setNumOfPassengers(Integer numOfPassengers) {this.numOfPassengers = numOfPassengers;}











}
