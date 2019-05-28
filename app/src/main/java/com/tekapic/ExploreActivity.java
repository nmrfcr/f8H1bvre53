package com.tekapic;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Picture;

import java.util.ArrayList;
import java.util.Collections;

public class ExploreActivity extends AppCompatActivity implements PicturesRecyclerViewAdapter.ListItemClickListener {

    private DatabaseReference databaseReference;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private Context context;
    public static int firstVisibleItemPosition = 0;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private PicturesRecyclerViewAdapter adapter;
    private ArrayList<String> usersIdList=new ArrayList<String>() ;


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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

    @Override
    protected void onResume() {
        super.onResume();

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }




    private void getDataFromFirebase() {


        ValueEventListener eventListener = new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {


                    DataSnapshot pictureDataSnapshot = userDataSnapshot.child("Pictures");

                    if(pictureDataSnapshot.hasChildren()) {
                        usersIdList.add(userDataSnapshot.child("userId").getValue(String.class));
                    }

                    int i = 0;

                    for(DataSnapshot ds: pictureDataSnapshot.getChildren()) {

                        if(i++ == pictureDataSnapshot.getChildrenCount() - 1) {

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

                            Picture picture = new Picture(pictureId, pictureUrl, date, me, family, friends, love, pets, nature, sport, persons, animals, vehicles, views, food, things, funny, places, art);


                            picturesList.add(picture);

                        }


                    }

                }



                adapter = new PicturesRecyclerViewAdapter(picturesList, mOnClickListener, context);
                mRecyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mRecyclerView = findViewById(R.id.exploreRecyclerView);

        mRecyclerView.setHasFixedSize(true);

        linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

        mOnClickListener = this;
        context = this;

        getDataFromFirebase();

        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(firstVisibleItemPosition);
        firstVisibleItemPosition = 0;



    }
    @Override
    protected void onPause() {
        super.onPause();

        firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(ExploreActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onListItemClick(int clickedItemIndex, Picture picture, int picturesListSize, ArrayList<Picture> picturesList) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        int x = 0;
        for(String userId: usersIdList) {
            if(clickedItemIndex == x++) {

                PictureExploreActivity.clickedItemIndex = clickedItemIndex;

                PictureExploreActivity.picturesList.clear();

                for(Picture p : picturesList) {
                    PictureExploreActivity.picturesList.add(p);
                }

                PictureExploreActivity.usersIdList.clear();

                for(String id : usersIdList) {
                    PictureExploreActivity.usersIdList.add(id);
                }

                Intent intent = new Intent(ExploreActivity.this, PictureExploreActivity.class);
                startActivity(intent);

//                finish();
                break;

            }
        }








//        int clickedPosition = getAdapterPosition();
//        int x = 0;
//
//        for(Picture picture : picturesList) {
//            if(x++ == clickedPosition) {
//
//
//                mOnClickListener.onListItemClick(clickedPosition, picture, getItemCount(), picturesList);
//                break;
//            }
//        }


    }
}
