
package com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model;

import java.util.ArrayList;
import java.util.List;
public class Leg {

    public ArrivalTime arrival_time;
    public DepartureTime departure_time;
    public Distance distance;
    public Duration duration;
    public String end_address;
    public EndLocation end_location;
    public String start_address;
    public StartLocation start_location;
    public List<Step> steps = new ArrayList<Step>();
    public List<Object> via_waypoint = new ArrayList<Object>();

}
