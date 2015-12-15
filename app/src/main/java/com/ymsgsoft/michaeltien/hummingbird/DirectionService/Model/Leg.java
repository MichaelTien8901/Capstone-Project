
package com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model;

import java.util.ArrayList;
import java.util.List;
public class Leg {

    public ArrivalTime arrivalTime;
    public DepartureTime departureTime;
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public EndLocation endLocation;
    public String startAddress;
    public StartLocation startLocation;
    public List<Step> steps = new ArrayList<Step>();
    public List<Object> viaWaypoint = new ArrayList<Object>();

}
