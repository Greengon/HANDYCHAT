package com.example.handychat.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "comments")
public class Comment {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "job_request_id")
    public String job_request_id;

    @ColumnInfo(name = "user_created")
    public String userCreated;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "comment")
    public String comment;

    // Empty constructor is needed for firebase loading
    public Comment(){

    }

    public Comment(@NonNull String id, String job_request_id, String userCreated, String date, String comment) {
        this.id = id;
        this.job_request_id = job_request_id;
        this.userCreated = userCreated;
        this.date = date;
        this.comment = comment;
    }

    // Getters
    public String getId() {
        return id;
    }
    public String getJobRequestId() {
        return job_request_id;
    }
    public String getUserCreated() {
        return userCreated;
    }
    public String getDate() {
        return date;
    }
    public String getComment() {
        return comment;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }
    public void setJobRequestId(String job_request_id) {
        this.job_request_id = job_request_id;
    }
    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
