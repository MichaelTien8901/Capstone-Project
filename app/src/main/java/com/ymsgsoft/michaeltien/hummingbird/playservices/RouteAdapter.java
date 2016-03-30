package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ymsgsoft.michaeltien.hummingbird.R;
import com.ymsgsoft.michaeltien.hummingbird.RouteParcelable;
import com.ymsgsoft.michaeltien.hummingbird.TransitNoView.TransitNoView;
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
        mHolder.bindData(cursor);
        mHolder.mTransitView.setDeartureTime(mHolder.mData.departTime);
        mHolder.mTransitView.setDuration(mHolder.mData.duration);
        mHolder.mTransitView.setTransitNo(mHolder.mData.transitNo);
//        mHolder.departTime.setText( mHolder.mData.departTime);
//        mHolder.duration.setText( mHolder.mData.duration);
//        mHolder.imageIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_bus));
//        String[] transits = mHolder.mData.transitNo.split(",");
//        if ( !transits[0].equals("null"))
//            mHolder.transitNo.setText(transits[0]);
//        else
//            mHolder.transitNo.setText("");
//        if ( transits.length > 1) {
//            for ( int i = 1; i < transits.length && i < 3; i ++) {
//                LinearLayout childView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_item_transit_no, null);
//                ImageView image = (ImageView) childView.findViewById(R.id.list_item_transit_icon1);
//                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_bus));
//                TextView textView = (TextView) childView.findViewById(R.id.list_item_transit_no1);
//                if ( !transits[i].equals("null"))
//                    textView.setText(transits[i]);
//                else
//                    textView.setText("");
//                mHolder.mContainer.addView(childView);
//            }
//        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        View mItem = LayoutInflater.from(context).inflate(R.layout.list_item_routes, parent, false);
        View mItem = LayoutInflater.from(context).inflate(R.layout.list_item_routes, parent, false);
        RouteAdapter.RouteHolder mHolder = new RouteAdapter.RouteHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }

    public class RouteHolder {
        TransitNoView mTransitView;
//        ImageView imageIcon;
//        TextView departTime;
//        TextView transitNo;
//        TextView duration;
//        LinearLayout mContainer;
        public RouteParcelable mData;
        public void bindData(Cursor cursor) {
            mData = new RouteParcelable();
            mData.routeId = cursor.getInt(cursor.getColumnIndex(RouteColumns.ID));
            mData.overviewPolyline = cursor.getString(cursor.getColumnIndex(RouteColumns.OVERVIEW_POLYLINES));
            mData.transitNo = cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_TRANSIT_NO));
            mData.departTime = cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DEPART_TIME));
            mData.duration = cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DURATION));
            mData.isFavorite = cursor.getInt(cursor.getColumnIndex(RouteColumns.IS_FAVORITE)) == 1;
            mData.deparTimeValue = cursor.getLong(cursor.getColumnIndex(RouteColumns.DEPART_TIME_VALUE));
        }
        public RouteHolder(View view) {
            mTransitView = (TransitNoView) view.findViewById(R.id.transit_no_view);
//            imageIcon = (ImageView) view.findViewById(R.id.list_item_route_icon);
//            departTime = (TextView) view.findViewById(R.id.list_item_depart_time);
//            transitNo = (TextView) view.findViewById(R.id.list_item_transit_no);
//            duration = (TextView) view.findViewById(R.id.list_item_duration);
//            mContainer = (LinearLayout) view.findViewById(R.id.list_item_routes_container);
        }
    }
}
