package com.tekapic.model;
/**
 * Created by LEV on 28/07/2018.
 */

public class User {

    private String email;
    private String username;
    private String userId;
    private String accountPrivacy;

    public User() {
        this.email = "none";
        this.username = "none";
        this.userId = "none";
        this.accountPrivacy = "public";
    }

    public User(String email, String username, String userId, String accountPrivacy) {
        this.email = email;
        this.username = username;
        this.userId = userId;
        this.accountPrivacy = accountPrivacy;
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

    public String getAccountPrivacy() {
        return accountPrivacy;
    }

    public void setAccountPrivacy(String accountPrivacy) {
        this.accountPrivacy = accountPrivacy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
