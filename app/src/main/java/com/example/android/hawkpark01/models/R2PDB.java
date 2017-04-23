package com.example.android.hawkpark01.models;

/**
 * Created by priya on 4/22/2017.
 */

public class R2PDB {
    private String uId, licence, lPlates, make, model;

    public R2PDB(){
    }
    public R2PDB(String uId, String licence, String lPlates, String make, String model){
        this.uId = uId;
        this.licence = licence;
        this.lPlates = lPlates;
        this.make = make;
        this.model = model;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getlPlates() {
        return lPlates;
    }

    public void setlPlates(String lPlates) {
        this.lPlates = lPlates;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
