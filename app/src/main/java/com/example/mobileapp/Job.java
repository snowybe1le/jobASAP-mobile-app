package com.example.mobileapp;

import java.io.Serializable;

public class Job implements Serializable {

    private String title;
    private String description;  // company/description
    private double payment;      // numeric salary/payment
    private String location;
    private String category;

    private String imageUrl;     // Firebase Storage image URL
    private String postedBy;     // email or username of poster

    private String userId;       // Firebase UID

    private double lat; // latitude
    private double lng; // longitude

    // empty constructor for Firebase
    public Job() {}

    // basic constructor
    public Job(String title, String description, double payment, String location) {
        this.title = title;
        this.description = description;
        this.payment = payment;
        this.location = location;
        this.category = "General";
    }

    // constructor with category
    public Job(String title, String description, double payment, String location, String category) {
        this.title = title;
        this.description = description;
        this.payment = payment;
        this.location = location;
        this.category = category;
    }

    // full constructor with image, postedBy, and coordinates
    public Job(String title, String description, double payment, String location,
               String category, String imageUrl, String postedBy, String userId,
               double lat, double lng) {
        this.title = title;
        this.description = description;
        this.payment = payment;
        this.location = location;
        this.category = category;
        this.imageUrl = imageUrl;
        this.postedBy = postedBy;
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
    }



    // getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }



    public double getPayment() { return payment; }
    public void setPayment(double payment) { this.payment = payment; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    // ✅ ADD THESE TWO LINES for Firestore compatibility with "uid" field
    public String getUid() { return userId; }
    public void setUid(String uid) { this.userId = uid; }


    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
}
