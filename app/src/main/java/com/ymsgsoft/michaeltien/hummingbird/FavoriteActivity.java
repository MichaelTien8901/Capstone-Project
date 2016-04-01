package com.ymsgsoft.michaeltien.hummingbird;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
    private final int FAVORITE_LOADER = 200;
    protected FavoriteRecyclerViewAdapter mAdapter;
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
        } else if (id == R.id.action_delete) {
            Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
            return true;
        } else if ( id == R.id.action_refresh) {
            Toast.makeText(this, "refresh", Toast.LENGTH_SHORT).show();
            return true;
        } else if ( id == R.id.action_share) {
            Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
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
    @Override
    public void OnItemClick(FavoriteRecyclerViewAdapter.FavoriteObject data, int position) {
        Intent resultData = new Intent();
        resultData.putExtra(START_PLACEID_PARAM, data.start_place_id);
        resultData.putExtra(END_PLACEID_PARAM, data.end_place_id);
        resultData.putExtra(START_PARAM, data.start_name);
        resultData.putExtra(END_PARAM, data.end_name);
        resultData.putExtra(ROUTE_ID_PARAM, data.routeId);
        setResult(RESULT_OK,resultData);
        finish();
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
}
