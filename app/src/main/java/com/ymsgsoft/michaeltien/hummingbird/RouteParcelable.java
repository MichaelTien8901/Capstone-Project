package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Parcel;
import android.os.Parcelable;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Route;

/**
 * Created by Michael Tien on 2016/2/20.
 */
public class RouteParcelable implements Parcelable {
    public Route route;
    public String Summary;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeParcelable(this.route, flags);
        dest.writeString(this.Summary);
    }

    public RouteParcelable() {
    }

    protected RouteParcelable(Parcel in) {
        this.route = in.readParcelable(Route.class.getClassLoader());
        this.Summary = in.readString();
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
