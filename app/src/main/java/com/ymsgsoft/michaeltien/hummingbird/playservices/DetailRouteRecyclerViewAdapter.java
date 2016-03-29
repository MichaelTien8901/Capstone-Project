package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.OnStepItemClickListener;
import com.ymsgsoft.michaeltien.hummingbird.R;
import com.ymsgsoft.michaeltien.hummingbird.data.StepColumns;

public class DetailRouteRecyclerViewAdapter extends CursorRecyclerAdapter<DetailRouteRecyclerViewAdapter.ViewHolder> {
    protected int mLayout;
    protected Context mContext;
    protected OnStepItemClickListener mListener;
    public DetailRouteRecyclerViewAdapter(Context context, int layout, Cursor c, OnStepItemClickListener listener) {
        super(c);
        mLayout = layout;
        mContext = context;
        mListener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(mLayout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, Cursor cursor) {
        StepData data = holder.bindData(cursor);
        holder.mInstruction.setText(data.instruction);
        holder.mDurationView.setText(data.durationText);
        if ( data.travalMode.equals("WALKING")) {
            holder.mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_walk));
            holder.mTravelMode.setText("");
            holder.mTravelMode.setVisibility(View.INVISIBLE);
        }
        else if (data.travalMode.equals("TRANSIT")) {
            holder.mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_bus));
            holder.mTravelMode.setVisibility(View.VISIBLE);
            holder.mTravelMode.setText(data.transitNo);
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
        public long mStepId;
        public StepData mItem;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mView = itemView;
            mInstruction = (TextView) itemView.findViewById(R.id.item_detail_instruction);
            mTravelMode = (TextView) itemView.findViewById(R.id.item_detail_travel_mode);
            mDurationView = (TextView) itemView.findViewById(R.id.item_detail_duration);
            mIcon = (ImageView) itemView.findViewById(R.id.item_detail_step_icon);
        }
        public StepData bindData(Cursor cursor) {
            mItem = new StepData();
            mItem.stepId = cursor.getLong(cursor.getColumnIndex(StepColumns.ID));
            mItem.instruction = cursor.getString(cursor.getColumnIndex(StepColumns.INSTRUCTION));
            mItem.polylinePoints = cursor.getString(cursor.getColumnIndex(StepColumns.POLYLINE));
            mItem.durationText = cursor.getString(cursor.getColumnIndex(StepColumns.DURATION_TEXT));
            mItem.travalMode = cursor.getString(cursor.getColumnIndex(StepColumns.TRAVEL_MODE));
            mItem.transitNo = cursor.getString(cursor.getColumnIndex(StepColumns.TRANSIT_NO));
            mItem.startLat = cursor.getDouble(cursor.getColumnIndex(StepColumns.START_LAT));
            mItem.startLng = cursor.getDouble(cursor.getColumnIndex(StepColumns.START_LNG));
            mItem.endLat = cursor.getDouble(cursor.getColumnIndex(StepColumns.END_LAT));
            mItem.endLng = cursor.getDouble(cursor.getColumnIndex(StepColumns.END_LNG));
            return mItem;
        }
        @Override
        public void onClick(View v) {
            if ( mListener != null)
                mListener.OnItemClick( mItem, getAdapterPosition());
        }
    }
}
