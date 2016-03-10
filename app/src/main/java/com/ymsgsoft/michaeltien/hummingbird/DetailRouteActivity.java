package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailRouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if ( savedInstanceState == null) {
            final String ARG_ROUTE_KEY_ID = getString(R.string.intent_route_key);
            final String ARG_OVERVIEW_POLYLINE_KEY = getString(R.string.intent_overview_polyline_key);

            long mRouteId = getIntent().getLongExtra(ARG_ROUTE_KEY_ID, -1);
            String mPolyLine = getIntent().getStringExtra(ARG_OVERVIEW_POLYLINE_KEY);
            Bundle arguments = new Bundle();
            arguments.putLong(ARG_ROUTE_KEY_ID, mRouteId);
            arguments.putString(ARG_OVERVIEW_POLYLINE_KEY, mPolyLine);

            DetailRouteFragment fragment = new DetailRouteFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail_route_id, fragment)
                    .commit();
        }
    }
}
