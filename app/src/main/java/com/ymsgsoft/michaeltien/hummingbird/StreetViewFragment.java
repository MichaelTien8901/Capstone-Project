package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StreetViewFragment extends Fragment
    implements NavigateActivity.Callback,
        OnStreetViewPanoramaReadyCallback
{
    protected StreetViewPanorama mStreetView;
    private boolean isMapReady = false;
    private Location mCurrentLocation;
    private boolean mPositionSync = true;
    @Bind(R.id.navigate_instruction) TextView mInstructionView;
    @Bind(R.id.navigate_transit_panel) View mTransitPanel;
    @Bind(R.id.navigate_detail_panel) View mDetailPanel;
    @Bind(R.id.navigate_transit_departure_stop) TextView mDepartureStop;
    @Bind(R.id.navigate_transit_arrival_stop) TextView mArrivalStop;
    @Bind(R.id.navigate_transit_stop_no) TextView mTransitStops;

    @Bind(R.id.navigate_detail_instruction) TextView mDetailedInstructionView;
    @Bind(R.id.navigate_step_transit_no) TextView mStepTransitNo;
    @Bind(R.id.navigate_step_icon) ImageView mStepIconView;
    private List<StepParcelable> mPendingStepList;
    private boolean mStepListManualUpdate;
    protected RouteParcelable mRouteObject;
    protected StepParcelable mStepObject;
    StreetViewPanoramaCamera mCamera;
    private com.ymsgsoft.michaeltien.hummingbird.OnNavigationFragmentListener mListener;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(NavigateActivity.SAVE_POSITION_SYNC_KEY, mPositionSync);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnNavigationFragmentListener) {
            mListener = (OnNavigationFragmentListener) context;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationFragmentListener) {
            mListener = (OnNavigationFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( savedInstanceState != null) {
            mPositionSync = savedInstanceState.getBoolean(NavigateActivity.SAVE_POSITION_SYNC_KEY, mPositionSync);
        } else {
            if (getArguments() != null) {
                if ( getArguments().containsKey(DetailRouteActivity.ARG_ROUTE_KEY))
                    mRouteObject = getArguments().getParcelable(DetailRouteActivity.ARG_ROUTE_KEY);
                if ( getArguments().containsKey(NavigateActivity.SAVE_POSITION_SYNC_KEY))
                    mPositionSync = getArguments().getBoolean(NavigateActivity.SAVE_POSITION_SYNC_KEY);

            }
        }
    }

    public StreetViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_street_view, container, false);
        ButterKnife.bind(this, rootView);
        StreetViewPanoramaFragment mapFragment = (StreetViewPanoramaFragment) getChildFragmentManager()
                .findFragmentById(R.id.streetview_panorama);
        mapFragment.getStreetViewPanoramaAsync(this);
        return rootView;
    }

    @Override
    public void fabMyLocationPressed() {
        mPositionSync = true;
        if ( mListener != null)
            mListener.onLocationSyncChange(true);

        if ( mCurrentLocation != null)
            moveCameraToCurrentLocation();
    }

    @Override
    public void stepUpdate(StepParcelable step, boolean manual_update) {
        mStepObject = step;
        if ( manual_update) {
            mPositionSync = false;
            if ( mListener != null)
                mListener.onLocationSyncChange(false);
        }
        if ( isMapReady ) {
            // draw polyline ignore
            // show instruction
            if (mStepObject.instruction != null && !mStepObject.instruction.isEmpty())
                if ( mStepObject.level == 0) {
                    String inst = Utils.getTrimmedHtml(mStepObject.instruction);
                    mInstructionView.setText(inst);
                    mInstructionView.setVisibility(View.VISIBLE);
                    mDetailedInstructionView.setVisibility(View.GONE);
                    mTransitPanel.setVisibility(View.GONE);
                    mDetailPanel.setVisibility(View.GONE);
                    if ( mStepObject.travel_mode.equals("WALKING")) {
//                        mStepIconView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_directions_walk));
                        mStepIconView.setImageDrawable(ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.ic_directions_walk, null));
                        mStepTransitNo.setText("");
                        mStepTransitNo.setVisibility(View.GONE);
                    } else {
//                        mStepIconView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_directions_bus));
                        mStepIconView.setImageDrawable(ResourcesCompat.getDrawable(getActivity().getResources(),R.drawable.ic_directions_bus, null));
                        if ( mStepObject.transit_no != null && !mStepObject.transit_no.isEmpty()) {
                            mStepTransitNo.setText(mStepObject.transit_no);
                            mStepTransitNo.setVisibility(View.VISIBLE);
                        } else {
                            mStepTransitNo.setVisibility(View.GONE);
                        }
                        // transit details
                        if ( mStepObject.departure_stop != null) { // use transit panel instead
                            mDepartureStop.setText(mStepObject.departure_stop);
                            if (mStepObject.arrival_stop != null) {
                                mArrivalStop.setText(mStepObject.arrival_stop);
                            } else
                                mArrivalStop.setText("");
                            if (mStepObject.num_stops != 0) {
                                mTransitStops.setText(String.valueOf(mStepObject.num_stops));
                            } else
                                mTransitStops.setText("");
                            mDetailPanel.setVisibility(View.VISIBLE);
                            mTransitPanel.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    String inst = Utils.getTrimmedHtml(mStepObject.instruction);
                    mDetailedInstructionView.setText(inst);
                    mDetailPanel.setVisibility(View.VISIBLE);
                    mDetailedInstructionView.setVisibility(View.VISIBLE);
                    mTransitPanel.setVisibility(View.GONE);
                }
            if ( mStepObject.level != 0 || (mStepObject.level == 0 && mStepObject.count == 0)) {
                // set position of street view
                // camera to the bearing of direction
                Location start_location = new Location("");
                start_location.setLatitude(mStepObject.start_lat);
                start_location.setLongitude(mStepObject.start_lng);
                Location end_location = new Location("");
                end_location.setLatitude(mStepObject.end_lat);
                end_location.setLongitude(mStepObject.end_lng);

                // change camera bearing
                mCamera = new StreetViewPanoramaCamera.Builder()
                        .bearing(start_location.bearingTo(end_location))
                        .build();
                mStreetView.setPosition(new LatLng(mStepObject.start_lat, mStepObject.start_lng),50);
//                mStreetView.animateTo(camera, 1000); // less than 500 might get wrong bearing
            }
        } else {
            if ( mPendingStepList == null) {
                mPendingStepList = new ArrayList<>();
                mStepListManualUpdate = false;
            }
            mPendingStepList.add(step);
            if ( manual_update) mStepListManualUpdate = true;
        }
    }
    private void moveCameraToCurrentLocation() {
        StreetViewPanoramaCamera.Builder builder = new StreetViewPanoramaCamera.Builder();
        if ( mCurrentLocation.hasBearing()) {
            builder.bearing(mCurrentLocation.getBearing());
        } else
            builder.bearing(mStreetView.getPanoramaCamera().bearing);
        mCamera = builder.build();
        mStreetView.setPosition(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 50);
    }
    @Override
    public void locationUpdate(Location location) {
        mCurrentLocation = location;
        if ( !isMapReady ) return;
        if ( !mPositionSync) return;
        moveCameraToCurrentLocation();
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        mStreetView = streetViewPanorama;
        isMapReady = true;
        mStreetView.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                if (streetViewPanoramaLocation != null && mCamera != null) {
                    mStreetView.animateTo(mCamera, 1000);
                    mCamera = null;
                }
            }
        });
        if ( mPendingStepList != null) {
            for( StepParcelable step: mPendingStepList) {
                stepUpdate(step, mStepListManualUpdate);
            }
            mPendingStepList.clear();
            mPendingStepList = null;
            mStepListManualUpdate = false;
        }
    }
}
