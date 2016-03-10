package com.ymsgsoft.michaeltien.hummingbird;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ymsgsoft.michaeltien.hummingbird.data.StepColumns;
import com.ymsgsoft.michaeltien.hummingbird.playservices.CursorRecyclerAdapter;

public class DetailRouteRecyclerViewAdapter extends CursorRecyclerAdapter<DetailRouteRecyclerViewAdapter.ViewHolder> {
    private int mLayout;
    protected Context mContext;
    public DetailRouteRecyclerViewAdapter(Context context, int layout, Cursor c) {
        super(c);
        mLayout = layout;
        mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(mLayout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, Cursor cursor) {
        holder.bindData(cursor);
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

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mView = itemView;
            mInstruction = (TextView) itemView.findViewById(R.id.item_detail_instruction);
            mTravelMode = (TextView) itemView.findViewById(R.id.item_detail_travel_mode);
            mDurationView = (TextView) itemView.findViewById(R.id.item_detail_duration);
            mIcon = (ImageView) itemView.findViewById(R.id.item_detail_step_icon);
        }
        public void bindData(Cursor cursor){
            mStepId = cursor.getLong(cursor.getColumnIndex(StepColumns.ID));
            mInstruction.setText(cursor.getString(cursor.getColumnIndex(StepColumns.INSTRUCTION)));
//            mTravelMode.setText(cursor.getString(cursor.getColumnIndex(StepColumns.TRAVEL_MODE)));
            mDurationView.setText(cursor.getString(cursor.getColumnIndex(StepColumns.DURATION_TEXT)));
            String travel_mode = cursor.getString(cursor.getColumnIndex(StepColumns.TRAVEL_MODE));
            if ( travel_mode.equals("WALKING")) {
                mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_walk));
                mTravelMode.setText("");
                mTravelMode.setVisibility(View.INVISIBLE);
            }
            else if (travel_mode.equals("TRANSIT")) {
                mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_directions_bus));
                mTravelMode.setVisibility(View.VISIBLE);
                mTravelMode.setText(cursor.getString(cursor.getColumnIndex(StepColumns.TRANSIT_NO)));
            }
        }

        @Override
        public void onClick(View v) {
            // mStepId is the current stepId
            String msg = "position = " + getPosition() + " stepID = " + mStepId;
            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
