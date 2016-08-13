package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.OnStepItemClickListener;
import com.ymsgsoft.michaeltien.hummingbird.R;
import com.ymsgsoft.michaeltien.hummingbird.data.StepColumns;

public class DetailRouteRecyclerViewAdapter extends CursorRecyclerAdapter<DetailRouteRecyclerViewAdapter.ViewHolder> {
    private final int WALKING = 0, TRANSIT = 1;
    protected int mLayout;
    protected int mLayoutTransit;
    protected Context mContext;
    protected OnStepItemClickListener mListener;
    public DetailRouteRecyclerViewAdapter(Context context, int layout, int layout_transit, Cursor c, OnStepItemClickListener listener) {
        super(c);
        mLayout = layout;
        mLayoutTransit = layout_transit;
        mContext = context;
        mListener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType) {
            default:
                View itemView1 = LayoutInflater.from(parent.getContext())
                        .inflate(mLayout, parent, false);
                return new ViewHolder(itemView1);
            case TRANSIT:
                View itemView2 = LayoutInflater.from(parent.getContext())
                        .inflate(mLayoutTransit, parent, false);
                return new ViewHolder2(itemView2);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if ( this.getCursor() != null ) {
            Cursor cursor = this.getCursor();
            cursor.moveToPosition(position);
            String travelMode = cursor.getString(cursor.getColumnIndex(StepColumns.TRAVEL_MODE));
            String arrivalStop = cursor.getString(cursor.getColumnIndex(StepColumns.ARRIVAL_STOP));
            if (travelMode.equals("TRANSIT") && arrivalStop != null) {
                return TRANSIT;
            }
        }
        return WALKING;
    }
    @Override
    public void onBindViewHolder (final ViewHolder holder, Cursor cursor) {
        StepData data = holder.bindData(cursor);
        holder.mInstruction.setText(data.instruction);
        holder.mDurationView.setText(data.durationText);
        if ( data.travelMode.equals("WALKING")) {
            holder.mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_walk));
            holder.mTravelMode.setText("");
            holder.mTravelMode.setVisibility(View.INVISIBLE);
        }
        else if (data.travelMode.equals("TRANSIT")) {
            holder.mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_bus));
            holder.mTravelMode.setVisibility(View.VISIBLE);
            holder.mTravelMode.setText(data.transitNo);
            String instruction = data.instruction;
            if ( data.arrivalStop != null) {
                ViewHolder2 holder2 = (ViewHolder2) holder;
                holder2.mTransitInfo.setVisibility(View.VISIBLE);
                holder2.mArrivalStop.setText(data.arrivalStop);
                holder2.mDepartureStop.setText(data.departureStop);
                if ( data.numStops != 0 ) {
                    String STOP;
                    STOP = data.numStops == 1 ?
                            mContext.getResources().getString(R.string.bus_stop):
                            mContext.getResources().getString(R.string.bus_stops);
                    instruction += "\n" + data.numStops + STOP;
                }
            }
            holder.mInstruction.setText(instruction);
        }
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mInstruction;
        public final TextView mTravelMode;
        public final TextView mDurationView;
        public final ImageView mIcon;
//        public final LinearLayout mTransitInfo;
//        public final TextView mDepartureStop;
//        public final TextView mArrivalStop;
//        public long mStepId;
        public StepData mItem;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mView = itemView;
            mInstruction = (TextView) itemView.findViewById(R.id.item_detail_instruction);
            mTravelMode = (TextView) itemView.findViewById(R.id.item_detail_travel_mode);
            mDurationView = (TextView) itemView.findViewById(R.id.item_detail_duration);
            mIcon = (ImageView) itemView.findViewById(R.id.item_detail_step_icon);
//            mTransitInfo = (LinearLayout) itemView.findViewById(R.id.item_detail_transit_info );
//            mDepartureStop = (TextView) itemView.findViewById(R.id.item_detail_transit_departure_stop);
//            mArrivalStop = (TextView) itemView.findViewById(R.id.item_detail_transit_arrival_stop);

        }
        public StepData bindData(Cursor cursor) {
            mItem = new StepData();
            mItem.stepId = cursor.getLong(cursor.getColumnIndex(StepColumns.ID));
            mItem.instruction = cursor.getString(cursor.getColumnIndex(StepColumns.INSTRUCTION));
            mItem.polylinePoints = cursor.getString(cursor.getColumnIndex(StepColumns.POLYLINE));
            mItem.durationText = cursor.getString(cursor.getColumnIndex(StepColumns.DURATION_TEXT));
            mItem.travelMode = cursor.getString(cursor.getColumnIndex(StepColumns.TRAVEL_MODE));
            mItem.transitNo = cursor.getString(cursor.getColumnIndex(StepColumns.TRANSIT_NO));
            mItem.startLat = cursor.getDouble(cursor.getColumnIndex(StepColumns.START_LAT));
            mItem.startLng = cursor.getDouble(cursor.getColumnIndex(StepColumns.START_LNG));
            mItem.endLat = cursor.getDouble(cursor.getColumnIndex(StepColumns.END_LAT));
            mItem.endLng = cursor.getDouble(cursor.getColumnIndex(StepColumns.END_LNG));
            mItem.arrivalStop = cursor.getString(cursor.getColumnIndex(StepColumns.ARRIVAL_STOP));
            mItem.departureStop = cursor.getString(cursor.getColumnIndex(StepColumns.DEPARTURE_STOP));
            mItem.numStops = cursor.getLong(cursor.getColumnIndex(StepColumns.NUM_STOPS));
            return mItem;
        }
        @Override
        public void onClick(View v) {
            if ( mListener != null)
                mListener.OnItemClick( mItem, getAdapterPosition());
        }
    }
    public class ViewHolder2 extends ViewHolder {
        public final LinearLayout mTransitInfo;
        public final TextView mDepartureStop;
        public final TextView mArrivalStop;
        public ViewHolder2(View itemView) {
            super(itemView);
            mTransitInfo = (LinearLayout) itemView.findViewById(R.id.item_detail_transit_info );
            mDepartureStop = (TextView) itemView.findViewById(R.id.item_detail_transit_departure_stop);
            mArrivalStop = (TextView) itemView.findViewById(R.id.item_detail_transit_arrival_stop);
        }
        @Override
        public StepData bindData(Cursor cursor) {
            mItem = super.bindData(cursor);
            mItem.arrivalStop = cursor.getString(cursor.getColumnIndex(StepColumns.ARRIVAL_STOP));
            mItem.departureStop = cursor.getString(cursor.getColumnIndex(StepColumns.DEPARTURE_STOP));
            mItem.numStops = cursor.getLong(cursor.getColumnIndex(StepColumns.NUM_STOPS));
            return mItem;
        }
    }
}
