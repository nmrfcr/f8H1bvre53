package com.tekapic;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.tekapic.model.Picture;

import java.util.ArrayList;

/**
 * Created by LEV on 25/04/2019.
 */

public class FragmentCollectionAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Picture> picturesList = new ArrayList<>();
    public static boolean isSystemUIHidden;



    public FragmentCollectionAdapter(FragmentManager fm, ArrayList<Picture> picturesList) {
        super(fm);
        this.picturesList = picturesList;
        isSystemUIHidden = false;
    }



    @Override
    public Fragment getItem(int position) {

        PictureFragment pictureFragment = new PictureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("pictureUrl", picturesList.get(position).getPictureUrl());
        pictureFragment.setArguments(bundle);

        return pictureFragment;
    }

    @Override
    public int getCount() {

        //amount of pages to display
        return picturesList.size();
    }
}
