package com.ymsgsoft.michaeltien.hummingbird.data;

/**
 * Created by Michael Tien on 2015/12/2.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Leg;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Route;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Step;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Step_;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.LegsValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.MicroStepsValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.RoutesValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.StepsValuesBuilder;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyBulkInsert;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = RoutesProvider.AUTHORITY,
        database = RoutesDbHelper.class,
        packageName = "com.ymsgsoft.michaeltien.hummingbird.generated_data")
public final class RoutesProvider {
    public static final String AUTHORITY = "com.ymsgsoft.michaeltien.hummingbird";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private RoutesProvider() {
    }

    interface Path {
        String ROUTES = "routes";
        String LEGS = "legs";
        String STEPS = "steps";
        String MICRO_STEPS = "micro_steps";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = Tables.ROUTES) public static class Routes {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +  AUTHORITY + "/" + Path.ROUTES;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "/" + Path.ROUTES;
        @ContentUri(
                path = Path.ROUTES,
//                type = "vnd.android.cursor.dir/" +  AUTHORITY + "/" + Path.ROUTES,
                type = CONTENT_TYPE,
                defaultSort = RouteColumns.ID + " ASC"
        )
//        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/routes");
        public static final Uri CONTENT_URI = buildUri(Path.ROUTES);

        @InexactContentUri(
                path = Path.ROUTES + "/#",
                name = "ROUTE_ID",
//                type = "vnd.android.cursor.item/" + AUTHORITY + "/" + Path.ROUTES,
                type = CONTENT_ITEM_TYPE,
                whereColumn = RouteColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.ROUTES, String.valueOf(id));
        }

        @NotifyBulkInsert(paths = Path.ROUTES)
        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] values, long[] ids) {
            return new Uri[] {
                    uri,
            };
        }
        @NotifyDelete(paths = Path.ROUTES + "/#") public static Uri[] onDelete(Context context,
                                                                              Uri uri) {
            final long noteId = Long.valueOf(uri.getPathSegments().get(1));
            return new Uri[] {
                    withId(noteId), uri
            };
        }

    }
    @TableEndpoint(table = Tables.LEGS) public static class Legs {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +  AUTHORITY + "/" + Path.LEGS;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "/" + Path.LEGS;
        @ContentUri(
                path = Path.LEGS,
//                type = "vnd.android.cursor.dir/" +  AUTHORITY + "/" + Path.LEGS,
                type = CONTENT_TYPE,
                defaultSort = LegColumns.ID + " ASC"
        )
        public static final Uri CONTENT_URI = buildUri(Path.LEGS);

        @InexactContentUri(
                name = "LEG_ID",
                path = Path.LEGS + "/#",
//                type = "vnd.android.cursor.item/" + AUTHORITY + "/" + Path.LEGS,
                type = CONTENT_ITEM_TYPE,
                whereColumn = LegColumns.ID,
                pathSegment = 1)
        public static Uri withId(long legId) {
            return buildUri(Path.LEGS, String.valueOf(legId));
        }

        @InexactContentUri(
                name = "FROM_ROUTE",
                path = Path.ROUTES  + "/#/" + Path.LEGS,
                type = "vnd.android.cursor.dir/" + AUTHORITY + "/" + Path.LEGS,
                whereColumn = LegColumns.ROUTES_ID,
                defaultSort = LegColumns.ID + " ASC",
                pathSegment = 1)
        public static Uri fromRoute(long routeId) {
            return buildUri(Path.ROUTES, String.valueOf(routeId), Path.LEGS );
        }

    }
    @TableEndpoint(table = Tables.STEPS) public static class Steps {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +  AUTHORITY + "/" + Path.STEPS;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "/" + Path.STEPS;
        @ContentUri(
                path = Path.STEPS,
//                type = "vnd.android.cursor.dir/" +  AUTHORITY + "/" + Path.STEPS,
                type = CONTENT_TYPE,
                defaultSort = StepColumns.ID + " ASC"
        )
        public static final Uri CONTENT_URI = buildUri(Path.STEPS);

        @InexactContentUri(
                name = "STEP_ID",
                path = Path.STEPS + "/#",
                type = "vnd.android.cursor.item/" + AUTHORITY + "/" + Path.STEPS,
                whereColumn = StepColumns.ID,
                pathSegment = 1)
        public static Uri withId(long stepId) {
            return buildUri(Path.STEPS, String.valueOf(stepId));
        }

        @InexactContentUri(
                name = "FROM_LEG",
                path = Path.LEGS + "/#/" + Path.STEPS + "/",
//                type = "vnd.android.cursor.dir/" + AUTHORITY + "/" + Path.STEPS,
                type = CONTENT_TYPE,
                whereColumn = StepColumns.LEG_ID,
                defaultSort = LegColumns.ID + " ASC",
                pathSegment = 1)
        public static Uri fromLeg(long legId) {
            return buildUri(Path.LEGS, String.valueOf(legId),Path.STEPS );
        }
    }
    @TableEndpoint(table = Tables.MICRO_STEPS) public static class MicroSteps {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +  AUTHORITY + "/" + Path.MICRO_STEPS;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "/" + Path.MICRO_STEPS;
        @ContentUri(
                path = Path.MICRO_STEPS,
//                type = "vnd.android.cursor.dir/" +  AUTHORITY + "/" + Path.MICRO_STEPS,
                type = CONTENT_TYPE,
                defaultSort = MicroStepColumns.ID + " ASC"
        )
        public static final Uri CONTENT_URI = buildUri(Path.MICRO_STEPS);

        @InexactContentUri(
                name = "STEP_ID",
                path = Path.MICRO_STEPS + "/#",
//                type = "vnd.android.cursor.item/" + AUTHORITY + "/" + Path.MICRO_STEPS,
                type = CONTENT_ITEM_TYPE,
                whereColumn = MicroStepColumns.ID,
                pathSegment = 1)
        public static Uri withId(long microStepId) {
            return buildUri(Path.MICRO_STEPS, String.valueOf(microStepId));
        }

        @InexactContentUri(
                name = "FROM_STEP",
                path = Path.STEPS + "/#/" + Path.MICRO_STEPS,
//                type = "vnd.android.cursor.dir/" + AUTHORITY + "/" + Path.MICRO_STEPS,
                type = CONTENT_TYPE,
                whereColumn = MicroStepColumns.STEP_ID,
                defaultSort = MicroStepColumns.ID + " ASC",
                pathSegment = 1)
        public static Uri fromStep(long stepId) {
            return buildUri(Path.STEPS, String.valueOf(stepId), Path.MICRO_STEPS);
        }

    }
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
    static ContentValues createStepValues(Step step, long legId){
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
    public static void insertRoute(Context mContext, Route route) {
        ContentValues routeValues = createRouteValues(route);
        Uri routeUri = mContext.getContentResolver().insert(RoutesProvider.Routes.CONTENT_URI, routeValues);
        long routeRowId = ContentUris.parseId(routeUri);
        for ( Leg leg: route.legs) {
            insertLeg( mContext, leg, routeRowId);
        }
    }
    public static void insertLeg(Context mContext, Leg leg, long routeRowId) {
        ContentValues values = createLegValues(leg, routeRowId);
        Uri legUri = mContext.getContentResolver().insert(RoutesProvider.Legs.CONTENT_URI, values);
        long legRowId = ContentUris.parseId(legUri);
        for (Step step: leg.steps) {
            insertStep(mContext, step, legRowId);
        }
    }
    public static void insertStep(Context mContext, Step step, long legRowId) {
        ContentValues values = createStepValues(step, legRowId);
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
