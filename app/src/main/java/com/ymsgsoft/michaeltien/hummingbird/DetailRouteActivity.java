package com.ymsgsoft.michaeltien.hummingbird;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DetailRouteActivity extends AppCompatActivity {
//    protected long mRouteId;
//    protected String mPolyLine;
    static final String SAVE_ARG_KEY = "save_arg_key";
    protected RouteParcelable mRouteObject;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ( mRouteObject != null)
            outState.putParcelable(SAVE_ARG_KEY, mRouteObject);
    }

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
                intent.putExtra(getString(R.string.intent_route_key), mRouteObject);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String ARG_ROUTE_KEY_ID = getString(R.string.intent_route_key);
        if ( savedInstanceState == null) {
            mRouteObject = getIntent().getParcelableExtra(ARG_ROUTE_KEY_ID);
        } else {
            if (savedInstanceState.containsKey(SAVE_ARG_KEY))
                mRouteObject = savedInstanceState.getParcelable(SAVE_ARG_KEY);
        }

        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_ROUTE_KEY_ID, mRouteObject);

        DetailRouteFragment fragment = new DetailRouteFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_detail_container, fragment, "fragment_detail_tag")
                .commit();
    }
}
