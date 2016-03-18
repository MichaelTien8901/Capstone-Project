package com.ymsgsoft.michaeltien.hummingbird.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Leg;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Route;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Step;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Step_;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.LegsValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.MicroStepsValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.NavigatesValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.RoutesValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.StepsValuesBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Tien on 2016/3/11.
 */
public class DbUtils {
    static ContentValues createRouteValues(Route route) {
        RoutesValuesBuilder builder = new RoutesValuesBuilder()
                .overviewPolylines(route.overview_polyline.points)
                .summary(route.summary);
        StringBuilder warnings = new StringBuilder();
        for ( String s: route.warnings) {
            warnings.append(s + "  " );
        }
        builder.warning(warnings.toString());
        return builder.values();
    }
    static ContentValues createLegValues(Leg leg, long routeId) {
        return new LegsValuesBuilder()
                .routesId( routeId)
                .arrivalTime(leg.arrival_time.value)
                .arrivalTimeText(leg.arrival_time.text)
                .departureTime(leg.departure_time.value)
                .departureTimeText(leg.departure_time.text)
                .distance(leg.distance.value)
                .distanceText(leg.distance.text)
                .duration(leg.duration.value)
                .durationText(leg.duration.text)
                .startAddress(leg.start_address)
                .startLat((float) leg.start_location.lat.floatValue())
                .startLng((float) leg.start_location.lng.floatValue())
                .endAddress(leg.end_address)
                .endLat((float) leg.end_location.lat.floatValue())
                .endLng((float) leg.end_location.lng.floatValue())
                .values();
    }
    static ContentValues createStepValues(Step step, long legId, long routeId){
        return new StepsValuesBuilder()
                .legId(legId)
                .polyline(step.polyline.points)
                .instruction(step.html_instructions)
                .distance(step.distance.value)
                .distanceText(step.distance.text)
                .duration(step.duration.value)
                .durationText(step.duration.text)
                .startLat(step.start_location.lat.floatValue())
                .startLng(step.start_location.lng.floatValue())
                .endLat(step.end_location.lat.floatValue())
                .endLng(step.end_location.lng.floatValue())
                .travelMode(step.travel_mode)
                .routeId(routeId)
                .values();
    }
    static ContentValues createMicroStepValues(Step_ step, long stepId){
        return new MicroStepsValuesBuilder()
                .stepId(stepId)
                .polyline(step.polyline.points)
                .instruction(step.html_instructions)
                .distance(step.distance.value)
                .distanceText(step.distance.text)
                .duration(step.duration.value)
                .durationText(step.duration.text)
                .startLat(step.start_location.lat.floatValue())
                .startLng(step.start_location.lng.floatValue())
                .endLat(step.end_location.lat.floatValue())
                .endLng(step.end_location.lng.floatValue())
                .travelMode(step.travel_mode)
                .values();
    }
    static ContentValues createNavigateValuesFromStep(Step step, long routeId, int count){
        return new NavigatesValuesBuilder()
                .polyline(step.polyline.points)
                .instruction(step.html_instructions)
                .distance(step.distance.value)
                .distanceText(step.distance.text)
                .duration(step.duration.value)
                .durationText(step.duration.text)
                .startLat(step.start_location.lat.floatValue())
                .startLng(step.start_location.lng.floatValue())
                .endLat(step.end_location.lat.floatValue())
                .endLng(step.end_location.lng.floatValue())
                .travelMode(step.travel_mode)
                .routesId(routeId)
                .level(0)
                .count(count)
                .values();
    }
    static ContentValues createNavigateValuesFromMicroStep(Step_ step, long routeId, long level){
        return new NavigatesValuesBuilder()
                .polyline(step.polyline.points)
                .instruction(step.html_instructions)
                .distance(step.distance.value)
                .distanceText(step.distance.text)
                .duration(step.duration.value)
                .durationText(step.duration.text)
                .startLat(step.start_location.lat.floatValue())
                .startLng(step.start_location.lng.floatValue())
                .endLat(step.end_location.lat.floatValue())
                .endLng(step.end_location.lng.floatValue())
                .travelMode(step.travel_mode)
                .routesId(routeId)
                .level(level)
                .count(0)
                .values();
    }
    static void extractRouteSummary(Route routeObject, ContentValues values ) {
        //values.put(RouteColumns.);
        String depart_time = "";
        String duration = "";
        String transitNo;
//        Boolean break_flag1 = false;
        List<String> transit_list = new ArrayList<>();

        for ( Leg legObject: routeObject.legs ) {
            if ( depart_time.isEmpty())
                depart_time = legObject.departure_time.text;
            if ( duration.isEmpty())
                duration = legObject.duration.text;
            for (Step step: legObject.steps) {
                if ( step.travel_mode.equals("TRANSIT"))
                    transit_list.add(step.transit_details.line.short_name);
            }
        }
        transitNo = TextUtils.join(",", transit_list);
        values.put(RouteColumns.EXT_DEPART_TIME, depart_time);
        values.put(RouteColumns.EXT_DURATION, duration);
        values.put(RouteColumns.EXT_TRANSIT_NO, transitNo);
    }
    static void extractStepSummary(Step stepObject, ContentValues values ) {
        if ( stepObject.transit_details != null && stepObject.transit_details.line != null) {
            String transitNo = stepObject.transit_details.line.short_name;
            values.put(StepColumns.TRANSIT_NO, transitNo);
        }
    }

