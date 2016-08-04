package com.ymsgsoft.michaeltien.hummingbird;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.ymsgsoft.michaeltien.hummingbird.Service.MapApiService;
import com.ymsgsoft.michaeltien.hummingbird.Service.Model.Route;
import com.ymsgsoft.michaeltien.hummingbird.data.DbUtils;
import com.ymsgsoft.michaeltien.hummingbird.data.FavoriteColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.HistoryColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RouteColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.FavoritesValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.HistoryValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.RoutesValuesBuilder;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

//import retrofit.Call;
//import retrofit.GsonConverterFactory;
//import retrofit.Retrofit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DirectionService extends IntentService {
    static final String LOG_TAG = DirectionService.class.getSimpleName();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DIRECTION_STATUS_OK,
            DIRECTION_STATUS_NO_NETWORK,
            DIRECTION_STATUS_SERVER_DOWN,
            DIRECTION_STATUS_SERVER_INVALID,
            DIRECTION_STATUS_NO_ROUTE_FOUND,
            DIRECTION_STATUS_UNKNOWN
    })
    public @interface DirectionStatus {}

    public static final int DIRECTION_STATUS_OK = 0;
    public static final int DIRECTION_STATUS_SERVER_DOWN = 1;
    public static final int DIRECTION_STATUS_SERVER_INVALID = 2;
    public static final int DIRECTION_STATUS_NO_NETWORK = 3;
    public static final int DIRECTION_STATUS_NO_ROUTE_FOUND = 4;
    public static final int DIRECTION_STATUS_UNKNOWN = 5;

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
    // for broadcast message
    public static final String ACTION_RECENT_DATA_UPDATED = "com.ymsgsoft.michaeltien.hummingbird.RECENT_DATA_UPDATED";

    public DirectionService() {
        super("DirectionIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionQueryDirection(Context context, String from, String to, long query_time) {
        Intent intent = new Intent(context, DirectionService.class);
        intent.setAction(ACTION_QUERY_DIRECTION);
        intent.putExtra(FROM_PARAM, from);
        intent.putExtra(TO_PARAM, to);
        intent.putExtra(TIME_PARAM, query_time);
        context.startService(intent);
    }
    public static void startActionSaveFavorite(Context context, PlaceObject from, PlaceObject to,
                                               String id_name, RouteParcelable routeObject, long query_time) {
        Intent intent = new Intent(context, DirectionService.class);
        intent.setAction(ACTION_ADD_FAVORITE_ROUTE);
        intent.putExtra(FROM_PARAM, from);
        intent.putExtra(TO_PARAM, to);
        intent.putExtra(ID_PARAM, id_name);
        intent.putExtra(ROUTE_PARAM, routeObject);
        intent.putExtra(TIME_PARAM, query_time);
        context.startService(intent);
    }
    public static void startActionRemoveFavorite(Context context,long routeId) {
        Intent intent = new Intent(context, DirectionService.class);
        intent.setAction(ACTION_REMOVE_FAVORITE_ROUTE);
        intent.putExtra(ROUTE_PARAM, routeId);
        context.startService(intent);
    }
    public static void startActionSavePlace(Context context, PlaceObject place, long query_time) {
        Intent intent = new Intent(context, DirectionService.class);
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
                final long query_time = intent.getLongExtra(TIME_PARAM, 0);
                try {
                    handleActionQueryDirection(param1, param2, query_time);
                } catch (IOException e) {
                }
            } else if ( ACTION_ADD_FAVORITE_ROUTE.equals(action)) {
                final PlaceObject mFrom = intent.getParcelableExtra(FROM_PARAM);
                final PlaceObject mTo = intent.getParcelableExtra(TO_PARAM);
                final String id_name = intent.getStringExtra(ID_PARAM);
                final RouteParcelable routeObject = intent.getParcelableExtra(ROUTE_PARAM);
                long query_time = intent.getLongExtra(TIME_PARAM, 0);
                handleActionAddFavorite(mFrom, mTo, id_name, routeObject, query_time);
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

    private void handleActionQueryDirection(String from, String to, long query_time) throws IOException {
        if ( !Utils.isOnline(getBaseContext())) {
            setDirectionStatus(getBaseContext(), DIRECTION_STATUS_NO_NETWORK);
        }
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
        // use distance pref
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String units = sharedPrefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default_value));
        String language = sharedPrefs.getString(getString(R.string.pref_language_key), getString(R.string.pref_language_default_value));

        Call<MapApiService.TransitRoutes> call =
                directionApi.getDirectionsWithDepartureTimeUnitsLanguage(
                        origin,
                        destination,
                        key,
                        String.valueOf(query_time),
                        units,
                        language);
//        Call<MapApiService.TransitRoutes> call =
//                directionApi.getDirectionsWithDepartureTimeUnits(
//                        origin,
//                        destination,
//                        key,
//                        String.valueOf(query_time),
//                        units);
        call.enqueue(new Callback<MapApiService.TransitRoutes>() {
            @Override
            public void onResponse(Call<MapApiService.TransitRoutes> call, Response<MapApiService.TransitRoutes> response) {
                Timber.d(LOG_TAG, "handleActionQueryDirection onResponse ");
                if ( response.isSuccess()) {
                    // load into contextProvider
                    MapApiService.TransitRoutes transitRoutes = response.body();
                    if ( "OK".equals(transitRoutes.status)) {
                        if (transitRoutes.routes.size() > 0) {
                            for (Route route : transitRoutes.routes) {
                                DbUtils.insertRoute(DirectionService.this, route);
                            }
                            setDirectionStatus(getBaseContext(), DIRECTION_STATUS_OK);
                        } else {
                            setDirectionStatus(getBaseContext(), DIRECTION_STATUS_NO_ROUTE_FOUND);
                            getContentResolver().notifyChange(RoutesProvider.Routes.CONTENT_URI, null);
                            Timber.d(LOG_TAG, "Direction API not route returned");
                        }
                    } else {
                        Timber.d(LOG_TAG, "Direction API not return OK");
                        setDirectionStatus(getBaseContext(), DIRECTION_STATUS_NO_ROUTE_FOUND);
                        getContentResolver().notifyChange(RoutesProvider.Routes.CONTENT_URI, null);
                    }
                } else {
                    Timber.d(LOG_TAG, "Direction API not return successfully");
                    setDirectionStatus(getBaseContext(), DIRECTION_STATUS_SERVER_INVALID);
                    getContentResolver().notifyChange(RoutesProvider.Routes.CONTENT_URI, null);
                }
            }

            @Override
            public void onFailure(Call<MapApiService.TransitRoutes> call, Throwable t) {
                // server down, like no internet
                Timber.d(LOG_TAG, "Retrofit return failure");
                setDirectionStatus(getBaseContext(), DIRECTION_STATUS_SERVER_DOWN);
                getContentResolver().notifyChange(RoutesProvider.Routes.CONTENT_URI, null);
            }
        });
//        MapApiService.TransitRoutes transitRoutes = call.execute().body();
//        // load into contextProvider
//        if ( "OK".equals(transitRoutes.status)) {
//            for(Route route: transitRoutes.routes) {
//                DbUtils.insertRoute(DirectionIntentService.this, route);
//            }
//        }
    }
    private void handleActionAddFavorite( PlaceObject from, PlaceObject to, String save_name, RouteParcelable routeObject, long start_time){
        // route table
        ContentValues values = new RoutesValuesBuilder().isFavorite(1).values();
        String mSelectionClause = RouteColumns.ID + "= ?";
        String[] mSelectionArgs = {String.valueOf(routeObject.routeId)};
        getContentResolver().update(RoutesProvider.Routes.CONTENT_URI, values,
                mSelectionClause, mSelectionArgs);
        // favorite table
        ContentValues favorValues = new FavoritesValuesBuilder()
                .idName(save_name)
                .routesId(routeObject.routeId)
                .startName(from.title)
                .startPlaceId(from.placeId)
                .endName(to.title)
                .endPlaceId(to.placeId)
                .queryTime(start_time)
                .duration(routeObject.duration)
                .transitNo(routeObject.transitNo)
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
        mSelectionClause = FavoriteColumns.ROUTES_ID + "= ?";
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
        updateWidgets(getApplicationContext());
    }
    private static void updateWidgets(Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_RECENT_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
    static private void setDirectionStatus(Context c, @DirectionStatus int directionStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_direction_status_key), directionStatus);
        spe.commit();
    }
}
