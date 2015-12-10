package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng Vancouver = new LatLng( 49.264911, -123.241917 );
        mMap.addMarker(new MarkerOptions().position(Vancouver).title("Marker in Vancouver"));
        CameraPosition target = CameraPosition.builder().target(Vancouver).zoom(14).build();
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(Vancouver));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }
}