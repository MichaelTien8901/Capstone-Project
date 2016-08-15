package com.ymsgsoft.michaeltien.hummingbird;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ymsgsoft.michaeltien.hummingbird.data.FavoriteColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RouteColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.playservices.FavoriteRecyclerViewAdapter;

public class FavoriteActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        OnFavoriteItemClickListener {
    public static final String START_PLACEID_PARAM = "start_placeid_param";
    public static final String END_PLACEID_PARAM = "end_placeid_param";
    public static final String START_PARAM = "start_param";
    public static final String END_PARAM = "end_param";
    public static final String ROUTE_ID_PARAM = "route_id_param";
    public static final String ACTION_PARAM = "action_param";
    public static final String ACTION_LOAD = "action_load";
    public static final String ACTION_PLANNING = "action_planning";
    private final int FAVORITE_LOADER = 200;
    protected FavoriteRecyclerViewAdapter mAdapter;
    FavoriteRecyclerViewAdapter.FavoriteObject mData;
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                RoutesProvider.Favorite.CONTENT_URI,
                null,
                null,
                null,
                FavoriteColumns.ID + " ASC" );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // for navigation back button to work
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_favorites);
        mAdapter = new FavoriteRecyclerViewAdapter(this, R.layout.list_item_favorite, null, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }
    private void showNoSelectionWarning() {
        Toast.makeText(this, R.string.favorite_no_selected_item, Toast.LENGTH_SHORT).show();
    }
    private void startShareIntent(FavoriteRecyclerViewAdapter.FavoriteObject data) {
        new ShareRouteTask().execute(data);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if ( id == R.id.action_home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();
            return true;
        } else if (id == R.id.action_navigate) {
            if (mData != null)
                startLoadFavoriteRouteAction(mData, ACTION_LOAD);
            else showNoSelectionWarning();
            return true;
        } else if (id == R.id.action_delete) {
            if ( mData != null) {
                DirectionService.startActionRemoveFavorite(this, mData.routeId);
                mData = null;
                mAdapter.resetSelection();
            } else showNoSelectionWarning();
            return true;
        } else if ( id == R.id.action_refresh) {
            if ( mData != null ){
                startLoadFavoriteRouteAction(mData, ACTION_PLANNING);
            } else showNoSelectionWarning();
            return true;
        } else if ( id == R.id.action_share) {
            if ( mData != null) {
                startShareIntent(mData);
            } else showNoSelectionWarning();
            return true;
        }
        return super.onOptionsItemSelected(item);
//        if ( id == R.id.action_search) {
////            performSearch();
////            return true;
//            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//            try {
//                startActivityForResult(
//                        builder.build(this), PLACE_PICKER_REQUEST);
//
//            } catch (GooglePlayServicesNotAvailableException
//                    | GooglePlayServicesRepairableException e) {
//                // What did you do?? This is why we check Google Play services in onResume!!!
//                // The difference in these exception types is the difference between pausing
//                // for a moment to prompt the user to update/install/enable Play services vs
//                // complete and utter failure.
//                // If you prefer to manage Google Play services dynamically, then you can do so
//                // by responding to these exceptions in the right moment. But I prefer a cleaner
//                // user experience, which is why you check all of this when the app resumes,
//                // and then disable/enable features based on that availability.
//            }

    }

    void startLoadFavoriteRouteAction(FavoriteRecyclerViewAdapter.FavoriteObject data, String action ) {
        Intent resultData = new Intent();
        resultData.putExtra(ACTION_PARAM, action); // load details or planning
        resultData.putExtra(START_PLACEID_PARAM, data.start_place_id);
        resultData.putExtra(END_PLACEID_PARAM, data.end_place_id);
        resultData.putExtra(START_PARAM, data.start_name);
        resultData.putExtra(END_PARAM, data.end_name);
        resultData.putExtra(ROUTE_ID_PARAM, data.routeId);
        setResult(RESULT_OK,resultData);
        finish();

    }
    @Override
    public void OnItemClick(FavoriteRecyclerViewAdapter.FavoriteObject data, int position) {
        mData = data;
//        startLoadFavoriteRouteAction( mData );
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
        } else {
            onSupportNavigateUp();
        }
//        super.onBackPressed();
    }
    private class ShareRouteTask extends AsyncTask<FavoriteRecyclerViewAdapter.FavoriteObject, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, result);
            startActivity(shareIntent);
        }

        @Override
        protected String doInBackground(FavoriteRecyclerViewAdapter.FavoriteObject... params) {
            String LF = System.getProperty(getString(R.string.LINE_SEPARATOR));
            StringBuilder builder = new StringBuilder();
            builder.append(params[0].start_name)
                    .append(getString(R.string.share_name_connector) )
                    .append(params[0].end_name)
                    .append(LF);
            Cursor cursor = getContentResolver().query(
                    RoutesProvider.Routes.CONTENT_URI,
                    null,
                    RouteColumns.ID + " =?",
                    new String[]{String.valueOf(params[0].routeId)},
                    null );
            if ( cursor != null ) {
                if ( cursor.moveToFirst()) {
                    builder.append(getString(R.string.share_departure_title))
                        .append(cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DEPART_TIME)))
                        .append(LF);

                    builder.append(getString(R.string.share_duration_title))
                        .append(cursor.getString(cursor.getColumnIndex(RouteColumns.EXT_DURATION)))
                        .append(LF);
                }
                cursor.close();
            }
            return builder.toString();
        }
    }

}
