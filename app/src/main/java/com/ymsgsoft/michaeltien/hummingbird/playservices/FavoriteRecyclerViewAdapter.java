package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.OnFavoriteItemClickListener;
import com.ymsgsoft.michaeltien.hummingbird.R;
import com.ymsgsoft.michaeltien.hummingbird.data.FavoriteColumns;

import java.text.DateFormat;

/**
 * Created by Michael Tien on 2016/3/28.
 */
public class FavoriteRecyclerViewAdapter extends CursorRecyclerAdapter<FavoriteRecyclerViewAdapter.ViewHolder>  {
    protected int mLayout;
    protected Context mContext;
    protected OnFavoriteItemClickListener mListener;
    public FavoriteRecyclerViewAdapter(Context context, int layout, Cursor c, OnFavoriteItemClickListener listener) {
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
        FavoriteObject data = holder.bindData(cursor);
        holder.mIdView.setText(data.id_name);
        String time_formatted = DateFormat.getDateTimeInstance().format(data.query_time*1000);
        holder.mDateTimeView.setText(time_formatted);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mIdView;
        public final TextView mDateTimeView;
        public FavoriteObject mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mView = itemView;
            mIdView = (TextView) itemView.findViewById(R.id.favor_list_item_id_name);
            mDateTimeView = (TextView) itemView.findViewById(R.id.favor_list_item_departure_time);
        }
        public FavoriteObject bindData(Cursor cursor) {
            mItem = new FavoriteObject();
            mItem.id_name = cursor.getString(cursor.getColumnIndex(FavoriteColumns.ID_NAME));
            mItem.routeId = cursor.getLong(cursor.getColumnIndex(FavoriteColumns.ROUTES_ID));
            mItem.start_name = cursor.getString(cursor.getColumnIndex(FavoriteColumns.START_NAME));
            mItem.start_place_id = cursor.getString(cursor.getColumnIndex(FavoriteColumns.START_PLACE_ID));
            mItem.end_name   = cursor.getString(cursor.getColumnIndex(FavoriteColumns.END_NAME));
            mItem.end_place_id = cursor.getString(cursor.getColumnIndex(FavoriteColumns.END_PLACE_ID));
            mItem.query_time = cursor.getLong(cursor.getColumnIndex(FavoriteColumns.QUERY_TIME));
            return mItem;
        }
        @Override
        public void onClick(View v) {
            if ( mListener != null)
                mListener.OnItemClick( mItem, getAdapterPosition());
        }
    }
    public class FavoriteObject {
        public String id_name;
        public String start_name;
        public String start_place_id;
        public String end_name;
        public String end_place_id;
        public long routeId;
        public long query_time;
    }
}
