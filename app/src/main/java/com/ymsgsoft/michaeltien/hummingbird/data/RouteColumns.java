package com.ymsgsoft.michaeltien.hummingbird.data;

/**
 * Created by Michael Tien on 2015/12/1.
 */

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface RouteColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";

    @DataType(TEXT) @NotNull String OVERVIEW_POLYLINES = "overview_polylines";
    @DataType(TEXT) String SUMMARY = "summary";
    @DataType(TEXT) String WARNING = "warnings";
}