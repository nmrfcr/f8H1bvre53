package com.tekapic.model;

import android.view.View;

/**
 * Created by LEV on 21/08/2018.
 */

public class Album {
    private String name;
    private int picture;
    private View view;

    public Album() {
        this.name = "none";
        this.picture = 0;
    }

    public Album(String name, int picture) {
        this.name = name;
        this.picture = picture;
    }
    public Album(String name, View view) {
        this.name = name;
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }
}
