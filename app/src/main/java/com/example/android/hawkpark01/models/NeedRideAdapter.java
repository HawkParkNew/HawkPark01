package com.example.android.hawkpark01.models;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.android.hawkpark01.R;
import java.util.List;



/**
 * Created by priya on 4/28/2017.
 */

public class NeedRideAdapter extends ArrayAdapter<NeedRideDB> {

    public NeedRideAdapter(Context context, int resource, List<NeedRideDB> objects) {

        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.need_ride_item, parent, false);
        }
        // Lookup views for data population
        TextView tv_needRide = (TextView) convertView.findViewById(R.id.tv_need_ride_item);
        NeedRideDB needRideDBItem = getItem(position);
        String requestRide;
        if (needRideDBItem.getName() == null) {
            requestRide = "X needs a ride to "
                    + needRideDBItem.getParkedLot() + " at " + needRideDBItem.getLeaveTime();
        }else{
            requestRide = needRideDBItem.getName() + " needs a ride to "
                + needRideDBItem.getParkedLot() + " at " + needRideDBItem.getLeaveTime();
        }
        tv_needRide.setText(requestRide);
        return convertView;
    }
}

