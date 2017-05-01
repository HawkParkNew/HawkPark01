package com.example.android.hawkpark01;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.hawkpark01.models.ConnectionsDB;
import com.example.android.hawkpark01.models.R2PDB;
import com.example.android.hawkpark01.utils.GeofenceConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.android.hawkpark01.utils.Utils.CONNECT_KEY;
import static com.example.android.hawkpark01.utils.Utils.CONNECT_NAME_KEY;
import static com.example.android.hawkpark01.utils.Utils.DB_KEY;
import static com.example.android.hawkpark01.utils.Utils.LOT_KEY;

public class Connect extends AppCompatActivity implements OnMapReadyCallback {
    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    DatabaseReference mconnectionsDBRef, mneedParkingDBRef;
    SessionManager session;
    String userId, userName;
    String meetSpot, meetTime, destination, rider, parker, connectName;
    String carMake, carModel, carLicPlates;
    TextView tv_connectName, tv_meetSpot, tv_meetTime, tv_destination, tv_header, tv_car;

    GoogleMap mgoogleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //======================================================================UNPACK SHARED PREFS.
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(SessionManager.KEY_USERID);// userId
        userName = user.get(SessionManager.KEY_NAME);//display name

        HashMap<String,String> connect = session.getConnectDetails();
        rider = connect.get(SessionManager.KEY_RIDER);
        parker = connect.get(SessionManager.KEY_PARKER);
        connectName = connect.get(SessionManager.KEY_NAME);
        meetTime = connect.get(SessionManager.KEY_TIME);
        meetSpot = connect.get(SessionManager.KEY_MEET);
        destination = connect.get(SessionManager.KEY_DEST);

        //============================================================================INITIALIZE MAP
        //checks to make sure googleServices is available before initializing map
        if(googleServicesAvailable()) {
            setContentView(R.layout.activity_connect);
            initMap();
        }else {
            //no Google Maps Layout
        }

        //==========================================================================INITIALIZE VIEWS
        tv_header = (TextView) findViewById(R.id.tv_connect_detail_header);
        tv_connectName = (TextView)findViewById(R.id.tv_name_connect);
        tv_meetSpot = (TextView)findViewById(R.id.tv_meeting_spot_connect);
        tv_destination = (TextView)findViewById(R.id.tv_destination_connect);
        tv_meetTime = (TextView)findViewById(R.id.tv_time_connect);
        tv_car = (TextView)findViewById(R.id.tv_car_connect);

        /**==================================CONNECT AND RETRIEVE MEETING DETAILS FROM CONNECTIONS DB
        mconnectionsDBRef = mdatabase.getReference("connections").child(parker);
        mconnectionsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        ConnectionsDB connection = dataSnapshot.getValue(ConnectionsDB.class);
                        meetSpot = connection.getMeetSpot();
                        meetTime = connection.getDisplayTime();
                        destination = connection.getDestination();
                }
                //=====================================================POPULATE MEETING DETAIL VIEWS
                //async listener so wait until listener is triggered to populate views
                String meetSpotStr = getString(R.string.tv_meet_spot_connect) + " " + meetSpot;
                String destinationStr = getString(R.string.tv_destination_str_connect)+
                        " "  + destination;
                String meetTimeStr = getString(R.string.tv_meet_time_str_connect) + " " + meetTime;
                tv_meetSpot.setText(meetSpotStr);
                tv_destination.setText(destinationStr);
                tv_meetTime.setText(meetTimeStr);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        //=============================================================POPULATE MEETING DETAIL VIEWS
        String meetSpotStr = getString(R.string.tv_meet_spot_connect) + " " + meetSpot;
        String destinationStr = getString(R.string.tv_destination_str_connect)+
        " "  + destination;
        String meetTimeStr = getString(R.string.tv_meet_time_str_connect) + " " + meetTime;
        tv_meetSpot.setText(meetSpotStr);
        tv_destination.setText(destinationStr);
        tv_meetTime.setText(meetTimeStr);

        //==============================================CONNECT AND RETRIEVE CAR DETAILS FROM R2P DB
        mneedParkingDBRef = mdatabase.getReference("r2pRegister").child(parker);
        mneedParkingDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    R2PDB parkerCarDetails = dataSnapshot.getValue(R2PDB.class);
                    carMake = parkerCarDetails.getMake();
                    carModel = parkerCarDetails.getModel();
                    carLicPlates = parkerCarDetails.getlPlates();
                }
                //==========================================================POPULATE CAR DETAIL VIEW
                String carStr = getString(R.string.tv_car_str_connect) + " "
                        + carMake +", " + carModel + ", " + carLicPlates;
                tv_car.setText(carStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});

        //============================================================================POPULATE VIEWS
        String welcome = userName + ", " + getResources().getString(R.string.tv_connect_detail_header);
        tv_header.setText(welcome);//format: username, you have connected with:
        tv_connectName.setText(connectName);

    }
    //===================================================================================GOOGLE MAPS
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
        String currentLot = meetSpot;
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

    private void goToLocationZoom(LatLng latlng, int zoom) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mgoogleMap.moveCamera(update);
    }

    // Checks if google services are available
    public boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if (api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }else {
            Toast.makeText(this, getString(R.string.x_connect_play_services_toast),
                    Toast.LENGTH_SHORT).show();
        }return false;
    }
    // Initialize map
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }
    //=============================================================HANDLE CANCELLATION OF CONNECTION
    public void cancelConnect(View view) {
    }
}
