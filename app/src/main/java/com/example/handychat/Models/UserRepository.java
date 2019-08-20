package com.example.handychat.Models;

import android.app.Application;
import android.os.AsyncTask;

public class UserRepository {
    static ModelFirebase modelFirebase;

    public UserRepository(Application application){
        AppLocalDbRepository db = ModelSql.getDatabase(application);
        modelFirebase = new ModelFirebase();
    }

    public static void addUser(User user){
        UserDao.insertUser(user);
        modelFirebase.addUser(user);
    }

    public static void insert(User user){
        UserDao.insertUser(user);
    }

    public interface GetUserListener{
        void onComplete(User user);
    }

    public interface FetchUserLocally{
        void onComplete(User user);
    }

    public void getUser(String userEmail,final GetUserListener listener){
        // We will first check locally if we can get our user
        UserDao.getUser(userEmail,user -> {
            // This is a listener created to get user object from local db
            if (user == null){
                // If we didn't find our user in cache we will give the task to FireBase
                // with giving the view listener directly to him so he would answer it.
                modelFirebase.getUser(userEmail,listener);
            }else{
                // If we find our user locally just answer the listener with the result
                listener.onComplete(user);
            }
        });
    }

    public static class UserDao {
        public static void insertUser(User user) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... Voids) {
                    ModelSql.INSTANCE.userDao().insert(user);
                    return null;
                }
            }.execute();
        }

        public static void getUser(String userEmail,FetchUserLocally listener) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... Voids) {
                    User user = ModelSql.INSTANCE.userDao().getUser(userEmail);
                    listener.onComplete(user);
                    return null;
                }
            }.execute();
        }
    }
}
