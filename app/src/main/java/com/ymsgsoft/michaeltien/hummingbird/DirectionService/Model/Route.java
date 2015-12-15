package com.ymsgsoft.michaeltien.hummingbird.DirectionService.Model;

import java.util.ArrayList;
import java.util.List;

public class Route {
//    public Bounds bounds;
    public String copyrights;
    public List<Leg> legs = new ArrayList<Leg>();
    public OverviewPolyline overviewPolyline;
    public String summary;
    public List<String> warnings = new ArrayList<String>();
//    public List<Object> waypointOrder = new ArrayList<Object>();
}
