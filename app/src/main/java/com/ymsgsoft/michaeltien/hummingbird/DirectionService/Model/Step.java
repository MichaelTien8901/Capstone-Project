
package com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model;

import java.util.ArrayList;
import java.util.List;
public class Step {
    public Distance distance;
    public Duration duration;
    public EndLocation endLocation;
    public String htmlInstructions;
    public Polyline polyline;
    public StartLocation startLocation;
    public List<Step_> steps = new ArrayList<Step_>();
    public String travelMode;
    public TransitDetails transitDetails;

}
