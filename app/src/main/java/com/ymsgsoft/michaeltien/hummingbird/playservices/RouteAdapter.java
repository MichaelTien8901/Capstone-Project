package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Leg;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Route;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Step;
import com.ymsgsoft.michaeltien.hummingbird.R;

import java.util.List;

/**
 * Created by Michael Tien on 2016/2/19.
 */
public class RouteAdapter extends ArrayAdapter<Route> {
        Context context;
        int layoutResId;
        List<Route> data = null;

    public RouteAdapter(Context context, int resource, List<Route> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResId = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RouteHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);
            holder = new RouteHolder();
            holder.imageIcon = (ImageView)convertView.findViewById(R.id.list_item_image_Icon);
            holder.departTime = (TextView) convertView.findViewById(R.id.list_item_depart_time);
            holder.transitNo = (TextView) convertView.findViewById(R.id.list_item_transit_no);
            holder.duration = (TextView) convertView.findViewById(R.id.list_item_duration);
            convertView.setTag(holder);
        } else {
            holder = (RouteHolder) convertView.getTag();
        }
        Route routeObject = data.get(position);
        Boolean break_flag1 = false;
        for ( int i = 0; i < routeObject.legs.size() && !break_flag1; i++) {
            Leg legObject = routeObject.legs.get(i);
            holder.departTime.setText(legObject.departure_time.text);
            holder.duration.setText(legObject.duration.text);
            Boolean break_flag2 = false;
            for ( int j = 0; j < legObject.steps.size() & !break_flag2; j ++ ) {
                Step step = legObject.steps.get(j);
                if ( step.travel_mode.equals("TRANSIT")) {
                    holder.transitNo.setText(step.transit_details.line.short_name);
                    break_flag2 = true;
                    break_flag1 = true;
                }
            }
        }
        return convertView;
    }
}
class RouteHolder {
    ImageView imageIcon;
    TextView departTime;
    TextView transitNo;
    TextView duration;
}