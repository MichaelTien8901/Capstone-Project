package com.ymsgsoft.michaeltien.hummingbird;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.ymsgsoft.michaeltien.hummingbird.data.NavigateColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.data.StepColumns;
import com.ymsgsoft.michaeltien.hummingbird.geofencing.Constants;
import com.ymsgsoft.michaeltien.hummingbird.geofencing.GeofenceErrorMessages;
import com.ymsgsoft.michaeltien.hummingbird.geofencing.GeofenceTransitionsIntentService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavigateActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        ResultCallback<Status> {
    static final String TAG = NavigateActivity.class.getSimpleName();
    static final String NAVIGATION_TAG = "com.ymsgsoft.michaeltien.hummingbird.navigation_fragment";
    static final int REQUEST_LOCATION = 103;
    public static final int NAVIGATION_LOADER =10;

//    private GoogleMap mMap;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
//    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected final static String NAVIGATION_POS_KEY = "navigation_pos_key";

    /**
        * Provides the entry point to Google Play services.
        */
    protected GoogleApiClient mGoogleApiClient;
    /**
        * Stores parameters for requests to the FusedLocationProviderApi.
        */
    protected LocationRequest mLocationRequest;
    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    protected String mLastUpdateTime;
    protected boolean mRequestingLocationUpdates = true;
    protected RouteParcelable mRouteObject;
    protected Fragment mFragment;
    protected Cursor mCursor;
    protected int mCursorPosition;
    protected ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    Boolean myReceiverIsRegistered = false;
    GeofenceReceiver mReceiver = null;
    @Bind(R.id.fab_navigation_forward)
    FloatingActionButton mForwardButton;
    @Bind(R.id.fab_navigation_backward)
    FloatingActionButton mBackwardButton;
    @Bind(R.id.fab_navigation_mode)
    FloatingActionButton mModeButton;
    @Bind(R.id.fab_streetview)
    FloatingActionButton mStreetviewButton;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final String ARG_ROUTE_KEY_ID = getString(R.string.intent_route_key);
        if ( mRouteObject != null)
            outState.putParcelable(ARG_ROUTE_KEY_ID, mRouteObject);
        if ( mCursor != null)
            outState.putInt(NAVIGATION_POS_KEY, mCursor.getPosition());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_navigation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mReceiver = new GeofenceReceiver(this);
        mGeofenceList = new ArrayList<Geofence>();
        mModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mFragment).fabMyLocationPressed();
            }
        });
        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationForward();
            }
        });
        mBackwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationBackward();
            }
        });

        final String ARG_ROUTE_KEY_ID = getString(R.string.intent_route_key);
        if ( savedInstanceState == null) {
            mRouteObject = getIntent().getParcelableExtra(ARG_ROUTE_KEY_ID);
            mCursorPosition = 0;
            Bundle arguments = new Bundle();
            arguments.putParcelable(ARG_ROUTE_KEY_ID, mRouteObject);

            mFragment = new NavigationFragment();
            mFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_navigation_container, mFragment, NAVIGATION_TAG)
                    .commit();
        } else {
            mFragment = getSupportFragmentManager().findFragmentByTag(NAVIGATION_TAG);
            mRouteObject = savedInstanceState.getParcelable(ARG_ROUTE_KEY_ID);

        }
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
        // start loader
        getSupportLoaderManager().initLoader(NAVIGATION_LOADER, null, NavigateActivity.this);
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
//            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
//                mRequestingLocationUpdates = savedInstanceState.getBoolean(
//                        REQUESTING_LOCATION_UPDATES_KEY);
//                setButtonsEnabledState();
//            }
            if ( savedInstanceState.containsKey(NAVIGATION_POS_KEY)) {
                mCursorPosition = savedInstanceState.getInt(NAVIGATION_POS_KEY, 0);
            }
            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

