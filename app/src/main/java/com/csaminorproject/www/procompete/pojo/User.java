package com.csaminorproject.www.procompete.pojo;

/**
 * Created by Nitin on 14/04/2017.
 */

public class User {
    private String name;
    private String photoUrl;
    public User() {

    }
    public User(String name, String photoUrl) {
        this.name = name;
        this.photoUrl = photoUrl;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}