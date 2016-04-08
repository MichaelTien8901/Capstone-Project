package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by Michael Tien on 2016/2/17.
 */
public class PlaceObject implements Parcelable {
    public String title;
    public String placeId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.placeId);
    }

    public PlaceObject() {
    }
    public PlaceObject(String aTitle, String aPlaceId) {
        title = aTitle;
        placeId = aPlaceId;
    }
    protected PlaceObject(Parcel in) {
        this.title = in.readString();
        this.placeId = in.readString();
    }

    public static final Parcelable.Creator<PlaceObject> CREATOR = new Parcelable.Creator<PlaceObject>() {
        public PlaceObject createFromParcel(Parcel source) {
            return new PlaceObject(source);
        }

        public PlaceObject[] newArray(int size) {
            return new PlaceObject[size];
        }
    };
    // add to serialize
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    static public PlaceObject create(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, PlaceObject.class);
    }
}
