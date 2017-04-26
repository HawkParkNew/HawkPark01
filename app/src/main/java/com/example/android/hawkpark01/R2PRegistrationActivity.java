package com.example.android.hawkpark01;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.hawkpark01.models.R2PDB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;
import static com.example.android.hawkpark01.utils.Utils.EMAIL_KEY;
import static com.example.android.hawkpark01.utils.Utils.ID_KEY;

public class R2PRegistrationActivity extends AppCompatActivity {

    private EditText et_licence,et_lic_plate,et_make,et_model;
    private DatabaseReference r2pDatabaseReference;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r2p_registration);
        session = new SessionManager(getApplicationContext());



        //init db
        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        r2pDatabaseReference = mdatabase.getReference("r2pRegister");
        //init views
        et_lic_plate = (EditText)findViewById(R.id.et_licence_plates);
        et_licence = (EditText) findViewById(R.id.et_licence_num);
        et_make = (EditText)findViewById(R.id.et_car_make);
        et_model = (EditText) findViewById(R.id.et_car_model);
    }

    public void r2p_signup_clicked(View view) {

        String licence = et_licence.getText().toString().trim();
        String lPlates = et_lic_plate.getText().toString().trim();
        String make = et_make.getText().toString().trim();
        String model = et_model.getText().toString().trim();

        //if entries empty make error toast
        if((isEmpty(licence))||isEmpty(lPlates)||isEmpty(make)||isEmpty(model))
        {
            Toast.makeText(this, R.string.missing_info_toast_r2p,Toast.LENGTH_SHORT).show();
        }
        //else upload to db
        else{
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();
            // userId
            String userId = user.get(SessionManager.KEY_USERID);
            R2PDB r2pRegister = new R2PDB(userId, licence, lPlates, make, model);
            r2pDatabaseReference.push().child(userId).setValue(r2pRegister);
            //clear values from all edit text fields
            et_lic_plate.setText("");
            et_licence.setText("");
            et_make.setText("");
            et_model.setText("");

            Toast.makeText(R2PRegistrationActivity.this,"Registration was successful",Toast.LENGTH_SHORT).show();

            //direct user to home activity
            Intent intent = new Intent(R2PRegistrationActivity.this,HomeActivity.class);
            startActivity(intent);
        }
    }



}