    public static void insertRoute(Context mContext, Route route) {
        ContentValues routeValues = createRouteValues(route);
        extractRouteSummary(route, routeValues);
        Uri routeUri = mContext.getContentResolver().insert(RoutesProvider.Routes.CONTENT_URI, routeValues);
        long routeRowId = ContentUris.parseId(routeUri);
        for ( Leg leg: route.legs) {
            insertLeg( mContext, leg, routeRowId);
        }
        insertNavigateValues(mContext, route, routeRowId);
    }
    public static void insertNavigateValues(Context mContext, Route route, long routeRowId) {
        for ( Leg leg: route.legs) {
            for (Step step: leg.steps) {
                ContentValues values = createNavigateValuesFromStep(step, routeRowId, step.steps.size());
                mContext.getContentResolver().insert(RoutesProvider.Navigates.CONTENT_URI, values);
                if ( step.steps != null && step.steps.size() != 0) {
                    long level = 1;
                    for ( Step_ microStep: step.steps) {
                        ContentValues values1 = createNavigateValuesFromMicroStep(microStep, routeRowId, level++);
                        mContext.getContentResolver().insert(RoutesProvider.Navigates.CONTENT_URI, values1);
                    }
                }
            }
        }
    }

    public static void insertLeg(Context mContext, Leg leg, long routeRowId) {
        ContentValues values = createLegValues(leg, routeRowId);
        Uri legUri = mContext.getContentResolver().insert(RoutesProvider.Legs.CONTENT_URI, values);
        long legRowId = ContentUris.parseId(legUri);
        for (Step step: leg.steps) {
            insertStep(mContext, step, legRowId, routeRowId);
        }
    }
    public static void insertStep(Context mContext, Step step, long legRowId, long routeRowId) {
        ContentValues values = createStepValues(step, legRowId, routeRowId);
        extractStepSummary(step, values);
        Uri stepUri = mContext.getContentResolver().insert(RoutesProvider.Steps.CONTENT_URI, values);
        long stepRowId = ContentUris.parseId(stepUri);
        for (Step_ micro_step : step.steps) {
            insertMicroStep(mContext, micro_step, stepRowId);
        }
    }
    static void insertMicroStep(Context mContext, Step_ micro_step, long stepRowId) {
        ContentValues values = createMicroStepValues(micro_step, stepRowId);
        mContext.getContentResolver().insert(RoutesProvider.MicroSteps.CONTENT_URI, values);
    }
}
