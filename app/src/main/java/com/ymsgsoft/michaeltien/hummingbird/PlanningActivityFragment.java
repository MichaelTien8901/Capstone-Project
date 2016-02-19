package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlanningActivityFragment extends Fragment {
    final String PLAN_FROM_ID = "PLAN_FROM_ID";
    final String PLAN_TO_ID = "PLAN_TO_ID";
    private final int SEARCH_FROM_REQUEST_ID = 1;
    private final int SEARCH_TO_REQUEST_ID = 2;
    @Bind(R.id.fromTextView) TextView mFromTextView;
    @Bind(R.id.toTextView) TextView mToTextView;
    @Bind(R.id.departTextView) TextView mDepartView;
    protected PlaceObject mFromObject;
    protected PlaceObject mToObject;

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
    private void updateSearchText()
    {
        String htmltext = "<html> <font size=\"24\" color=\"red\">" + getString(R.string.plan_from_title) + "</font>" + " " + mFromObject.title + "</html>";
        Spanned sp = Html.fromHtml(htmltext);
        mFromTextView.setText(sp);
        htmltext = "<html> <font size=\"24\" color=\"red\">" + getString(R.string.plan_to_title) + "</font>" + " " + mToObject.title + "</html>";
        sp = Html.fromHtml(htmltext);
        mToTextView.setText(sp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planning, container, false);
        ButterKnife.bind(this, rootView);
        if (savedInstanceState != null) {
            mFromObject = savedInstanceState.getParcelable(PLAN_FROM_ID);
            mToObject = savedInstanceState.getParcelable(PLAN_TO_ID);
        } else {
            Bundle arguments = getArguments();
            mFromObject = new PlaceObject();
            mFromObject.title = getString(R.string.init_search_here);
            mToObject = new PlaceObject();
            mToObject.title = "";

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
                    mFromObject.placeId = place_id;
                    updateSearchText();
                }
                break;
        }
    }
}
