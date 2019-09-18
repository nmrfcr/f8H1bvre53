package com.tekapic.model;

import java.io.Serializable;

/**
 * Created by LEV on 29/07/2018.
 */

public class Picture implements Serializable {

    private String pictureId;
    private String pictureUrl;
    private String date;

    //albums
    private Boolean me;
    private Boolean family;
    private Boolean friends;
    private Boolean love;
    private Boolean pets;
    private Boolean nature;
    private Boolean sport;
    private Boolean persons;
    private Boolean animals;
    private Boolean vehicles;
    private Boolean views;
    private Boolean food;
    private Boolean things;
    private Boolean funny;
    private Boolean places;
    private Boolean art;


    public static final int numberOfAlbums = 16;
    public static final String[] albumsNames = { "me", "family", "friends", "love", "pets", "nature", "sport", "persons", "animals", "vehicles", "views", "food", "things", "funny", "places", "art"};
    public static final String[] albumsNamesUpperCase = { "Me", "Family", "Friends", "Love", "Pets", "Nature", "Sport", "Persons", "Animals", "Vehicles", "Views", "Food", "Things", "Funny", "Places", "Art"};


    public Picture() {
        pictureId = "none";
        this.pictureUrl = "none";
        this.date = "00/00/00";
        //albums
        this.me = false;
        this.family = false;
        this.friends = false;
        this.love = false;
        this.pets = false;
        this.nature = false;
        this.sport = false;;
        this.persons = false;
        this.animals = false;
        this.vehicles = false;
        this.views = false;
        this.food = false;
        this.things = false;
        this.funny = false;
        this.places = false;
        this.art = false;
    }

    public Picture(String pictureId, String pictureUrl, String date, Boolean me, Boolean family, Boolean friends, Boolean love, Boolean pets, Boolean nature, Boolean sport, Boolean persons, Boolean animals, Boolean vehicles, Boolean views, Boolean food, Boolean things, Boolean funny, Boolean places, Boolean art) {
        this.pictureId = pictureId;
        this.pictureUrl = pictureUrl;
        this.date = date;
        this.me = me;
        this.family = family;
        this.friends = friends;
        this.love = love;
        this.pets = pets;
        this.nature = nature;
        this.sport = sport;
        this.persons = persons;
        this.animals = animals;
        this.vehicles = vehicles;
        this.views = views;
        this.food = food;
        this.things = things;
        this.funny = funny;
        this.places = places;
        this.art = art;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getMe() {
        return me;
    }

    public void setMe(Boolean me) {
        this.me = me;
    }

    public Boolean getFamily() {
        return family;
    }

    public void setFamily(Boolean family) {
        this.family = family;
    }

    public Boolean getFriends() {
        return friends;
    }

    public void setFriends(Boolean friends) {
        this.friends = friends;
    }

    public Boolean getLove() {
        return love;
    }

    public void setLove(Boolean love) {
        this.love = love;
    }

    public Boolean getPets() {
        return pets;
    }

    public void setPets(Boolean pets) {
        this.pets = pets;
    }

    public Boolean getNature() {
        return nature;
    }

    public void setNature(Boolean nature) {
        this.nature = nature;
    }

    public Boolean getSport() {
        return sport;
    }

    public void setSport(Boolean sport) {
        this.sport = sport;
    }

    public Boolean getPersons() {
        return persons;
    }

    public void setPersons(Boolean persons) {
        this.persons = persons;
    }

    public Boolean getAnimals() {
        return animals;
    }

    public void setAnimals(Boolean animals) {
        this.animals = animals;
    }

    public Boolean getVehicles() {
        return vehicles;
    }

    public void setVehicles(Boolean vehicles) {
        this.vehicles = vehicles;
    }

    public Boolean getViews() {
        return views;
    }

    public void setViews(Boolean views) {
        this.views = views;
    }

    public Boolean getFood() {
        return food;
    }

    public void setFood(Boolean food) {
        this.food = food;
    }

    public Boolean getThings() {
        return things;
    }

    public void setThings(Boolean things) {
        this.things = things;
    }

    public Boolean getFunny() {
        return funny;
    }

    public void setFunny(Boolean funny) {
        this.funny = funny;
    }

    public Boolean getPlaces() {
        return places;
    }

    public void setPlaces(Boolean places) {
        this.places = places;
    }

    public Boolean getArt() {
        return art;
    }

    public void setArt(Boolean art) {
        this.art = art;
    }

    public static int getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public static String[] getAlbumsNames() {
        return albumsNames;
    }

    public static String[] getAlbumsNamesUpperCase() {
        return albumsNamesUpperCase;
    }
}
