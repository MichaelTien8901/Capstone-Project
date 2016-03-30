package com.ymsgsoft.michaeltien.hummingbird;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.playservices.FavoriteRecyclerViewAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        OnFavoriteItemClickListener {
    private final String LAST_LOCATION_KEY = "LAST_LOCATION_KEY";
    private final int MY_SEARCH_ACTIVITY_REQUEST_ID = 1;
    protected FavoriteRecyclerViewAdapter mAdapter;
    public static final int FAVORITE_LOADER =1;

    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected int REQUEST_LOCATION = 101;
    private Boolean locationReady = false, mapReady = false;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.list_favorites)  RecyclerView mRecyclerView;
    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        //Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

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
        if (savedInstanceState != null) {
            mLastLocation = savedInstanceState.getParcelable(LAST_LOCATION_KEY);
        }

        mAdapter = new FavoriteRecyclerViewAdapter(this, R.layout.list_item_favorite, null, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL,
//                getResources().getDrawable(R.drawable.line_divider)));
        mRecyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(FAVORITE_LOADER, null, this);
    }
    private void performSearch() {
        Intent intent = new Intent(MapsActivity.this, PlanningActivity.class);
        PlaceObject mFromObject = new PlaceObject();
        mFromObject.title = "Here";
        if ( mLastLocation == null) return;
        //mFromObject.placeId = String.format("lat=%f,lng=%f",mLastLocation.getLatitude(),mLastLocation.getLongitude());
        mFromObject.placeId = String.format("%f,%f", mLastLocation.getLatitude(), mLastLocation.getLongitude());
        intent.putExtra(getString(R.string.intent_plan_key_from), mFromObject);
        startActivity(intent);
//        startActivityForResult(intent, MY_SEARCH_ACTIVITY_REQUEST_ID);
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case MY_SEARCH_ACTIVITY_REQUEST_ID:
//                if (resultCode == RESULT_OK) {
//                    String place_id = data.getStringExtra(PlaceActivity.PLACE_ID);
//                    //String place_name = data.getStringExtra(PlaceActivity.PLACE_TEXT);
//                    CharSequence place_name = data.getCharSequenceExtra(PlaceActivity.PLACE_TEXT);
////                    mSearchButton.setText(place_name);
//                }
//                break;
//        }
//    }

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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        View toolbar = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
//                getParent()).findViewById(Integer.parseInt("4"));
//        // and next place it, for example, on bottom right (as Google Maps app)
//        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
//        // position on right bottom
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        rlp.setMargins(0, 0, 30, 30);

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
                    mapReady = false;
                }
            }
        }

    }

    protected void showCurrentPosition() {
        LatLng Here = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(Here).title("You Are Here"));
        CameraPosition target = CameraPosition.builder().target(Here).zoom(14).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mLastLocation != null) {
            outState.putParcelable(LAST_LOCATION_KEY, mLastLocation);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mLastLocation = savedInstanceState.getParcelable(LAST_LOCATION_KEY);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_options, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_search) {
            performSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                RoutesProvider.Favorite.CONTENT_URI,
                null,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void OnItemClick(FavoriteRecyclerViewAdapter.FavoriteObject data, int position) {
        Toast.makeText(this, data.id_name, Toast.LENGTH_SHORT).show();
        mDrawer.closeDrawers();
    }
}
