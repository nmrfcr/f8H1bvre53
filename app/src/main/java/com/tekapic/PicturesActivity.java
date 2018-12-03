package com.tekapic;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Album;
import com.tekapic.model.Picture;

import java.util.ArrayList;
import java.util.Collections;

public class PicturesActivity extends AppCompatActivity implements PicturesRecyclerViewAdapter.ListItemClickListener {

    private RecyclerView mRecyclerView;
    private PicturesRecyclerViewAdapter adapter;
    public static String wantedAlbum;
    private FirebaseAuth mAuth;
    private Context context;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    RecyclerView.LayoutManager layoutManager;
    static int topView;
    public static int fVisibleItemPosition = 0;


//    @Override
//    protected void onPause() {
//        super.onPause();
//        positionIndex = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//    }


    private void getPicturesByAlbum() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child(mAuth.getUid());

        ValueEventListener eventListener = new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasAnyPicture = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String albumValue = ds.child(wantedAlbum).getValue(String.class);
                    if(albumValue.equals("1")) {

                        hasAnyPicture = true;

                        String pictureUrl = ds.child("pictureUrl").getValue(String.class);

                        String date = ds.child("date").getValue(String.class);

                        String pictureId = ds.child("pictureId").getValue(String.class);

                        String me = ds.child("me").getValue(String.class);
                        String family = ds.child("family").getValue(String.class);
                        String friends = ds.child("friends").getValue(String.class);
                        String love = ds.child("love").getValue(String.class);
                        String pets = ds.child("pets").getValue(String.class);
                        String nature = ds.child("nature").getValue(String.class);
                        String sport = ds.child("sport").getValue(String.class);
                        String persons = ds.child("persons").getValue(String.class);
                        String animals = ds.child("animals").getValue(String.class);
                        String vehicles = ds.child("vehicles").getValue(String.class);
                        String views = ds.child("views").getValue(String.class);
                        String food = ds.child("food").getValue(String.class);
                        String things = ds.child("things").getValue(String.class);
                        String funny = ds.child("funny").getValue(String.class);
                        String places = ds.child("places").getValue(String.class);
                        String art = ds.child("art").getValue(String.class);

                        Picture picture = new Picture(pictureId, pictureUrl, date, me, family,friends,love, pets,  nature,  sport,  persons, animals,  vehicles, views, food, things, funny, places,  art);

                        picturesList.add(picture);
                    }
                }

                if(hasAnyPicture == false) {
                    startActivity(new Intent(PicturesActivity.this, AlbumsActivity.class));
                    return;
                }
                Collections.reverse(picturesList);
                adapter = new PicturesRecyclerViewAdapter(picturesList,mOnClickListener,context);
                mRecyclerView.setAdapter(adapter);

//                GridLayoutManager mGridLayoutManager = new GridLayoutManager(PicturesActivity.this, 3);
//                mRecyclerView.setLayoutManager(mGridLayoutManager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(PicturesActivity.this, AlbumsActivity.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String album = wantedAlbum.substring(0, 1).toUpperCase() + wantedAlbum.substring(1);
        setTitle(album);

        setContentView(R.layout.activity_pictures);

        context = this;
        mOnClickListener = this;

        mAuth = FirebaseAuth.getInstance();

//        wantedAlbum = getIntent().getStringExtra("wanted_album");

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = findViewById(R.id.picturesRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getApplicationContext(),3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));




//        mRecyclerView.setLayoutManager(linearLayoutManager);

        getPicturesByAlbum();

        if(PostActivity.flag) {
            check();
        }

        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(fVisibleItemPosition);
        fVisibleItemPosition = 0;

    }

    private void check() {

        Log.i("check", "inCheck()");

        final Thread t1 = new Thread(new Runnable() {
//            Handler handler = new Handler();
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                        Log.i("while", "in Loop !!!!!!!!!!!!!!!!!!");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(PostActivity.flag == false) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @SuppressLint("NewApi")
                            @Override
                            public void run() {

                                adapter.notifyItemRangeRemoved(0, picturesList.size());
                                adapter.notifyItemRangeInserted(0, picturesList.size() + 1 );

                                picturesList.clear();

                                getPicturesByAlbum();
                            }
                        });
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        });
                        break;
                    }

                }
            }
        });

        t1.start();

    }


    private void popUpAlertDialogConnectionError() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Error");
        builder1.setMessage("There might be problems with the server or network connection.");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "TRY AGAIN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        if(isNetworkConnected() == false) {
//                            popUpAlertDialogConnectionError();
//                        }

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


//        mRecyclerView.scrollToPosition(positionIndex);



        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex, Picture picture, int picturesListSize, ArrayList<Picture> picturesList) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        PictureActivity.picturesListSize = picturesListSize;
        PictureActivity.clickedItemIndex = clickedItemIndex;
        PictureActivity.picturesList = picturesList;


//        Toast.makeText(getApplicationContext(), "clickedItemIndex = " + clickedItemIndex, Toast.LENGTH_SHORT).show();
//        Log.i("pictureUrl", picture.getPictureUrl());

        PictureActivity.picture = picture;
        PictureActivity.isPictureFromAlbum = true;
        Intent intent = new Intent(PicturesActivity.this, PictureActivity.class);
        startActivity(intent);

        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();

        fVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }
}
