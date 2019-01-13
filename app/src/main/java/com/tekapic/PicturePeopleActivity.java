package com.tekapic;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tekapic.model.Picture;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



public class PicturePeopleActivity extends AppCompatActivity {

    private ImageView imageView;
    public static Picture picture;
    public static boolean isPictureFromAlbum;
    private ProgressDialog mDialog;
    private ProgressBar progressBar;

    public static int clickedItemIndex;
    public static int picturesListSize;
    public static ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private Menu menu;
    private boolean isSystemUIHidden;
    private Drawable image;
    private LinearLayout linearLayout;
    FloatingActionButton floatingActionButtonPrev, floatingActionButtonNext;


    public void prevPicture(View view) {
        previousPicture();
    }
    public void nextPicture(View view) {
        nextPicture();
    }

    private void setPictureUrl(Context context, String pictureUrl) {
        Glide.with(context)
                .load(pictureUrl).apply(new RequestOptions().override(1000, 1000))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        Log.i("onLoadFailed", "Failed to load picture");
                        Toast.makeText(getApplicationContext(), "Failed to load picture.", Toast.LENGTH_SHORT).show();

                        goBack();

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setImageDrawable(resource);

                        return false;
                    }
                })
                .into(imageView);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return false;
        }
        switch (item.getItemId()) {

            case R.id.showDatePicturePeopleMenu:
                showDate();
                return true;

            case R.id.reportAbusePicturePeople:
                Toast.makeText(this, "Report Abuse", Toast.LENGTH_SHORT).show();
                return true;

            case android.R.id.home:
                goBack();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void nextPicture() {

        if(clickedItemIndex == 0 && picturesListSize == 1) {
            return;
        }

        if((clickedItemIndex + 1) == picturesListSize) {
            clickedItemIndex = -1;
        }
        progressBar.setVisibility(View.VISIBLE);

        picture = picturesList.get(clickedItemIndex+1);

        setPictureUrl(getApplicationContext(), picture.getPictureUrl());
        ++clickedItemIndex;

        updatePicturePosition();
    }

    private void previousPicture() {

        if(clickedItemIndex == 0 && picturesListSize == 1) {
            return;
        }

        if(clickedItemIndex == 0) {
            clickedItemIndex = picturesListSize;
        }
        progressBar.setVisibility(View.VISIBLE);

        picture = picturesList.get(clickedItemIndex-1);

        setPictureUrl(getApplicationContext(), picture.getPictureUrl());

        --clickedItemIndex;

        updatePicturePosition();
    }

    private void updatePicturePosition() {

        setTitle(Integer.toString(clickedItemIndex+1) + "/" + Integer.toString(picturesListSize));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_people_menu, menu);

        this.menu = menu;

        updatePicturePosition();

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_people);

        isSystemUIHidden = false;

        floatingActionButtonPrev = findViewById(R.id.prev_picturePeople);
        floatingActionButtonNext = findViewById(R.id.next_picturePeople);

        mDialog = new ProgressDialog(this);

        imageView = findViewById(R.id.photo_viewPicturePeople);

        progressBar = findViewById(R.id.progressPicturePeople);

        hideSystemUI();
        showSystemUI();

        setPictureUrl(this, picture.getPictureUrl());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getSystemUiVisibility() == 3840) {
                    isSystemUIHidden = false;
                }

                if(isSystemUIHidden == false) {
                    floatingActionButtonPrev.setVisibility(View.GONE);
                    floatingActionButtonNext.setVisibility(View.GONE);
                    hideSystemUI();
                    isSystemUIHidden = true;
                }
                else {
                    floatingActionButtonPrev.setVisibility(View.VISIBLE);
                    floatingActionButtonNext.setVisibility(View.VISIBLE);
                    showSystemUI();
                    isSystemUIHidden = false;
                }
            }
        });


        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            // adjustments to your UI, such as showing the action bar or
                            // other navigational controls.
                            floatingActionButtonPrev.setVisibility(View.VISIBLE);
                            floatingActionButtonNext.setVisibility(View.VISIBLE);

                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.

                        }
                    }
                });

    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
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

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private int getSystemUiVisibility() {
        View decorView = getWindow().getDecorView();
        return decorView.getSystemUiVisibility();
    }




    private void goBack() {
        finish();
        if(isPictureFromAlbum) {
            startActivity(new Intent(PicturePeopleActivity.this, PicturesPeopleActivity.class));
        }
        else {
            startActivity(new Intent(PicturePeopleActivity.this, HomePeopleActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }


    private void popUpAlertDialogConnectionError() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Error");
        builder1.setMessage("There might be problems with the server or network connection.");

        builder1.setPositiveButton(
                "TRY AGAIN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }

    private void showDate() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Date Taken");
        builder1.setMessage(picture.getDate());

        builder1.setPositiveButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }









}
