package com.tekapic;

/**
 * Created by LEV on 17/01/2019.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.tekapic.model.Picture;

import java.util.ArrayList;

public class TouchImageAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Picture> picturesList = new ArrayList<>();
    private boolean isSystemUIHidden;
    private int currentPage;



    public TouchImageAdapter(Context context,ArrayList<Picture> picturesList){
        this.picturesList = picturesList;
        this.context = context;
        isSystemUIHidden = false;
    }



    @Override
    public int getCount() {
        return picturesList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {

        PhotoView img = new PhotoView(container.getContext());
        ViewGroup.LayoutParams lp= new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        img.setLayoutParams(lp);
//        img.setImageDrawable(getImageFromSdCard(filename,position));

        Glide.with(context)
                .load(picturesList.get(position).getPictureUrl())
                .into(img);

        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);



        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getSystemUiVisibility() == 3840) {
                    isSystemUIHidden = false;
                }

                if(isSystemUIHidden == false) {
                    hideSystemUI();
                    isSystemUIHidden = true;
                }
                else {
                    showSystemUI();
                    isSystemUIHidden = false;
                }
            }
        });


        return img;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }




    private int getSystemUiVisibility() {
        View decorView = ((Activity) context).getWindow().getDecorView();
        return decorView.getSystemUiVisibility();
    }

    private void showSystemUI() {
        View decorView = ((Activity) context).getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = ((Activity) context).getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }





}