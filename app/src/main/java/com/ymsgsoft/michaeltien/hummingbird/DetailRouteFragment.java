package com.ymsgsoft.michaeltien.hummingbird;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.ymsgsoft.michaeltien.hummingbird.data.PrefUtils;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.data.StepColumns;
import com.ymsgsoft.michaeltien.hummingbird.playservices.DetailRouteRecyclerViewAdapter;
import com.ymsgsoft.michaeltien.hummingbird.playservices.StepData;

import java.util.List;

public class DetailRouteFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnStepItemClickListener {
    static final String SAVE_ARG_KEY = "save_arg_key";
    protected int REQUEST_LOCATION = 101;
    protected DetailRouteRecyclerViewAdapter mAdapter;
    public static final int ROUTE_LOADER =1;
    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
//    protected String mOverviewPolyline;
    protected long mRouteId = -1;
    protected RouteParcelable mRouteObject;
    protected Polyline mStepline;

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                RoutesProvider.Steps.CONTENT_URI,
                null,
                StepColumns.ROUTE_ID + "=?",
                new String[] {String.valueOf(mRouteId)},
                StepColumns.ID + " ASC" );
//        return new CursorLoader(getActivity(),
//                RoutesProvider.Legs.CONTENT_URI,
//                null,
//                null,
//                null,
//                null);
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
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailRouteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailRouteActivity.ARG_ROUTE_KEY)) {
                mRouteObject = arguments.getParcelable(DetailRouteActivity.ARG_ROUTE_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if ( mRouteObject != null) {
            outState.putParcelable(SAVE_ARG_KEY, mRouteObject);
            PrefUtils.saveRouteParcelableToPref(getContext(), DetailRouteActivity.ARG_ROUTE_KEY, mRouteObject);
        }
        super.onSaveInstanceState(outState);
    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_route, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_detail_route);
        mAdapter = new DetailRouteRecyclerViewAdapter(getContext(), R.layout.list_item_detail_route, null, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL,
//                getResources().getDrawable(R.drawable.line_divider)));
        recyclerView.setAdapter(mAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.detail_map_id);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

;
        if ( savedInstanceState != null && savedInstanceState.containsKey(SAVE_ARG_KEY)) {
                mRouteObject = savedInstanceState.getParcelable(SAVE_ARG_KEY);
        }
        if ( mRouteObject == null) {
            mRouteObject = PrefUtils.restoreRouteParcelableFromPref(getContext(), DetailRouteActivity.ARG_ROUTE_KEY);
        }
        if ( mRouteObject != null) {
            mRouteId = mRouteObject.routeId;
        }
//        if ( mRouteObject != null && mRouteObject.transitNo != null) {
//            createDetailTitleView(inflater, view);
//        }
        mapFragment.getMapAsync(this);
        getLoaderManager().initLoader(ROUTE_LOADER, null, this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
//        mMap.getUiSettings().setMapToolbarEnabled(false);
        if ( mMap != null && mRouteObject != null)
            drawOverviewPolyline(mRouteObject.overviewPolyline);
    }
    void drawOverviewPolyline(String polyline) {
        if ( polyline != null ) {
//            LatLng Here = new LatLng( 49.2649, -123.24169 );
//            mMap.addMarker(new MarkerOptions().position(Here).title("You Are Here"));
//            CameraPosition target = CameraPosition.builder().target(Here).zoom(10).build();
//            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));

            List<LatLng> points = PolyUtil.decode(polyline);
            PolylineOptions options = new PolylineOptions()
                    .addAll(points)
                    .color(Color.BLUE);
            mMap.addPolyline(options);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for ( LatLng point: points) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            int padding = 40; // offset from edges of the map in pixels
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    mMap.setOnCameraChangeListener(null);
                    try {
//                        mMap.animateCamera(cu);
                        mMap.moveCamera(cu);
                    } catch (IllegalStateException e) {
                    }
                }
            });
//            try {
//                mMap.animateCamera(cu);
//            } catch (IllegalStateException e){
//                // layout not initialized
//                final View mapView = getFragmentManager().findFragmentById(R.id.detail_map_id).getView();
//                if ( mapView.getViewTreeObserver().isAlive()) {
//                    mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                                mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                            } else {
//                                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            }
//                            mMap.animateCamera(cu);
//                        }
//                    });
//                }
//            }
        }
    }

    @Override
    public void OnItemClick(StepData data, int position) {
        if ( data.polylinePoints != null) {
            List<LatLng> points = PolyUtil.decode(data.polylinePoints);
            PolylineOptions options = new PolylineOptions()
                    .addAll(points)
                    .color(getResources().getColor(R.color.colorAccent));
            options.zIndex(10);
            if ( mStepline == null) {
                mStepline = mMap.addPolyline(options);
            } else {
                mStepline.setPoints(points);
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for ( LatLng point: points) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            int padding = 40; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            try {
                mMap.animateCamera(cu);
            } catch (IllegalStateException e){
                // layout not initialized
            }

        }
    }

     @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        //Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}

