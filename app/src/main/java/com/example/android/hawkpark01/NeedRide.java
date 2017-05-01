package com.example.android.hawkpark01;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.hawkpark01.models.ConnectionsDB;
import com.example.android.hawkpark01.models.NeedParkingDB;
import com.example.android.hawkpark01.models.NeedParkingAdapter;
import com.example.android.hawkpark01.models.NeedRideDB;
import com.example.android.hawkpark01.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.example.android.hawkpark01.utils.Utils.CONNECT_KEY;

public class NeedRide extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    EditText et_leavingTime;
    Spinner spinner_parkedLot;
    Spinner spinner_pickup_location;
    RadioGroup rg_passengers;
    ListView lv_needParking;
    Button btn_submit;

    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    DatabaseReference mNeedRideDBRef = mdatabase.getReference("need-ride");
    DatabaseReference mNeedParkingDBRef = mdatabase.getReference("need-parking");
    DatabaseReference mconnectionsDBRef = mdatabase.getReference("connections");
    SessionManager session;
    NeedParkingAdapter needParkingAdapter;
    String userId, name;

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
        setContentView(R.layout.activity_need_ride);

        //=================================================================SESSION FOR SHARED PREFS.
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(SessionManager.KEY_USERID);// userId
        name = user.get(SessionManager.KEY_NAME);//display name
        //==========================================================================INITIALIZE VIEWS
        rg_passengers = (RadioGroup)findViewById(R.id.radio_num_passengers_nr);
        btn_submit = (Button) findViewById(R.id.btn_submit_nr);
        et_leavingTime = (EditText) findViewById(R.id.et_leaving_in_nr);
        lv_needParking = (ListView) findViewById(R.id.lv_need_park_nr) ;
        //================================================SET UP TIME-PICKER DIALOG FOR LEAVING TIME
        et_leavingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NeedRide.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //ensure time is in the format HH:mm
                        if(selectedHour<10){//hour is less than 10
                            String hourFormatter = "0"+selectedHour;
                            if(selectedMinute<10){// hour and minute < 10
                                String minuteFormatter = "0"+ selectedMinute;
                                et_leavingTime.setText( hourFormatter + ":" + minuteFormatter);
                            }else
                                et_leavingTime.setText( hourFormatter + ":" + selectedMinute);
                        }
                        else{
                            if(selectedMinute<10){// only minute < 10
                                String minuteFormatter = "0"+ selectedMinute;
                                et_leavingTime.setText( selectedHour + ":" + minuteFormatter);
                            }else
                                et_leavingTime.setText( selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        //==========================================SET UP SPINNERS FOR PARKED LOT & PICKUP LOCATION
        String[] lotNames = {"Select Lot", "Car Parc Diem", "Lot 24", "Lot 60"};
        ArrayAdapter lotAdapter;
        spinner_parkedLot = (Spinner)findViewById(R.id.spinner_parked_in_nr);
        spinner_parkedLot.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the pick-up location list
        lotAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,lotNames);
        lotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spinner_parkedLot.setAdapter(lotAdapter);

        final String[] pickupLocation = {"Select location", "Rec. Center", "Opposite Carparc"};
        ArrayAdapter pickupAdapter;
        spinner_pickup_location = (Spinner)findViewById(R.id.spinner_pickup_from_nr);
        spinner_pickup_location.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the pick-up location list
        pickupAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,pickupLocation);
        pickupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner_pickup_location.setAdapter(pickupAdapter);

        //==========================================================SET UP LIST VIEW FOR NEED A RIDE
        // Initialize NEED parking ListView and its adapter
        final List<NeedParkingDB> needParkingDBList = new ArrayList<>();
        needParkingAdapter = new NeedParkingAdapter(NeedRide.this, R.layout.need_parking_item,
                                                        needParkingDBList);
        lv_needParking.setAdapter(needParkingAdapter);
        // Set item click listener for the lot view
        lv_needParking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Access the row position here to get the correct data item
                NeedParkingDB selectedParking = needParkingDBList.get(i);
                // Get details of connected user who needs parking
                String parkerId = selectedParking.getUserId();
                long arriveTime = selectedParking.getReqTimeMillis();
                String displayTime = selectedParking.getArriveTime();
                String name = selectedParking.getName();
                String seats = selectedParking.getNumSeats();
                String parkingLot = selectedParking.getLotPref1();
                String pickupLoc = pickupLocation[1];//todo change this to get user input first
                String msg = name + " arriving at " + displayTime + " has space for " + seats + " riders.";

                //creates a dialog for user to review and confirm connection
                confirmDialog(msg, userId, parkerId, pickupLoc, parkingLot, arriveTime, seats);

            }
        });
        ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //add to lot list
                NeedParkingDB needParkingDB = dataSnapshot.getValue(NeedParkingDB.class);
                needParkingAdapter.add(needParkingDB
                );
                needParkingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //get new status and change color, position etc
                needParkingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //remove from listview
                needParkingAdapter.notifyDataSetChanged();

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
        mNeedParkingDBRef.orderByChild("reqTimeMillis").addChildEventListener(mChildEventListener);

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
        navigationView.setCheckedItem(R.id.needride_id);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_id:
                        Intent intentHome = new Intent(NeedRide.this, HomeActivity.class);
                        startActivity(intentHome);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.home_id);
                        break;
                    case R.id.profile_id:
                        Intent intentProfile = new Intent(NeedRide.this, SettingsActivity.class);
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
                            Intent intentCar = new Intent(NeedRide.this, CarLocation.class);
                            startActivity(intentCar);
                            mDrawerLayout.closeDrawers();
                            navigationView.setCheckedItem(R.id.wheresmycar_id);
                        }else{
                            Toast.makeText(NeedRide.this, getString(R.string.car_not_set), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.setcar_id:
                        SharedPreferences sharedPref = getSharedPreferences("car_location", Context.MODE_PRIVATE);

                        SharedPreferences.Editor edit = sharedPref.edit();
                        edit.putString(getString(R.string.car_lat_position), lat);
                        edit.putString(getString(R.string.car_lng_position), lng);
                        edit.commit();
                        Toast.makeText(NeedRide.this, getString(R.string.car_location_saved), Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.needpark_id:
                        Intent intentNeedPark = new Intent(NeedRide.this, NeedParking.class);
                        startActivity(intentNeedPark);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.needpark_id);
                        break;
                    case R.id.needride_id:
                        Intent intentNeedRide = new Intent(NeedRide.this, NeedRide.class);
                        startActivity(intentNeedRide);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.needride_id);
                        break;
                    case R.id.register_id:
                        Intent intentRegister = new Intent(NeedRide.this, R2PRegistrationActivity.class);
                        startActivity(intentRegister);
                        mDrawerLayout.closeDrawers();
                        navigationView.setCheckedItem(R.id.register_id);
                        break;
                }
                return true;
            }
        });
    }

    //if user selects an item of the list view
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Auto-generated method stub
    }
    //==================================================================BUTTON SUBMIT NEED RIDE FORM
    //Checks if all required info. on the need a ride form is complete and if yes submits it to db
    public void submitNeedRide(View view) {
        int time = et_leavingTime.getText().toString().trim().length();//length of input in EditText
        int parkedId = spinner_parkedLot.getSelectedItemPosition();
        int pickupId = spinner_pickup_location.getSelectedItemPosition();
        int passengerId =  rg_passengers.getCheckedRadioButtonId();//Default "1" set in xml

        //check that all the required fields are selected.
        if(time==0||!(parkedId>0)||!(pickupId>0)){
            Toast.makeText(this, R.string.incomplete_info, Toast.LENGTH_SHORT).show();
        }
        else{//all required info is available, send to db
            String parkedLot = spinner_parkedLot.getSelectedItem().toString();
            String pickupLoc = spinner_pickup_location.getSelectedItem().toString();

            String pickerTime = et_leavingTime.getText().toString();//in format HH:mm
            final String leavingTime = Utils.setReqTime(pickerTime);//in format d MMM, HH:mm
            String reqDate = Utils.createReqDateString(pickerTime); //in format dd-MM-yyyy HH:mm:ss
            long timeMillis = Utils.getTimeinMillis(reqDate);

            String numPassenger = "1";
            switch(passengerId){
                case R.id.r_btn_one_nr:
                    numPassenger = "1";
                    break;
                case R.id.r_btn_two_nr:
                    numPassenger = "2";
                    break;
                case R.id.r_btn_three_nr:
                    numPassenger = "3";
                    break;
            }
            NeedRideDB needDBitem = new NeedRideDB(userId, name, leavingTime,timeMillis, parkedLot,
                                                                        pickupLoc, numPassenger);
            mNeedRideDBRef.push().setValue(needDBitem, new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if (error != null) {
                        Toast.makeText(NeedRide.this, R.string.error_saving_toast,
                                Toast.LENGTH_SHORT).show();
                        System.out.println("Data could not be saved " + error.getMessage());
                    }
                    else{
                        Toast.makeText(NeedRide.this, R.string.success_nr_toast,
                                Toast.LENGTH_SHORT).show();
                        et_leavingTime.setText("");
                        spinner_parkedLot.setSelection(-1);
                        spinner_pickup_location.setSelection(-1);                    }
                }
            });
        }
    }
    //Alert dialog activated when user clicks on listview item
    //if confirm- connection is made and user is directed to connect screen
    //if back dialog is closed
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void confirmDialog(String msg,
                              final String riderId , final String parkerId,
                              final String meetSpot, final String destination,
                              final long meetTime, String seats){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.connected_with_dialog_np_nr);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher_round);
        alertDialogBuilder.setMessage(msg);
                alertDialogBuilder.setPositiveButton(R.string.btn_confirm_dialog_np_nr,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //create new item in connection db
                                ConnectionsDB mConnectionsDB = new ConnectionsDB(riderId,parkerId,meetSpot,destination,meetTime);
                                mconnectionsDBRef.push().setValue(mConnectionsDB,new DatabaseReference.CompletionListener() {
                                    //Set listener to wait for creation of connection in db
                                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                                        if (error != null) {
                                            Toast.makeText(NeedRide.this, R.string.error_saving_toast, Toast.LENGTH_SHORT).show();
                                            System.out.println("Data could not be saved " + error.getMessage());
                                        } else {
                                            Toast.makeText(NeedRide.this, R.string.success_nr_toast, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                //change status of item in "need-parking" db to connected('2')
                                mNeedParkingDBRef.child(parkerId).child("status").setValue("2");
                                //direct the user to the connections activity
                                Intent intent = new Intent(NeedRide.this,Connect.class);
                                intent.putExtra(CONNECT_KEY,parkerId);
                                startActivity(intent);
                            }
                        });

        alertDialogBuilder.setNegativeButton(R.string.btn_other_dialog_np_nr,
                                                    new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
        navigationView.setCheckedItem(R.id.needride_id);
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
        navigationView.setCheckedItem(R.id.needride_id);
        mDrawerLayout.openDrawer(Gravity.START);
        mDrawerLayout.addDrawerListener(drawerListener);
    }
}
