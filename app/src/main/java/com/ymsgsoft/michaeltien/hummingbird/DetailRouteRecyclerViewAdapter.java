package com.ymsgsoft.michaeltien.hummingbird;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.data.LegColumns;
import com.ymsgsoft.michaeltien.hummingbird.dummy.DummyContent.DummyItem;
import com.ymsgsoft.michaeltien.hummingbird.playservices.CursorRecyclerAdapter;

public class DetailRouteRecyclerViewAdapter extends CursorRecyclerAdapter<DetailRouteRecyclerViewAdapter.ViewHolder> {
    private int mLayout;
    public DetailRouteRecyclerViewAdapter(int layout, Cursor c) {
        super(c);
        mLayout = layout;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, Cursor cursor) {
        holder.mStartAddressView.setText(cursor.getString(cursor.getColumnIndex(LegColumns.START_ADDRESS)));
        holder.mDepartureTimeView.setText(cursor.getString(cursor.getColumnIndex(LegColumns.DEPARTURE_TIME_TEXT)));
        holder.mDurationView.setText(cursor.getString(cursor.getColumnIndex(LegColumns.DURATION_TEXT)));
    }
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
//
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
//    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mStartAddressView;
        public final TextView mDepartureTimeView;
        public final TextView mDurationView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mStartAddressView = (TextView) view.findViewById(R.id.item_detail_start_address);
            mDepartureTimeView = (TextView) view.findViewById(R.id.item_detail_departure_time);
            mDurationView = (TextView) view.findViewById(R.id.item_detail_duration);
        }

    }
}
