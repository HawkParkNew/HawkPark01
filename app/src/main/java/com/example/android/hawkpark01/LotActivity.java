package com.example.android.hawkpark01;

import android.*;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.hawkpark01.models.FeedBackDB;
import com.example.android.hawkpark01.models.HomeLotDB;
import com.example.android.hawkpark01.utils.GeofenceConstants;
import com.example.android.hawkpark01.utils.GeofenceErrorMessages;
import com.example.android.hawkpark01.utils.GeofenceTransitionsIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.android.hawkpark01.utils.Utils.ID_KEY;
import static com.example.android.hawkpark01.utils.Utils.LOT_KEY;
import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

public class LotActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {

    GoogleMap mgoogleMap;
    RadioButton empty;
    RadioButton some;
    RadioButton full;
    Button submit;
    TextView header;
    RadioGroup rg;
    String fback = "0" ;
    private String lotname, feedback , time;
    private FirebaseDatabase mdatabase;
    private DatabaseReference feedbackDatabaseReference,mlot24DBRef;
    private DatabaseReference mlotSummaryDBRef;
    private FeedBackDB feedBack;
    private HomeLotDB mhomeLotDB;
    SessionManager sessionManager;

    /*---------------------------------------------
        |   NAVIGATION DRAWER VARIABLES
     *-------------------------------------------*/
    DrawerLayout mDrawerLayout;
    ImageView drawerButton;
    NavigationView navigationView;

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
    private String lat;
    private String lng;
    protected ArrayList<Geofence> mGeofenceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());

        String currentLot = getIntent().getStringExtra(LOT_KEY);

        mdatabase = FirebaseDatabase.getInstance();
        feedbackDatabaseReference = mdatabase.getReference("feedbackDB");
        mlotSummaryDBRef = mdatabase.getReference("lot-summary");

        //checks to make sure googleServices is available
        if(googleServicesAvailable()) {
            setContentView(R.layout.activity_lot);
            initMap();
        }else {
            //no Google Maps Layout
        }
        //initializes resource files
        empty = (RadioButton) findViewById(R.id.radio_empty);
        some = (RadioButton) findViewById(R.id.radio_some);
        full = (RadioButton) findViewById(R.id.radio_full);
        submit = (Button) findViewById(R.id.btn_lot_submit);
        rg = (RadioGroup)findViewById(R.id.rg);
        header = (TextView)findViewById(R.id.tv_lot_header);

        header.setText(currentLot);
        //case switch for feedback radio group
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_empty:
                        fback = "1";
                        break;
                    case R.id.radio_some:
                        fback = "2";
                        break;
                    case R.id.radio_full:
                        fback = "3";
                        break;
                }
            }
        });

        /*---------------------------------------------------------
            | NAVIGATION DRAWER
         *-------------------------------------------------------*/
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerButton = (ImageView)findViewById(R.id.menu_btn);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_id:
                        Intent intentHome = new Intent(LotActivity.this, HomeActivity.class);
                        startActivity(intentHome);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.home_id);
                        break;
                    case R.id.profile_id:
                        Intent intentProfile = new Intent(LotActivity.this, SettingsActivity.class);
                        startActivity(intentProfile);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.profile_id);
                        break;
                    case R.id.wheresmycar_id:
                        SharedPreferences locationSharedPref = getSharedPreferences("car_location", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = locationSharedPref.edit();
                        editor.putString(getString(R.string.last_known_lat),lat);
                        editor.putString(getString(R.string.last_known_lng),lng);
                        editor.commit();

                        if (locationSharedPref.contains(getString(R.string.car_lat_position))) {
                            Intent intentCar = new Intent(LotActivity.this, CarLocation.class);
                            startActivity(intentCar);
                            mDrawerLayout.closeDrawers();
                            navigationView.setCheckedItem(R.id.wheresmycar_id);
                        }else{
                            Toast.makeText(LotActivity.this, getString(R.string.car_not_set), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.setcar_id:
                        SharedPreferences sharedPref = getSharedPreferences("car_location", Context.MODE_PRIVATE);

                        SharedPreferences.Editor edit = sharedPref.edit();
                        edit.putString(getString(R.string.car_lat_position), lat);
                        edit.putString(getString(R.string.car_lng_position), lng);
                        edit.commit();
                        Toast.makeText(LotActivity.this, getString(R.string.car_location_saved), Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.needpark_id:
                        Intent intentNeedPark = new Intent(LotActivity.this, NeedParking.class);
                        startActivity(intentNeedPark);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.needpark_id);
                        break;
                    case R.id.needride_id:
                        Intent intentNeedRide = new Intent(LotActivity.this, NeedRide.class);
                        startActivity(intentNeedRide);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.needride_id);
                        break;
                    case R.id.register_id:
                        Intent intentRegister = new Intent(LotActivity.this, R2PRegistrationActivity.class);
                        startActivity(intentRegister);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.register_id);
                        break;
                }
                return true;
            }
        });

        mGeofenceList = new ArrayList<Geofence>();
        populateGeofenceList();
        buildGoogleApiClient();
        createLocationRequest();
    }

    public void submitFeedback (View view){
        //checks to make sure a feedback option is selected
        String feedbackLot = getIntent().getStringExtra(LOT_KEY);
        Location currentLotLocation = new Location("currentLot");
        float radius=0;
        String cpd = "CarParc Diem";
        String lot24 = "Lot 24";
        String lot60 = "Lot 60";

        if (feedbackLot.equals(cpd)){
            currentLotLocation.setLatitude(GeofenceConstants.carparcDiem.latitude);
            currentLotLocation.setLatitude(GeofenceConstants.carparcDiem.longitude);
            radius = GeofenceConstants.carparcDiemRad;
        }
        if (feedbackLot.equals(lot24)){
            currentLotLocation.setLatitude(GeofenceConstants.lot24.latitude);
            currentLotLocation.setLatitude(GeofenceConstants.lot24.longitude);
            radius = GeofenceConstants.lot24Rad;
        }
        if (feedbackLot.equals(lot60)){
            currentLotLocation.setLatitude(GeofenceConstants.lot60.latitude);
            currentLotLocation.setLatitude(GeofenceConstants.lot60.longitude);
            radius = GeofenceConstants.lot60Rad;
        }

        double dlat = Double.parseDouble(lat);
        double dlng = Double.parseDouble(lng);

        Location mCurrentLocation = new Location("currentLocation");
        mCurrentLocation.setLatitude(dlat);
        mCurrentLocation.setLongitude(dlng);

        if(mLastLocation.distanceTo(currentLotLocation) <= radius) {
            if (fback.equals("0")) {
                Toast.makeText(this, getString(R.string.select_feedback_option_toast), Toast.LENGTH_SHORT).show();
            } else {

                time = getCurrentTime();

                HashMap<String, String> user = sessionManager.getUserDetails();
                String userId = user.get(SessionManager.KEY_USERID);

                feedBack = new FeedBackDB(userId, lotname, fback, time);
                feedbackDatabaseReference.push().child(userId).setValue(feedBack);
                mhomeLotDB = new HomeLotDB(lotname, fback);
                mlotSummaryDBRef.child(lotname).setValue(mhomeLotDB);
                Toast.makeText(this, getString(R.string.feedback_successfully_submitted_toast),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LotActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }else{
            Toast.makeText(this, R.string.lot_feedback, Toast.LENGTH_SHORT).show();
        }
    }
    public boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if (api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }else {
            Toast.makeText(this, getString(R.string.x_connect_play_services_toast), Toast.LENGTH_SHORT).show();
        }return false;
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
        String currentLot = getIntent().getStringExtra(LOT_KEY);
        String cpd = "CarParc Diem";
        String lot24 = "Lot 24";
        String lot60 = "Lot 60";




        //Taken from Google Maps API (shapes section)
        CircleOptions circleOptionscpd = new CircleOptions()
                .center(new LatLng(40.865314, -74.197107))
                .radius(50); // In meters

        CircleOptions circleOptionslot24 = new CircleOptions()
                .center(new LatLng(40.866385, -74.196641))
                .radius(50); // In meters

        CircleOptions circleOptionslot60 = new CircleOptions()
                .center(new LatLng(40.872985, -74.198942))
                .radius(50); // In meters



        //adds markers
       // mlot24DBRef = mdatabase.getReference("lot-summary").child(lot24).child("status");
        MarkerOptions optionscpd = new MarkerOptions()
                .title(getString(R.string.lot_name_carparc))
                .position(GeofenceConstants.carparcDiem);
        mgoogleMap.addMarker(optionscpd).showInfoWindow();

        MarkerOptions optionslot24 = new MarkerOptions()
                .title(getString(R.string.lot_name_24))
                .position(GeofenceConstants.lot24);
        mgoogleMap.addMarker(optionslot24).showInfoWindow();

        MarkerOptions optionslot60 = new MarkerOptions()
                .title(getString(R.string.lot_name_60))
                .position(GeofenceConstants.lot60);
        mgoogleMap.addMarker(optionslot60).showInfoWindow();



        //Dashed circle pattern
        List<PatternItem> pattern = Arrays.<PatternItem>asList(
                 new Dash(30), new Gap(30) );


        //automatically displays infoWindow depending on which lot selected from home
        if(currentLot.equals(cpd)){
            mgoogleMap.addMarker(optionscpd).showInfoWindow();
            Circle circle1 = mgoogleMap.addCircle(circleOptionscpd);
            circle1.setStrokePattern(pattern);
        }else if (currentLot.equals(lot24)){
            mgoogleMap.addMarker(optionslot24).showInfoWindow();
            Circle circle2 = mgoogleMap.addCircle(circleOptionslot24);
            circle2.setStrokePattern(pattern);
        }else if (currentLot.equals(lot60)){
            mgoogleMap.addMarker(optionslot60).showInfoWindow();
            Circle circle3 = mgoogleMap.addCircle(circleOptionslot60);
            circle3.setStrokePattern(pattern);
        }


        //zooms in on lot selected in home screen
        if(currentLot.equals(cpd) ) {

            goToLocationZoom(GeofenceConstants.carparcDiem, 16);

        }else if (currentLot.equals(lot24)){
            goToLocationZoom(GeofenceConstants.lot24, 17);

        }else if (currentLot.equals(lot60)){
            goToLocationZoom(GeofenceConstants.lot60, 17);

        }else {
            Toast.makeText(this, getString(R.string.x_find_location_toast), Toast.LENGTH_SHORT).show();
        }
    }



    private void goToLocationZoom( LatLng latlng, int zoom) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mgoogleMap.moveCamera(update);
    }
    //Taken from Week11FireBaseChat Demo
    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.US);
        return format.format(Calendar.getInstance().getTime());
    }

    /*---------------------------------------------------------------------*
        |   LOCATION SERVICES
     *---------------------------------------------------------------------*/

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        // CONNECT THE CLIENT
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                                    LotActivity.this,
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
        permissionCheck = PermissionChecker.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            addGeofences();
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
        |   NAVIGATION DRAWER
     *---------------------------------------------------------------------*/

    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {

        @Override
        public void onDrawerClosed(View drawerView) {}

        @Override
        public void onDrawerOpened(View drawerView) {}

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {}

        @Override
        public void onDrawerStateChanged(int newState) {}
    };

    public void openDrawer(View view){
        mDrawerLayout.openDrawer(Gravity.START);
        mDrawerLayout.addDrawerListener(drawerListener);
    }
}
