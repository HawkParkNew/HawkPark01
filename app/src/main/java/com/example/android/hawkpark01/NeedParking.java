package com.example.android.hawkpark01;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.hawkpark01.models.NeedParkingDB;
import com.example.android.hawkpark01.models.NeedRideDB;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class NeedParking extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    EditText et_arrivalTime;
    Spinner spinner_pref1;
    Spinner spinner_pref2;
    RadioGroup rg_seats;
    ListView lv_needRide;
    Button btn_submit;

    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    DatabaseReference mNeedParkingDBRef = mdatabase.getReference("need-parking");
    SessionManager session;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_parking);

        //=================================================================SESSION FOR SHARED PREFS.
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(SessionManager.KEY_USERID);// userId

        //==========================================================================INITIALIZE VIEWS
        rg_seats = (RadioGroup)findViewById(R.id.radio_num_seats_np);
        btn_submit = (Button) findViewById(R.id.btn_submit_np);
        et_arrivalTime = (EditText) findViewById(R.id.et_arriving_in_np);
        //================================================SET UP TIME-PICKER DIALOG FOR LEAVING TIME
        et_arrivalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NeedParking.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et_arrivalTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        //==========================================SET UP SPINNERS FOR PARKING LOT PREFS 1 & 2
        String[] lotNames = {"Select Lot", "Any", "Car Parc Diem", "Lot 24", "Lot 60"};
        ArrayAdapter lotAdapter;
        spinner_pref1 = (Spinner)findViewById(R.id.spinner_pref_1_np);
        spinner_pref1.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the pick-up location list
        lotAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,lotNames);
        lotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner_pref1.setAdapter(lotAdapter);

        ArrayAdapter pickupAdapter;
        spinner_pref2 = (Spinner)findViewById(R.id.spinner_pickup_from_nr);
        spinner_pref2.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the pick-up location list
        pickupAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,lotNames);
        pickupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner_pref2.setAdapter(pickupAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //===============================================================BUTTON SUBMIT NEED PARKING FORM
    //Checks if all required info. on the need a ride form is complete and if yes submits it to db
    public void submitNeedParking(View view) {
        int time = et_arrivalTime.getText().toString().trim().length();//length of input in EditText
        int pref_1_id = spinner_pref1.getSelectedItemPosition();
        final int pref_2_id = spinner_pref2.getSelectedItemPosition();
        int seatsId =  rg_seats.getCheckedRadioButtonId();//Default "3" set in xml

        //check that all the required fields are selected.-arrival time and pref1 are mandatory
        if(time==0||!(pref_1_id>0)){
            Toast.makeText(this, R.string.incomplete_info, Toast.LENGTH_SHORT).show();
        }
        else{//all required info is available, send to db
            final String pref_1 = spinner_pref1.getSelectedItem().toString();
            String pref_2 = spinner_pref2.getSelectedItem().toString();
            if(isEmpty(pref_2)){
                pref_2 = "None";
            }
            final String arrivalTime = et_arrivalTime.getText().toString();
            String numSeats = "3";
            switch(seatsId){
                case R.id.r_btn_one_np:
                    numSeats = "1";
                    break;
                case R.id.r_btn_two_np:
                    numSeats = "2";
                    break;
                case R.id.r_btn_three_np:
                    numSeats = "3";
                    break;
            }
            NeedParkingDB needDBitem = new NeedParkingDB(userId, arrivalTime, pref_1, pref_2, numSeats);
            mNeedParkingDBRef.push().child(userId).setValue(needDBitem, new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if (error != null) {
                        Toast.makeText(NeedParking.this, R.string.error_saving_toast,Toast.LENGTH_SHORT).show();
                        System.out.println("Data could not be saved " + error.getMessage());
                    }
                    else{
                        Toast.makeText(NeedParking.this, R.string.success_nr_toast,Toast.LENGTH_SHORT).show();
                        et_arrivalTime.setText("");
                        spinner_pref1.setSelection(-1);
                        spinner_pref2.setSelection(-1);                    }

                }
            });
        }
    }
}
