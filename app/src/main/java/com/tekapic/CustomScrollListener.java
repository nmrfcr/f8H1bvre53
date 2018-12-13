package com.tekapic;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

/**
 * Created by LEV on 13/12/2018.
 */

public class CustomScrollListener extends RecyclerView.OnScrollListener {



    public CustomScrollListener() {
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                System.out.println("The RecyclerView is not scrolling");
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                System.out.println("Scrolling now");
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                System.out.println("Scroll Settling");
                break;

        }

    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {



        if (dx > 0) {
            System.out.println("Scrolled Right");
        } else if (dx < 0) {
            System.out.println("Scrolled Left");
        } else {
            System.out.println("No Horizontal Scrolled");
        }

        if (dy > 0) {
            System.out.println("Scrolled Downwards");
            PicturesRecyclerViewAdapter.flag = true;
        } else if (dy < 0) {
            System.out.println("Scrolled Upwards");
            PicturesRecyclerViewAdapter.flag = true;

        } else {
            System.out.println("No Vertical Scrolled");
        }
    }
}