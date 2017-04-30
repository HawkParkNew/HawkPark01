package com.example.android.hawkpark01;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.hawkpark01.models.FeedBackDB;
import com.example.android.hawkpark01.models.HomeLotDB;
import com.example.android.hawkpark01.utils.GeofenceConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.android.hawkpark01.utils.Utils.ID_KEY;
import static com.example.android.hawkpark01.utils.Utils.LOT_KEY;

public class LotActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected GoogleApiClient mGoogleApiClient;
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
    }

    public void submitFeedback (View view){
        //checks to make sure a feedback option is selected
        if(fback.equals("0")) {
            Toast.makeText(this, getString(R.string.select_feedback_option_toast), Toast.LENGTH_SHORT).show();
        }
        else {
            String lotname = getIntent().getStringExtra(LOT_KEY);

            time = getCurrentTime();

            HashMap<String, String> user = sessionManager.getUserDetails();
            String userId = user.get(SessionManager.KEY_USERID);

            feedBack = new FeedBackDB(userId, lotname, fback, time);
            feedbackDatabaseReference.push().child(userId).setValue(feedBack);
            mhomeLotDB = new HomeLotDB(lotname,fback);
            mlotSummaryDBRef.child(lotname).setValue(mhomeLotDB);
            Toast.makeText(this, getString(R.string.feedback_successfully_submitted_toast),
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LotActivity.this, HomeActivity.class);
            startActivity(intent);
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
}
