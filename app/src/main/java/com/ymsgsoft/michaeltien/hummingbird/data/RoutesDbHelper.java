package com.ymsgsoft.michaeltien.hummingbird.data;

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
    }

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
        Log.e(TAG, "onCreate");
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
    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {
    }

//    @ExecOnCreate
//    public static final String EXEC_ON_CREATE = "SELECT * FROM " + ROUTES;
}
