package com.example.android.hawkpark01.models;

/**
 * Created by neeks on 4/24/2017.
 */

public class FeedBackDB {
    private String uId, lotname, feedback, time;

    public FeedBackDB(){
    }
    public FeedBackDB(String uId, String lotname, String feedback, String time){
        this.uId = uId;
        this.lotname = lotname;
        this.feedback = feedback;
        this.time = time;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getLotname() {
        return lotname;
    }

    public void setLotname(String lotname) {
        this.lotname = lotname;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
