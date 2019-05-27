package com.tekapic.model;
/**
 * Created by LEV on 28/07/2018.
 */

public class User {

    private String email;
    private String username;
    private String userId;
    private String accountPrivacy;
    private int numberOfViolatingTermsOfUse;
    private boolean warnForViolatingTermsOfUse;
    private String profilePictureUrl;

    public User() {
        this.profilePictureUrl = "none";
        this.email = "none";
        this.username = "none";
        this.userId = "none";
        this.accountPrivacy = "none";
        this.numberOfViolatingTermsOfUse = -1;
        this.warnForViolatingTermsOfUse = false;
    }

    public User(String email, String username, String userId, String accountPrivacy, int numberOfViolatingTermsOfUse,
                boolean warnForViolatingTermsOfUse, String profilePictureUrl) {
        this.email = email;
        this.username = username;
        this.userId = userId;
        this.accountPrivacy = accountPrivacy;
        this.numberOfViolatingTermsOfUse = numberOfViolatingTermsOfUse;
        this.warnForViolatingTermsOfUse = warnForViolatingTermsOfUse;
        this.profilePictureUrl = profilePictureUrl;
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

    public int getNumberOfViolatingTermsOfUse() {
        return numberOfViolatingTermsOfUse;
    }

    public void setNumberOfViolatingTermsOfUse(int numberOfViolatingTermsOfUse) {
        this.numberOfViolatingTermsOfUse = numberOfViolatingTermsOfUse;
    }

    public boolean isWarnForViolatingTermsOfUse() {
        return warnForViolatingTermsOfUse;
    }

    public void setWarnForViolatingTermsOfUse(boolean warnForViolatingTermsOfUse) {
        this.warnForViolatingTermsOfUse = warnForViolatingTermsOfUse;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
