package com.ymsgsoft.michaeltien.hummingbird.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by Michael Tien on 2015/12/2.
 */
public interface NavigateColumns {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";

    @DataType(INTEGER) String DISTANCE = "distance"; // meter
    @DataType(TEXT) String DISTANCE_TEXT = "distance_text"; // meter
    @DataType(INTEGER) String DURATION = "duration"; // seconds
    @DataType(TEXT) String DURATION_TEXT = "duration_text"; // seconds
    @DataType(TEXT) String POLYLINE = "polyline";
    @DataType(TEXT) String INSTRUCTION = "instruction";
    @DataType(TEXT) String TRAVEL_MODE = "travel_mode";
    @DataType(REAL) String START_LAT = "start_lat";
    @DataType(REAL) String START_LNG = "start_lng";
    @DataType(REAL) String END_LAT = "end_lat";
    @DataType(REAL) String END_LNG = "end_lng";
    @DataType(TEXT) String TRANSIT_NO = "transit_no";
    @DataType(INTEGER)  @References(table = RoutesDbHelper.Tables.ROUTES, column = RouteColumns.ID)
    String ROUTES_ID = "route_id";
}
