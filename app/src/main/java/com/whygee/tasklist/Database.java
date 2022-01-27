package com.whygee.tasklist;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;

@androidx.room.Database(entities = {MainData.class}, version = 2, exportSchema = false)

public abstract class Database extends RoomDatabase {
    private static Database db;
    private static final String DATABASE_NAME = "database";

    public synchronized static Database getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context.getApplicationContext(), Database.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return db;
    }

    public abstract TaskDataObject mainDao();

}
