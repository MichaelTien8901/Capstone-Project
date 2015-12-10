package com.ymsgsoft.michaeltien.hummingbird;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;
import com.ymsgsoft.michaeltien.hummingbird.utils.PollingCheck;

/**
 * Created by Michael Tien on 2015/12/4.
 */
public class TestRoutesProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestRoutesProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                RoutesProvider.MicroSteps.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                RoutesProvider.Steps.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                RoutesProvider.Legs.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                RoutesProvider.Routes.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                RoutesProvider.Routes.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Routes table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                RoutesProvider.Legs.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Legs table during delete", 0, cursor.getCount());
        cursor.close();
        cursor = mContext.getContentResolver().query(
                RoutesProvider.Steps.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Steps table during delete", 0, cursor.getCount());
        cursor.close();
        cursor = mContext.getContentResolver().query(
                RoutesProvider.MicroSteps.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from MicroSteps table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                com.ymsgsoft.michaeltien.hummingbird.generated_data.RoutesProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: RoutesProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + RoutesProvider.AUTHORITY,
                    providerInfo.authority, RoutesProvider.AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: RoutesProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.ymsgsoft.hummingbird/routes/
        String type = mContext.getContentResolver().getType(RoutesProvider.Routes.CONTENT_URI);
        // vnd.android.cursor.dir/com.ymsgsoft.michaeltien.hummingbird/routes
        assertEquals("Error: the Routes CONTENT_URI should return RoutesProvider.Routes.CONTENT_TYPE",
                RoutesProvider.Routes.CONTENT_TYPE, type);
        // content://com.ymsgsoft.hummingbird/routes/1234
        type = mContext.getContentResolver().getType(RoutesProvider.Routes.withId(1234));
        assertEquals("Error: the Routes CONTENT_URI with routeId should return RoutesProvider.Routes.CONTENT_ITEM_TYPE",
                RoutesProvider.Routes.CONTENT_ITEM_TYPE, type);

        // content://com.ymsgsoft.hummingbird/legs/
        type = mContext.getContentResolver().getType(RoutesProvider.Legs.CONTENT_URI);
        // vnd.android.cursor.dir/com.ymsgsoft.michaeltien.hummingbird/legs
        assertEquals("Error: the Legs CONTENT_URI should return RoutesProvider.Routes.CONTENT_TYPE",
                RoutesProvider.Legs.CONTENT_TYPE, type);
        // content://com.ymsgsoft.hummingbird/legs/1234
        type = mContext.getContentResolver().getType(RoutesProvider.Legs.withId(5678));
        assertEquals("Error: the Legs CONTENT_URI with routeId should return RoutesProvider.Legs.CONTENT_ITEM_TYPE",
                RoutesProvider.Legs.CONTENT_ITEM_TYPE, type);
        // content://com.ymsgsoft.hummingbird/routes/1234/legs
        type = mContext.getContentResolver().getType(RoutesProvider.Legs.fromRoute(1234));
        assertEquals("Error: the Legs CONTENT_URI with routeId should return RoutesProvider.Legs.CONTENT_ITEM_TYPE",
                RoutesProvider.Legs.CONTENT_TYPE, type);

        // content://com.ymsgsoft.hummingbird/steps/
        type = mContext.getContentResolver().getType(RoutesProvider.Steps.CONTENT_URI);
        // vnd.android.cursor.dir/com.ymsgsoft.michaeltien.hummingbird/steps
        assertEquals("Error: the Steps CONTENT_URI should return RoutesProvider.Steps.CONTENT_TYPE",
                RoutesProvider.Steps.CONTENT_TYPE, type);
        // content://com.ymsgsoft.hummingbird/steps/1234
        type = mContext.getContentResolver().getType(RoutesProvider.Steps.withId(5678));
        assertEquals("Error: the Steps CONTENT_URI with routeId should return RoutesProvider.Steps.CONTENT_ITEM_TYPE",
                RoutesProvider.Steps.CONTENT_ITEM_TYPE, type);
        // content://com.ymsgsoft.hummingbird/legs/1234/steps
        type = mContext.getContentResolver().getType(RoutesProvider.Steps.fromLeg(1234));
        assertEquals("Error: the Steps CONTENT_URI with routeId should return RoutesProvider.Steps.CONTENT_ITEM_TYPE",
                RoutesProvider.Steps.CONTENT_TYPE, type);

        // content://com.ymsgsoft.hummingbird/micro_steps/
        type = mContext.getContentResolver().getType(RoutesProvider.MicroSteps.CONTENT_URI);
        // vnd.android.cursor.dir/com.ymsgsoft.michaeltien.hummingbird/MicroSteps
        assertEquals("Error: the MicroSteps CONTENT_URI should return RoutesProvider.MicroSteps.CONTENT_TYPE",
                RoutesProvider.MicroSteps.CONTENT_TYPE, type);
        // content://com.ymsgsoft.hummingbird/micro_steps/1234
        type = mContext.getContentResolver().getType(RoutesProvider.MicroSteps.withId(5678));
        assertEquals("Error: the MicroSteps CONTENT_URI with routeId should return RoutesProvider.MicroSteps.CONTENT_ITEM_TYPE",
                RoutesProvider.MicroSteps.CONTENT_ITEM_TYPE, type);
        // content://com.ymsgsoft.hummingbird/steps/1234/micro_steps
        type = mContext.getContentResolver().getType(RoutesProvider.MicroSteps.fromStep(1234));
        assertEquals("Error: the MicroSteps CONTENT_URI with routeId should return RoutesProvider.MicroSteps.CONTENT_ITEM_TYPE",
                RoutesProvider.MicroSteps.CONTENT_TYPE, type);

    }

    public void testBasicRouteQuery() {
//        com.ymsgsoft.michaeltien.hummingbird.generated_data.RoutesDbHelper dbHelper =
//                com.ymsgsoft.michaeltien.hummingbird.generated_data.RoutesDbHelper.getInstance(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestDb.createRouteValues();
        long routeRowId = TestDb.insertUBCRouteValues(mContext);
        // Test the basic content provider query
        Cursor routeCursor = mContext.getContentResolver().query(
                RoutesProvider.Routes.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Make sure we get the correct cursor out of the database
        TestDb.validateCursor("testBasicRouteQuery, route query", routeCursor, testValues);
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Routes Query did not properly set NotificationUri",
                    routeCursor.getNotificationUri(), RoutesProvider.Routes.CONTENT_URI);
        }
    }
    public void testBasicLegQuery() {
        long routeRowId = TestDb.insertUBCRouteValues(mContext);
        ContentValues testValues = TestDb.createLegsValues(routeRowId);
        long legRowId = TestDb.insertUBCLegValues(mContext, routeRowId);
        // Test the basic content provider query
        Cursor legCursor = mContext.getContentResolver().query(
                RoutesProvider.Legs.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Make sure we get the correct cursor out of the database
        TestDb.validateCursor("testBasicRouteQuery, route query", legCursor, testValues);
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Legs Query did not properly set NotificationUri",
                    legCursor.getNotificationUri(), RoutesProvider.Legs.CONTENT_URI);
        }
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
    public void testInsertReadProvider() {
        // Test Routes ----------------------------------------------------------------------------------------
        ContentValues testValues = TestDb.createRouteValues();
        TestContentObserver tco = TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RoutesProvider.Routes.CONTENT_URI, true, tco);
        Uri routeUri = mContext.getContentResolver().insert(RoutesProvider.Routes.CONTENT_URI, testValues);
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        long routeRowId = ContentUris.parseId(routeUri);
        // Verify we got a row back.
        assertTrue(routeRowId != -1);
        Cursor cursor = mContext.getContentResolver().query(
                RoutesProvider.Routes.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor("testInsertReadProvider. RoutesProvider.Routes.",
                cursor, testValues);
        cursor.close();
        // Test Legs ----------------------------------------------------------------------------------------
        // add some legs into route
        ContentValues legValues = TestDb.createLegsValues(routeRowId);
        // The TestContentObserver is a one-shot class
        tco = TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RoutesProvider.Legs.CONTENT_URI, true, tco);
        Uri legInsertUri = mContext.getContentResolver()
                .insert(RoutesProvider.Legs.CONTENT_URI, legValues);
        assertTrue(legInsertUri != null);

        // Did our content observer get called?  If this fails, your insert leg
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        long legRowId = ContentUris.parseId(legInsertUri);
        assertTrue(legRowId != -1);
        // A cursor is your primary interface to the query results.
        Cursor legCursor = mContext.getContentResolver().query(
                RoutesProvider.Legs.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor("testInsertReadProvider. Error validating RoutesProvider.legs insert.",
                legCursor, legValues);

        // Add the legs values in with the route data so that we can make
        // sure that the join worked and we actually get all the values back
//        legValues.putAll(testValues);
        // Get the joined legs and routes data
       legCursor = mContext.getContentResolver().query(
                RoutesProvider.Legs.fromRoute(routeRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor("testInsertReadProvider.  Error validating joined Legs and Routes with routeRowId.",
                legCursor, legValues);

        legCursor = mContext.getContentResolver().query(
                RoutesProvider.Legs.withId(legRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor("testInsertReadProvider.  Error validating Legs with legRowId.",
                legCursor, legValues);
        legCursor.close();
        // Test Steps ----------------------------------------------------------------------------------------
        ContentValues stepValues = TestDb.createStepValues(legRowId);
        // The TestContentObserver is a one-shot class
        tco = TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RoutesProvider.Steps.CONTENT_URI, true, tco);
        Uri stepInsertUri = mContext.getContentResolver()
                .insert(RoutesProvider.Steps.CONTENT_URI, stepValues);
        assertTrue(stepInsertUri != null);

        // Did our content observer get called?  If this fails, your insert leg
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        long stepRowId = ContentUris.parseId(stepInsertUri);
        assertTrue(stepRowId != -1);
        // A cursor is your primary interface to the query results.
        Cursor stepCursor = mContext.getContentResolver().query(
                RoutesProvider.Steps.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor("testInsertReadProvider. Error validating RoutesProvider.Steps insert.",
                stepCursor, stepValues);

       stepCursor = mContext.getContentResolver().query(
                RoutesProvider.Steps.fromLeg(legRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor("testInsertReadProvider.  Error validating joined Steps and Legs with legRowId.",
                stepCursor, stepValues);

        stepCursor = mContext.getContentResolver().query(
                RoutesProvider.Steps.withId(stepRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor("testInsertReadProvider.  Error validating Steps with stepRowId.",
                stepCursor, stepValues);
        stepCursor.close();
        // Test MicroSteps ----------------------------------------------------------------------------------------
        ContentValues microStepValues = TestDb.createMicroStepValues(stepRowId);
        // The TestContentObserver is a one-shot class
        tco = TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RoutesProvider.MicroSteps.CONTENT_URI, true, tco);
        Uri microStepInsertUri = mContext.getContentResolver()
                .insert(RoutesProvider.MicroSteps.CONTENT_URI, microStepValues);
        assertTrue(microStepInsertUri != null);

        // Did our content observer get called?  If this fails, your insert leg
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        long microStepRowId = ContentUris.parseId(microStepInsertUri);
        assertTrue(microStepRowId != -1);
        // A cursor is your primary interface to the query results.
        Cursor microStepCursor = mContext.getContentResolver().query(
                RoutesProvider.MicroSteps.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor("testInsertReadProvider. Error validating RoutesProvider.MicroSteps insert.",
                microStepCursor, microStepValues);

       microStepCursor = mContext.getContentResolver().query(
                RoutesProvider.MicroSteps.fromStep(stepRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor("testInsertReadProvider.  Error validating joined MicroSteps and Steps with stepRowId.",
                microStepCursor, microStepValues);

        microStepCursor = mContext.getContentResolver().query(
                RoutesProvider.MicroSteps.withId(microStepRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor("testInsertReadProvider.  Error validating MicroSteps with microStepRowId.",
                microStepCursor, microStepValues);
        microStepCursor.close();
    }
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our routes delete.
        TestContentObserver routeObserver = TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RoutesProvider.Routes.CONTENT_URI, true, routeObserver);

        // Register a content observer for our weather delete.
        TestContentObserver legObserver = TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RoutesProvider.Legs.CONTENT_URI, true, legObserver);

        deleteAllRecordsFromProvider();

        routeObserver.waitForNotificationOrFail();
        legObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(legObserver);
        mContext.getContentResolver().unregisterContentObserver(routeObserver);
    }
    public void testBulkInsert() {
        ContentValues testValues = TestDb.createRouteValues();
        Uri routeUri = mContext.getContentResolver().insert(RoutesProvider.Routes.CONTENT_URI, testValues);
        long routeRowId = ContentUris.parseId(routeUri);
        // Verify we got a row back.
        assertTrue(routeRowId != -1);
        Cursor cursor = mContext.getContentResolver().query(
                RoutesProvider.Routes.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor("testBulkInsert. Error validatingRoutesProvider.Routes.",
                cursor, testValues);
        // verify bulk insert into legs
        ContentValues[] bulkInsertContentValues = TestDb.createBulkInsertLegsValues(routeRowId);

        // Register a content observer for our bulk insert.
        TestContentObserver legObserver = TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RoutesProvider.Legs.CONTENT_URI, true, legObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(RoutesProvider.Legs.CONTENT_URI, bulkInsertContentValues);

        // If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        legObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(legObserver);

        assertEquals(insertCount, TestDb.BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                RoutesProvider.Legs.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), TestDb.BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < TestDb.BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestDb.validateCurrentRecord("testBulkInsert.  Error validating Legs " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
