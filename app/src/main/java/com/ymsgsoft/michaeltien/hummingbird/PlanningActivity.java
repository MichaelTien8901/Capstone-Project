package com.ymsgsoft.michaeltien.hummingbird;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlanningActivity extends AppCompatActivity  {
    final String LOG_TAG = PlaceActivity.class.getSimpleName();
    private final String PLANNING_TAG = "planning_tag";
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");

    protected SlideDateTimeListener mListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            Toast.makeText(PlanningActivity.this,
                    mFormatter.format(date), Toast.LENGTH_SHORT).show();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(PlanningActivity.this,
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };


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
                    .add(R.id.fragment_planning_id, fragment, PLANNING_TAG)
                    .commit();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.test_fab);
        if ( fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(mListener)
                            .setInitialDate(new Date())
                                    .setMinDate(Calendar.getInstance().getTime())
                                    //.setMaxDate(maxDate)
                                    //.setIs24HourTime(true)
                                    .setTheme(SlideDateTimePicker.HOLO_LIGHT)
//                                    .setIndicatorColor(Color.parseColor("#990000"))
                                    .setIndicatorColor(getResources().getColor(R.color.colorAccent))
                    .build()
                            .show();                 }
            });
    }
}
