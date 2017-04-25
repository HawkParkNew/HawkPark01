package com.example.android.hawkpark01;


import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.android.hawkpark01.models.HomeLotAdapter;
import com.example.android.hawkpark01.models.HomeLotDB;
import com.example.android.hawkpark01.utils.GeofenceConstants;
import com.example.android.hawkpark01.utils.GeofenceErrorMessages;
import com.example.android.hawkpark01.utils.GeofenceTransitionsIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.android.hawkpark01.utils.Utils.EMAIL_KEY;
import static com.example.android.hawkpark01.utils.Utils.ID_KEY;
import static com.example.android.hawkpark01.utils.Utils.LOT_KEY;
import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {

    //Parking availability in lots==================================================================
    private ListView lv_lot_list;
    private Button btn_r2p,btn_settings;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mlotSummaryDBRef;
    private ChildEventListener mChildEventListener;
    private HomeLotAdapter mhomeLotAdapter;

    /*---------------------------------------------
        |   LOCATION / GEOFENCE VARIABLES
     *-------------------------------------------*/
    private static final String LOG_TAG = "Location/Geofence";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private int permissionCheck;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    protected ArrayList<Geofence> mGeofenceList;
    private String lat;
    private String lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lv_lot_list = (ListView)findViewById(R.id.lv_lot_btn_ha);
        final String userId = getIntent().getStringExtra(ID_KEY);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mlotSummaryDBRef = mFirebaseDatabase.getReference("lot-summary");

        // Initialize lotSummary ListView and its adapter
        final List<HomeLotDB> homeLotItemsList = new ArrayList<>();
        mhomeLotAdapter = new HomeLotAdapter(HomeActivity.this, R.layout.home_lot_item, homeLotItemsList);
        lv_lot_list.setAdapter(mhomeLotAdapter);
        // Set item click listener for the lot view
        lv_lot_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Access the row position here to get the correct data item
                HomeLotDB selectedLot = homeLotItemsList.get(i);
                String name = selectedLot.getName();
                Intent intent = new Intent(HomeActivity.this,LotActivity.class);
                intent.putExtra(ID_KEY,userId);
                intent.putExtra(LOT_KEY,name);
                startActivity(intent);
            }
        });
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //add to lot list
                HomeLotDB homeLotDB = dataSnapshot.getValue(HomeLotDB.class);
                mhomeLotAdapter.add(homeLotDB);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //get new status and change color, position etc

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //remove from listview
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //do nothing
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //error message
            }
        };
        mlotSummaryDBRef.addChildEventListener(mChildEventListener);

        /*---------------------------------------------------------
            |   PERMISSION CHECKER
         *-------------------------------------------------------*/
        if (PermissionChecker.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        mGeofenceList = new ArrayList<Geofence>();
        populateGeofenceList();
        buildGoogleApiClient();
        createLocationRequest();
    }

    //Radio buttons in home activity
    //Sort lots based on user preference
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_btn_distance_ha:
                if (checked)
                    // Sort ListView by distance
                    break;
            case R.id.radio_btn_availability_ha:
                if (checked)
                    // Sort ListView by availability
                    break;
            case R.id.radio_btn_favorites_ha:
                if (checked)
                    // Sort ListView by favorites
                    break;
        }
    }

    public void onButtonClicked_ha(View view) {
        String userId = getIntent().getStringExtra(ID_KEY);
        int id = view.getId();
        switch (id){
            case R.id.btn_r2p://directs user to ride2park screen
                //change this
                Intent intent = new Intent(HomeActivity.this,R2PRegistrationActivity.class);
                intent.putExtra(ID_KEY, userId);
                startActivity(intent);
                break;
            case R.id.btn_settings://directs user to settings screen
                //change this
                Intent i = new Intent(HomeActivity.this,SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.btn_car_location:
                SharedPreferences sharedPref = getSharedPreferences("car_location", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("last_lat",lat);
                editor.putString("last_lng",lng);
                editor.commit();

                Intent intentCar = new Intent(HomeActivity.this, CarLocation.class);
                startActivity(intentCar);
                break;
        }

    }

    /*---------------------------------------------------------------------*
        |   RUN TIME PERMISSION REQUEST / CHECK
        |   CHECK IF THE PHONE HAS GPS SERVICES ENABLE
        |       IF NOT, PROMPT USER TO ENABLE IT
        |   ADD LOCATION SERVICES
        |   ADD GEOFENCES
        *------------------------------------------------------------------*/

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    onStart();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStart() {
        // CONNECT THE CLIENT
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000); //UPDATE LOCATION EVERY SECOND (TIME IN MS)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    HomeActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        permissionCheck = PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            addGeofences();

            /*if (mLastLocation != null) {
                latOutput.setText(String.valueOf(mLastLocation.getLatitude()));
                longOutput.setText(String.valueOf(mLastLocation.getLongitude()));
            }*/
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = (String.valueOf(location.getLatitude()));
        lng = (String.valueOf(location.getLongitude()));
    }

    @Override
    protected void onStop() {
        // DISCONNECT THE CLIENT
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "GoogleApiClient connection has failed");
    }

    public void addGeofences(){
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        }catch (SecurityException securityException){
            // LOG EXCEPTION
        }
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            /*Toast.makeText(
                    this,
                    "Geofences added",
                    Toast.LENGTH_SHORT
            ).show();*/
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this, status.getStatusCode());
            Log.e(LOG_TAG, errorMessage);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : GeofenceConstants.MSU_PARKING.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            GeofenceConstants.LOT_RADIUS.get(entry.getKey()))
                    .setExpirationDuration(NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    /*---------------------------------------------------------------------*
        |   onClick TO SET CAR LOCATION
        |   WRITE TO SHARED PREFERENCES
     *---------------------------------------------------------------------*/

    public void setCarLocation(View view) {

        SharedPreferences sharedPref = getSharedPreferences("car_location", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.car_lat_position), lat);
        editor.putString(getString(R.string.car_lng_position), lng);
        editor.putString("last_lat",String.valueOf(mLastLocation.getLatitude()));
        editor.putString("last_lng",String.valueOf(mLastLocation.getLongitude()));
        editor.commit();
        Toast.makeText(this, getString(R.string.car_location_saved), Toast.LENGTH_SHORT).show();
    }
}
