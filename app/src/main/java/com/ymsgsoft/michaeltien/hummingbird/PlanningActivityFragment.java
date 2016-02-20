package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.MapApiService;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Route;
import com.ymsgsoft.michaeltien.hummingbird.playservices.RouteAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlanningActivityFragment extends Fragment {
    final String LOG_TAG = PlanningActivityFragment.class.getSimpleName();
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MapApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapApiService.DirectionApi directionApi = retrofit.create(MapApiService.DirectionApi.class);
        String key = getString(R.string.google_maps_server_key);
        String origin = mFromObject.placeId;
        String destination = mToObject.placeId;
        Call<MapApiService.TransitRoutes> call = directionApi.getDirections(origin, destination, key);
        call.enqueue(new Callback<MapApiService.TransitRoutes>() {
            @Override
            public void onResponse(Response<MapApiService.TransitRoutes> response, Retrofit retrofit) {
                MapApiService.TransitRoutes transitRoutes = response.body();
                Log.v(LOG_TAG, "retrofit query direction status return: " + transitRoutes.status);
                //assertTrue(LOG_TAG + ": retrofit query direction status return: " + transitRoutes.status,
                // transitRoutes.status.equals("OK"));
                mRouteAdapter.clear();
                mRouteAdapter.addAll(transitRoutes.routes);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.v(LOG_TAG, "retrofit query direction status return: " + t.getMessage());
                Toast.makeText(getContext(), "Route not found.", Toast.LENGTH_SHORT).show();
                //fail(LOG_TAG + "testDirectionAsyncQuery" + t.getMessage());
            }
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planning, container, false);
        ButterKnife.bind(this, rootView);
        mRouteAdapter = new RouteAdapter(
                getContext(), // The current context (this activity)
                R.layout.list_item_routes, // The name of the layout ID.
                new ArrayList<Route>());

        mRouteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // choose the details
            }
        });
        mRouteListView.setAdapter(mRouteAdapter);

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
}
