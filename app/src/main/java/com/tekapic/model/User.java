package com.tekapic.model;
/**
 * Created by LEV on 28/07/2018.
 */

public class User {

    private String email;
    private String userId;

    public User() {
        this.email = "none";
        this.userId = "none";
    }

    public User(String email, String userId) {
        this.email = email;
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
