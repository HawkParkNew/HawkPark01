package com.example.android.hawkpark01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

public class NeedRide extends AppCompatActivity {

    RadioGroup rg_leaving;
    RadioButton rbtn_now, rbtn_later;
    EditText et_leavingTime;
    EditText et_parkedLot;
    EditText et_pickupSpot;
    RadioGroup rg_passengers;
    ListView lv_needParking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_ride);
    }
}
