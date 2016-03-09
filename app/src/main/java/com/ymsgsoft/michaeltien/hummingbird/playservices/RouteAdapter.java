package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.R;
import com.ymsgsoft.michaeltien.hummingbird.data.RouteColumns;

/**
 * Created by Michael Tien on 2016/2/19.
 */
public class RouteAdapter extends CursorAdapter {
    public long selectedRouteId;
    public RouteAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final RouteAdapter.RouteHolder mHolder = (RouteAdapter.RouteHolder) view.getTag();
        mHolder.departTime.setText( cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DEPART_TIME)));
        mHolder.duration.setText( cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DURATION)));
        mHolder.transitNo.setText(cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_TRANSIT_NO)));
        mHolder.routeId = cursor.getInt(cursor.getColumnIndex(RouteColumns.ID));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.list_item_routes, parent, false);
        RouteAdapter.RouteHolder mHolder = new RouteAdapter.RouteHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }
    public class RouteHolder {
        ImageView imageIcon;
        TextView departTime;
        TextView transitNo;
        TextView duration;
        public long routeId;

        public RouteHolder(View view) {
            imageIcon = (ImageView) view.findViewById(R.id.list_item_image_Icon);
            departTime = (TextView) view.findViewById(R.id.list_item_depart_time);
            transitNo = (TextView) view.findViewById(R.id.list_item_transit_no);
            duration = (TextView) view.findViewById(R.id.list_item_duration);
        }
    }
}
