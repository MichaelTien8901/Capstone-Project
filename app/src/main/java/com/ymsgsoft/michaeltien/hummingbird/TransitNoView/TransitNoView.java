package com.ymsgsoft.michaeltien.hummingbird.TransitNoView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.R;


/**
 * Created by Michael Tien on 2016/3/29.
 */
public class TransitNoView extends LinearLayout {
    private String mTransitNumbers;
    int mIconSrc; // icon resource
    int mMaxIcons;
    private String mDepartureTime;
    private String mDuration;

    private final LinearLayout mDetail_title_container;
    private TextView mDepartureTextView;
    private TextView mDurationTextView;
    private TextView mTransitNoView;
    public TransitNoView(Context context, AttributeSet attrs){
        super(context, attrs);
        int layout; // layout resource
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TransitNoView,
                0, 0);
        try {
            mTransitNumbers = a.getString(R.styleable.TransitNoView_transit_numbers);
            mDepartureTime = a.getString(R.styleable.TransitNoView_departure);
            mDuration = a.getString(R.styleable.TransitNoView_duration);
            layout = a.getResourceId(R.styleable.TransitNoView_layout_file, 0);
            mIconSrc = a.getResourceId(R.styleable.TransitNoView_res_icon, 0);
            mMaxIcons = a.getInteger(R.styleable.TransitNoView_max_icons, 3);
        } finally {
            a.recycle();
        }
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(layout, this, true);
        mDepartureTextView = (TextView) findViewById(R.id.detail_depart_time);
        mDepartureTextView.setText(mDepartureTime);

        mDurationTextView = (TextView) findViewById(R.id.detail_duration);
        mDurationTextView.setText(mDuration);
        mTransitNoView = (TextView) findViewById(R.id.detail_transit_no2);
        ImageView image = (ImageView) findViewById(R.id.list_item_transit_icon2);
        image.setImageDrawable(getResources().getDrawable(mIconSrc));
        String[] transits = mTransitNumbers.split(",");
        if ( !transits[0].equals("null"))
            mTransitNoView.setText(transits[0]);
        else
            mTransitNoView.setText("");
        // create rest of bus number
        mDetail_title_container = (LinearLayout) findViewById(R.id.list_detail_title);
        if ( transits.length > 1 ) {
            for (int i = 1; i < transits.length && i < mMaxIcons; i++) {
                View childView = mInflater.inflate(R.layout.list_item_transit_no, null);
                ImageView image1 = (ImageView) childView.findViewById(R.id.list_item_transit_icon1);
                image1.setImageDrawable(getResources().getDrawable(mIconSrc));
                TextView textView = (TextView) childView.findViewById(R.id.list_item_transit_no1);
                if (!transits[i].equals("null"))
                    textView.setText(transits[i]);
                else
                    textView.setText("");
                mDetail_title_container.addView(childView);
            }
        }
    }
    private void refreshView() {
        String[] transits = mTransitNumbers.split(",");
        if ( !transits[0].equals("null"))
            mTransitNoView.setText(transits[0]);
        else
            mTransitNoView.setText("");
        boolean doBreak = false;
        while (!doBreak) {
            int count = mDetail_title_container.getChildCount();
            int i;
            for ( i = 0;i < count; i ++ ) {
                View view = mDetail_title_container.getChildAt(i);
                int resId = view.getId();
                if ( resId != R.id.detail_transit_no2 && resId != R.id.list_item_transit_icon2 ) {
                    mDetail_title_container.removeView(view);
                    break;
                }
            }
            if ( i == count) doBreak = true;
        }
        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if ( transits.length > 1 ) {
            for (int i = 1; i < transits.length && i < mMaxIcons; i++) {
                View childView = mInflater.inflate(R.layout.list_item_transit_no, null);
                ImageView image1 = (ImageView) childView.findViewById(R.id.list_item_transit_icon1);
                image1.setImageDrawable(getResources().getDrawable(mIconSrc));
                TextView textView = (TextView) childView.findViewById(R.id.list_item_transit_no1);
                if (!transits[i].equals("null"))
                    textView.setText(transits[i]);
                else
                    textView.setText("");
                mDetail_title_container.addView(childView);
            }
        }
    }
    public void setTransitNo(String transitNos) {
        mTransitNumbers = transitNos;
        refreshView();
    }
    public void setDeartureTime(String deartureTime) {
        mDepartureTime = deartureTime;
        mDepartureTextView.setText(mDepartureTime);
    }
    public void setDuration(String duration) {
        mDuration = duration;
        mDurationTextView.setText(mDuration);
    }
}
