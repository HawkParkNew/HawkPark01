package com.example.android.hawkpark01.models;

/**
 * Created by priya on 4/23/2017.
 */

public class UserDB {
    private String uID, name, email, photoURL;

    public UserDB() {
    }
    public UserDB(String uID, String name, String email) {
        this.uID = uID;
        this.name = name;
        this.email = email;
    }

    public UserDB(String uID, String name, String email, String photoURL) {
        this.uID = uID;
        this.name = name;
        this.email = email;
        this.photoURL = photoURL;
    }
    public String getuID() {
        return uID;
    }
    public void setuID(String uID) {
        this.uID = uID;
    }
}
