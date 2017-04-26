package com.example.android.hawkpark01;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by priya on 4/25/2017.
 */

public class SessionManager {

    private static SharedPreferences userSharedPref,locationPref,lotPref;
    private SharedPreferences.Editor editorUser, editorLocation, editorLot;
    private Context _context;
    private static int count = 0;
    private static final String PREF_USER =
            "com.example.android.hawkpark02.USER_SP";
    private static final String PREF_CAR_LOCATION =
            "com.example.android.hawkpark02.LOCATION_SP";
    private static final String PREF_LOT =
            "com.example.android.hawkpark02.LOT_SP";
    private static final String IS_LOGIN = "isLoggedIn";
    public static final String KEY_NAME = "userName";
    public static final String KEY_R2P = "hasR2P";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHOTO = "email";
    static final String KEY_USERID = "userId";
    private static final String KEY_SETTINGS = "setting_name";
    private static final String KEY_CAR_LOCATION = "location_name";
    private static final String KEY_LOT = "lot_name";


    SessionManager(Context context){
        this._context = context;
        userSharedPref = _context.getSharedPreferences(PREF_USER, MODE_PRIVATE);
        locationPref = _context.getSharedPreferences(PREF_CAR_LOCATION,MODE_APPEND);
        lotPref = _context.getSharedPreferences(PREF_LOT,MODE_APPEND);


        editorUser = userSharedPref.edit();
        editorLocation = locationPref.edit();
        editorLot = lotPref.edit();
    }
    /**
     * Create userSharedPref session
     */
    void createUserSPSession(String firebaseID, String displayName,String photoUrl, String email){

        editorUser.putBoolean(IS_LOGIN, true);

        editorUser.putString(KEY_USERID,firebaseID);
        editorUser.putString(KEY_NAME,displayName);
        //editorUser.putString(KEY_R2P,R2P);
        editorUser.putString(KEY_PHOTO,photoUrl);
        editorUser.putString(KEY_EMAIL,email);

        editorUser.apply();
    }
    /**
     * Get stored shared pref data
     **/
    HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        // user name
        user.put(KEY_NAME, userSharedPref.getString(KEY_NAME, null));
        // user id
        user.put(KEY_USERID, userSharedPref.getString(KEY_USERID, null));
        // r2p
        user.put(KEY_R2P, userSharedPref.getString(KEY_R2P, null));
        // email
        user.put(KEY_EMAIL, userSharedPref.getString(KEY_EMAIL, null));
        // photoUrl
        user.put(KEY_PHOTO, userSharedPref.getString(KEY_PHOTO, null));
        // return user
        return user;
    }
    /**
     * Clear shared pref details
     **/
    void logoutUser() {
        // Clearing all data from Shared Preferences
        editorUser.clear();
        editorUser.commit();
    }
    /**
     * Quick check for login
     **/
    // Get Login State
    boolean isLoggedIn(){
        return userSharedPref.getBoolean(IS_LOGIN, false);
    }
}