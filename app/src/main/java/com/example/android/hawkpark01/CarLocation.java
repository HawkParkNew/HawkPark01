package com.example.android.hawkpark01;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;


public class CarLocation extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    protected GoogleApiClient mGoogleApiClient;
    GoogleMap mGoogleMap;
    private int permissionCheck;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    LocationRequest mLocationRequest;
    Location mLastLocation,carLocation;
    LatLng mCarLatLng, mLastLatLng, midpoint;
    TextView distance;
    private String lat;
    private String lng;

    /*---------------------------------------------
        |   NAVIGATION DRAWER VARIABLES
     *-------------------------------------------*/
    DrawerLayout mDrawerLayout;
    ImageView drawerButton;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleServicesAvailable()) {
            setContentView(R.layout.activity_car_location);
            distance = (TextView)findViewById(R.id.distance);
            initMap();
        } else {
            //no Google Maps Layout
        }

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

        buildGoogleApiClient();
        createLocationRequest();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerButton = (ImageView)findViewById(R.id.logo_login);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.wheresmycar_id);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_id:
                        Intent intentHome = new Intent(CarLocation.this, HomeActivity.class);
                        startActivity(intentHome);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.home_id);
                        break;
                    case R.id.profile_id:
                        Intent intentProfile = new Intent(CarLocation.this, SettingsActivity.class);
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
                            Intent intentCar = new Intent(CarLocation.this, CarLocation.class);
                            startActivity(intentCar);
                            mDrawerLayout.closeDrawers();
                            navigationView.setCheckedItem(R.id.wheresmycar_id);
                        }else{
                            Toast.makeText(CarLocation.this, getString(R.string.car_not_set), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.setcar_id:
                        SharedPreferences sharedPref = getSharedPreferences("car_location", Context.MODE_PRIVATE);

                        SharedPreferences.Editor edit = sharedPref.edit();
                        edit.putString(getString(R.string.car_lat_position), lat);
                        edit.putString(getString(R.string.car_lng_position), lng);
                        edit.commit();
                        Toast.makeText(CarLocation.this, getString(R.string.car_location_saved), Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.needpark_id:
                        Intent intentNeedPark = new Intent(CarLocation.this, NeedParking.class);
                        startActivity(intentNeedPark);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.needpark_id);
                        break;
                    case R.id.needride_id:
                        Intent intentNeedRide = new Intent(CarLocation.this, NeedRide.class);
                        startActivity(intentNeedRide);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.needride_id);
                        break;
                    case R.id.register_id:
                        Intent intentRegister = new Intent(CarLocation.this, R2PRegistrationActivity.class);
                        startActivity(intentRegister);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.register_id);
                        break;
                }
                return true;
            }
        });
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000); //UPDATE LOCATION EVERY SECOND (TIME IN MS)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.wheresmycar_id);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, getText(R.string.x_connect_play_services_toast), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        SharedPreferences sharedPref = getSharedPreferences("car_location", Context.MODE_PRIVATE);

        String tempCarLat = sharedPref.getString(getString(R.string.car_lat_position), "");
        String tempCarLng = sharedPref.getString(getString(R.string.car_lng_position),"");
        String tempPersonLat = sharedPref.getString(getString(R.string.last_known_lat),"");
        String tempPersonLng = sharedPref.getString(getString(R.string.last_known_lng),"");

        double carLat = Double.parseDouble(tempCarLat);
        double carLng = Double.parseDouble(tempCarLng);
        double lastLat = Double.parseDouble(tempPersonLat);
        double lastLng = Double.parseDouble(tempPersonLng);

        mCarLatLng = new LatLng(carLat,carLng);
        mLastLatLng = new LatLng(lastLat,lastLng);

        double midLat = (carLat+lastLat)/2;
        double midLng = (carLng+lastLng)/2;
        midpoint = new LatLng(midLat, midLng);

        carLocation = new Location("carLocation");
        carLocation.setLatitude(carLat);
        carLocation.setLongitude(carLng);

        goToLocationZoom(mCarLatLng,mLastLatLng,midpoint,16);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
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

    private void goToLocationZoom(LatLng car, LatLng person, LatLng mid, int zoom) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mid, zoom);
        addMarkers(car, person, mid);
        mGoogleMap.moveCamera(update);
    }

    private void addMarkers(LatLng car, LatLng person, LatLng midpoint){
        MarkerOptions carMarker = new MarkerOptions()
                .position(car)
                .title(getString(R.string.car_location_marker))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.carlocation));
        mGoogleMap.addMarker(carMarker).showInfoWindow();

        MarkerOptions lastKnown = new MarkerOptions()
                .position(person)
                .title(getString(R.string.you_are_here_marker));
        mGoogleMap.addMarker(lastKnown);

        MarkerOptions mid = new MarkerOptions()
                .position(midpoint)
                .visible(false);
        mGoogleMap.addMarker(mid);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        permissionCheck = PermissionChecker.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            String carDistance = String.valueOf(String.format("%.2f",mLastLocation.distanceTo(carLocation)/1609.344));

            distance.setText(getString(R.string.distance_to_car_cla)+carDistance+getString(R.string.miles_cla));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = (String.valueOf(location.getLatitude()));
        lng = (String.valueOf(location.getLongitude()));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        navigationView.setCheckedItem(R.id.wheresmycar_id);
        mDrawerLayout.openDrawer(Gravity.START);
        mDrawerLayout.addDrawerListener(drawerListener);
    }
}

