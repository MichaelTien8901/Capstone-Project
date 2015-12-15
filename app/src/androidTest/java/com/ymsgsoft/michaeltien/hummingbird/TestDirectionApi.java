package com.ymsgsoft.michaeltien.hummingbird;

import android.test.AndroidTestCase;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.MapApiService;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Michael Tien on 2015/12/14.
 */
public class TestDirectionApi  extends AndroidTestCase  {
    public static final String LOG_TAG = TestDirectionApi.class.getSimpleName();
    public void testDirectionQuery() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MapApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapApiService.DirectionApi directionApi = retrofit.create(MapApiService.DirectionApi.class);
        String key = mContext.getString(R.string.google_maps_server_key);
        String origin = "place_id:ChIJAx7UL8xyhlQR86Iqc-fUncc";
        String destination = "place_id:ChIJNbea5OF2hlQRDfHhEXerrAM";
        Call<MapApiService.TransitRoutes> call = directionApi.getDirections(origin, destination, key);
        MapApiService.TransitRoutes transitRoutes = call.execute().body();
        assertTrue(LOG_TAG + ": retrofit query direction status return: " + transitRoutes.status, transitRoutes.status.equals("OK"));
    }
    public void testDirectionAsyncQuery() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MapApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapApiService.DirectionApi directionApi = retrofit.create(MapApiService.DirectionApi.class);
        String key = mContext.getString(R.string.google_maps_server_key);
        String origin = "place_id:ChIJAx7UL8xyhlQR86Iqc-fUncc";
        String destination = "place_id:ChIJNbea5OF2hlQRDfHhEXerrAM";
        Call<MapApiService.TransitRoutes> call = directionApi.getDirections(origin, destination, key);
        call.enqueue(new Callback<MapApiService.TransitRoutes>() {
            @Override
            public void onResponse(Response<MapApiService.TransitRoutes> response, Retrofit retrofit) {
                MapApiService.TransitRoutes transitRoutes = response.body();
                assertTrue(LOG_TAG + ": retrofit query direction status return: " + transitRoutes.status,
                        transitRoutes.status.equals("OK"));
            }

            @Override
            public void onFailure(Throwable t) {
                fail(LOG_TAG + "testDirectionAsyncQuery" + t.getMessage());
            }
        });
    }
}
