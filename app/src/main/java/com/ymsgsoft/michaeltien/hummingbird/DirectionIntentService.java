package com.ymsgsoft.michaeltien.hummingbird;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.MapApiService;
import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Route;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DirectionIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_QUERY_DIRECTION
    private static final String ACTION_QUERY_DIRECTION = "com.ymsgsoft.michaeltien.hummingbird.action.query_direction";

    private static final String FROM_PARAM = "com.ymsgsoft.michaeltien.hummingbird.extra.FROM_PARAM";
    private static final String TO_PARAM = "com.ymsgsoft.michaeltien.hummingbird.extra.TO_PARAM";

    public DirectionIntentService() {
        super("DirectionIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionQueryDirection(Context context, String from, String to) {
        Intent intent = new Intent(context, DirectionIntentService.class);
        intent.setAction(ACTION_QUERY_DIRECTION);
        intent.putExtra(FROM_PARAM, from);
        intent.putExtra(TO_PARAM, to);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_QUERY_DIRECTION.equals(action)) {
                final String param1 = intent.getStringExtra(FROM_PARAM);
                final String param2 = intent.getStringExtra(TO_PARAM);
                try {
                    handleActionQueryDirection(param1, param2);
                } catch (IOException e) {

                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionQueryDirection(String from, String to) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MapApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapApiService.DirectionApi directionApi = retrofit.create(MapApiService.DirectionApi.class);
        String key = getString(R.string.google_maps_server_key);
        String origin = from;
        String destination = to;
        Call<MapApiService.TransitRoutes> call = directionApi.getDirections(origin, destination, key);
        MapApiService.TransitRoutes transitRoutes = call.execute().body();
        // load into contextProvider
        if ( "OK".equals(transitRoutes.status)) {
            for(Route route: transitRoutes.routes) {
                RoutesProvider.insertRoute(DirectionIntentService.this, route);
            }
        }
    }
}
