package com.w3engineers.ecommerce.bootic.data.helper.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import android.content.Context;
import androidx.annotation.NonNull;


import java.util.ArrayList;
import java.util.List;

public abstract class AppDatabase extends RoomDatabase {
    protected static <T extends RoomDatabase> T createDb(Context context, String dbName,
                                                         Class<T> dbService,
                                                         String... migrationScripts) {
        RoomDatabase.Builder<T> builder = Room.databaseBuilder(context, dbService, dbName);

        for (Migration migration : getMigrations(migrationScripts)) {
            builder.addMigrations(migration);
        }

        return builder.allowMainThreadQueries().build();
    }

    private static List<Migration> getMigrations(String... migrationScripts) {
        List<Migration> migrationList = new ArrayList<>();

        int startVersion = 1;
        int endVersion = 2;

        Migration migration;

        for (final String migrationSchema : migrationScripts) {
            migration = new Migration(startVersion, endVersion) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    database.execSQL(migrationSchema);
                }
            };

            startVersion++;
            endVersion++;

            migrationList.add(migration);
        }

        return migrationList;
    }
}
