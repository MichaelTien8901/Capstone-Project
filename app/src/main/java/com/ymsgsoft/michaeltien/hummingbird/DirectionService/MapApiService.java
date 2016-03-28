package com.ymsgsoft.michaeltien.hummingbird.DirectionService;

import com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model.Route;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//import retrofit.Call;
//import retrofit.http.GET;
//import retrofit.http.Query;

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
//        @GET("/maps/api/directions/json?mode=transit&alternatives=true&language=zh-TW") // test different language
        Call<TransitRoutes> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String key
                );
        Call<TransitRoutes> getDirectionsWithLanguage(
                @Query("origin") String origin,
                @Query("destination") String destination,
                @Query("key") String key,
                @Query("language") String language );
        Call<TransitRoutes> getDirectionsWithDepartureTime(
                        @Query("origin") String origin,
                @Query("destination") String destination,
                @Query("key") String key,
                @Query("departure_time") String departure_time
        );
    }
}
