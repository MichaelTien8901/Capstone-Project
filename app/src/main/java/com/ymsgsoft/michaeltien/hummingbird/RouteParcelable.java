package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael Tien on 2016/3/11.
 */
public class RouteParcelable implements Parcelable {
    public long routeId;
    public String overviewPolyline;
    public String transitNo;
    public String departTime;
    public String duration;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.routeId);
        dest.writeString(this.overviewPolyline);
        dest.writeString(this.transitNo);
        dest.writeString(this.departTime);
        dest.writeString(this.duration);
    }

    public RouteParcelable() {
    }

    protected RouteParcelable(Parcel in) {
        this.routeId = in.readLong();
        this.overviewPolyline = in.readString();
        this.transitNo = in.readString();
        this.departTime = in.readString();
        this.duration = in.readString();
    }

    public static final Parcelable.Creator<RouteParcelable> CREATOR = new Parcelable.Creator<RouteParcelable>() {
        public RouteParcelable createFromParcel(Parcel source) {
            return new RouteParcelable(source);
        }

        public RouteParcelable[] newArray(int size) {
            return new RouteParcelable[size];
        }
    };
}
