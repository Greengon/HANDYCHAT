package com.example.handychat.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.handychat.MyApplication;

public class ModelSql {
    final public static ModelSql instance = new ModelSql();
    MyHelper mDbHelper;

    /********* Relevant user table static values ******/
    static final String USER_TABLE = "users";
    static final String USER_NAME = "name";
    static final String USER_IMAGE = "image";
    static final String USER_ADDRESS = "address";
    static final String USER_EMAIL = "email";
    static final String USER_DATE = "date";
    static final String USER_CUSTOMER = "customer";
    static final String USER_HANDYMAN = "handyMan";
    static final String USER_CATEGORY = "category";
    static final String USER_AREA = "area";
    /********* Relevant user table static values ******/

    /********* Relevant job request table static values ******/
    static final String JOB_REQUEST_TABLE = "job_requests";
    static final String JOB_REQUEST_ID = "id";
    static final String JOB_REQUEST_IMAGE_URL = "image_url";
    static final String JOB_REQUEST_USER_CREATED = "user_created";
    static final String JOB_REQUEST_DATE = "date";
    static final String JOB_REQUEST_ADDRESS = "address";
    static final String JOB_REQUEST_DESCRIPTION = "description";
    /********* Relevant job request table static values ******/


    public ModelSql(){
        mDbHelper = new MyHelper(MyApplication.getContext());
    }



    /******************* SQL actions ********************/

    /******************* User SQL actions ********************/

    public void addUser(User user){
        ContentValues values = new ContentValues();
        values.put(USER_NAME,user.name);
        values.put(USER_IMAGE,user.image);
        values.put(USER_EMAIL,user.email);
        values.put(USER_ADDRESS,user.address);
        values.put(USER_CUSTOMER,user.customer);
        values.put(USER_HANDYMAN,user.handyMan);
        values.put(USER_CATEGORY,user.category);
        values.put(USER_AREA,user.area);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(USER_TABLE,USER_EMAIL,values);
    }

    /******************* User SQL actions ********************/

    /******************* Job request SQL actions ********************/

    public void addJobRequest(JobRequest jobRequest){
        ContentValues values = new ContentValues();
        values.put(JOB_REQUEST_ID,jobRequest.id);
        values.put(JOB_REQUEST_IMAGE_URL,jobRequest.imageUrl);
        values.put(JOB_REQUEST_USER_CREATED,jobRequest.userCreated);
        values.put(JOB_REQUEST_DATE,jobRequest.date);
        values.put(JOB_REQUEST_ADDRESS,jobRequest.address);
        values.put(JOB_REQUEST_DESCRIPTION,jobRequest.description);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(JOB_REQUEST_TABLE,JOB_REQUEST_ID,values);
    }

    public JobRequest getJobRequest(String jobId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] selectionArgs = {jobId};
//        Cursor cursor = db.rawQuery("SELECT * FROM job_requests",null);
//        Cursor cursor = db.query(JOB_REQUEST_TABLE,null,null, null,null,null,null);
        Cursor cursor = db.query(JOB_REQUEST_TABLE,null,JOB_REQUEST_ID + " = ?", selectionArgs,null,null,null);

        // Next condition will return null only if no results found
        int count = cursor.getCount();
        if (cursor.moveToFirst()){
            return createJobRequestFromCursor(cursor);
        }
        return null;
    }

    // Given a result lets create a job request object
    private static JobRequest createJobRequestFromCursor(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(JOB_REQUEST_ID);
        int imageUrlIndex = cursor.getColumnIndex(JOB_REQUEST_IMAGE_URL);
        int userCreatedIndex = cursor.getColumnIndex(JOB_REQUEST_USER_CREATED);
        int dateIndex = cursor.getColumnIndex(JOB_REQUEST_DATE);
        int addressIndex = cursor.getColumnIndex(JOB_REQUEST_ADDRESS);
        int descriptionIndex = cursor.getColumnIndex(JOB_REQUEST_DESCRIPTION);

        JobRequest jobRequest = new JobRequest();
        jobRequest.setId(cursor.getString(idIndex));
        jobRequest.setImageUrl(cursor.getString(imageUrlIndex));
        jobRequest.setDate(cursor.getString(dateIndex));
        jobRequest.setAddress(cursor.getString(addressIndex));
        jobRequest.setDescription(cursor.getString(descriptionIndex));

        return jobRequest;
    }

    /******************* Job request SQL actions ********************/

    /******************* SQL actions ********************/


    /******************* MyHelper class ********************/
    class MyHelper extends SQLiteOpenHelper{

        public MyHelper(Context context){
            super(context,"database.db",null,4);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + USER_TABLE + "("
                    + USER_NAME + " text," +
                    USER_IMAGE + " text ," +
                    USER_EMAIL + " text primary key," +
                    USER_ADDRESS + " text," +
                    USER_CUSTOMER + " int," +
                    USER_HANDYMAN + " int," +
                    USER_CATEGORY + " text," +
                    USER_AREA + " text)");
            db.execSQL("create table "+ JOB_REQUEST_TABLE +"("
                    + JOB_REQUEST_ID +" text primary key," +
                    JOB_REQUEST_IMAGE_URL + " text ," +
                    JOB_REQUEST_USER_CREATED + " text," +
                    JOB_REQUEST_DATE + " text," +
                    JOB_REQUEST_ADDRESS + " text," +
                    JOB_REQUEST_DESCRIPTION + " text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table users");
            db.execSQL("drop table job_requests");
            db.execSQL("create table " + USER_TABLE + "("
                    + USER_NAME + " text," +
                    USER_IMAGE + " text ," +
                    USER_EMAIL + " text primary key," +
                    USER_ADDRESS + " text," +
                    USER_CUSTOMER + " int," +
                    USER_HANDYMAN + " int," +
                    USER_CATEGORY + " text," +
                    USER_AREA + " text)");
            db.execSQL("create table "+ JOB_REQUEST_TABLE +"("
                    + JOB_REQUEST_ID +" text primary key," +
                    JOB_REQUEST_IMAGE_URL + " text ," +
                    JOB_REQUEST_USER_CREATED + " text," +
                    JOB_REQUEST_DATE + " text," +
                    JOB_REQUEST_ADDRESS + " text," +
                    JOB_REQUEST_DESCRIPTION + " text)");
        }
    }
    /******************* MyHelper class ********************/
}
