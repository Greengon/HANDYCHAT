package com.example.handychat.Models;

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
}
