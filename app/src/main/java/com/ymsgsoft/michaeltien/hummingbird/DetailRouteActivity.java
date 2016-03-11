package com.ymsgsoft.michaeltien.hummingbird;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DetailRouteActivity extends AppCompatActivity {
    protected long mRouteId;
    protected String mPolyLine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        findViewById(R.id.fab_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Remove", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        findViewById(R.id.fab_navigate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Navigate", Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show();
                Intent intent = new Intent( DetailRouteActivity.this, NavigateActivity.class);
                intent.putExtra(getString(R.string.intent_route_key), mRouteId);
                intent.putExtra(getString(R.string.intent_overview_polyline_key), mPolyLine);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if ( savedInstanceState == null) {
            final String ARG_ROUTE_KEY_ID = getString(R.string.intent_route_key);
            final String ARG_OVERVIEW_POLYLINE_KEY = getString(R.string.intent_overview_polyline_key);

            mRouteId = getIntent().getLongExtra(ARG_ROUTE_KEY_ID, -1);
            mPolyLine = getIntent().getStringExtra(ARG_OVERVIEW_POLYLINE_KEY);
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