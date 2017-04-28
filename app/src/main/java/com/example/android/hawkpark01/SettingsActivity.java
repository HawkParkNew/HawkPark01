package com.example.android.hawkpark01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import static android.text.InputType.TYPE_CLASS_TEXT;

public class SettingsActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    EditText name, license, car, plates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radioGroup = (RadioGroup)findViewById(R.id.rg_lotpref);
        name = (EditText)findViewById(R.id.et_displayname);
        license = (EditText)findViewById(R.id.et_licence_num);
        car = (EditText)findViewById(R.id.et_car_model);
        plates = (EditText)findViewById(R.id.et_licence_plates);
    }

    public void enableEdit (View view){
        radioGroup.setVisibility(View.VISIBLE);
        name.setFocusable(true);
        license.setFocusable(true);
        car.setFocusable(true);
        plates.setFocusable(true);

    }
}
