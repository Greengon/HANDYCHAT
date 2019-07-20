package com.example.handychat.Models;

public class User {
    public String name;
    public String image = null;
    public String email;
    public String address;
    public boolean customer = false;
    public boolean handyMan = false;
    public String category;
    public String area;

    public User(){

    }

    public User(String name,String email,String address,boolean customer, boolean handyMan,String category, String area){
        this.name = name;
        this.email = email;
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
