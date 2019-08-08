package com.example.handychat.Models;

import android.graphics.Bitmap;

public class Model {
    final public static Model instance = new Model();
    ModelSql modelSql;
    ModelFirebase modelFirebase;

    private Model(){
        modelSql = new ModelSql();
        modelFirebase = new ModelFirebase();
    }

    /******** User handling **********/
    public interface AddUserListener{
        void onComplete(boolean success);
    }

    public void addUser(User user, AddUserListener listener){
        modelSql.addUser(user);
        modelFirebase.addUser(user,listener);
    }
    /******** User handling **********/

    /******** JobRequest handling **********/
    public interface AddJobRequestListener{
        void onComplete(boolean success);
    }

    public void addJobRequest(JobRequest jobRequest, AddJobRequestListener listener){
        modelSql.addJobRequest(jobRequest);
        modelFirebase.addJobRequest(jobRequest,listener);
    }
    /******** JobRequest handling **********/

    /******** Image saving *********/
    public interface SaveImageListener{
        void onComplete(String url);
    }

    public void saveImage(Bitmap imageBitmap, SaveImageListener listener){
        modelFirebase.saveImage(imageBitmap,listener);
    }
    /******** Image saving *********/
}
