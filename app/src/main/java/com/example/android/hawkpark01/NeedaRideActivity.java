package com.example.android.hawkpark01;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.view.View;

import com.example.android.hawkpark01.models.NaRDB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by catalinamorales on 4/23/17.
 */

public class NeedaRideActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private NaRDB needarideSelection;
    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private DatabaseReference NaRDatabaseReference;

    Spinner lotSpinner;
    Spinner pickUpLocation_spinner;
    ArrayAdapter adapter1;
    ArrayAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.needaride);

        adapter1 = ArrayAdapter.createFromResource(this, R.array.parkingLot_array, android.R.layout.simple_spinner_item);
        lotSpinner = (Spinner) findViewById(R.id.lot_spinner);
        lotSpinner.setAdapter(adapter1);
        lotSpinner.setOnItemSelectedListener(NeedaRideActivity.this);


        adapter2 = ArrayAdapter.createFromResource(this, R.array.pickup_array, android.R.layout.simple_spinner_item);
        pickUpLocation_spinner = (Spinner) findViewById(R.id.pickupLocation_spinner);
        pickUpLocation_spinner.setAdapter(adapter2);
        pickUpLocation_spinner.setOnItemSelectedListener(NeedaRideActivity.this);

        mdatabase = FirebaseDatabase.getInstance();
        NaRDatabaseReference = mdatabase.getReference("needarideSelection");


    }

    public void onRadioButtonClicked1(View view){
        boolean checked =   ((RadioButton) view). isChecked();
         switch (view.getId()){
            case R.id.leaving_now:
                if (checked)

                    break;
            case R.id.leaving_in:
                if (checked)


                    break;


        }
    }
    public void onRadioButtonClicked2(View view){
        boolean checked = ((RadioButton) view).isChecked();
                switch(view.getId()){
                    case R.id.one_passenger:
                        if (checked)

                            break;
                    case R.id.two_passengers:
                        if (checked)

                            break;
                    case R.id.three_passengers:
                        if (checked)

                            break;

    }


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}