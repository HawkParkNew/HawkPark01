package com.example.android.hawkpark01.models;

import com.example.android.hawkpark01.utils.Utils;

/**
 * Created by priya on 4/21/2017.
 */

public class HomeLotDB {

    private String name;
    private String status;
    private String time;

    //empty constructor
    public HomeLotDB(){
    }
    //constructor
    public HomeLotDB(String name, String status){
        this.name = name;
        this.status = status;
        time = Utils.getCurrentTime();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime(){return time;}

    public void setTime(String time){this.time = time;}
}
