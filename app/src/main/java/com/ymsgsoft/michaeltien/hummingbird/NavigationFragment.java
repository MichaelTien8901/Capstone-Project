package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
    protected GoogleMap mMap;
    private boolean isMapReady = false;
    protected Marker mMarker;
    protected Marker mFlagMarker;
    protected Circle mCircle;
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
        if (getArguments() != null) {
            mRouteObject = getArguments().getParcelable(DetailRouteActivity.ARG_ROUTE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.bind(this, rootView);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
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
                if (mCurrentLocation != null) {
                    float[] result = new float[3];
                    Location.distanceBetween(
                            mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude(),
                            cameraPosition.target.latitude,
                            cameraPosition.target.longitude, result);
                    if (result[0] > DISTANCE_TOLERENCE) {
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
    private void drawDestination(LatLng center) {
        if ( mCircle == null ) {
            CircleOptions option = new CircleOptions().center(center).strokeWidth(2).radius(20).fillColor(0x5500ff00).zIndex(5);
            mCircle = mMap.addCircle(option);
        } else
            mCircle.setCenter(center);
        if ( mFlagMarker == null) {
            mFlagMarker = mMap.addMarker(new MarkerOptions().position(center));
            mFlagMarker.setIcon(Utils.getBitmapDescriptor(getActivity(), R.drawable.ic_flag));
            mFlagMarker.setAnchor((float)0.25, (float)0.833);

        } else {
            mFlagMarker.setPosition(center);
        }
    }
    @Override
    public void stepUpdate(StepParcelable step) {
        mStepObject = step;
        if ( isMapReady ) {
            // draw polyline
            if ( mStepObject.polyline != null && !mStepObject.polyline.isEmpty())
                drawPolyline(mStepObject.polyline,
                        mStepObject.level,
                        mStepObject.level == 0,
                        mStepObject.level > 0 || (mStepObject.level == 0 && mStepObject.count == 0));
            // draw end location
            if ( mStepObject.level != 0 || mStepObject.count == 0)
                drawDestination(new LatLng(mStepObject.end_lat, mStepObject.end_lng));
            // show instruction
            if (mStepObject.instruction != null && !mStepObject.instruction.isEmpty())
                if ( mStepObject.level == 0) {
                    mInstructionView.setText(Html.fromHtml(mStepObject.instruction));
                    mInstructionView.setVisibility(View.VISIBLE);
                    mDetailedInstructionView.setVisibility(View.INVISIBLE);
                    if ( mStepObject.travel_mode.equals("WALKING")) {
                        mStepIconView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_directions_walk));
                        mStepTransitNo.setText("");
                        mStepTransitNo.setVisibility(View.INVISIBLE);
                    } else {
                        mStepIconView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_directions_bus));
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
                mPendingStepList = new ArrayList<>();
//                mPendingStepList = new ArrayList<StepParcelable>();
            mPendingStepList.add(step);
        }
    }
    private void drawPolyline( String polyline, long level, boolean isMoveCamera, boolean isHightLightColor) {
        List<LatLng> points = PolyUtil.decode(polyline);

        int poly_color = isHightLightColor? getResources().getColor(R.color.colorAccent): Color.BLUE;
        if ( level == 0) {
            if ( mPolyline1 != null) {
                mPolyline1.remove();
                mPolyline1 = null;
            }
            if ( mPolyline0 == null) {
                PolylineOptions options = new PolylineOptions()
                        .addAll(points)
                        .width(15)
                        .color(poly_color);
                options.zIndex(1);
                mPolyline0 = mMap.addPolyline(options);
            } else {
                mPolyline0.setPoints(points);
                mPolyline0.setColor(poly_color);
            }
        } else {
            if ( mPolyline1 == null) {
                PolylineOptions options = new PolylineOptions()
                        .addAll(points)
                        .width(15)
                        .color(poly_color);
                options.zIndex(10);
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

    @Override
    public void locationUpdate(Location location) {
        // check current marker
        mCurrentLocation = location;
        if ( !isMapReady) return;
        updateUI();

    }

    private void updateUI() {
        LatLng position = new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        float zoom;
        if ( mMarker == null) {
            mMarker = mMap.addMarker(new MarkerOptions().position(position));
//            mMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
            mMarker.setIcon(Utils.getBitmapDescriptor(getActivity(), R.drawable.ic_person_pin_black));
            mMarker.setAnchor((float)0.5, (float) (23.0/24.0));
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
        void onFragmentInteraction(Uri uri);
    }
}
