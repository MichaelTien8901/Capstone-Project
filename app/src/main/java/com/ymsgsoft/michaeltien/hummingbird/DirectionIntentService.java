package com.ymsgsoft.michaeltien.hummingbird;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.MapApiService;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Route;
import com.ymsgsoft.michaeltien.hummingbird.data.DbUtils;
import com.ymsgsoft.michaeltien.hummingbird.data.HistoryColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RouteColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.FavoritesValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.HistoryValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.RoutesValuesBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import retrofit.Call;
//import retrofit.GsonConverterFactory;
//import retrofit.Retrofit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DirectionIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_QUERY_DIRECTION
    private static final String ACTION_QUERY_DIRECTION = "com.ymsgsoft.michaeltien.hummingbird.action.query_direction";
    private static final String ACTION_ADD_FAVORITE_ROUTE = "com.ymsgsoft.michaeltien.hummingbird.action.add_favorite_route";
    private static final String ACTION_REMOVE_FAVORITE_ROUTE = "com.ymsgsoft.michaeltien.hummingbird.action.remove_favorite_route";
    private static final String ACTION_ADD_PLACE_HISTORY = "com.ymsgsoft.michaeltien.hummingbird.action.add_place_history";

    private static final String FROM_PARAM = "com.ymsgsoft.michaeltien.hummingbird.extra.FROM_PARAM";
    private static final String TO_PARAM = "com.ymsgsoft.michaeltien.hummingbird.extra.TO_PARAM";
    private static final String ID_PARAM = "com.ymsgsoft.michaeltien.hummingbird.extra.ID_PARAM";
    private static final String ROUTE_PARAM = "com.ymsgsoft.michaeltien.hummingbird.extra.ROUTE_PARAM";
    private static final String TIME_PARAM = "com.ymsgsoft.michaeltien.hummingbird.extra.TIME_PARAM";
    private static final String PLACE_PARAM = "com.ymsgsoft.michaeltien.hummingbird.extra.PLACE_PARAM";
    public DirectionIntentService() {
        super("DirectionIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionQueryDirection(Context context, String from, String to) {
        Intent intent = new Intent(context, DirectionIntentService.class);
        intent.setAction(ACTION_QUERY_DIRECTION);
        intent.putExtra(FROM_PARAM, from);
        intent.putExtra(TO_PARAM, to);
        context.startService(intent);
    }
    public static void startActionSaveFavorite(Context context, PlaceObject from, PlaceObject to,
                                               String id_name, long routeId, long start_time) {
        Intent intent = new Intent(context, DirectionIntentService.class);
        intent.setAction(ACTION_ADD_FAVORITE_ROUTE);
        intent.putExtra(FROM_PARAM, from);
        intent.putExtra(TO_PARAM, to);
        intent.putExtra(ID_PARAM, id_name);
        intent.putExtra(ROUTE_PARAM, routeId);
        intent.putExtra(TIME_PARAM, start_time);
        context.startService(intent);
    }
    public static void startActionRemoveFavorite(Context context,long routeId) {
        Intent intent = new Intent(context, DirectionIntentService.class);
        intent.setAction(ACTION_REMOVE_FAVORITE_ROUTE);
        intent.putExtra(ROUTE_PARAM, routeId);
        context.startService(intent);
    }
    public static void startActionSavePlace(Context context, PlaceObject place, long query_time) {
        Intent intent = new Intent(context, DirectionIntentService.class);
        intent.setAction( ACTION_ADD_PLACE_HISTORY);
        intent.putExtra(PLACE_PARAM, place);
        intent.putExtra(TIME_PARAM, query_time);
        context.startService(intent);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_QUERY_DIRECTION.equals(action)) {
                // save favorite
                ContentValues values = new RoutesValuesBuilder().isArchive(1).values();
                String mSelectionClause = RouteColumns.IS_FAVORITE + "= ?";
                String[] mSelectionArgs = {String.valueOf(1)};
                getContentResolver().update(RoutesProvider.Routes.CONTENT_URI, values,
                        mSelectionClause, mSelectionArgs);

                // delete all route, except marked archieve
                mSelectionClause = RouteColumns.IS_ARCHIVE + "= ?";
                mSelectionArgs[0] ="0";
                getContentResolver().delete(RoutesProvider.Routes.CONTENT_URI,
                        mSelectionClause, mSelectionArgs);
                final String param1 = intent.getStringExtra(FROM_PARAM);
                final String param2 = intent.getStringExtra(TO_PARAM);
                try {
                    handleActionQueryDirection(param1, param2);
                } catch (IOException e) {
                }
            } else if ( ACTION_ADD_FAVORITE_ROUTE.equals(action)) {
                final PlaceObject mFrom = intent.getParcelableExtra(FROM_PARAM);
                final PlaceObject mTo = intent.getParcelableExtra(TO_PARAM);
                final String id_name = intent.getStringExtra(ID_PARAM);
                final long routeId = intent.getLongExtra(ROUTE_PARAM, 0);
                long start_time = intent.getLongExtra(TIME_PARAM, 0);
                handleActionAddFavorite(mFrom, mTo, id_name, routeId, start_time);
            } else if ( ACTION_REMOVE_FAVORITE_ROUTE.equals(action)) {
                final long routeId = intent.getLongExtra(ROUTE_PARAM, 0);
                handleActionRemoveFavorite(routeId);
            } else if ( ACTION_ADD_PLACE_HISTORY.equals(action)) {
                long query_time = intent.getLongExtra(TIME_PARAM, 0);
                PlaceObject place = intent.getParcelableExtra(PLACE_PARAM);
                handleActionAddPlaceHistory( place, query_time);
            }
        }
    }

    private void handleActionQueryDirection(String from, String to) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MapApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapApiService.DirectionApi directionApi = retrofit.create(MapApiService.DirectionApi.class);
        String key = getString(R.string.google_maps_server_key);
        String origin = from;
        String destination = to;

