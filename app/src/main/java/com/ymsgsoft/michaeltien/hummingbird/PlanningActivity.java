package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import butterknife.OnClick;

public class PlanningActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    final String LOG_TAG = PlaceActivity.class.getSimpleName();
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");
    public static final int DIRECTION_LOADER = 0;
    final String PLAN_FROM_ID = "PLAN_FROM_ID";
    final String PLAN_TO_ID = "PLAN_TO_ID";
    final String PLAN_LIST_VISIBLE_ID = "PLAN_LIST_VISIBLE_ID";
    private final int SEARCH_FROM_REQUEST_ID = 1;
    private final int SEARCH_TO_REQUEST_ID = 2;
    @Bind(R.id.fromTextView) TextView mFromTextView;
    @Bind(R.id.toTextView) TextView mToTextView;
    @Bind(R.id.departTextView) TextView mDepartView;
    @Bind(R.id.routeListView) ListView mRouteListView;
    @Bind(R.id.fragment_planning_id) LinearLayout mListLayout;
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
        outState.putBoolean(PLAN_LIST_VISIBLE_ID, mListLayout.getVisibility() == View.VISIBLE);
        super.onSaveInstanceState(outState);
    }
    private void updateSearchText(){
        mFromTextView.setText(mFromObject.title);
        mToTextView.setText(mToObject.title);
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
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRouteAdapter = new RouteAdapter( this, null, 0 );
        mRouteListView.setAdapter(mRouteAdapter);
//        getSupportLoaderManager().initLoader(DIRECTION_LOADER, null, this);
        mRouteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // retrieve routeId
                RouteAdapter.RouteHolder selected = (RouteAdapter.RouteHolder) view.getTag();
                mRouteAdapter.selectedRouteId = selected.mData.routeId;
                // launch detail activity
                Intent intent = new Intent(PlanningActivity.this, DetailRouteActivity.class);
                intent.putExtra(getString(R.string.intent_route_key), selected.mData);
                intent.putExtra(PLAN_FROM_ID, mFromObject);
                intent.putExtra(PLAN_TO_ID, mToObject);
                startActivity(intent);
            }
        });
        if ( savedInstanceState != null) {
            mFromObject = savedInstanceState.getParcelable(PLAN_FROM_ID);
            mToObject = savedInstanceState.getParcelable(PLAN_TO_ID);
            if (savedInstanceState.getBoolean(PLAN_LIST_VISIBLE_ID))
                getSupportLoaderManager().initLoader(DIRECTION_LOADER, null, this);
        } else {
            Intent intent = getIntent();
            final String ARG_PLAN_FROM_ID = getString(R.string.intent_plan_key_from);
            final String ARG_PLAN_TO_ID = getString(R.string.intent_plan_key_to);
            mFromObject = intent.getParcelableExtra(ARG_PLAN_FROM_ID);
            if ( mFromObject != null)
                mToObject = intent.getParcelableExtra(ARG_PLAN_TO_ID);
            else {
                // from back or up
                getSupportLoaderManager().initLoader(DIRECTION_LOADER, null, this);
            }
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
                    tryQueryRoutes();
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
                    tryQueryRoutes();
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
        if ( cursor != null) {
            mListLayout.setVisibility(View.VISIBLE);
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_planning, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if ( id == R.id.action_settings) {
//            //performSearch();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    @OnClick(R.id.action_up)
    public void backPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
        } else {
            onSupportNavigateUp();
        }
    }
    @OnClick(R.id.from_container)
    public void searchFromPlace() {
        Intent intent = new Intent(PlanningActivity.this, PlaceActivity.class);
        String searchText = mFromObject.title;
        if (!searchText.equals("Here")) {
            intent.putExtra(PlaceActivity.PLACE_TEXT, searchText);
        }
        startActivityForResult(intent, SEARCH_FROM_REQUEST_ID);
    }
    @OnClick(R.id.to_container)
    public void searchToPlace() {
        Intent intent = new Intent(PlanningActivity.this, PlaceActivity.class);
        String searchText = mToObject.title;
        if (!searchText.equals("")) {
            intent.putExtra(PlaceActivity.PLACE_TEXT, searchText);
        }
        startActivityForResult(intent, SEARCH_TO_REQUEST_ID);
    }
}
