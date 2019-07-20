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

    /******************* SQL actions ********************/


    /******************* MyHelper class ********************/
    class MyHelper extends SQLiteOpenHelper{

        public MyHelper(Context context){
            super(context,"database.db",null,4);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table users(name text,image text ,email text primary key,address text,customer int,handyman int,category text,area text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table users");
            db.execSQL("create table users(name text,image text ,email text primary key,address text,customer int,handyman int,category text,area text)");
        }
    }
    /******************* MyHelper class ********************/
}
