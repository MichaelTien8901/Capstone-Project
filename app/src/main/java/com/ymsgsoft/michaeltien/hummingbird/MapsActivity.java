package com.ymsgsoft.michaeltien.hummingbird;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ymsgsoft.michaeltien.hummingbird.data.PrefUtils;
import com.ymsgsoft.michaeltien.hummingbird.data.RouteColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        NavigationView.OnNavigationItemSelectedListener,
        ResultCallback<LocationSettingsResult> {
    final String LOG_TAG = MapsActivity.class.getSimpleName();
    public static final String PLACE_PARAM = "place_param";
    private static final String LAST_LOCATION_KEY = "LAST_LOCATION_KEY";
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final int SEARCH_TO_REQUEST_ID = 2;
    private final int FAVORITE_REQUEST_ID = 3;
    private final int HISTORY_REQUEST_ID = 4;
    private final int REQUEST_CHECK_SETTINGS = 5;
//    public final int PLACE_PICKER_REQUEST = 102;
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

    private GoogleMap mMap;
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;
    protected Location mLastLocation;
    protected PlaceObject mPendingPlaceObject;
    protected int REQUEST_LOCATION = 101;

    private Boolean locationReady = false, mapReady = false;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected boolean mRequestingLocationUpdates;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    private Marker mMarker;

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        //Log.i(LOG_TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
//        mDrawer.setDrawerListener(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mRequestingLocationUpdates = false;
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        if (savedInstanceState != null) {
            mLastLocation = savedInstanceState.getParcelable(LAST_LOCATION_KEY);
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }
        } else {
            PrefUtils.resetLocationRequestFlag(this, getString(R.string.pref_key_location_request_flag));
            if (!Utils.checkNetworkAvailable(this)) {
                    Log.d(LOG_TAG, "onCreate: service not available!");
//                    Utils.NetworkDialogFragment.newInstance(
//                            R.string.network_error_title,
//                            R.string.network_error_message )
//                        .show(getFragmentManager(), "NetworkDialog");
            } else {
                Intent intent = getIntent();
                mPendingPlaceObject = intent.getParcelableExtra(PLACE_PARAM);
            }
        }
        AdView mAdView = (AdView) findViewById(R.id.adView);
        if (mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                            //                .setLocation(currentLocation)
                    .build();
            mAdView.loadAd(adRequest);
        }
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); // without never button
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(LOG_TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(LOG_TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                if ( !PrefUtils.isLocationRequestFlag(MapsActivity.this, getString(R.string.pref_key_location_request_flag)))
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        PrefUtils.setLocationRequestFlag(MapsActivity.this, getString(R.string.pref_key_location_request_flag));
                        status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i(LOG_TAG, "PendingIntent unable to execute request.");
                    }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(LOG_TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
//                setButtonsEnabledState();
                break;
        }

    }

    private void startActivityRouteDetails(String startName, String startPlaceId, String endName, String endPlaceId, long routeId) {
        new LoadRouteTask().execute(
                String.valueOf(routeId),
                startName,
                startPlaceId,
                endName,
                endPlaceId);
    }


    private class LoadRouteTask extends AsyncTask<String, Void, RouteParcelable> {
        PlaceObject mFromObject, mToObject;

        @Override
        protected void onPostExecute(RouteParcelable routeObject) {
            Intent intent = new Intent(MapsActivity.this, DetailRouteActivity.class);
            intent.putExtra(DetailRouteActivity.ARG_ROUTE_KEY, routeObject);
            intent.putExtra(PlanningActivity.PLAN_FROM_ID, mFromObject);
            intent.putExtra(PlanningActivity.PLAN_TO_ID, mToObject);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this
                ).toBundle();
                startActivity(intent, bundle);
            } else
                startActivity(intent);
        }

        @Override
        protected RouteParcelable doInBackground(String... params) {
            mFromObject = new PlaceObject(params[1], params[2]);
            mToObject = new PlaceObject(params[3], params[4]);
            RouteParcelable mData;
            Cursor cursor = getContentResolver().query(
                    RoutesProvider.Routes.CONTENT_URI,
                    null,
                    RouteColumns.ID + " =?",
                    new String[]{params[0]},
                    null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    mData = new RouteParcelable();
                    mData.routeId = cursor.getInt(cursor.getColumnIndex(RouteColumns.ID));
                    mData.overviewPolyline = cursor.getString(cursor.getColumnIndex(RouteColumns.OVERVIEW_POLYLINES));
                    mData.transitNo = cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_TRANSIT_NO));
                    mData.departTime = cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DEPART_TIME));
                    mData.duration = cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DURATION));
                    mData.isFavorite = cursor.getInt(cursor.getColumnIndex(RouteColumns.IS_FAVORITE)) == 1;
                    mData.deparTimeValue = cursor.getLong(cursor.getColumnIndex(RouteColumns.DEPART_TIME_VALUE));
                    cursor.close();
                    return mData;
                }
                cursor.close();
            }
            return null;
        }
    }

    void startActivityPlanning(PlaceObject mFromObject, PlaceObject mToObject) {
        Intent intent = new Intent(MapsActivity.this, PlanningActivity.class);
        intent.putExtra(PlanningActivity.PLAN_FROM_ID, mFromObject);
        intent.putExtra(PlanningActivity.PLAN_TO_ID, mToObject);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this
            ).toBundle();
            startActivity(intent, bundle);
        } else
            startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PlaceObject mToObject = new PlaceObject();
            PlaceObject mFromObject = new PlaceObject();
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    Log.i(LOG_TAG, "User agreed to make required location settings changes.");
                    startLocationUpdates();

                    break;
                case FAVORITE_REQUEST_ID:
                    String start_place_id = data.getStringExtra(FavoriteActivity.START_PLACEID_PARAM);
                    String start_name = data.getStringExtra(FavoriteActivity.START_PARAM);
                    String end_place_id = data.getStringExtra(FavoriteActivity.END_PLACEID_PARAM);
                    String end_name = data.getStringExtra(FavoriteActivity.END_PARAM);
                    long routeId = data.getLongExtra(FavoriteActivity.ROUTE_ID_PARAM, 0);
                    String action = data.getStringExtra(FavoriteActivity.ACTION_PARAM);
                    if (FavoriteActivity.ACTION_LOAD.equals(action)) {
                        startActivityRouteDetails(start_name, start_place_id, end_name, end_place_id, routeId);
                    } else if (FavoriteActivity.ACTION_PLANNING.equals(action)) {
                        if ( !Utils.checkServicesAvailable(this, mGoogleApiClient, mRequestingLocationUpdates)) {
//                        if ( !Utils.checkNetworkAvailable(this)) {
                            Log.d(LOG_TAG, "onActivityResult: service not available!");
                            return;
                        }
                        mFromObject = new PlaceObject(start_name, start_place_id);
                        mToObject = new PlaceObject(end_name, end_place_id);
                        DirectionService.startActionSavePlace(this, mFromObject, System.currentTimeMillis());
                        DirectionService.startActionSavePlace(this, mToObject, System.currentTimeMillis());
                        // go to planning
                        startActivityPlanning(mFromObject, mToObject);
                    }
                    break;
                case HISTORY_REQUEST_ID:
                    if ( mLastLocation == null) {
                        mRequestingLocationUpdates = false; // enable error
                    }
                    if ( !Utils.checkServicesAvailable(this, mGoogleApiClient, mRequestingLocationUpdates)) {
//                    if ( !Utils.checkNetworkAvailable(this)) {
                        Log.d(LOG_TAG, "onActivityResult: service not available!");
                        return;
                    }
                    mToObject = new PlaceObject(
                            data.getStringExtra(HistoryActivity.PLACE_PARAM),
                            data.getStringExtra(HistoryActivity.PLACEID_PARAM));
                    mFromObject = new PlaceObject(
                            getString(R.string.default_location_title),
                            String.format("%f,%f", mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    // save history, again to update query time
                    DirectionService.startActionSavePlace(this, mToObject, System.currentTimeMillis());
                    // go to planning
                    startActivityPlanning(mFromObject, mToObject);
                    break;

//            case PLACE_PICKER_REQUEST:
//                if ( resultCode == RESULT_OK) {
//                    Place place = PlacePicker.getPlace(this, data);
//                    String placeId = place.getId();
//                }
//                break;
                case SEARCH_TO_REQUEST_ID:
                    if (mLastLocation == null) return;
                    String place_id = data.getStringExtra(PlaceActivity.PLACE_ID);
                    CharSequence place_name = data.getCharSequenceExtra(PlaceActivity.PLACE_TEXT);
                    mToObject.title = place_name.toString();
                    mToObject.placeId = place_id;
                    DirectionService.startActionSavePlace(this, mToObject, System.currentTimeMillis());
                    mFromObject.title = getString(R.string.default_location_title);
                    mFromObject.placeId = String.format("%f,%f", mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    startActivityPlanning(mFromObject, mToObject);
            }
        } else {
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    Log.i(LOG_TAG, "User chose not to make required location settings changes.");
//                    setButtonsEnabledState();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (locationReady) {
            showCurrentPosition();
        } else {
            mapReady = true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                locationReady = true;
                if (mapReady) {
                    showCurrentPosition();
//                    mapReady = false;
                }
            }
        }
