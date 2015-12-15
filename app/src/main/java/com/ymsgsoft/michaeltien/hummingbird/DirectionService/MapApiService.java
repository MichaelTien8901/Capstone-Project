package com.ymsgsoft.michaeltien.hummingbird.DirectionService;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Route;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Michael Tien on 2015/12/13.
 */
public final class  MapApiService {
    public static final String API_URL = "https://maps.googleapis.com";
    public class TransitRoutes {
        //    public List<GeocodedWaypoint> geocodedWaypoints = new ArrayList<GeocodedWaypoint>();
        public List<Route> routes = new ArrayList<Route>();
        public String status;
    }

    public interface DirectionApi {
//        @GET("/maps/api/directions/json?")
        @GET("/maps/api/directions/json?mode=transit&alternatives=true")
        Call<TransitRoutes> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String key
                );
    }
}
