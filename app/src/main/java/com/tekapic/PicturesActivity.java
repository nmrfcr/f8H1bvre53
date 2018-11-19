package com.tekapic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
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

public class PicturesActivity extends AppCompatActivity implements PicturesRecyclerViewAdapter.ListItemClickListener {

    private RecyclerView mRecyclerView;
    private PicturesRecyclerViewAdapter adapter;
    public static String wantedAlbum;
    private FirebaseAuth mAuth;
    private Context context;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;



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
        setContentView(R.layout.activity_pictures);

        context = this;
        mOnClickListener = this;

        mAuth = FirebaseAuth.getInstance();

//        wantedAlbum = getIntent().getStringExtra("wanted_album");

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = findViewById(R.id.picturesRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));




//        mRecyclerView.setLayoutManager(linearLayoutManager);

        getPicturesByAlbum();

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

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex, Picture picture) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

//        Toast.makeText(getApplicationContext(), "clickedItemIndex = " + clickedItemIndex, Toast.LENGTH_SHORT).show();
//        Log.i("pictureUrl", pictureUrl);

        PictureActivity.picture = picture;
        PictureActivity.isPictureFromAlbum = true;
        Intent intent = new Intent(PicturesActivity.this, PictureActivity.class);
        startActivity(intent);

    }
}
