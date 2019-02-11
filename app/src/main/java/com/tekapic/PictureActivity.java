package com.tekapic;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tekapic.model.Picture;
import java.util.ArrayList;


public class PictureActivity extends AppCompatActivity {

    private HackyViewPager mViewPager;
    private DatabaseReference mStatusDB;
    private FirebaseAuth mAuth;
    private FirebaseStorage storageReference;
    private ProgressDialog mDialog;
    private android.support.v7.app.ActionBar actionBar;


    public static Picture picture;
    public static boolean isPictureFromAlbum;
    public static int clickedItemIndex;
    public static ArrayList<Picture> picturesList=new ArrayList<Picture>() ;



    private void deledePictureFromFirebase() {

        mDialog.setMessage("Deleting...");
        mDialog.show();
        mDialog.setCancelable(false);


        mStatusDB.child(picture.getPictureId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDialog.dismiss();
                goBack();
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


    private void updatePicturePosition(int index) {

        setTitle(Integer.toString(index+1) + "/" + Integer.toString(picturesList.size()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_menu, menu);

        updatePicturePosition(clickedItemIndex);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80000000));

        Log.i("PictureActivity", "onCreate was called");

        mViewPager = findViewById(R.id.view_pager);


        mViewPager.setAdapter(new TouchImageAdapter(this,picturesList));
        mViewPager.setCurrentItem(clickedItemIndex);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                updatePicturePosition(position);

                picture = picturesList.get(position);

                clickedItemIndex = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        mStatusDB = FirebaseDatabase.getInstance().getReference().child(mAuth.getUid());
        storageReference =  FirebaseStorage.getInstance().getReference().getStorage();

        hideSystemUI();
        showSystemUI();

        picture = picturesList.get(clickedItemIndex);



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
        builder1.setTitle("Date Added");
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