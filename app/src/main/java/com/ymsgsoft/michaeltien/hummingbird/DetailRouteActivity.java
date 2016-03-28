package com.ymsgsoft.michaeltien.hummingbird;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.data.PrefUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailRouteActivity extends AppCompatActivity {
//    protected long mRouteId;
//    protected String mPolyLine;
    static final String SAVE_ARG_KEY = "save_arg_key";
    protected RouteParcelable mRouteObject;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ( mRouteObject != null) {
            outState.putParcelable(SAVE_ARG_KEY, mRouteObject);
            PrefUtils.saveRouteParcelableToPref(this, getString(R.string.intent_route_key), mRouteObject);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_route);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        findViewById(R.id.fab_add).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Add", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        findViewById(R.id.fab_remove).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Remove", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//            }
//        });
//        findViewById(R.id.fab_navigate).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Navigate", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Intent intent = new Intent(DetailRouteActivity.this, NavigateActivity.class);
//                intent.putExtra(getString(R.string.intent_route_key), mRouteObject);
//                startActivity(intent);
//            }
//        });
        final String ARG_ROUTE_KEY_ID = getString(R.string.intent_route_key);
        if ( savedInstanceState == null ) {
            mRouteObject = getIntent().getParcelableExtra(ARG_ROUTE_KEY_ID);
        } else {
            if (savedInstanceState.containsKey(SAVE_ARG_KEY))
                mRouteObject = savedInstanceState.getParcelable(SAVE_ARG_KEY);
        }
        if ( mRouteObject == null) {
            mRouteObject = PrefUtils.restoreRouteParcelableFromPref(this, ARG_ROUTE_KEY_ID);
        }

        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_ROUTE_KEY_ID, mRouteObject);

        DetailRouteFragment fragment = new DetailRouteFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_detail_container, fragment, "fragment_detail_tag")
                .commit();
        if ( mRouteObject != null && mRouteObject.transitNo != null) {
            createDetailTitleView(getLayoutInflater());
        }
    }
    private void createDetailTitleView(LayoutInflater inflater) {
        ((TextView) findViewById(R.id.detail_depart_time)).setText(mRouteObject.departTime);
        ((TextView) findViewById(R.id.detail_duration)).setText(mRouteObject.duration);
        String[] transits = mRouteObject.transitNo.split(",");
        TextView transitNoView = (TextView) findViewById(R.id.detail_transit_no2);
        if ( !transits[0].equals("null"))
            transitNoView.setText(transits[0]);
        else
            transitNoView.setText("");
        // create rest of bus number
        if ( transits.length > 1 ) {
            LinearLayout detail_title_container = (LinearLayout) findViewById(R.id.list_detail_title);
            for (int i = 1; i < transits.length && i < 3; i++) {
                View childView = inflater.inflate(R.layout.list_item_transit_no, null);
                ImageView image = (ImageView) childView.findViewById(R.id.list_item_transit_icon1);
                image.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bus));
                TextView textView = (TextView) childView.findViewById(R.id.list_item_transit_no1);
                if (!transits[i].equals("null"))
                    textView.setText(transits[i]);
                else
                    textView.setText("");
                detail_title_container.addView(childView);
            }
        }
    }

    @OnClick(R.id.fab_navigate)
    public void navigate(View view) {
        Snackbar.make(view, "Navigate", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Intent intent = new Intent(DetailRouteActivity.this, NavigateActivity.class);
        intent.putExtra(getString(R.string.intent_route_key), mRouteObject);
        startActivity(intent);

    }
    @OnClick(R.id.fab_add)
    public void addFavorite(View view) {
        Snackbar.make(view, "Add", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
    }

    @OnClick(R.id.fab_remove)
    public void removeFavorite(View view) {
        Snackbar.make(view, "Remove", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
    }
    @OnClick(R.id.action_up)
    public void backPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
        } else {
            onSupportNavigateUp();
        }
    }
    @OnClick(R.id.action_home)
    public void homePressed() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
