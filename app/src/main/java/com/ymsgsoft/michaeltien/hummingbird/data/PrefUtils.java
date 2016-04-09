package com.ymsgsoft.michaeltien.hummingbird.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ymsgsoft.michaeltien.hummingbird.PlaceObject;
import com.ymsgsoft.michaeltien.hummingbird.RouteParcelable;

/**
 * Created by Michael Tien on 2016/3/14.
 */
public class PrefUtils {
    static public void saveRouteParcelableToPref(Context context, String key, RouteParcelable data) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, data.serialize());
        editor.apply();
    }
    static public RouteParcelable restoreRouteParcelableFromPref( Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String serialData = preferences.getString(key, "");
        if ( serialData.isEmpty()) return null;
        return RouteParcelable.create(serialData);
    }
    static public void savePlaceParcelableToPref(Context context, String key, PlaceObject data) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, data.serialize());
        editor.apply();
    }
    static public PlaceObject restorePlaceParcelableFromPref( Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String serialData = preferences.getString(key, "");
        if ( serialData.isEmpty()) return null;
        return PlaceObject.create(serialData);
    }
    static public void setLocationRequestFlag(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, true );
        editor.apply();
    }
    static public void resetLocationRequestFlag(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, false );
        editor.apply();
    }
    static public boolean isLocationRequestFlag(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }
}