//            // Update the value of mLastUpdateTime from the Bundle and update the UI.
//            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
//                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
//            }
//            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }
    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!myReceiverIsRegistered) {
            registerReceiver(mReceiver, new IntentFilter(Constants.NAVIGATION_MESSAGE));
            myReceiverIsRegistered = true;
        }}

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        if (myReceiverIsRegistered) {
            unregisterReceiver(mReceiver);
            myReceiverIsRegistered = false;
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            } else {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//            updateUI();
                if ( mCursor != null) {
                    // already loaded
                    createGeofenceFunction();
                }
            }
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        updateUI();
        Toast.makeText(this, location.toString(),
                Toast.LENGTH_SHORT).show();
        if ( mFragment instanceof Callback) {
            ((Callback) mFragment).locationUpdate(location);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if ( mCursor != null)
            mCursor.close();
        mCursor = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                RoutesProvider.Navigates.CONTENT_URI,
                null,
                NavigateColumns.ROUTES_ID + "=?",
                new String[]{String.valueOf(mRouteObject.routeId)},
                StepColumns.ID + " ASC");
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if ( mCursor != null)
            mCursor.close();
        mCursor = data;
        if ( mCursor == null) return;
        // start navigation geofencing
        mCursor.moveToPosition(mCursorPosition);
        adjustPosition(mCursor);
        completeStepUpdate();
        // popular geofencing point
        if (mGoogleApiClient.isConnected()) {
            createGeofenceFunction();
        }
    }
    void adjustPosition(Cursor cursor) {
        StepParcelable navStep = StepParcelable.readStepParcelable(cursor);
        if ( navStep.level == 0 && navStep.count != 0) {
            cursor.moveToNext();
        }
    }
    void fastUpdateCursorStep(Cursor cursor) {
        StepParcelable navStep = StepParcelable.readStepParcelable(cursor);
        ((Callback) mFragment).stepUpdate(navStep);
        if ( navStep.level == 0 && navStep.count != 0){
            cursor.moveToNext();
            navStep = StepParcelable.readStepParcelable(cursor);
            ((Callback) mFragment).stepUpdate(navStep);
        }
        mForwardButton.setEnabled(!mCursor.isLast());
        mBackwardButton.setEnabled(!mCursor.isFirst());
    }

    public void navigationForward(){
        if ( mCursor != null && mCursor.moveToNext()) {
                fastUpdateCursorStep(mCursor);
        }
    }
    private void navigationBackward(){
        if ( mCursor != null && mCursor.moveToPrevious()) {
            StepParcelable navStep = StepParcelable.readStepParcelable(mCursor);
            if ( navStep.level == 0 ) {
                if (navStep.count != 0) { // slip
                    mCursor.moveToPrevious();
                }
            }
            completeStepUpdate();
        }
    }
    void completeStepUpdate() {
        boolean isBackEnabled;
        StepParcelable navStep = StepParcelable.readStepParcelable(mCursor);
        if ( navStep.level != 0) {
            int offset = navStep.level;
            if ( !mCursor.move(-offset)) return; // error
            StepParcelable navStep0 = StepParcelable.readStepParcelable(mCursor);
            isBackEnabled = !(mCursor.isFirst() && navStep.level == 1);
            mCursor.move(offset);
            ((Callback) mFragment).stepUpdate(navStep0);
        } else {
            isBackEnabled = !mCursor.isFirst();
        }
        ((Callback) mFragment).stepUpdate(navStep);
        mForwardButton.setEnabled(!mCursor.isLast());
        mBackwardButton.setEnabled(isBackEnabled);
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
//            mGeofencesAdded = !mGeofencesAdded;
//            SharedPreferences.Editor editor = mSharedPreferences.edit();
//            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
//            editor.apply();
//            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
//            // geofences enables the Add Geofences button.
//            setButtonsEnabledState();

//            Toast.makeText(
//                    this,
//                    getString(mGeofencesAdded ? R.string.geofences_added :
//                            R.string.geofences_removed),
//                    Toast.LENGTH_SHORT
//            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }

    }
    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }
    public void addGeofencesList() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }
    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }
    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList(List<LatLng> points) {
        int position = 0;
        for (LatLng point: points) {
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(String.valueOf(position))

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            point.latitude,
                            point.longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                            // Create the geofence.
                    .build());
        }
    }
    private void createGeofenceFunction() {
        int position = mCursor.getPosition();
        // test
        mCursor.moveToFirst();
        List<LatLng> list = new ArrayList<>();
        do {
            StepParcelable navStep = StepParcelable.readStepParcelable(mCursor);
//            list.add(new LatLng(navStep.start_lat, navStep.start_lng));
            list.add(new LatLng(navStep.end_lat, navStep.end_lng));
        } while(mCursor.moveToNext());
        mCursor.moveToPosition(position);
        populateGeofenceList(list);
        addGeofencesList();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeGeofences();
    }

    public interface Callback {
        void locationUpdate(Location location);
        void stepUpdate(StepParcelable step);
        void fabMyLocationPressed();
    }
}
