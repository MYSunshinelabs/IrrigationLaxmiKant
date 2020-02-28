package com.ve.irrigation.DataValues;

import android.arch.persistence.db.SupportSQLiteOpenHelper;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by laxmi on 13/6/18.
 */
@Database(entities = {ConnectionSourceData.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase appDataBase;

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    public static AppDataBase getAppDataBase(Context context) {
        if (appDataBase == null)
            appDataBase = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "user-database")
                    // allow queries on the main thread.
                    // Don't do this on a real app! See PersistenceBasicSample for an example.

                    .build();


        return appDataBase;
    }


    public abstract ConnectionSourceDAO connectionSourceDAO();

}
