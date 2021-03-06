package com.example.android.hawkpark01.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.hawkpark01.R;
import com.example.android.hawkpark01.SessionManager;
import com.example.android.hawkpark01.models.HomeLotDB;

import java.util.List;


/**
 * Created by priya on 4/21/2017.
 */

public class HomeLotAdapter extends ArrayAdapter<HomeLotDB> {

    public HomeLotAdapter(Context context, int resource, List<HomeLotDB> objects) {

        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.home_lot_item, parent, false);
        }
       // SharedPreferences lotSharedPref = getSharedPreferences("lot_location", Context.MODE_PRIVATE);

       // SharedPreferences.Editor editor = lotSharedPref.edit();

        // Lookup views for data population
        TextView tv_lotName = (TextView) convertView.findViewById(R.id.tv_lot_item);
        TextView tv_update_time = (TextView) convertView.findViewById(R.id.tv_update_time);
        HomeLotDB lotItem = getItem(position);
        int lotStatus = Integer.parseInt(lotItem.getStatus());
        //editor.putString(lotItem.getStatus(),tv_lotName.getText().toString());
        //editor.apply();
        //Set background color based on the lot status
        switch (lotStatus)
        {
            case 3://lot is full
                convertView.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.round_corner_red));
                break;
            case 2://lot half full
                convertView.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.round_corner_yellow));
                break;
            case 1://lot is empty
                convertView.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.round_corner_green));
                break;
        }
        //
        tv_lotName.setText(lotItem.getName());
        tv_update_time.setText(lotItem.getTime());
        tv_lotName.setTag(position);

    return convertView;
    }



}