//        startLocationUpdates();
        checkLocationSettings();
    }

    protected void showCurrentPosition() {
        LatLng here = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        if (mMarker == null) {
            mMarker = mMap.addMarker(new MarkerOptions().position(here).title(getString(R.string.you_are_here_title)));
            mMarker.setIcon(Utils.getBitmapDescriptor(this, R.drawable.ic_person_pin_black));
            mMarker.setAnchor((float) 0.5, (float) (23.0 / 24.0));
        } else {
            mMarker.setPosition(here);
        }
        CameraPosition target = CameraPosition.builder().target(here).zoom(14).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectedFailed: " + connectionResult.toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (mLastLocation != null) {
            savedInstanceState.putParcelable(LAST_LOCATION_KEY, mLastLocation);
        }
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLastLocation = savedInstanceState.getParcelable(LAST_LOCATION_KEY);
            mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_options, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
//        if ( id == R.id.action_search) {
////            performSearch();
////            return true;
//            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//            try {
//                startActivityForResult(
//                        builder.build(this), PLACE_PICKER_REQUEST);
//
//            } catch (GooglePlayServicesNotAvailableException
//                    | GooglePlayServicesRepairableException e) {
//                // What did you do?? This is why we check Google Play services in onResume!!!
//                // The difference in these exception types is the difference between pausing
//                // for a moment to prompt the user to update/install/enable Play services vs
//                // complete and utter failure.
//                // If you prefer to manage Google Play services dynamically, then you can do so
//                // by responding to these exceptions in the right moment. But I prefer a cleaner
//                // user experience, which is why you check all of this when the app resumes,
//                // and then disable/enable features based on that availability.
//            }
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_history) {
            launchHistoryActivity();
        } else if (id == R.id.nav_manage) {
            launchSettingsActivity();
        } else if (id == R.id.nav_favorites) {
            launchFavoriteActivity();
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this
            ).toBundle();
            startActivity(intent, bundle);
        } else
            startActivity(intent);
    }

    private void launchHistoryActivity() {
        Intent intent = new Intent(this, HistoryActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this
            ).toBundle();
            startActivityForResult(intent, HISTORY_REQUEST_ID, bundle);
        } else
            startActivityForResult(intent, HISTORY_REQUEST_ID);
    }

    private void launchFavoriteActivity() {
        Intent intent = new Intent(this, FavoriteActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this
            ).toBundle();
            startActivityForResult(intent, FAVORITE_REQUEST_ID, bundle);
        } else
            startActivityForResult(intent, FAVORITE_REQUEST_ID);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(LOG_TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
                    mGoogleApiClient, mLocationRequest, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            mRequestingLocationUpdates = true;
//                            setButtonsEnabledState();
                            PrefUtils.resetLocationRequestFlag(MapsActivity.this, getString(R.string.pref_key_location_request_flag));
                        }
                    });
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
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                        mRequestingLocationUpdates = false;
//                        setButtonsEnabledState();
                    }
                });
    }

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
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        locationReady = true;
        if (mapReady) {
            showCurrentPosition();
//            mapReady = false;
        }
        if (mPendingPlaceObject != null) {
            if ( !Utils.checkServicesAvailable(this, mGoogleApiClient, mRequestingLocationUpdates)) { // is it too early to check googleapiclient connected?
                mPendingPlaceObject = null;
                Log.d(LOG_TAG, "onLocationChanged: service not available!");
//                new Utils.NetworkDialog().show(getFragmentManager(), "NetworkDialog");
                return;
            }
            PlaceObject mFromObject = new PlaceObject();
            mFromObject.title = getString(R.string.default_location_title);
            mFromObject.placeId = String.format("%f,%f", mLastLocation.getLatitude(), mLastLocation.getLongitude());
            // save history, again to update query time
            DirectionService.startActionSavePlace(this, mPendingPlaceObject, System.currentTimeMillis());
            // go to planning
            startActivityPlanning(mFromObject, mPendingPlaceObject);
            mPendingPlaceObject = null;
        }
    }

    @OnClick(R.id.fab_direction)
    public void directionPressed() {
        if ( !Utils.checkServicesAvailable(this, mGoogleApiClient, mRequestingLocationUpdates)) {
            Log.d(LOG_TAG, "directonPressed: service not available!");
            return;
        }
        Intent intent = new Intent(this, PlaceActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this
            ).toBundle();
            startActivityForResult(intent, SEARCH_TO_REQUEST_ID, bundle);
        } else
            startActivityForResult(intent, SEARCH_TO_REQUEST_ID);
    }
//    private boolean isServiceAvailable() {
//        return mGoogleApiClient.isConnected() && Utils.isOnline(this) && mRequestingLocationUpdates;
//    }
//    private boolean checkServicesAvailable() {
//        if ( isServiceAvailable()) return true;
//        Utils.NetworkDialogFragment dialog;
//        if ( !(mGoogleApiClient.isConnected() && Utils.isOnline(this)))
//            dialog = Utils.NetworkDialogFragment.newInstance(
//                    R.string.network_error_title,
//                    R.string.network_error_message);
//        else
//            dialog = Utils.NetworkDialogFragment.newInstance(
//                    R.string.location_service_error_title,
//                    R.string.location_service_error_message);
//
//        dialog.show(getFragmentManager(), "NetworkDialog");
//        return false;
//    }
}