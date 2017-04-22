package com.example.android.hawkpark01;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;


/**
 * Created by priya on 4/21/2017.
 */

public class HomeLotAdapter extends ArrayAdapter<HomeLotItem> {

    public HomeLotAdapter(Context context, int resource, List<HomeLotItem> objects) {

        super(context, resource, objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.home_lot_item, parent, false);
        }
        TextView tv_lotName = (TextView) convertView.findViewById(R.id.tv_lot_item);
        HomeLotItem lotItem = getItem(position);

        tv_lotName.setText(lotItem.getName());
        int lotStatus = Integer.parseInt(lotItem.getStatus());
        switch (lotStatus)
        {
            case 1://lot is full
                convertView.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.round_corner_red));
                break;
            case 2://lot half full
                convertView.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.round_corner_yellow));
                break;
            case 3://lot is empty
                convertView.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.round_corner_green));
                break;
        }
    return convertView;
    }
}
