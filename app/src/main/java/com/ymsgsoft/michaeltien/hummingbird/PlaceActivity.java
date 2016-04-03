package com.ymsgsoft.michaeltien.hummingbird;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Places;
import com.ymsgsoft.michaeltien.hummingbird.playservices.PlaceAutocompleteAdapter;

public class PlaceActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {
    final static String TAG = PlaceActivity.class.getSimpleName();
    public static final String PLACE_ID = "place_id";
    public static final String PLACE_TEXT = "place_text";
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;

//    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
//            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        setContentView(R.layout.activity_place);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteView.setOnEditorActionListener(mAutoCompleteEditorActionLister);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString(PLACE_TEXT);
            mAutocompleteView.setText(value);
        }
        // Retrieve the TextViews that will display details and attributions of the selected place.

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null, null);
//        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        mAutocompleteView.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        Button clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });
        Button searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformSearch();
            }
        });
//        setContentView(R.layout.activity_place);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private TextView.OnEditorActionListener mAutoCompleteEditorActionLister
            = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                PerformSearch();
                return true;
            }
            return false;
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = "place_id:" + item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);
            Intent resultData = new Intent();
            resultData.putExtra(PLACE_ID, placeId);
            resultData.putExtra(PLACE_TEXT, primaryText);
            setResult(RESULT_OK,resultData);
            finish();
            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(mGoogleApiClient, placeId);
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//
//            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
//                    Toast.LENGTH_SHORT).show();
//            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };
    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
//            = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
//                // Request did not complete successfully
//                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            // Get the Place object from the buffer.
//            final Place place = places.get(0);
//
//            // Format details of the place for display and show it in a TextView.
//            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));
//
//            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }
//
//            Log.i(TAG, "Place details received: " + place.getName());
//
//            places.release();
//        }
//    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

    }
    private void PerformSearch() {
        final CharSequence primaryText = mAutocompleteView.getText();
        final String place_id = primaryText.toString();
        Intent resultData = new Intent();
        resultData.putExtra(PLACE_ID, place_id);
        resultData.putExtra(PLACE_TEXT, primaryText);
        setResult(RESULT_OK, resultData);
        finish();
    }

//    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
//                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
//        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//    }
//
}
