package com.example.handychat.Models;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {JobRequest.class,User.class}, version = 1)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract JobRequestDao jobRequestDao();
    public abstract UserDao userDao();
}

public class ModelSql {
    public static volatile AppLocalDbRepository INSTANCE;
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    static AppLocalDbRepository getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (AppLocalDbRepository.class){
                if (INSTANCE == null){
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppLocalDbRepository.class,"database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
            }
        }
    }
    return INSTANCE;
    }

    private static class PopulateDbAsync extends AsyncTask<Void,Void,Void> {
        private final JobRequestDao mDao;

        public PopulateDbAsync(AppLocalDbRepository instance) {
            mDao = instance.jobRequestDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.deleteAll();
            // What to do on open app
            return null;
        }
    }
}
