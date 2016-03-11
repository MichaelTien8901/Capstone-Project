package com.ymsgsoft.michaeltien.hummingbird.data;

/**
 * Created by Michael Tien on 2015/12/2.
 */

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables;

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
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

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
}
