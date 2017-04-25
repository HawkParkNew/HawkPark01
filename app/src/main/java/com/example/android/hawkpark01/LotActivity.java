package com.example.android.hawkpark01;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.hawkpark01.models.FeedBackDB;
import com.example.android.hawkpark01.models.R2PDB;
import com.example.android.hawkpark01.utils.GeofenceConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private DatabaseReference feedbackDatabaseReference;
    private FeedBackDB feedBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String currentLot = getIntent().getStringExtra(LOT_KEY);

        mdatabase = FirebaseDatabase.getInstance();
        feedbackDatabaseReference = mdatabase.getReference("feedbackDB");

        if(googleServicesAvailable()) {
            setContentView(R.layout.activity_lot);
            initMap();
        }else {
            //no Google Maps Layout
        }
        empty = (RadioButton) findViewById(R.id.radio_empty);
        some = (RadioButton) findViewById(R.id.radio_some);
        full = (RadioButton) findViewById(R.id.radio_full);
        submit = (Button) findViewById(R.id.btn_lot_submit);
        rg = (RadioGroup)findViewById(R.id.rg);
        header = (TextView)findViewById(R.id.tv_lot_header);

        header.setText(currentLot);

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

        if(fback.equals("0")) {
        //    Intent intent = new Intent (LotActivity.this, LotActivity.class);
        //    startActivity(intent);
            Toast.makeText(this, getString(R.string.select_ffedback_option_toast), Toast.LENGTH_LONG).show();
        }
        else {

            String lotname = getIntent().getStringExtra(LOT_KEY);
            String userId = getIntent().getStringExtra(ID_KEY);
            time = getCurrentTime();

            feedBack = new FeedBackDB(userId, lotname, fback, time);
            feedbackDatabaseReference.push().child(userId).setValue(feedBack);
            Intent intent = new Intent(LotActivity.this, HomeActivity.class);
            startActivity(intent);


            Toast.makeText(this, getString(R.string.submission_received_toast) + fback, Toast.LENGTH_LONG).show();

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
            Toast.makeText(this, getString(R.string.x_connect_plat_services_toast), Toast.LENGTH_LONG).show();
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


        if(currentLot.equals(cpd) ) {
            goToLocationZoom(GeofenceConstants.carparcDiem, 18);
            MarkerOptions options = new MarkerOptions()
                    .title(getString(R.string.lot_name_carparc))
                    .position(GeofenceConstants.carparcDiem);
            mgoogleMap.addMarker(options).showInfoWindow();
        }else if (currentLot.equals(lot24)){
            goToLocationZoom(GeofenceConstants.lot24, 18);
            MarkerOptions options = new MarkerOptions()
                    .title(getString(R.string.lot_name_24))
                    .position(GeofenceConstants.lot24);
            mgoogleMap.addMarker(options).showInfoWindow();
        }else if (currentLot.equals(lot60)){
            goToLocationZoom(GeofenceConstants.lot60, 18);
            MarkerOptions options = new MarkerOptions()
                    .title(getString(R.string.lot_name_60))
                    .position(GeofenceConstants.lot60);
            mgoogleMap.addMarker(options).showInfoWindow();
        }else {
            Toast.makeText(this, getString(R.string.x_find_location_toast), Toast.LENGTH_LONG).show();;
        }
    }

    private void goToLocationZoom( LatLng latlng, int zoom) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mgoogleMap.moveCamera(update);
    }
    //Taken from Week11FireBaseChat Demo
    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        return format.format(Calendar.getInstance().getTime());
    }


}
