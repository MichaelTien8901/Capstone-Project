package com.ymsgsoft.michaeltien.hummingbird;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ymsgsoft.michaeltien.hummingbird.data.NavigateColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.data.StepColumns;

import java.text.DateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavigateActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener, LoaderManager.LoaderCallbacks<Cursor> {
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
//    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected final static String LOCATION_KEY = "location-key";
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
        if ( mRouteObject != null)
            outState.putParcelable(DetailRouteActivity.ARG_ROUTE_KEY, mRouteObject);
        if ( mCursor != null)
            outState.putInt(NAVIGATION_POS_KEY, mCursor.getPosition());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        ButterKnife.bind(this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_navigation);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if ( savedInstanceState == null) {
            mRouteObject = getIntent().getParcelableExtra(DetailRouteActivity.ARG_ROUTE_KEY);
            mCursorPosition = 0;
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailRouteActivity.ARG_ROUTE_KEY, mRouteObject);

            mFragment = new NavigationFragment();
            mFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_navigation_container, mFragment, NAVIGATION_TAG)
                    .commit();
            mStreetviewButton.setContentDescription(getString(R.string.street_view_description));
        } else {
            mFragment = getFragmentManager().findFragmentByTag(NAVIGATION_TAG);
            mRouteObject = savedInstanceState.getParcelable(DetailRouteActivity.ARG_ROUTE_KEY);
            if ( mFragment instanceof StreetViewFragment) {
                mStreetviewButton.setImageResource(R.drawable.ic_map_black);
                mStreetviewButton.setContentDescription(getString(R.string.map_view_description));
            }
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
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
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
//        Toast.makeText(this, location.toString(),
//                Toast.LENGTH_SHORT).show();
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

    private void navigationForward(){
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
    @OnClick(R.id.fab_navigation_forward)
    public void forwardPressed(){
        navigationForward();
    }
    @OnClick(R.id.fab_navigation_backward)
    public void backwardPressed(){
        navigationBackward();
    }
    @OnClick(R.id.action_up)
    public void backPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
        } else {
            onSupportNavigateUp();
        }
    }
    @OnClick(R.id.action_home)
    public void homePressedClick() {
        DialogFragment newFragment = new ConfirmHomeDialog();
        newFragment.show(getFragmentManager(), "TagHomeDialog");
    }
    public void homePressed() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // no need for transition here
        startActivity(intent);
    }
    @OnClick(R.id.fab_navigation_mode)
    public void modePressed() {
        ((Callback) mFragment).fabMyLocationPressed();
    }
    @OnClick(R.id.fab_streetview)
    public void streetViewPressed() {
        mRouteObject = getIntent().getParcelableExtra(DetailRouteActivity.ARG_ROUTE_KEY);
        mCursorPosition = 0;
        Bundle arguments = new Bundle();
        arguments.putParcelable(DetailRouteActivity.ARG_ROUTE_KEY,mRouteObject);

        // replace fragment
        Fragment newFragment;
        if ( mFragment instanceof NavigationFragment) {
            newFragment = new StreetViewFragment();
            mStreetviewButton.setImageResource(R.drawable.ic_map_black);
            mStreetviewButton.setContentDescription(getString(R.string.map_view_description));
        }
        else {
            newFragment = new NavigationFragment();
            mStreetviewButton.setImageResource(R.drawable.ic_streetview_black);
            mStreetviewButton.setContentDescription(getString(R.string.street_view_description));
        }
        newFragment.setArguments(arguments);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_navigation_container, newFragment, NAVIGATION_TAG)
                .commit();
        mFragment = newFragment;
        completeStepUpdate();
    }
    public interface Callback {
        void locationUpdate(Location location);
        void stepUpdate(StepParcelable step);
        void fabMyLocationPressed();
    }
    public static class ConfirmHomeDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_confirm_home_title)
                    .setPositiveButton(R.string.dialog_confirm_home_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((NavigateActivity)getActivity()).homePressed();
                        }
                    })
                    .setNegativeButton(R.string.dialog_confirm_home_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            return builder.create();
        }
    }
}
