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

import com.ymsgsoft.michaeltien.hummingbird.data.HistoryColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.playservices.HistoryRecyclerViewAdapter;


public class HistoryActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        OnHistoryItemClickListener {
    public static final String PLACEID_PARAM = "start_placeid_param";
    public static final String PLACE_PARAM = "start_param";
    private final int FAVORITE_LOADER = 201;
    protected HistoryRecyclerViewAdapter mAdapter;
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
                RoutesProvider.History.CONTENT_URI,
                null,
                null,
                null,
                HistoryColumns.QUERY_TIME + " DESC" );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_history);
        mAdapter = new HistoryRecyclerViewAdapter(this, R.layout.list_item_history, null, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_favorite, menu);
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
//        } else if (id == R.id.action_delete) {
//            Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
//            return true;
//        } else if ( id == R.id.action_refresh) {
//            Toast.makeText(this, "refresh", Toast.LENGTH_SHORT).show();
//            return true;
//        } else if ( id == R.id.action_share) {
//            Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
//            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void OnItemClick(HistoryRecyclerViewAdapter.HistoryObject data, int position) {
        Intent resultData = new Intent();
        resultData.putExtra(PLACE_PARAM, data.place_name);
        resultData.putExtra(PLACEID_PARAM, data.place_id);
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
    }
}
