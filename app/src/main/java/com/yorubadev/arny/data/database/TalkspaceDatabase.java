package com.yorubadev.arny.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {UserEntry.class, ContactEntry.class}, version = 3)
public abstract class TalkspaceDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "TalkspaceDatabase";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static TalkspaceDatabase sInstance;


    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS messages");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.beginTransaction();
            try {
                database.execSQL("ALTER TABLE users ADD COLUMN statusMessage TEXT DEFAULT ''");
                database.execSQL("ALTER TABLE contacts ADD COLUMN statusMessage TEXT DEFAULT ''");
                database.execSQL("ALTER TABLE contacts ADD COLUMN missedRequestCount INTEGER NOT NULL DEFAULT 0");
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    };

    public static TalkspaceDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        TalkspaceDatabase.class, TalkspaceDatabase.DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                        .build();
            }
        }
        return sInstance;
    }

    // The associated DAOs for the TalkspaceDatabase
    public abstract UserDao userDao();
    public abstract ContactDao contactDao();
}
