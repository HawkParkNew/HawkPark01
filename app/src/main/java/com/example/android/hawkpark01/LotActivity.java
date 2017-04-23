package com.example.android.hawkpark01;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.android.hawkpark01.utils.Utils.LOT_KEY;

public class LotActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected GoogleApiClient mGoogleApiClient;
    GoogleMap mgoogleMap;
    RadioButton empty;
    RadioButton some;
    RadioButton full;
    Button submit;
    TextView temp;
    RadioGroup rg;
    int fback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



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




        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_empty:
                        fback = 1;
                        break;
                    case R.id.radio_some:
                        fback = 2;
                        break;
                    case R.id.radio_full:
                        fback = 3;
                        break;
                }
            }
        });






    }

    public void submitFeedback (View view){
        Toast.makeText(this, "Submission Received: " + fback, Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "error connecting to play services", Toast.LENGTH_LONG).show();
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


        if(currentLot == "CarParc Diem" ) {
            goToLocationZoom(GeofenceConstants.carparcDiem, 18);
            MarkerOptions options = new MarkerOptions()
                    .title("CarParc Diem")
                    .position(GeofenceConstants.carparcDiem);
            mgoogleMap.addMarker(options);
        }else if (currentLot == "Lot 24"){
            goToLocationZoom(GeofenceConstants.lot24, 18);
            MarkerOptions options = new MarkerOptions()
                    .title("Lot 24")
                    .position(GeofenceConstants.lot24);
            mgoogleMap.addMarker(options);
        }else if (currentLot == "Lot 60"){
            goToLocationZoom(GeofenceConstants.lot60, 18);
            MarkerOptions options = new MarkerOptions()
                    .title("Lot 60")
                    .position(GeofenceConstants.lot60);
            mgoogleMap.addMarker(options);
        }

    }

    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mgoogleMap.moveCamera(update);
    }

    private void goToLocationZoom( LatLng latlng, int zoom) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mgoogleMap.moveCamera(update);
    }



}
