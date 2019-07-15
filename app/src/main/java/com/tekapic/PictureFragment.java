package com.tekapic;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment {

    private PhotoView photoView;
    private ImageView imageView;
//    private boolean isSystemUIHidden = false;


    public PictureFragment() {
        // Required empty public constructor
    }



    private void setPicture(final String pictureUrl, final Context context) {

        Glide.with(context)
                .load(pictureUrl).apply(new RequestOptions().override(1000, 1000))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        Log.i("onLoadFailed", "Failed to load picture");
//                        Toast.makeText(context, "Failed to load picture.", Toast.LENGTH_LONG).show();


                        //1. go back?
                        //2. delete that data?

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        imageView.setVisibility(View.GONE);
                        photoView.setVisibility(View.VISIBLE);
                        photoView.setImageDrawable(resource);

                        return false;
                    }
                })
                .into(photoView);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture, container, false);

        photoView = view.findViewById(R.id.photo_view);
        imageView = view.findViewById(R.id.loading_picture);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("System Ui Visibility", Integer.toString(getSystemUiVisibility() ));

                if(getSystemUiVisibility() == 3328) {
                    FragmentCollectionAdapter.isSystemUIHidden = false;
                }

                if(FragmentCollectionAdapter.isSystemUIHidden == false) {
                    hideSystemUI();
                    FragmentCollectionAdapter.isSystemUIHidden = true;
                }
                else {
                    showSystemUI();
                    FragmentCollectionAdapter.isSystemUIHidden = false;
                }
            }
        });


        String pictureUrl = getArguments().getString("pictureUrl");

        Context context = getActivity();

        setPicture(pictureUrl, context);

        return  view;
    }

    private int getSystemUiVisibility() {
        View decorView = ((Activity) getActivity()).getWindow().getDecorView();
        return decorView.getSystemUiVisibility();
    }

    private void showSystemUI() {
        View decorView = ((Activity) getActivity()).getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = ((Activity) getActivity()).getWindow().getDecorView();
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
