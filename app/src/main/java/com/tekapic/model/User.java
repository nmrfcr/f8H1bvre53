package com.tekapic.model;
/**
 * Created by LEV on 28/07/2018.
 */

public class User {

    private String email;
    private String username;
    private String userId;
    private Boolean privateAccount;
    private String profilePictureUrl;

    private Integer numberOfViolatingTermsOfUse;
    private Boolean warnForViolatingTermsOfUse;
    private Boolean sharingPicturesEnabled;

    public User() {
        this.profilePictureUrl = "none";
        this.email = "none";
        this.username = "none";
        this.userId = "none";
        this.privateAccount = false;
        this.numberOfViolatingTermsOfUse = -1;
        this.warnForViolatingTermsOfUse = false;
        this.sharingPicturesEnabled = false;
    }

    public User(String email, String username, String userId, Boolean privateAccount, Integer numberOfViolatingTermsOfUse,
                Boolean warnForViolatingTermsOfUse, String profilePictureUrl, Boolean sharingPicturesEnabled) {
        this.email = email;
        this.username = username;
        this.userId = userId;
        this.privateAccount = privateAccount;
        this.numberOfViolatingTermsOfUse = numberOfViolatingTermsOfUse;
        this.warnForViolatingTermsOfUse = warnForViolatingTermsOfUse;
        this.profilePictureUrl = profilePictureUrl;
        this.sharingPicturesEnabled = sharingPicturesEnabled;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getPrivateAccount() {
        return privateAccount;
    }

    public void setPrivateAccount(Boolean privateAccount) {
        this.privateAccount = privateAccount;
    }

    public Integer getNumberOfViolatingTermsOfUse() {
        return numberOfViolatingTermsOfUse;
    }

    public void setNumberOfViolatingTermsOfUse(Integer numberOfViolatingTermsOfUse) {
        this.numberOfViolatingTermsOfUse = numberOfViolatingTermsOfUse;
    }

    public Boolean getWarnForViolatingTermsOfUse() {
        return warnForViolatingTermsOfUse;
    }

    public void setWarnForViolatingTermsOfUse(Boolean warnForViolatingTermsOfUse) {
        this.warnForViolatingTermsOfUse = warnForViolatingTermsOfUse;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Boolean getSharingPicturesEnabled() {
        return sharingPicturesEnabled;
    }

    public void setSharingPicturesEnabled(Boolean sharingPicturesEnabled) {
        this.sharingPicturesEnabled = sharingPicturesEnabled;
    }
}
