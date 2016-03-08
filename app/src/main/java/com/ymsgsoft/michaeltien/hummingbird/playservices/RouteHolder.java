package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.R;

/**
 * Created by Michael Tien on 2016/3/7.
 */
public class RouteHolder {
    ImageView imageIcon;
    TextView departTime;
    TextView transitNo;
    TextView duration;
    public int routeId;
    public RouteHolder(View view) {
        imageIcon = (ImageView) view.findViewById(R.id.list_item_image_Icon);
        departTime = (TextView) view.findViewById(R.id.list_item_depart_time);
        transitNo = (TextView) view.findViewById(R.id.list_item_transit_no);
        duration = (TextView) view.findViewById(R.id.list_item_duration);
    }
}