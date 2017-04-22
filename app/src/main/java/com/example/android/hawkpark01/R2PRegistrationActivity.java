package com.example.android.hawkpark01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import static android.R.attr.entries;
import static android.text.TextUtils.isEmpty;

public class R2PRegistrationActivity extends AppCompatActivity {
    private EditText et_licence,et_lic_plate,et_make,et_model;
    private String lPlates,licence,make, model;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r2p_registration);

        et_lic_plate = (EditText)findViewById(R.id.et_licence_plates);
        et_licence = (EditText) findViewById(R.id.et_licence_num);
        et_make = (EditText)findViewById(R.id.et_car_make);
        et_model = (EditText) findViewById(R.id.et_car_model);


    }

    public void r2p_signup_clicked(View view) {

        licence = et_licence.getText().toString();
        lPlates = et_lic_plate.getText().toString();
        make = et_make.getText().toString();
        model = et_model.getText().toString();

        //if entries empty make error toast
        if((isEmpty(licence))||isEmpty(lPlates)||isEmpty(make)||isEmpty(model))
        {
            Toast.makeText(this,"Missing information.Please try again.",Toast.LENGTH_SHORT).show();
        }
        //else check formats, if error make toast
        else{
            if(licence.length()!=15)
                et_licence.setError(getString(R.string.licence_err_r2p));
            else
            {

            }
        }
        //else upload to db, report success, direct user to home page

    }
}
