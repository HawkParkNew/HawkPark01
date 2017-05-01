package com.example.android.hawkpark01;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.hawkpark01.models.R2PDB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;
import static com.example.android.hawkpark01.utils.Utils.EMAIL_KEY;
import static com.example.android.hawkpark01.utils.Utils.ID_KEY;

public class R2PRegistrationActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private EditText et_licence,et_lic_plate,et_make,et_model;
    private DatabaseReference r2pDatabaseReference;
    SessionManager session;

    /*---------------------------------------------
        |   LOCATION VARIABLES
     *-------------------------------------------*/
    protected GoogleApiClient mGoogleApiClient;
    private int permissionCheck;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    LocationRequest mLocationRequest;
    Location mLastLocation;
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
        setContentView(R.layout.activity_r2p_registration);

        //init session manager
        session = new SessionManager(getApplicationContext());

        //init db
        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        r2pDatabaseReference = mdatabase.getReference("r2pRegister");
        //init views
        et_lic_plate = (EditText)findViewById(R.id.et_licence_plates);
        et_licence = (EditText) findViewById(R.id.et_licence_num);
        et_make = (EditText)findViewById(R.id.et_car_make);
        et_model = (EditText) findViewById(R.id.et_car_model);

        /*---------------------------------------------
        |   LOCATION SERVICES
        *-------------------------------------------*/

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
        drawerButton = (ImageView)findViewById(R.id.menu_btn);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.register_id);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_id:
                        Intent intentHome = new Intent(R2PRegistrationActivity.this, HomeActivity.class);
                        startActivity(intentHome);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.home_id);
                        break;
                    case R.id.profile_id:
                        Intent intentProfile = new Intent(R2PRegistrationActivity.this, SettingsActivity.class);
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
                            Intent intentCar = new Intent(R2PRegistrationActivity.this, CarLocation.class);
                            startActivity(intentCar);
                            mDrawerLayout.closeDrawers();
                            navigationView.setCheckedItem(R.id.wheresmycar_id);
                        }else{
                            Toast.makeText(R2PRegistrationActivity.this, getString(R.string.car_not_set), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.setcar_id:
                        SharedPreferences sharedPref = getSharedPreferences("car_location", Context.MODE_PRIVATE);

                        SharedPreferences.Editor edit = sharedPref.edit();
                        edit.putString(getString(R.string.car_lat_position), lat);
                        edit.putString(getString(R.string.car_lng_position), lng);
                        edit.commit();
                        Toast.makeText(R2PRegistrationActivity.this, getString(R.string.car_location_saved), Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.needpark_id:
                        Intent intentNeedPark = new Intent(R2PRegistrationActivity.this, NeedParking.class);
                        startActivity(intentNeedPark);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.needpark_id);
                        break;
                    case R.id.needride_id:
                        Intent intentNeedRide = new Intent(R2PRegistrationActivity.this, NeedRide.class);
                        startActivity(intentNeedRide);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.needride_id);
                        break;
                    case R.id.register_id:
                        Intent intentRegister = new Intent(R2PRegistrationActivity.this, R2PRegistrationActivity.class);
                        startActivity(intentRegister);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.register_id);
                        break;
                }
                return true;
            }
        });
    }

    public void r2p_signup_clicked(View view) {

        String licence = et_licence.getText().toString().trim();
        String lPlates = et_lic_plate.getText().toString().trim();
        String make = et_make.getText().toString().trim();
        String model = et_model.getText().toString().trim();

        //if entries empty make error toast
        if((isEmpty(licence))||isEmpty(lPlates)||isEmpty(make)||isEmpty(model))
        {
            Toast.makeText(this, R.string.missing_info_toast_r2p,Toast.LENGTH_SHORT).show();
        }
        //else upload to db
        else{
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();
            // userId
            String userId = user.get(SessionManager.KEY_USERID);
            R2PDB r2pRegister = new R2PDB(userId, licence, lPlates, make, model);
            r2pDatabaseReference.child(userId).setValue(r2pRegister);

            //clear values from all edit text fields
            et_lic_plate.setText("");
            et_licence.setText("");
            et_make.setText("");
            et_model.setText("");

            Toast.makeText(R2PRegistrationActivity.this, R.string.success_registration_toast_r2p,Toast.LENGTH_SHORT).show();

            //direct user to home activity
            Intent intent = new Intent(R2PRegistrationActivity.this,HomeActivity.class);
            startActivity(intent);
        }
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
        navigationView.setCheckedItem(R.id.register_id);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        permissionCheck = PermissionChecker.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
        navigationView.setCheckedItem(R.id.register_id);
        mDrawerLayout.openDrawer(Gravity.START);
        mDrawerLayout.addDrawerListener(drawerListener);
    }
}

