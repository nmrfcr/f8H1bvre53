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
import android.support.v4.view.ViewPager;
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



public class PictureActivity extends AppCompatActivity {

    private HackyViewPager mViewPager;
    private ImageView imageView;
    public static Picture picture;
    public static boolean isPictureFromAlbum;
    private DatabaseReference mStatusDB;
    private FirebaseAuth mAuth;
    private FirebaseStorage storageReference;
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

    private void deledePictureFromFirebase() {

        mDialog.setMessage("Deleting...");
        mDialog.show();
        mDialog.setCancelable(false);


        mStatusDB.child(picture.getPictureId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "Picture deleted.", Toast.LENGTH_SHORT).show();
    ;               goBack();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                mDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Error! picture wasn't deleted.", Toast.LENGTH_SHORT).show();
            }
        });

        StorageReference photoRef = storageReference.getReferenceFromUrl(picture.getPictureUrl());

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.i("onSuccess", "picture deleted from storage");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.i("onFailure", "picture wasn't deleted from storage");
            }
        });
    }

    private void popUpAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);
        builder.setTitle("Delete Picture");
        builder.setMessage("Are you sure you want to delete this picture?" + "\n\n" +
                "The picture will be deleted from all albums.");

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //check network state
                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                deledePictureFromFirebase();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
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

            case R.id.editPictureMenu:
                Intent intent = new Intent(PictureActivity.this, EditActivity.class);
                startActivity(intent);
                return true;
            case R.id.showDateMenu:
                showDate();
                return true;
            case R.id.deletePictureMenu:
                popUpAlertDialog();
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
        getMenuInflater().inflate(R.menu.picture_menu, menu);

        this.menu = menu;

        updatePicturePosition();


        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        mViewPager = findViewById(R.id.view_pager);

        mViewPager.setAdapter(new TouchImageAdapter(this,picturesList));
        mViewPager.setCurrentItem(clickedItemIndex);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setTitle(Integer.toString(position+1) + "/" + Integer.toString(picturesListSize));

                picture = picturesList.get(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



//        isSystemUIHidden = false;


        mDialog = new ProgressDialog(this);

//        progressBar = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();
        mStatusDB = FirebaseDatabase.getInstance().getReference().child(mAuth.getUid());
        storageReference =  FirebaseStorage.getInstance().getReference().getStorage();

        hideSystemUI();
        showSystemUI();



//        mViewPager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(getSystemUiVisibility() == 3840) {
//                    isSystemUIHidden = false;
//                }
//
//                if(isSystemUIHidden == false) {
//                    hideSystemUI();
//                    isSystemUIHidden = true;
//                }
//                else {
//                    showSystemUI();
//                    isSystemUIHidden = false;
//                }
//            }
//        });


//        imageView.setOnTouchListener(new OnSwipeTouchListener(PictureActivity.this) {
//
//            public void onSwipeRight() {
//                previousPicture();
//            }
//            public void onSwipeLeft() {
//                nextPicture();
//            }
//
//            public void onClick() {
////                Toast.makeText(PictureActivity.this, "click", Toast.LENGTH_SHORT).show();
//
//                if(getSystemUiVisibility() == 3840) {
//                    isSystemUIHidden = false;
//                }
//
//                if(isSystemUIHidden == false) {
//                    hideSystemUI();
//                    isSystemUIHidden = true;
//                }
//                else {
//                    showSystemUI();
//                    isSystemUIHidden = false;
//                }
//
//            }
//
////            public void onDoubleClick() {
////
//////                hideSystemUI();
//////                showSystemUI();
////                new Zoom(getApplicationContext(), image);
////
////            }
//
//        });

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if(getSystemUiVisibility() == 3840) {
//                    isSystemUIHidden = false;
//                }
//
//                if(isSystemUIHidden == false) {
//                    hideSystemUI();
//                    isSystemUIHidden = true;
//                }
//                else {
//                    showSystemUI();
//                    isSystemUIHidden = false;
//                }
//
//            }
//        });

//        View decorView = getWindow().getDecorView();
//        decorView.setOnSystemUiVisibilityChangeListener
//                (new View.OnSystemUiVisibilityChangeListener() {
//                    @Override
//                    public void onSystemUiVisibilityChange(int visibility) {
//                        // Note that system bars will only be "visible" if none of the
//                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
//                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                            // TODO: The system bars are visible. Make any desired
//                            // adjustments to your UI, such as showing the action bar or
//                            // other navigational controls.
//                            floatingActionButtonPrev.setVisibility(View.VISIBLE);
//                            floatingActionButtonNext.setVisibility(View.VISIBLE);
//
//                        } else {
//                            // TODO: The system bars are NOT visible. Make any desired
//                            // adjustments to your UI, such as hiding the action bar or
//                            // other navigational controls.
//
//                        }
//                    }
//                });

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
            startActivity(new Intent(PictureActivity.this, PicturesActivity.class));
        }
        else {
            startActivity(new Intent(PictureActivity.this, HomeActivity.class));

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
