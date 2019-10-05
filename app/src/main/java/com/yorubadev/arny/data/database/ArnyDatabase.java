package com.yorubadev.arny.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserEntry.class}, version = 1)
public abstract class ArnyDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "ArnyDatabase";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static ArnyDatabase sInstance;

    public static ArnyDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        ArnyDatabase.class, ArnyDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    // The associated DAOs for the ArnyDatabase
    public abstract UserDao userDao();
}
