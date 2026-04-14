package com.example.mobileapp;

public class Review {
    public String reviewerName;
    public String avatarId;
    public float rating;
    public String comment;
    public String reviewerId;


    // empty constructor required by Firestore
    public Review() {}

    public Review(String reviewerName, String avatarId, float rating, String comment, String reviewerId) {
        this.reviewerName = reviewerName;
        this.avatarId = avatarId;
        this.rating = rating;
        this.comment = comment;
        this.reviewerId = reviewerId;
    }
}

