package com.ymsgsoft.michaeltien.hummingbird.playservices;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.OnHistoryItemClickListener;
import com.ymsgsoft.michaeltien.hummingbird.R;
import com.ymsgsoft.michaeltien.hummingbird.data.HistoryColumns;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Michael Tien on 2016/4/1.
 */
public class HistoryRecyclerViewAdapter extends CursorRecyclerAdapter<HistoryRecyclerViewAdapter.ViewHolder>  {
    protected int mLayout;
    protected Context mContext;
    protected OnHistoryItemClickListener mListener;
    public HistoryRecyclerViewAdapter(Context context, int layout, Cursor c, OnHistoryItemClickListener listener) {
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
        HistoryObject data = holder.bindData(cursor);
        holder.mName.setText(data.place_name);
        DateFormat formatter = new SimpleDateFormat("MMM/dd/yyyy HH:mm");
        String time_formatted = formatter.format(data.query_time);
        holder.mTime.setText(time_formatted);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mName;
        public final TextView mTime;
        public HistoryObject mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mView = itemView;
            mName = (TextView) itemView.findViewById(R.id.history_list_item_place);
            mTime = (TextView) itemView.findViewById(R.id.history_list_item_time);
        }
        public HistoryObject bindData(Cursor cursor) {
            mItem = new HistoryObject();
            mItem.place_name = cursor.getString(cursor.getColumnIndex(HistoryColumns.PLACE_NAME));
            mItem.place_id = cursor.getString(cursor.getColumnIndex(HistoryColumns.PLACE_ID));
            mItem.query_time = cursor.getLong(cursor.getColumnIndex(HistoryColumns.QUERY_TIME));
            return mItem;
        }
        @Override
        public void onClick(View v) {
            if ( mListener != null)
                mListener.OnItemClick( mItem, getAdapterPosition());
        }
    }
    public class HistoryObject {
        public String place_name;
        public String place_id;
        public long query_time;
    }
}

