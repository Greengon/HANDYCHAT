package com.example.handychat.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "image")
    public String image = null;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "customer")
    public boolean customer = false;

    @ColumnInfo(name = "handyman")
    public boolean handyMan = false;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "area")
    public String area;

    public User(){

    }

    public User(String name,@NonNull String email,String image,String address,boolean customer, boolean handyMan,String category, String area){
        this.name = name;
        this.email = email;
        this.image = image;
        this.address = address;
        this.customer = customer;
        this.handyMan = handyMan;
        this.category = category;
        this.area = area;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public boolean isCustomer() {
        return customer;
    }

    public boolean isHandyMan() {
        return handyMan;
    }

    public String getCategory() {
        return category;
    }

    public String getArea() {
        return area;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCustomer(boolean customer) {
        this.customer = customer;
    }

    public void setHandyMan(boolean handyMan) {
        this.handyMan = handyMan;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
