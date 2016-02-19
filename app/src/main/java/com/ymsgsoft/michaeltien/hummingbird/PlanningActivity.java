package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PlanningActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if ( savedInstanceState == null){
//            String ARG_ITEM_ID = getString(R.string.package_prefix) + getString(R.string.intent_key_movie_object);
//            MovieObject mv  = getIntent().getParcelableExtra(ARG_ITEM_ID);
//
//            Bundle arguments = new Bundle();
//            arguments.putParcelable(ARG_ITEM_ID, mv);
//
//            DetailActivityFragment fragment = new DetailActivityFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.movie_detail_container, fragment)
//                    .commit();
            PlanningActivityFragment fragment = new PlanningActivityFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_planning_id, fragment)
                    .commit();
        }
    }

}
