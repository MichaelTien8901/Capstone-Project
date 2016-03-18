package com.ymsgsoft.michaeltien.hummingbird.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Michael Tien on 2015/12/1.
 */
@Database(version = RoutesDbHelper.VERSION, fileName = RoutesDbHelper.DATABASE_NAME,
        packageName = "com.ymsgsoft.michaeltien.hummingbird.generated_data")
public final class RoutesDbHelper {
    static final String TAG = RoutesDbHelper.class.getSimpleName();
    private RoutesDbHelper() {
    }

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "hummingbird.sqlite";
    // Migrations
    // Just put the sql, and increment {@ #VERSION} value
    public static final String[] MIGRATIONS = {};

    public static class Tables {
        @Table(RouteColumns.class) @IfNotExists public static final String ROUTES = "routes";
        @Table(LegColumns.class)  @IfNotExists public static final String LEGS = "legs";
        @Table(StepColumns.class)  @IfNotExists public static final String STEPS = "steps";
        @Table(MicroStepColumns.class)  @IfNotExists public static final String MICRO_STEPS = "microSteps";
        @Table(NavigateColumns.class)  @IfNotExists public static final String NAVIGATES = "navigates";
    }

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
        Log.e(TAG, "onCreate");
        // add trigger
        String trigger1 =
                "CREATE TRIGGER delete_route_with BEFORE DELETE ON " +
                Tables.ROUTES +
                " FOR EACH ROW BEGIN" +
                   " DELETE FROM " + Tables.LEGS +
                   " WHERE OLD." + RouteColumns.ID +
                      " =  " +
                      Tables.LEGS + '.' + LegColumns.ROUTES_ID + " ;" +
                " END ";
        db.execSQL(trigger1);
        String trigger2 =
                "CREATE TRIGGER delete_leg_with BEFORE DELETE ON " +
                        Tables.LEGS +
                        " FOR EACH ROW BEGIN" +
                        " DELETE FROM " + Tables.STEPS +
                        " WHERE OLD." + LegColumns.ID +
                        " =  " +
                        Tables.STEPS + '.' + StepColumns.LEG_ID + " ;" +
                        " END ";
        db.execSQL( trigger2 );
        String trigger3 =
                "CREATE TRIGGER delete_step_with BEFORE DELETE ON " +
                        Tables.STEPS +
                        " FOR EACH ROW BEGIN" +
                        " DELETE FROM " + Tables.MICRO_STEPS +
                        " WHERE OLD." + StepColumns.ID +
                        " =  " +
                        Tables.MICRO_STEPS + '.' + MicroStepColumns.STEP_ID + " ;" +
                        " END ";
        db.execSQL( trigger3 );
        String trigger4 =
                "CREATE TRIGGER delete_navigate_with BEFORE DELETE ON " +
                        Tables.ROUTES +
                        " FOR EACH ROW BEGIN" +
                        " DELETE FROM " + Tables.NAVIGATES +
                        " WHERE OLD." + RouteColumns.ID +
                        " =  " +
                        Tables.NAVIGATES + '.' + NavigateColumns.ROUTES_ID + " ;" +
                        " END ";
        db.execSQL( trigger4);
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                            int newVersion) {
        Log.e(TAG, "onUpgrade");
        for (int i = oldVersion; i < newVersion; i++) {
            String migration = MIGRATIONS[i];
            db.beginTransaction();
            try {
                db.execSQL(migration);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(TAG, String.format(
                        "Failed to upgrade database with script: %s", migration), e);
                break;
            }
        }
    }
    @SuppressLint("NewApi")
    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            db.setForeignKeyConstraintsEnabled(true);
//        } else {
//            db.execSQL("PRAGMA foreign_keys=ON");
//        }
    }

//    @ExecOnCreate
//    public static final String EXEC_ON_CREATE = "SELECT * FROM " + ROUTES;
}
