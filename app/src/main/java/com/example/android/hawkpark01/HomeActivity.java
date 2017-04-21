package com.example.android.hawkpark01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;

import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
    //Parking availability in lots==================================================================
    private ListView lv_lot_list;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    //Radio buttons in home activity
    //Sort lots based on user preference
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_btn_distance:
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
