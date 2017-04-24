package com.example.android.hawkpark01;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by catalinamorales on 4/23/17.
 */

public class NeedParkingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayAdapter adapter3;
    ArrayAdapter adapter4;
    ArrayAdapter adapter5;
    Spinner pref1Spinner;
    Spinner pref2Spinner;
    Spinner pref3Spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.needparking);


        adapter3 = ArrayAdapter.createFromResource(this, R.array.pref1_array, android.R.layout.simple_spinner_item);
        pref1Spinner = (Spinner) findViewById(R.id.pref1_spinner);
        pref1Spinner.setAdapter(adapter3);
        pref1Spinner.setOnItemSelectedListener(NeedParkingActivity.this);

        adapter4 = ArrayAdapter.createFromResource(this, R.array.pref2_array, android.R.layout.simple_spinner_item);
        pref2Spinner = (Spinner) findViewById(R.id.pref2_spinner);
        pref2Spinner.setAdapter(adapter4);
        pref2Spinner.setOnItemSelectedListener(NeedParkingActivity.this);

        adapter5 = ArrayAdapter.createFromResource(this, R.array.pref3_array, android.R.layout.simple_spinner_item);
        pref3Spinner = (Spinner) findViewById(R.id.pref3_spinner);
        pref3Spinner.setAdapter(adapter5);
        pref3Spinner.setOnItemSelectedListener(NeedParkingActivity.this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
