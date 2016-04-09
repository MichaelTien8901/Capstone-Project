package com.ymsgsoft.michaeltien.hummingbird;

import android.test.AndroidTestCase;
import android.util.Log;

import com.ymsgsoft.michaeltien.hummingbird.Service.MapApiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import retrofit.Call;
//import retrofit.Callback;
//import retrofit.GsonConverterFactory;
//import retrofit.Response;
//import retrofit.Retrofit;

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
        Call<MapApiService.TransitRoutes> call = directionApi.getDirections(origin, destination, key );

        call.enqueue(new Callback<MapApiService.TransitRoutes>() {
            @Override
            public void onResponse(Call<MapApiService.TransitRoutes> call, Response<MapApiService.TransitRoutes> response) {
                if ( response.isSuccess()) {
                    MapApiService.TransitRoutes transitRoutes = response.body();
                    assertTrue(LOG_TAG + ": retrofit query direction status return: " + transitRoutes.status,
                            transitRoutes.status.equals("OK"));
                }
            }
            @Override
            public void onFailure(Call<MapApiService.TransitRoutes> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
}
