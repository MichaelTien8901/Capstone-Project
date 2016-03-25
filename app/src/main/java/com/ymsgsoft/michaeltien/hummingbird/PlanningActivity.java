package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.playservices.RouteAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlanningActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    final String LOG_TAG = PlaceActivity.class.getSimpleName();
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");
    public static final int DIRECTION_LOADER = 0;
    final String PLAN_FROM_ID = "PLAN_FROM_ID";
    final String PLAN_TO_ID = "PLAN_TO_ID";
    private final int SEARCH_FROM_REQUEST_ID = 1;
    private final int SEARCH_TO_REQUEST_ID = 2;
    @Bind(R.id.fromTextView) TextView mFromTextView;
    @Bind(R.id.toTextView) TextView mToTextView;
    @Bind(R.id.departTextView) TextView mDepartView;
    @Bind(R.id.routeListView) ListView mRouteListView;
    protected PlaceObject mFromObject;
    protected PlaceObject mToObject;
    protected RouteAdapter mRouteAdapter;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if ( mFromObject != null) {
            outState.putParcelable(PLAN_FROM_ID, mFromObject);
        }
        if ( mToObject != null) {
            outState.putParcelable(PLAN_TO_ID, mToObject);
        }
        super.onSaveInstanceState(outState);
    }
    private void updateSearchText(){
        String htmltext = "<html> <font size=\"24\" color=\"red\">" + getString(R.string.plan_from_title) + "</font>" + " " + mFromObject.title + "</html>";
        Spanned sp = Html.fromHtml(htmltext);
        mFromTextView.setText(sp);
        htmltext = "<html> <font size=\"24\" color=\"red\">" + getString(R.string.plan_to_title) + "</font>" + " " + mToObject.title + "</html>";
        sp = Html.fromHtml(htmltext);
        mToTextView.setText(sp);
    }
    private void tryQueryRoutes() {
        if ( mFromObject.placeId.isEmpty() || mToObject.placeId.isEmpty()) return;
        String origin = mFromObject.placeId;
        String destination = mToObject.placeId;
        DirectionIntentService.startActionQueryDirection(this, origin, destination);
        getSupportLoaderManager().restartLoader(DIRECTION_LOADER, null, this);
    }

    protected SlideDateTimeListener mListener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date)
        {
            Toast.makeText(PlanningActivity.this,
                    mFormatter.format(date), Toast.LENGTH_SHORT).show();
        }
        // Optional cancel listener
//        @Override
//        public void onDateTimeCancel()
//        {
//            Toast.makeText(PlanningActivity.this,
//                    "Canceled", Toast.LENGTH_SHORT).show();
//        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRouteAdapter = new RouteAdapter( this, null, 0 );
        mRouteListView.setAdapter(mRouteAdapter);
        getSupportLoaderManager().initLoader(DIRECTION_LOADER, null, this);
        mRouteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // retrieve routeId
                RouteAdapter.RouteHolder selected = (RouteAdapter.RouteHolder) view.getTag();
                mRouteAdapter.selectedRouteId = selected.mData.routeId;
                // launch detail activity
                Intent intent = new Intent(PlanningActivity.this, DetailRouteActivity.class);
                intent.putExtra(getString(R.string.intent_route_key), selected.mData);
                startActivity(intent);
            }
        });
        if ( savedInstanceState != null) {
            mFromObject = savedInstanceState.getParcelable(PLAN_FROM_ID);
            mToObject = savedInstanceState.getParcelable(PLAN_TO_ID);
        } else {
            final String ARG_PLAN_FROM_ID = getString(R.string.intent_plan_key_from);
            final String ARG_PLAN_TO_ID = getString(R.string.intent_plan_key_to);
            mFromObject = getIntent().getParcelableExtra(ARG_PLAN_FROM_ID);
            mToObject = getIntent().getParcelableExtra(ARG_PLAN_TO_ID);
        }
        if ( mFromObject == null) {
            mFromObject = new PlaceObject();
            mFromObject.title = getString(R.string.init_search_here);
            mFromObject.placeId = "";
        }
        if ( mToObject == null) {
            mToObject = new PlaceObject();
            mToObject.title = "";
            mToObject.placeId = "";
        }

        updateSearchText();
        mFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanningActivity.this, PlaceActivity.class);
                String searchText = mFromObject.title;
                if (!searchText.equals("Here")) {
                    intent.putExtra(PlaceActivity.PLACE_TEXT, searchText);
                }
                startActivityForResult(intent, SEARCH_FROM_REQUEST_ID);
            }
        });
        mToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanningActivity.this, PlaceActivity.class);
                String searchText = mToObject.title;
                if (!searchText.equals("")) {
                    intent.putExtra(PlaceActivity.PLACE_TEXT, searchText);
                }
                startActivityForResult(intent, SEARCH_TO_REQUEST_ID);
            }
        });
        mDepartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // test query
                tryQueryRoutes();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.test_fab);
        if ( fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateTimeDialog();
                }
            });
    }
    private void showDateTimeDialog() {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(mListener)
                .setInitialDate(new Date())
                .setMinDate(Calendar.getInstance().getTime())
                        //.setMaxDate(maxDate)
                        //.setIs24HourTime(true)
                .setTheme(SlideDateTimePicker.HOLO_LIGHT)
//                                    .setIndicatorColor(Color.parseColor("#990000"))
                .setIndicatorColor(getResources().getColor(R.color.colorAccent))
                .build()
                .show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch( requestCode ){
            case SEARCH_FROM_REQUEST_ID:
                if ( resultCode == Activity.RESULT_OK) {
                    String place_id = data.getStringExtra(PlaceActivity.PLACE_ID);
                    //String place_name = data.getStringExtra(PlaceActivity.PLACE_TEXT);
                    CharSequence place_name = data.getCharSequenceExtra(PlaceActivity.PLACE_TEXT);
                    mFromObject.title = place_name.toString();
                    mFromObject.placeId = place_id;
                    updateSearchText();
                }

                break;
            case SEARCH_TO_REQUEST_ID:
                if ( resultCode == Activity.RESULT_OK) {
                    String place_id = data.getStringExtra(PlaceActivity.PLACE_ID);
                    //String place_name = data.getStringExtra(PlaceActivity.PLACE_TEXT);
                    CharSequence place_name = data.getCharSequenceExtra(PlaceActivity.PLACE_TEXT);
                    mToObject.title = place_name.toString();
                    mToObject.placeId = place_id;
                    updateSearchText();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mRouteAdapter.swapCursor(null);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // set filter for current
//        Uri base_url = Uri.parse("content://"+RoutesProvider.AUTHORITY);
//        base_url.buildUpon().appendPath(RouteProvider.)
        return new CursorLoader(this,
                RoutesProvider.Routes.CONTENT_URI,
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mRouteAdapter.swapCursor(cursor);
    }

}
