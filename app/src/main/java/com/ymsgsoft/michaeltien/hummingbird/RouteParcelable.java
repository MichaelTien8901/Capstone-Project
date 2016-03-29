package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by Michael Tien on 2016/3/11.
 */
public class RouteParcelable implements Parcelable {
    public long routeId;
    public String overviewPolyline;
    public String transitNo;
    public String departTime;
    public String duration;
    public long deparTimeValue;
    public Boolean isFavorite;

    public RouteParcelable() {
    }

    public RouteParcelable(long routeId,
            String overviewPolyline,
            String transitNo,
            String departTime,
            String duration,
            Boolean isFavorite,
            long deparTimeValue) {
        this.routeId = routeId;
        this.overviewPolyline = overviewPolyline;
        this.transitNo = transitNo;
        this.departTime = departTime;
        this.duration = duration;
        this.isFavorite = isFavorite;
        this.deparTimeValue = deparTimeValue;
    }

    public class RoutePojo {
        public long routeId;
        public String overviewPolyline;
        public String transitNo;
        public String departTime;
        public String duration;
        public Boolean isFavorite;
        public long deparTimeValue;
        public RoutePojo( RouteParcelable p) {
            this.routeId = p.routeId;
            this.overviewPolyline = p.overviewPolyline;
            this.transitNo = p.transitNo;
            this.departTime = p.departTime;
            this.duration = p.duration;
            this.isFavorite = p.isFavorite;
            this.deparTimeValue = p.deparTimeValue;
        }
        public String serialize() {
            // Serialize this class into a JSON string using GSON
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
    public String serialize() {
        RoutePojo pojo = new RoutePojo(this);
        return pojo.serialize();
    }
    static public RouteParcelable create(String serializedData) {
        Gson gson = new Gson();
        RoutePojo pojo = gson.fromJson(serializedData, RoutePojo.class);
        return new RouteParcelable( pojo.routeId,
                pojo.overviewPolyline,
                pojo.transitNo,
                pojo.departTime,
                pojo.duration,
                pojo.isFavorite,
                pojo.deparTimeValue);
    }

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
        dest.writeLong(this.deparTimeValue);
        dest.writeValue(this.isFavorite);
    }

    protected RouteParcelable(Parcel in) {
        this.routeId = in.readLong();
        this.overviewPolyline = in.readString();
        this.transitNo = in.readString();
        this.departTime = in.readString();
        this.duration = in.readString();
        this.deparTimeValue = in.readLong();
        this.isFavorite = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<RouteParcelable> CREATOR = new Creator<RouteParcelable>() {
        @Override
        public RouteParcelable createFromParcel(Parcel source) {
            return new RouteParcelable(source);
        }

        @Override
        public RouteParcelable[] newArray(int size) {
            return new RouteParcelable[size];
        }
    };
}
