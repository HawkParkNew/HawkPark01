package com.example.android.hawkpark01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.hawkpark01.models.R2PDB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.text.TextUtils.isEmpty;
import static com.example.android.hawkpark01.utils.Utils.EMAIL_KEY;
import static com.example.android.hawkpark01.utils.Utils.ID_KEY;

public class R2PRegistrationActivity extends AppCompatActivity {
    private EditText et_licence,et_lic_plate,et_make,et_model;
    private String lPlates,licence,make, model;
    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private DatabaseReference r2pDatabaseReference;
    private R2PDB r2pRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r2p_registration);

        mdatabase = FirebaseDatabase.getInstance();
        r2pDatabaseReference = mdatabase.getReference("r2pRegister");

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
            Toast.makeText(this, R.string.missing_info_toast_r2p,Toast.LENGTH_SHORT).show();
        }
        //else upload to db
        else{
            String userId = getIntent().getStringExtra(ID_KEY);
            r2pRegister = new R2PDB(userId, licence, lPlates, make, model);
            r2pDatabaseReference.push().child(userId).setValue(r2pRegister);
            Intent intent = new Intent(R2PRegistrationActivity.this,HomeActivity.class);
            startActivity(intent);
        }
    }
        //else upload to db, report success, direct user to home page


}

