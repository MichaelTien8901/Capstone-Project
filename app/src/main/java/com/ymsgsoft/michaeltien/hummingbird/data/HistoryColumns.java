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
    @DataType(TEXT) String PLACE_NAME = "place_name";
    @DataType(TEXT) String PLACE_ID = "place_id";
    @DataType(INTEGER) String QUERY_TIME = "query_time"; // seconds
}
