package com.example.android.hawkpark01.models;

/**
 * Created by priya on 4/23/2017.
 */

public class UserDB {
    private String uID, name, email, r2p, photoURL;

    public UserDB() {
    }
    public UserDB(String uID, String name, String email, String r2p) {
        this.uID = uID;
        this.name = name;
        this.email = email;
        this.r2p = r2p;
    }
    public UserDB(String uID, String name, String email, String r2p, String photoURL) {
        this.uID = uID;
        this.name = name;
        this.email = email;
        this.r2p = r2p;
        this.photoURL = photoURL;
    }
    public String getuID() {
        return uID;
    }
    public void setuID(String uID) {
        this.uID = uID;
    }
}
