package com.example.android.hawkpark01;

/**
 * Created by priya on 4/22/2017.
 */

public class R2PItem {
    private String email, licence, lPlates, make, model;

    public R2PItem(){
    }
    public R2PItem(String email, String licence, String lPlates, String make, String model){
        this.email = email;
        this.licence = licence;
        this.lPlates = lPlates;
        this.make = make;
        this.model = model;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
