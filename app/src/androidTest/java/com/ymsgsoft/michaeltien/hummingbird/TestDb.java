package com.ymsgsoft.michaeltien.hummingbird;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.ymsgsoft.michaeltien.hummingbird.generated_data.RoutesDbHelper;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.LegsValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.MicroStepsValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.RoutesValuesBuilder;
import com.ymsgsoft.michaeltien.hummingbird.generated_data.values.StepsValuesBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Michael Tien on 2015/12/3.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.ROUTES);
        tableNameHashSet.add(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.LEGS);
        tableNameHashSet.add(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.STEPS);
        tableNameHashSet.add(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.MICRO_STEPS);

        String databaseName = com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.DATABASE_NAME;
        mContext.deleteDatabase(databaseName);

        SQLiteDatabase db = RoutesDbHelper.getInstance(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());
        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );
        assertTrue("Error: Your database was created without all tables",
                tableNameHashSet.isEmpty());
        db.close();
    }
    public void testInsertReadDb() {
        RoutesDbHelper dbHelper = RoutesDbHelper.getInstance(this.mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues routesValues = createRouteValues();
        long routeRowId = db.insert(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.ROUTES, null, routesValues);
        assertTrue(routeRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor routeCursor = db.query(
                com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.ROUTES,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor("Error: routes table.", routeCursor, routesValues);
        routeCursor.close();
        // test leg table
        ContentValues legValues = createLegsValues(routeRowId);
        long legRowId = db.insert(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.LEGS, null, legValues);
        assertTrue(legRowId != -1);
        Cursor legCursor = db.query(
                com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.LEGS,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        validateCursor("Error: legs table.", legCursor, legValues);
        legCursor.close();
        // test step database
        ContentValues stepValues = createStepValues(legRowId);
        long stepRowId = db.insert(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.STEPS, null, stepValues);
        assertTrue(legRowId != -1);
        Cursor stepCursor = db.query(
                com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.STEPS,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        validateCursor("Error: steps table.", stepCursor, stepValues);
        stepCursor.close();;
        // test microstep table
        ContentValues mStepValues = createMicroStepValues(stepRowId);
        long mStepRowId = db.insert(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.MICRO_STEPS, null, mStepValues);
        assertTrue(mStepRowId != -1);
        Cursor mStepCursor = db.query(
                com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.MICRO_STEPS,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        validateCursor("Error: micro_steps table.", mStepCursor, mStepValues);
        mStepCursor.close();;

        dbHelper.close();

        db.close();
    }
    static ContentValues createRouteValues() {
        return new RoutesValuesBuilder().overviewPolylines(
        "ajukHrmfoVEa@e@b@wAfAuCjC?d@H\\BNA@EQRMPp@qEnCOTA^[PQu@@u@Rw@`@e@xBgAbI_HbIsGvRwOlHiGdKwInDwCnGuFPENLNDNCNOFU@OAa@IUMOOCWe@qG}VeA}Es@aFw@iG_@eGQsEKsF@oIPqo@Xsq@Rsk@^uw@Pgd@dMB|ODrMBjB@DkM?iSB{PBo\\D_BNy@Rk@fHwORw@Ju@HkCBeJE_FOuBOwA{@kDkDyLoDeMcBgGyBaIOcAE[KiCLaCXiBV_AjFqLt@}B\\cCNiBFwC@{G@cA@aFVqa@Ps\\HsTB{MFyVJ}MPkWNo_@BmEBcHCeCEoAB{EJgU@sCCK@gEFiLDwMFiSF_ZJeZDyXJog@@qP@sE|AkEfFsNvIsUxCmI`DqIfGaQhA_DoC_DiAoAc@U{@YmBE{G@aGAuD?aB?a@y@kB{@?pBkFC}BAAsK@uMDg_@@sRIY@wL@_H?if@Ect@A{HbG?fB@?u@@iV@kT?q@OmDsA_XAuAVoCXmAFOh@sA~CmEVe@d@}A\\qBLsADeBGoBo@sFGa@g@VeAJ}CAmGAmEQ"
        ).warning(
                "Walking directions are in beta.    Use caution – This route may be missing sidewalks or pedestrian paths."
        ).values();
    }
    static ContentValues createLegsValues(long routeRowId) {
        return new LegsValuesBuilder()
                .routesId(routeRowId)
                .arrivalTime(1449004769)
                .departureTime(1449000817)
                .distance(22152)
                .duration(3952)
                .endAddress("British Columbia Institute of Technology (BCIT Burnaby), 3700 Willingdon Ave, Burnaby, BC V5G 3H2, Canada")
                .endLat((float) 49.251498)
                .endLng((float) -123.004148)
                .startAddress("British Columbia Institute of Technology (BCIT Burnaby), 3700 Willingdon Ave, Burnaby, BC V5G 3H2, Canada")
                .startLat((float) 49.251498)
                .startLng((float) -123.004148)
                .values();
    }
    static ContentValues createStepValues(long legRowId) {
        return new StepsValuesBuilder()
                .distance(223)
                .duration(167)
                .endLat((float) 49.267764)
                .endLng((float) -123.247358)
                .startLat((float) 49.26641240000001)
                .startLng((float) -123.2458634)
                .travelMode("WALKING")
                .instruction("Walk to Ubc Loop Bay 11")
                .polyline(
                        "ajukHrmfoVEa@e@b@o@d@g@`@OLYVOL{AvAA^@DHZ?@BNA@"
                )
                .legId(legRowId)
                .values();
    }
    static ContentValues createMicroStepValues(long stepRowId) {
        return new MicroStepsValuesBuilder()
                .distance(223)
                .duration(167)
                .endLat((float) 49.267764)
                .endLng((float) -123.247358)
                .startLat((float) 49.26641240000001)
                .startLng((float) -123.2458634)
                .travelMode("WALKING")
                .instruction("Walk to Ubc Loop Bay 11")
                .polyline(
                        "ajukHrmfoVEa@e@b@o@d@g@`@OLYVOL{AvAA^@DHZ?@BNA@"
                )
                .stepId(stepRowId)
                .values();
    }
    static public final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertLegsValues(long routeRowId) {
        long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++ ) {
            ContentValues legValues = new LegsValuesBuilder()
                    .routesId(routeRowId)
                    .arrivalTime(1449004769+i*millisecondsInADay)
                    .departureTime(1449000817+i*millisecondsInADay)
                    .distance(22152+i*1000)
                    .duration(3952 + 600*i)
                    .endAddress("British Columbia Institute of Technology (BCIT Burnaby), 3700 Willingdon Ave, Burnaby, BC V5G 3H2, Canada")
                    .endLat((float) 49.251498)
                    .endLng((float) -123.004148)
                    .startAddress("British Columbia Institute of Technology (BCIT Burnaby), 3700 Willingdon Ave, Burnaby, BC V5G 3H2, Canada")
                    .startLat((float) 49.251498)
                    .startLng((float) -123.004148)
                    .values();
            returnContentValues[i] = legValues;
        }
        return returnContentValues;
    }
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse( error + "Column name " + columnName + " not found", idx == -1);
            switch( valueCursor.getType(idx)) {
                case Cursor.FIELD_TYPE_STRING:
                case Cursor.FIELD_TYPE_INTEGER:
                    // the getValue always return back string value.  So the test can't use 5.0 for real type
                    String expectedValue = entry.getValue().toString();
                    assertEquals(error + " Value '" + entry.getValue().toString() +
                            "' did not match the expected value '" +
                            expectedValue + "'. ",
                            expectedValue, valueCursor.getString(idx));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    Float expectedRealValue = (Float) entry.getValue();
                    Double getDoubleValue = valueCursor.getDouble(idx);
                    Double t = 1E-30;
                    assertTrue( error + " Value '" + entry.getValue().toString() +
                            "' did not match the expected value '" +
                            expectedRealValue + "'. ",
                            (getDoubleValue + t) >= expectedRealValue);
                    assertTrue( error + " Value '" + entry.getValue().toString() +
                                    "' did not match the expected value '" +
                                    expectedRealValue + "'. ",
                            (getDoubleValue - t) <= expectedRealValue);
                    break;
            }
        }
        valueCursor.close();
    }
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            switch( valueCursor.getType(idx)) {
                case Cursor.FIELD_TYPE_STRING:
                case Cursor.FIELD_TYPE_INTEGER:
                    String expectedValue = entry.getValue().toString();
                    assertEquals("Value '" + entry.getValue().toString() +
                            "' did not match the expected value '" +
                            expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    Float expectedRealValue = (Float) entry.getValue();
                    Double getDoubleValue = valueCursor.getDouble(idx);
                    Double t = 1E-30;
                    assertTrue( error + " Value '" + entry.getValue().toString() +
                                    "' did not match the expected value '" +
                                    expectedRealValue + "'. ",
                            (getDoubleValue + t) >= expectedRealValue);
                    assertTrue( error + " Value '" + entry.getValue().toString() +
                                    "' did not match the expected value '" +
                                    expectedRealValue + "'. ",
                            (getDoubleValue - t) <= expectedRealValue);
                    break;
            }
        }
    }

    static long insertUBCRouteValues(Context mContext) {
        // insert our test records into the database
        com.ymsgsoft.michaeltien.hummingbird.generated_data.RoutesDbHelper dbHelper =
                com.ymsgsoft.michaeltien.hummingbird.generated_data.RoutesDbHelper.getInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestDb.createRouteValues();
        long routeRowId;
        routeRowId = db.insert(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.ROUTES, null, testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert UBC Leg Values", routeRowId != -1);
        return routeRowId;
    }
    static long insertUBCLegValues(Context mContext, long routeRowId) {
        // insert our test records into the database
        com.ymsgsoft.michaeltien.hummingbird.generated_data.RoutesDbHelper dbHelper =
                com.ymsgsoft.michaeltien.hummingbird.generated_data.RoutesDbHelper.getInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestDb.createLegsValues(routeRowId);
        long legRowId;
        legRowId = db.insert(com.ymsgsoft.michaeltien.hummingbird.data.RoutesDbHelper.Tables.LEGS, null, testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert UBC Leg Values", legRowId != -1);
        return legRowId;
    }

}

