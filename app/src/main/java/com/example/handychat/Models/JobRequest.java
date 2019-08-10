package com.example.handychat.Models;

public class JobRequest {
    public String id;
    public String imageUrl;
    public String userCreated;
    public String date;
    public String address;
    public String description;

    // Empty constructor is needed for firebase loading
    public JobRequest(){

    }

    public JobRequest(String id, String imageUrl, String userCreated, String date, String address, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.userCreated = userCreated;
        this.date = date;
        this.address = address;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
