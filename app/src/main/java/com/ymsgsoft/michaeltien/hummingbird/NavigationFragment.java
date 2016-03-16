package com.ymsgsoft.michaeltien.hummingbird;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationFragment extends Fragment implements
        OnMapReadyCallback,
        NavigateActivity.Callback
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected GoogleMap mMap;
    protected Marker mMarker;
    protected float  mCurrentCameraZoom = -1;
    protected Location mCurrentLocation;
    protected boolean mPositionSync = true;
    protected int mNavigationMode = 0;
    final static double DISTANCE_TOLERENCE = 2.0; // meters
    final static float ZOOM_LEVEL = 15;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavigationFragment newInstance(String param1, String param2) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_navigation, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.navigate_map);
        mapFragment.getMapAsync(this);

        return rootview;
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
//        LatLng Vancouver = new LatLng( 49.264911, -123.241917 );
//        mMarker = mMap.addMarker(new MarkerOptions().position(Vancouver).title("Marker in Vancouver"));
//        CameraPosition target = CameraPosition.builder().target(Vancouver).zoom(14).build();
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(Vancouver));
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));

    }

    @Override
    public void stepUpdate(String step) {

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
        if ( mPositionSync || !CheckMarkerVisibility(mMarker)) {
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.target(position);
            if ( mPositionSync ) {
                switch (mNavigationMode) {
                    case 1:
                        if (mCurrentLocation.hasBearing())
                            builder.bearing(mCurrentLocation.getBearing());
                        break;
                    case 0:
                        builder.bearing(0);
                        break;
                }
            }
            CameraPosition target = builder.zoom(zoom).build();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target));
        }
    }

    private boolean CheckMarkerVisibility(Marker myPosition)
    {
        //This is the current user-viewable region of the map
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        return (bounds.contains(myPosition.getPosition()));
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
