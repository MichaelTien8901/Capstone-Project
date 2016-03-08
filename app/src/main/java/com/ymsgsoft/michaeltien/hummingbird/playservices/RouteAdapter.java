package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ymsgsoft.michaeltien.hummingbird.R;
import com.ymsgsoft.michaeltien.hummingbird.data.RouteColumns;

/**
 * Created by Michael Tien on 2016/2/19.
 */
public class RouteAdapter extends CursorAdapter {
    public int selectedRouteId;
    public RouteAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final RouteHolder mHolder = (RouteHolder) view.getTag();
        mHolder.departTime.setText( cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DEPART_TIME)));
        mHolder.duration.setText( cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DURATION)));
        mHolder.transitNo.setText(cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_TRANSIT_NO)));
        mHolder.routeId = cursor.getInt(cursor.getColumnIndex(RouteColumns.ID));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.list_item_routes, parent, false);
        RouteHolder mHolder = new RouteHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }
}
