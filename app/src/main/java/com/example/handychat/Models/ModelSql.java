package com.example.handychat.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.handychat.MyApplication;

public class ModelSql {
    MyHelper mDbHelper;

    public ModelSql(){
       mDbHelper = new MyHelper(MyApplication.getContext());
    }


    /******************* SQL actions ********************/

    /******************* User SQL actions ********************/
    public void addUser(User user){
        ContentValues values = new ContentValues();
        values.put("name",user.name);
        values.put("image",user.image);
        values.put("email",user.email);
        values.put("address",user.address);
        values.put("customer",user.customer);
        values.put("handyMan",user.handyMan);
        values.put("category",user.category);
        values.put("area",user.area);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert("users","email",values);
    }

    /******************* Job request SQL actions ********************/
    public void addJobRequest(JobRequest jobRequest){
        ContentValues values = new ContentValues();
        values.put("id",jobRequest.id);
        values.put("image_url",jobRequest.imageUrl);
        values.put("user_created",jobRequest.userCreated);
        values.put("date",jobRequest.date);
        values.put("address",jobRequest.address);
        values.put("description",jobRequest.description);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert("jobRequests","id",values);
    }

    /******************* SQL actions ********************/


    /******************* MyHelper class ********************/
    class MyHelper extends SQLiteOpenHelper{

        public MyHelper(Context context){
            super(context,"database.db",null,4);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table users(name text,image text ,email text primary key,address text,customer int,handyman int,category text,area text)");
            db.execSQL("create table job_requests(id text primary key,image_url text ,user_created text,date text,address text,description text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table users");
            db.execSQL("drop table job_requests");
            db.execSQL("create table users(name text,image text ,email text primary key,address text,customer int,handyman int,category text,area text)");
            db.execSQL("create table job_requests(id text primary key,imageUrl text ,user_created text,date text,address text,description text)");
        }
    }
    /******************* MyHelper class ********************/
}