//        String lang = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            lang = Locale.getDefault().toLanguageTag();
//        } else {
//            lang = Locale.getDefault().getLanguage();
//            if ( !Locale.getDefault().getCountry().equals(""))
//                lang = lang + "-" + Locale.getDefault().getCountry();
//        }
//        Call<MapApiService.TransitRoutes> call = directionApi.getDirections(origin, destination, key, lang);
        Call<MapApiService.TransitRoutes> call = directionApi.getDirections(origin, destination, key);
        MapApiService.TransitRoutes transitRoutes = call.execute().body();
        // load into contextProvider
        if ( "OK".equals(transitRoutes.status)) {
            for(Route route: transitRoutes.routes) {
                DbUtils.insertRoute(DirectionIntentService.this, route);
            }
        }
    }
    private void handleActionAddFavorite( PlaceObject from, PlaceObject to, String save_name, long routeId, long start_time){
        // route table
        ContentValues values = new RoutesValuesBuilder().isFavorite(1).values();
        String mSelectionClause = RouteColumns.ID + "= ?";
        String[] mSelectionArgs = {String.valueOf(routeId)};
        getContentResolver().update(RoutesProvider.Routes.CONTENT_URI, values,
                mSelectionClause, mSelectionArgs);
        // favorite table
        ContentValues favorValues = new FavoritesValuesBuilder()
                .idName(save_name)
                .routesId(routeId)
                .startName(from.title)
                .startPlaceId(from.placeId)
                .endName(to.title)
                .endPlaceId(to.placeId)
                .queryTime(start_time)
                .values();
        getContentResolver().insert(RoutesProvider.Favorite.CONTENT_URI, favorValues);
    }
    private void handleActionRemoveFavorite( long routeId){
        // route table
        ContentValues values = new RoutesValuesBuilder().isFavorite(0).isArchive(0).values();
        String mSelectionClause = RouteColumns.ID + "= ?";
        String[] mSelectionArgs = {String.valueOf(routeId)};
        getContentResolver().update(RoutesProvider.Routes.CONTENT_URI, values,
                mSelectionClause, mSelectionArgs);
        getContentResolver().delete(RoutesProvider.Favorite.CONTENT_URI, mSelectionClause, mSelectionArgs);
    }
    private void handleActionAddPlaceHistory( PlaceObject place, long query_time) {
        ContentValues values = new HistoryValuesBuilder()
                .queryTime(query_time)
                .values();
        String mSelectionClause = HistoryColumns.PLACE_ID + "= ?";
        String[] mSelectionArgs = {place.placeId};
        int count = getContentResolver().update(RoutesProvider.History.CONTENT_URI,
                values,
                mSelectionClause,
                mSelectionArgs
                );
        if ( count == 0) {
            values = new HistoryValuesBuilder()
                    .placeId(place.placeId)
                    .placeName(place.title)
                    .queryTime(query_time).values();
            getContentResolver().insert(RoutesProvider.History.CONTENT_URI, values);
        }
    }
}
