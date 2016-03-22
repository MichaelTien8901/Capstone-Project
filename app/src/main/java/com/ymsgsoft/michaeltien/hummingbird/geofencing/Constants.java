package com.ymsgsoft.michaeltien.hummingbird.geofencing;

import com.google.android.gms.location.Geofence;

/**
 * Created by Michael Tien on 2016/3/21.
 */
public final class Constants {
    private Constants() {
    }

//    // Request code to attempt to resolve Google Play services connection failures.
//    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
//    // Timeout for making a connection to GoogleApiClient (in milliseconds).
//    public static final long CONNECTION_TIME_OUT_MS = 100;

    // An app with dynamically-created geofences would want to include a reasonable expiration time.
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE;
    public static final float GEOFENCE_RADIUS_IN_METERS = 15; // 15m
    public static final int NAVIGATION_NOTIFY_ID = 198; //
    public static final String NAVIGATION_MESSAGE = "com.ymsgsoft.michaeltien.hummingbird.NAVIGATION_GEOFENCING";
}
