package com.ymsgsoft.michaeltien.hummingbird.data;

/**
 * Created by Michael Tien on 2015/12/2.
 */
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
public interface MicroStepColumns {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";

    @DataType(INTEGER) String DISTANCE = "distance"; // meter
    @DataType(INTEGER) String DURATION = "duration"; // seconds
    @DataType(TEXT) String POLYLINE = "polyline";
    @DataType(TEXT) String INSTRUCTION = "instruction";
    @DataType(TEXT) String TRAVEL_MODE = "travel_mode";
    @DataType(REAL) String START_LAT = "start_lat";
    @DataType(REAL) String START_LNG = "start_lng";
    @DataType(REAL) String END_LAT = "end_lat";
    @DataType(REAL) String END_LNG = "end_lng";
    @DataType(INTEGER)  @References(table = RoutesDbHelper.Tables.STEPS, column = StepColumns.ID) String STEP_ID = "step_id";
}