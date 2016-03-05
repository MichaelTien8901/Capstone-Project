package com.ymsgsoft.michaeltien.hummingbird.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by Michael Tien on 2015/12/1.
 */

public interface  LegColumns {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";
    // arrival time
    @DataType(INTEGER) String ARRIVAL_TIME = "arrival_time";
    @DataType(TEXT) String ARRIVAL_TIME_TEXT = "arrival_time_text";

    @DataType(INTEGER) String DEPARTURE_TIME = "departure_time";
    @DataType(TEXT) String DEPARTURE_TIME_TEXT = "departure_time_text";

    @DataType(INTEGER) String DISTANCE = "distance"; // meter
    @DataType(TEXT) String DISTANCE_TEXT = "distance_text"; // meter
    @DataType(INTEGER) String DURATION = "duration"; // seconds
    @DataType(TEXT) String DURATION_TEXT = "duration_text"; // seconds
    @DataType(TEXT) @NotNull String START_ADDRESS = "start_address";
    @DataType(REAL) String START_LAT = "start_lat";
    @DataType(REAL) String START_LNG = "start_lng";
    @DataType(TEXT) @NotNull String END_ADDRESS = "end_address";
    @DataType(REAL) String END_LAT = "end_lat";
    @DataType(REAL) String END_LNG = "end_lng";

    @DataType(INTEGER)  @References(table = RoutesDbHelper.Tables.ROUTES, column = RouteColumns.ID) String ROUTES_ID = "route_id";

}
