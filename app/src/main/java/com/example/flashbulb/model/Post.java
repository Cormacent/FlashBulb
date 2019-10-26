package com.example.flashbulb.model;
import com.google.firebase.database.ServerValue;
public class Post {
    private String postkey;
    private String title;
    private String description;
    private String picture;
    private String userId;
    private String userPhoto,userName;
    private float ratingUser;
    private Object timeStamp;
    private String userEmail;
    public Post() { }
    public Post(String title, String description, String picture, String userId, String userPhoto, String userName,String userEmail, float ratingUser) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.userEmail = userEmail;
        this.ratingUser = ratingUser;
        this.timeStamp = ServerValue.TIMESTAMP;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getPostkey() {
        return postkey;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getPicture() {
        return picture;
    }
    public String getUserId() {
        return userId;
    }
    public String getUserPhoto() {
        return userPhoto;
    }
    public String getUserName() {
        return userName;
    }
    public float getRatingUser() {
        return ratingUser;
    }
    public Object getTimeStamp() {
        return timeStamp;
    }
    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setRatingUser(float ratingUser) {
        this.ratingUser = ratingUser;
    }
    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
