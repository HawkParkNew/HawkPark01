package com.example.android.hawkpark01.utils;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Created by jeffs on 4/23/2017.
 */

public class GeofenceErrorMessages {

    private GeofenceErrorMessages(){}

    public static String getErrorString(Context context, int errorCode){
        Resources mResources = context.getResources();
        switch (errorCode){
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence service is not available now";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Your app has registered too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "You have provided too many pending intents to the addGeofence";
            default:
                return "Unknown error: the Geofence service is not available";
        }
    }
}
