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
import static java.security.AccessController.getContext;

/**
 * Created by priya on 4/28/2017.
 */

public class NeedParkingAdapter extends ArrayAdapter<NeedParkingDB> {

    public NeedParkingAdapter(Context context, int resource, List<NeedParkingDB> objects) {

        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.need_parking_item, parent, false);
        }
        // Lookup views for data population
        TextView tv_needParking = (TextView) convertView.findViewById(R.id.tv_need_parking_item);
        NeedParkingDB needParkingDB = getItem(position);
        String requestParking =needParkingDB.getName() +  " arriving at " +
                needParkingDB.getArriveTime() + " looking for spot in "
                + needParkingDB.getLotPref1() ;

        tv_needParking.setText(requestParking);
        return convertView;
    }
}


