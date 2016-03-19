package com.ymsgsoft.michaeltien.hummingbird;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link NavigationFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link NavigationFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class NavigationFragment extends Fragment implements
        OnMapReadyCallback,
        NavigateActivity.Callback
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
    protected GoogleMap mMap;
    private boolean isMapReady = false;
    protected Marker mMarker;
    protected Polyline mPolyline0;
    protected Polyline mPolyline1;
    protected float  mCurrentCameraZoom = -1;
    protected Location mCurrentLocation;
    protected boolean mPositionSync = true;
    protected int mNavigationMode = 0;
    final static double DISTANCE_TOLERENCE = 2.0; // meters
    final static float ZOOM_LEVEL = 15;
    protected RouteParcelable mRouteObject;
    protected StepParcelable mStepObject;
    @Bind(R.id.navigate_instruction) TextView mInstructionView;
    @Bind(R.id.navigate_detail_instruction) TextView mDetailedInstructionView;
    @Bind(R.id.navigate_step_transit_no) TextView mStepTransitNo;
    @Bind(R.id.navigate_step_icon) ImageView mStepIconView;

    private OnFragmentInteractionListener mListener;
    private List<StepParcelable> mPendingStepList;
    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String ARG_ROUTE_KEY_ID = getString(R.string.intent_route_key);
        if (getArguments() != null) {
            mRouteObject = getArguments().getParcelable(ARG_ROUTE_KEY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.bind(this, rootView);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.navigate_map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void fabMyLocationPressed() {
        if ( !mPositionSync) {
            mPositionSync = true;
            mNavigationMode = 0;
        } else {
            if ( mNavigationMode ++ == 1)
                mNavigationMode = 0;
        }
//        mCurrentCameraZoom = ZOOM_LEVEL;
        updateUI();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady = true;
        mMap = googleMap;
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mCurrentCameraZoom = cameraPosition.zoom;
                if ( mCurrentLocation != null) {
                    float [] result = new float[3];
                    Location.distanceBetween(
                            mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude(),
                            cameraPosition.target.latitude,
                            cameraPosition.target.longitude, result);
                    if ( result[0] > DISTANCE_TOLERENCE) {
                        mPositionSync = false;
                    }
                }
            }
        });
        if ( mPendingStepList != null) {
            for( StepParcelable step: mPendingStepList) {
                stepUpdate(step);
            }
            mPendingStepList.clear();
            mPendingStepList = null;
        }
    }

    @Override
    public void stepUpdate(StepParcelable step) {
        mStepObject = step;
        if ( isMapReady ) {
            if ( mStepObject.polyline != null && !mStepObject.polyline.isEmpty())
                drawPolyline(mStepObject.polyline, mStepObject.level, mStepObject.level == 0);
            // show instruction
            if (mStepObject.instruction != null && !mStepObject.instruction.isEmpty())
                if ( mStepObject.level == 0) {
                    mInstructionView.setText(Html.fromHtml(mStepObject.instruction));
                    mInstructionView.setVisibility(View.VISIBLE);
                    mDetailedInstructionView.setVisibility(View.INVISIBLE);
                    if ( mStepObject.travel_mode.equals("WALKING")) {
                        mStepIconView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_directions_walk));
                        mStepTransitNo.setText("");
                        mStepTransitNo.setVisibility(View.INVISIBLE);
                    } else {
                        mStepIconView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_directions_bus));
                        if ( mStepObject.transit_no != null && !mStepObject.transit_no.isEmpty()) {
                            mStepTransitNo.setText(mStepObject.transit_no);
                            mStepTransitNo.setVisibility(View.VISIBLE);
                        } else {
                            mStepTransitNo.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    mDetailedInstructionView.setText(Html.fromHtml(mStepObject.instruction));
                    mDetailedInstructionView.setVisibility(View.VISIBLE);
                }
        } else {
            if ( mPendingStepList == null)
                mPendingStepList = new ArrayList<StepParcelable>();
            mPendingStepList.add(step);
        }
    }
    private void drawPolyline( String polyline, long level, boolean isMoveCamera) {
        List<LatLng> points = PolyUtil.decode(polyline);
        if ( level == 0) {
            if ( mPolyline1 != null) {
                mPolyline1.remove();
                mPolyline1 = null;
            }
            if ( mPolyline0 == null) {
                PolylineOptions options = new PolylineOptions()
                        .addAll(points)
                        .color(getResources().getColor(R.color.colorAccent));
                options.zIndex(1);
                mPolyline0 = mMap.addPolyline(options);
            } else {
                mPolyline0.setPoints(points);
            }
        } else {
            if ( mPolyline1 == null) {
                PolylineOptions options = new PolylineOptions()
                        .addAll(points)
                        .color(Color.BLUE);
                options.zIndex(level+1);
                mPolyline1 = mMap.addPolyline(options);
            } else {
                mPolyline1.setPoints(points);
            }
        }
        // move camera within range
        if ( isMoveCamera ) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : points) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            int padding = 40; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            try {
                mMap.animateCamera(cu);
            } catch (IllegalStateException e) {
                // layout not initialized
            }
        }
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    private static BitmapDescriptor getBitmapDescriptor(Context context, int id) {
        Drawable vectorDrawable;
        if ( Build.VERSION.SDK_INT < 21) {
            vectorDrawable = ContextCompat.getDrawable(context, id);
        } else
            vectorDrawable = context.getDrawable(id);
        int h = (int) convertDpToPixel(40, context);
        int w = (int) convertDpToPixel(40, context);
        vectorDrawable.setBounds(0, 0, w, h);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    @Override
    public void locationUpdate(Location location) {
        // check current marker
        mCurrentLocation = location;
        updateUI();

    }

    private void updateUI() {
        LatLng position = new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        float zoom;
        if ( mMarker == null) {
            mMarker = mMap.addMarker(new MarkerOptions().position(position));
//            mMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
            mMarker.setIcon(getBitmapDescriptor(getContext(), R.drawable.ic_person_pin_black));
            zoom = ZOOM_LEVEL;
        } else {
            mMarker.setPosition(position);
            if ( mCurrentCameraZoom != -1) {
                zoom = mCurrentCameraZoom;
            } else zoom = ZOOM_LEVEL;
        }

//        if ( mPositionSync || !CheckMarkerVisibility(mMarker)) {
//            CameraPosition.Builder builder = new CameraPosition.Builder();
//            builder.target(position);
//            if ( mPositionSync ) {
//                switch (mNavigationMode) {
//                    case 1:
//                        if (mCurrentLocation.hasBearing())
//                            builder.bearing(mCurrentLocation.getBearing());
//                        break;
//                    case 0:
//                        builder.bearing(0);
//                        break;
//                }
//            }
//            CameraPosition target = builder.zoom(zoom).build();
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target));
//        }
    }

    private boolean CheckMarkerVisibility(Marker myPosition)
    {
        //This is the current user-viewable region of the map
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        return (bounds.contains(myPosition.getPosition()));
    }
     public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
