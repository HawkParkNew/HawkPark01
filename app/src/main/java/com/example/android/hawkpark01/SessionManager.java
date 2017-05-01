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

    private static SharedPreferences userSharedPref,connectionPref,lotPref;
    private SharedPreferences.Editor editorUser, editorConnect, editorLot;
    private Context _context;
    private static int count = 0;
    private static final String PREF_USER =
            "com.example.android.hawkpark02.USER_SP";
    private static final String PREF_CAR_LOCATION =
            "com.example.android.hawkpark02.LOCATION_SP";
    private static final String PREF_LOT =
            "com.example.android.hawkpark02.LOT_SP";
    private static final String IS_LOGIN = "isLoggedIn";
    private static final String IS_CONNECTED = "isConnected";
    public static final String KEY_NAME = "userName";
    private static final String KEY_R2P = "hasR2P";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHOTO = "photo";
    static final String KEY_USERID = "userId";
    private static final String KEY_SETTINGS = "setting_name";
    private static final String KEY_CONNECTION = "connect";
    private static final String KEY_LOT = "lot_name";
    static final String KEY_RIDER = "rider_id";
    static final String KEY_PARKER = "parker_id";
    static final String KEY_TIME = "meet_time";
    static final String KEY_MEET = "meet_spot_name";
    static final String KEY_DEST = "destination";

    SessionManager(Context context){
        this._context = context;
        userSharedPref = _context.getSharedPreferences(PREF_USER, MODE_PRIVATE);
        connectionPref = _context.getSharedPreferences(PREF_CAR_LOCATION,MODE_APPEND);
        lotPref = _context.getSharedPreferences(PREF_LOT,MODE_APPEND);


        editorUser = userSharedPref.edit();
        editorConnect = connectionPref.edit();
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
    /**
     * Create HawkPark connectSharedPref session
     */
    void createConnectedSPSession(String riderId, String parkerId,String connectName,String meetSpot, String destination, String meetTime){

        editorConnect.putBoolean(IS_CONNECTED, true);

        editorConnect.putString(KEY_RIDER,riderId);
        editorConnect.putString(KEY_PARKER,parkerId);
        editorConnect.putString(KEY_NAME,connectName);//name of the person user is connecting to
        editorConnect.putString(KEY_TIME,meetTime);
        editorConnect.putString(KEY_MEET,meetSpot);
        editorConnect.putString(KEY_DEST,destination);

        editorConnect.apply();
    }
    /**
     * Get stored HawkParkconnect shared pref data
     **/

    HashMap<String, String> getConnectDetails(){
        HashMap<String, String> connect = new HashMap<>();
        // riderid
        connect.put(KEY_RIDER, connectionPref.getString(KEY_RIDER, null));
        // PARKER id
        connect.put(KEY_PARKER, connectionPref.getString(KEY_PARKER, null));
        // connectName- name of person the user is making a connection to
        connect.put(KEY_NAME, connectionPref.getString(KEY_NAME, null));
        // meet spot
        connect.put(KEY_MEET, connectionPref.getString(KEY_MEET, null));
        // destination
        connect.put(KEY_DEST, connectionPref.getString(KEY_DEST, null));
        // time
        connect.put(KEY_TIME, connectionPref.getString(KEY_TIME, null));
        // return connect
        return connect;
    }
    /**
     * Quick check for HawkParkConnect
     **/
    // Get Connect State
    boolean isConnected(){
        return connectionPref.getBoolean(IS_CONNECTED, false);
    }

    /**
     * Clear shared pref details of HawkPark connect
     **/
    void logoutConnection() {
        // Clearing all data from Shared Preferences
        editorConnect.clear();
        editorConnect.commit();
    }
}