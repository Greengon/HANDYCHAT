package com.example.handychat.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "job_requests")
public class JobRequest {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "image_url")
    public String imageUrl;

    @ColumnInfo(name = "user_created")
    public String userCreated;

    @ColumnInfo(name = "user_image")
    public String userImage;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "area")
    public String area;

    // Empty constructor is needed for FireBase loading
    public JobRequest(){

    }

    public JobRequest(@NonNull String id, String imageUrl, String userCreated,String userImage, String date, String address, String description,String category, String area) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.userCreated = userCreated;
        this.userImage = userImage;
        this.date = date;
        this.address = address;
        this.description = description;
        this.category = category;
        this.area = area;
    }

    // Getters
    public String getId() {
        return id;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getUserCreated() {
        return userCreated;
    }
    public String getUserImage(){
        return userImage;
    }
    public String getDate() {
        return date;
    }
    public String getAddress() {
        return address;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() {
        return category;
    }
    public String getArea() {
        return area;
    }


    // Setters
    public void setId(String id) {
        this.id = id;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }
    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setArea(String area) {
        this.area = area;
    }
}
