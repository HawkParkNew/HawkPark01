package com.example.android.hawkpark01;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    //Parking availability in lots==================================================================
    private ListView lv_lot_list;
    private Button btn_r2p,btn_settings;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mlotSummaryDBRef;
    private ChildEventListener mChildEventListener;
    private HomeLotAdapter mhomeLotAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lv_lot_list = (ListView)findViewById(R.id.lv_lot_btn_ha);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mlotSummaryDBRef = mFirebaseDatabase.getReference("lot-summary");
        // Initialize lotSummary ListView and its adapter
        List<HomeLotItem> homeLotItemsList = new ArrayList<>();
        mhomeLotAdapter = new HomeLotAdapter(HomeActivity.this, R.layout.home_lot_item, homeLotItemsList);
        lv_lot_list.setAdapter(mhomeLotAdapter);


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //add to lot list
                HomeLotItem homeLotItem = dataSnapshot.getValue(HomeLotItem.class);
                mhomeLotAdapter.add(homeLotItem);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //get new status and change color, position etc
                HomeLotItem homeLotItem = dataSnapshot.getValue(HomeLotItem.class);
                mhomeLotAdapter.add(homeLotItem);

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



}
