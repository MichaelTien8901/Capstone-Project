package com.ymsgsoft.michaeltien.hummingbird.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by Michael Tien on 2016/3/25.
 */
public interface HistoryColumns {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";
    @DataType(TEXT) String START_NAME = "start_name";
    @DataType(TEXT) String START_PLACE_ID = "start_place_id";
    @DataType(TEXT) String END_NAME = "end_name";
    @DataType(TEXT) String END_PLACE_ID = "end_place_id";
    @DataType(INTEGER) String QUERY_TIME = "query_time"; // seconds
}
