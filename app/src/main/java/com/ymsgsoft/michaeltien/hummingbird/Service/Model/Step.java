
package com.ymsgsoft.michaeltien.hummingbird.Service.Model;

import java.util.ArrayList;
import java.util.List;
public class Step {
    public Distance distance;
    public Duration duration;
    public EndLocation end_location;
    public String html_instructions;
    public Polyline polyline;
    public StartLocation start_location;
    public List<Step_> steps = new ArrayList<Step_>();
    public String travel_mode;
    public TransitDetails transit_details;

}
