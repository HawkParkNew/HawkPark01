package com.example.android.hawkpark01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static com.example.android.hawkpark01.utils.Utils.LOT_KEY;

public class LotActivity extends AppCompatActivity {
    TextView temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lot);

        String currentLot = getIntent().getStringExtra(LOT_KEY);
        temp = (TextView)findViewById(R.id.tv_temp);
        temp.setText("The current lot is: " + currentLot);


    }
}
