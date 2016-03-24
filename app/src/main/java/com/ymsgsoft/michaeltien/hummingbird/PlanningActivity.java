package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class PlanningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            final String ARG_PLAN_FROM_ID = getString(R.string.intent_plan_key_from);
            final String ARG_PLAN_TO_ID = getString(R.string.intent_plan_key_to);

            PlaceObject mFromObject = getIntent().getParcelableExtra(ARG_PLAN_FROM_ID);
            PlaceObject mToObject = getIntent().getParcelableExtra(ARG_PLAN_TO_ID);
            PlanningActivityFragment fragment = new PlanningActivityFragment();

            Bundle arguments = new Bundle();
            if (mFromObject != null)
                arguments.putParcelable(ARG_PLAN_FROM_ID, mFromObject);
            if (mToObject != null)
                arguments.putParcelable(ARG_PLAN_TO_ID, mToObject);
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_planning_id, fragment)
                    .commit();
        }
    }
}
