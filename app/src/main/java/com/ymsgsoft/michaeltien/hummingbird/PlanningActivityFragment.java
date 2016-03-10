package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.playservices.RouteAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlanningActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final String LOG_TAG = PlanningActivityFragment.class.getSimpleName();
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
        super.onSaveInstanceState(outState);
        if ( mFromObject != null) {
            outState.putParcelable(PLAN_FROM_ID, mFromObject);
        }
        if ( mToObject != null) {
           outState.putParcelable(PLAN_TO_ID, mToObject);
        }
    }

    public PlanningActivityFragment() {
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
        DirectionIntentService.startActionQueryDirection(getContext(), origin, destination);
        getLoaderManager().restartLoader(DIRECTION_LOADER, null, this);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planning, container, false);
        ButterKnife.bind(this, rootView);
        mRouteAdapter = new RouteAdapter( getContext(), null, 0 );
        mRouteListView.setAdapter(mRouteAdapter);
        getLoaderManager().initLoader(DIRECTION_LOADER, null, this);
        mRouteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // retrieve routeId
                RouteAdapter.RouteHolder selected = (RouteAdapter.RouteHolder) view.getTag();
                mRouteAdapter.selectedRouteId = selected.routeId;
                // launch detail activity
                Intent intent = new Intent( getContext(), DetailRouteActivity.class);
                intent.putExtra(getString(R.string.intent_route_key), selected.routeId);
                intent.putExtra(getString(R.string.intent_overview_polyline_key), selected.mOverviewPolyline);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            mFromObject = savedInstanceState.getParcelable(PLAN_FROM_ID);
            mToObject = savedInstanceState.getParcelable(PLAN_TO_ID);
        } else {
            Bundle arguments = getArguments();
            mFromObject = new PlaceObject();
            mFromObject.title = getString(R.string.init_search_here);
            mFromObject.placeId = "";
            mToObject = new PlaceObject();
            mToObject.title = "";
            mToObject.placeId = "";

            if (arguments != null) {
                final String ARG_PLAN_FROM_ID = getString(R.string.intent_plan_key_from);
                final String ARG_PLAN_TO_ID = getString(R.string.intent_plan_key_to);
                if ( arguments.containsKey(ARG_PLAN_FROM_ID))
                    mFromObject = arguments.getParcelable(ARG_PLAN_FROM_ID);
                if ( arguments.containsKey(ARG_PLAN_TO_ID))
                    mToObject = arguments.getParcelable(ARG_PLAN_TO_ID);
            }
        }
        updateSearchText();
        mFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PlaceActivity.class);
                String searchText = mFromObject.title;
                if ( !searchText.equals("Here")) {
                    intent.putExtra(PlaceActivity.PLACE_TEXT, searchText);
                }
                startActivityForResult(intent, SEARCH_FROM_REQUEST_ID);
            }
        });
        mToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PlaceActivity.class);
                String searchText = mToObject.title;
                if ( !searchText.equals("")) {
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
        return rootView;
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
        return new CursorLoader(getActivity(),
                RoutesProvider.Routes.CONTENT_URI,
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mRouteAdapter.swapCursor(cursor);
    }

}
