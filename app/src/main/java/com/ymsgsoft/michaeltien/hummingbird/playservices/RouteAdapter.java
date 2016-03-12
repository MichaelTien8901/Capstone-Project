package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.R;
import com.ymsgsoft.michaeltien.hummingbird.data.RouteColumns;

/**
 * Created by Michael Tien on 2016/2/19.
 */
public class RouteAdapter extends CursorAdapter {
    public long selectedRouteId;
    private LayoutInflater inflater = null;
    public RouteAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View mItem = inflater.inflate(R.layout.list_item_routes, parent, false);
        RouteAdapter.RouteHolder mHolder = new RouteAdapter.RouteHolder(mItem);
        mItem.setTag(mHolder);
        Cursor c = getCursor();
        if (c != null) {
            c.moveToPosition(position);
            bindView(mItem, mContext, c);
        }
        return mItem;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final RouteAdapter.RouteHolder mHolder = (RouteAdapter.RouteHolder) view.getTag();
        mHolder.departTime.setText( cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DEPART_TIME)));
        mHolder.duration.setText( cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DURATION)));
        mHolder.routeId = cursor.getInt(cursor.getColumnIndex(RouteColumns.ID));
        mHolder.mOverviewPolyline = cursor.getString(cursor.getColumnIndex(RouteColumns.OVERVIEW_POLYLINES));
        mHolder.imageIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_bus));
        mHolder.transitNo.setText(cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_TRANSIT_NO)));
        String[] transits = cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_TRANSIT_NO)).split(",");
        mHolder.transitNo.setText(transits[0]);
        if ( transits.length > 1) {
            for ( int i = 1; i < transits.length; i ++) {
                LinearLayout childView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_item_transit_no, null);
//                LinearLayout childView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_item_transit_no, mHolder.mContainer);
                ImageView image = (ImageView) childView.findViewById(R.id.list_item_transit_icon1);
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_bus));
                TextView textView = (TextView) childView.findViewById(R.id.list_item_transit_no1);
                textView.setText(transits[i]);
                mHolder.mContainer.addView(childView);
            }
        }
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
        public String mOverviewPolyline;
        LinearLayout mContainer;
        public RouteHolder(View view) {
            imageIcon = (ImageView) view.findViewById(R.id.list_item_route_icon);
            departTime = (TextView) view.findViewById(R.id.list_item_depart_time);
            transitNo = (TextView) view.findViewById(R.id.list_item_transit_no);
            duration = (TextView) view.findViewById(R.id.list_item_duration);
            mContainer = (LinearLayout) view.findViewById(R.id.list_item_routes_container);
        }
    }
}
