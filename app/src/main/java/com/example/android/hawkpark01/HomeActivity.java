package com.example.android.hawkpark01;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import com.example.android.hawkpark01.models.HomeLotAdapter;
import com.example.android.hawkpark01.models.HomeLotDB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.hawkpark01.utils.Utils.EMAIL_KEY;
import static com.example.android.hawkpark01.utils.Utils.ID_KEY;
import static com.example.android.hawkpark01.utils.Utils.LOT_KEY;


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
            case R.id.btn_settings://directs user to ride2park screen
                //change this
                Intent i = new Intent(HomeActivity.this,SettingsActivity.class);
                startActivity(i);
                break;

        }

    }
}
