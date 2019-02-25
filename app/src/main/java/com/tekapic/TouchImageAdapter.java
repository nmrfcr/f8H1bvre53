package com.tekapic;

/**
 * Created by LEV on 17/01/2019.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.tekapic.model.Picture;
import java.util.ArrayList;

public class TouchImageAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Picture> picturesList = new ArrayList<>();
    private boolean isSystemUIHidden;
    private PhotoView img;

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

        Log.i("instantiateItem", "position: " + Integer.toString(position));

        img = new PhotoView(container.getContext());
        ViewGroup.LayoutParams lp= new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        img.setLayoutParams(lp);


        Glide.with(context)
                .load(picturesList.get(position).getPictureUrl()).apply(new RequestOptions().override(1000, 1000).placeholder(R.drawable.loading_picture))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        Log.i("onLoadFailed", "Failed to load picture");
                        Toast.makeText(context, "Failed to load picture.", Toast.LENGTH_LONG).show();

//                        goBack();

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        img.setImageDrawable(resource);

                        return false;
                    }
                })
                .into(img);



        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);



        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("System Ui Visibility", Integer.toString(getSystemUiVisibility() ));

                if(getSystemUiVisibility() == 3328) {
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
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}