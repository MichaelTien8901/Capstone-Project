package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ymsgsoft.michaeltien.hummingbird.data.PrefUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailRouteActivity extends AppCompatActivity implements FavoriteDialog.FavoriteDialogListener {
    static final String SAVE_ARG_KEY = "save_arg_key";
    final String PLAN_FROM_ID = "PLAN_FROM_ID";
    final String PLAN_TO_ID = "PLAN_TO_ID";

    protected RouteParcelable mRouteObject;
    protected PlaceObject mFromObject;
    protected PlaceObject mToObject;
    @Bind(R.id.fab_add)
    FloatingActionButton mAddRemoveBtn;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ( mRouteObject != null) {
            outState.putParcelable(SAVE_ARG_KEY, mRouteObject);
            PrefUtils.saveRouteParcelableToPref(this, getString(R.string.intent_route_key), mRouteObject);
        }
        if ( mFromObject != null) {
            outState.putParcelable(PLAN_FROM_ID, mFromObject);
        }
        if ( mToObject != null) {
            outState.putParcelable(PLAN_TO_ID, mToObject);
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

        final String ARG_ROUTE_KEY_ID = getString(R.string.intent_route_key);
        if ( savedInstanceState == null ) {
            mRouteObject = getIntent().getParcelableExtra(ARG_ROUTE_KEY_ID);
            mFromObject = getIntent().getParcelableExtra(PLAN_FROM_ID);
            mToObject = getIntent().getParcelableExtra(PLAN_TO_ID);
        } else {
            if (savedInstanceState.containsKey(SAVE_ARG_KEY))
                mRouteObject = savedInstanceState.getParcelable(SAVE_ARG_KEY);
            if (savedInstanceState.containsKey(PLAN_FROM_ID))
                mFromObject = savedInstanceState.getParcelable(PLAN_FROM_ID);
            if ( savedInstanceState.containsKey(PLAN_TO_ID))
                mToObject = savedInstanceState.getParcelable(PLAN_TO_ID);
        }
        if ( mRouteObject == null) {
            mRouteObject = PrefUtils.restoreRouteParcelableFromPref(this, getString(R.string.intent_route_key));
//            mRouteObject = PrefUtils.restoreRouteParcelableFromPref(this, ARG_ROUTE_KEY_ID);
        }
        if ( mRouteObject.isFavorite) {
            mAddRemoveBtn.setImageResource(R.drawable.ic_remove);
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
    public void addFavoriteClick(View view) {
        if ( !mRouteObject.isFavorite) {
            String suggest_name;
            if (mFromObject.title.length() > 0)
                suggest_name = mFromObject.title + " to " + mToObject.title;
            else
                suggest_name = mToObject.title;
            DialogFragment newFragment = new FavoriteDialog();
            Bundle bundle = new Bundle();
            bundle.putString("SUGGEST_NAME", suggest_name);
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "TagFavoriteDialog");
        } else {
            DialogFragment newFragment = new ConfirmRemoveDialog();
            newFragment.show(getFragmentManager(), "TagFavoriteDialog");
        }
    }

//    @OnClick(R.id.fab_remove)
//    public void removeFavoriteClick(View view) {
//        DirectionIntentService.startActionRemoveFavorite(this, mRouteObject.routeId );
//        mRouteObject.isFavorite = false;
//    }
    public void removeFavorite() {
        DirectionIntentService.startActionRemoveFavorite(this, mRouteObject.routeId );
        mRouteObject.isFavorite = false;
        mAddRemoveBtn.setImageResource(R.drawable.ic_add);
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

    //  FavoriteDialog.FavoriteDialogListener
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String saveName) {
        DirectionIntentService.startActionSaveFavorite(this, mFromObject, mToObject, saveName, mRouteObject.routeId );
        mRouteObject.isFavorite = true;
        mAddRemoveBtn.setImageResource(R.drawable.ic_remove);
    }
    public static class ConfirmRemoveDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_favorite_remove_confirm)
                    .setPositiveButton(R.string.dialog_favorite_remove, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Activity activity = getActivity();
                            if (activity instanceof DetailRouteActivity) {
                                ((DetailRouteActivity) activity).removeFavorite();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_favorite_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            return builder.create();
        }
    }
}
