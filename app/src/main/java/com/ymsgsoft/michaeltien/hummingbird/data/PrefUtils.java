package com.ymsgsoft.michaeltien.hummingbird.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
}
