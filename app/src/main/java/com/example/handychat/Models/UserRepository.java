package com.example.handychat.Models;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;


public class UserRepository {
    private UserDao mUserDao;
    static ModelFirebase modelFirebase;

    public UserRepository(Application application){
        AppLocalDbRepository db = ModelSql.getDatabase(application);
        mUserDao = db.userDao();
        modelFirebase = new ModelFirebase();
    }

    public void insert(User user){
        new insertAsyncTask(mUserDao).execute(user);
        modelFirebase.addUser(user);
    }

    private class insertAsyncTask extends AsyncTask<User,Void,Void> {
        private UserDao mAsyncTaskDao;

        public insertAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(User... users) {
            mAsyncTaskDao.insert(users[0]);
            return null;
        }
    }
}
