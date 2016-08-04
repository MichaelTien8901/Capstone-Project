package com.ymsgsoft.michaeltien.hummingbird;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.ymsgsoft.michaeltien.hummingbird.data.NavigateColumns;

/**
 * Created by Michael Tien on 2016/3/13.
 */
public class StepParcelable implements Parcelable {
    public String instruction;
    public double start_lat, start_lng, end_lat, end_lng;
    public String distance_text, duration_text;
    public int distance, duration;
    public String polyline;
    public String travel_mode;
    public String transit_no;
    public int level;
    public int count;
    public String arrival_stop;
    public String departure_stop;
    public int num_stops;

    public StepParcelable() {
    }

    static public StepParcelable readStepParcelable(Cursor data) {
        StepParcelable result = new StepParcelable();
        result.instruction = data.getString(data.getColumnIndex(NavigateColumns.INSTRUCTION));
        result.distance = data.getInt(data.getColumnIndex(NavigateColumns.DISTANCE));
        result.distance_text = data.getString(data.getColumnIndex(NavigateColumns.DISTANCE_TEXT));
        result.duration = data.getInt(data.getColumnIndex(NavigateColumns.DURATION));
        result.duration_text = data.getString(data.getColumnIndex(NavigateColumns.DURATION_TEXT));
        result.start_lat = data.getDouble(data.getColumnIndex(NavigateColumns.START_LAT));
        result.start_lng = data.getDouble(data.getColumnIndex(NavigateColumns.START_LNG));
        result.end_lat = data.getDouble(data.getColumnIndex(NavigateColumns.END_LAT));
        result.end_lng = data.getDouble(data.getColumnIndex(NavigateColumns.END_LNG));
        result.polyline = data.getString(data.getColumnIndex(NavigateColumns.POLYLINE));
        result.travel_mode = data.getString(data.getColumnIndex(NavigateColumns.TRAVEL_MODE));
        result.transit_no = data.getString(data.getColumnIndex(NavigateColumns.TRANSIT_NO));
        result.level = data.getInt(data.getColumnIndex(NavigateColumns.LEVEL));
        result.count = data.getInt(data.getColumnIndex(NavigateColumns.COUNT));
        result.arrival_stop = data.getString(data.getColumnIndex(NavigateColumns.ARRIVAL_STOP));
        result.departure_stop = data.getString(data.getColumnIndex(NavigateColumns.DEPARTURE_STOP));
        result.num_stops = data.getInt(data.getColumnIndex(NavigateColumns.NUM_STOPS));
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.instruction);
        dest.writeDouble(this.start_lat);
        dest.writeDouble(this.start_lng);
        dest.writeDouble(this.end_lat);
        dest.writeDouble(this.end_lng);
        dest.writeString(this.distance_text);
        dest.writeString(this.duration_text);
        dest.writeInt(this.distance);
        dest.writeInt(this.duration);
        dest.writeString(this.polyline);
        dest.writeString(this.travel_mode);
        dest.writeString(this.transit_no);
        dest.writeInt(this.level);
        dest.writeInt(this.count);
        dest.writeString(this.arrival_stop);
        dest.writeString(this.departure_stop);
        dest.writeInt(this.num_stops);
    }

    protected StepParcelable(Parcel in) {
        this.instruction = in.readString();
        this.start_lat = in.readDouble();
        this.start_lng = in.readDouble();
        this.end_lat = in.readDouble();
        this.end_lng = in.readDouble();
        this.distance_text = in.readString();
        this.duration_text = in.readString();
        this.distance = in.readInt();
        this.duration = in.readInt();
        this.polyline = in.readString();
        this.travel_mode = in.readString();
        this.transit_no = in.readString();
        this.level = in.readInt();
        this.count = in.readInt();
        this.arrival_stop = in.readString();
        this.departure_stop = in.readString();
        this.num_stops = in.readInt();
    }

    public static final Creator<StepParcelable> CREATOR = new Creator<StepParcelable>() {
        @Override
        public StepParcelable createFromParcel(Parcel source) {
            return new StepParcelable(source);
        }

        @Override
        public StepParcelable[] newArray(int size) {
            return new StepParcelable[size];
        }
    };
}
