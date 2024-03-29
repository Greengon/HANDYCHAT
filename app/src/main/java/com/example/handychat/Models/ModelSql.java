package com.example.handychat.Models;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {JobRequest.class,User.class,Comment.class}, version = 6)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract JobRequestDao jobRequestDao();
    public abstract UserDao userDao();
    public abstract CommentDao commentDao();
}

public class ModelSql {
    public static volatile AppLocalDbRepository INSTANCE;
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    // If we want our app to work with out internet this should be removed
                    new CleanDbAsync(INSTANCE).execute();
                }
            };

    static AppLocalDbRepository getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (AppLocalDbRepository.class){
                if (INSTANCE == null){
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppLocalDbRepository.class,"database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
            }
        }
    }
    return INSTANCE;
    }

    // To delete all content and repopulate the database whenever the app is started
    private static class CleanDbAsync extends AsyncTask<Void,Void,Void>{
        private JobRequestDao mJobRequestDao;
        private UserDao mUserDao;
        private CommentDao mCommentDao;

        CleanDbAsync(AppLocalDbRepository db){
            mCommentDao = db.commentDao();
            mJobRequestDao = db.jobRequestDao();
            mUserDao = db.userDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mCommentDao.deleteAll();
            mJobRequestDao.deleteAll();
            mUserDao.deleteAll();
            return null;
        }
    }
}
