package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael Tien on 2016/3/13.
 */
public class StepParcelable implements Parcelable {
    public String instruction;
    public double start_lat, start_lng, end_lat, end_lng;
    public String distance_text, duration_text;
    public String polyline;
    public String travel_mode;
    public String transit_no;

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
        dest.writeString(this.polyline);
        dest.writeString(this.travel_mode);
        dest.writeString(this.transit_no);
    }

    public StepParcelable() {
    }

    protected StepParcelable(Parcel in) {
        this.instruction = in.readString();
        this.start_lat = in.readDouble();
        this.start_lng = in.readDouble();
        this.end_lat = in.readDouble();
        this.end_lng = in.readDouble();
        this.distance_text = in.readString();
        this.duration_text = in.readString();
        this.polyline = in.readString();
        this.travel_mode = in.readString();
        this.transit_no = in.readString();
    }

    public static final Parcelable.Creator<StepParcelable> CREATOR = new Parcelable.Creator<StepParcelable>() {
        public StepParcelable createFromParcel(Parcel source) {
            return new StepParcelable(source);
        }

        public StepParcelable[] newArray(int size) {
            return new StepParcelable[size];
        }
    };
}
