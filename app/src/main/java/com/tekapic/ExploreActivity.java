package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Picture;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExploreActivity extends AppCompatActivity implements PicturesRecyclerViewAdapter.ListItemClickListener {

    private DatabaseReference databaseReference;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    public static int firstVisibleItemPosition = 0;

    private ArrayList<Picture> picturesList = new ArrayList<Picture>() ;
    private ArrayList<String> datesList = new ArrayList<String>() ;
    private ArrayList<Picture> newPicturesList=new ArrayList<Picture>() ;
    private ArrayList<String> newUsersIdList=new ArrayList<String>() ;

    private PicturesRecyclerViewAdapter adapter;
    private ArrayList<String> usersIdList=new ArrayList<String>() ;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private Context context;


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


        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        menuItem.setEnabled(false);

        SpannableStringBuilder title = new SpannableStringBuilder(menuItem.getTitle());
        StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        title.setSpan(styleSpan, 0, title.length(), 0);
        menuItem.setTitle((title));


        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }
//        picturesList.clear();
//        getDataFromFirebase();

        checkWarnings();

    }

    private void checkWarnings() {

            databaseReference.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    boolean warnForViolatingTermsOfUse = (Boolean)dataSnapshot.child("warnForViolatingTermsOfUse").getValue();

                    if(warnForViolatingTermsOfUse) {
                        Log.i("warn", "true");

                    }else {
                        Log.i("warn", "false");

                    }


                    if(warnForViolatingTermsOfUse) {
                        //warn
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setCancelable(false);
                        builder1.setMessage("You upload and share illigal content, " +
                                "therefore this illegal content was deleted, your option for sharing and uploading pictures might be blocked.");

                        builder1.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        databaseReference.child(mAuth.getUid()).child("warnForViolatingTermsOfUse").setValue(false);
                                    }
                                });

                        AlertDialog alertDialog = builder1.create();
                        alertDialog.show();

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    public static List<Picture> sortByYears(List<Picture> moviesList) {

        Collections.sort(moviesList, new Comparator<Picture>() {


            @Override
            public int compare(Picture picture, Picture t1) {
                return 0;
            }
        });
        Collections.reverse(moviesList);

        return moviesList;
    }



    private void getDataFromFirebase() {


        ValueEventListener eventListener = new ValueEventListener() {

            boolean wasCalled = false;



            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(wasCalled) {
                    newPicturesList.clear();

                }



                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {

                    Boolean privateAccount = (Boolean)userDataSnapshot.child("privateAccount").getValue();

                    DataSnapshot pictureDataSnapshot = userDataSnapshot.child("Pictures");

                    if(pictureDataSnapshot.hasChildren() && privateAccount == false) {

//                        usersIdList.add(userDataSnapshot.child("userId").getValue(String.class));

                        int i = 0;

                        for(DataSnapshot ds: pictureDataSnapshot.getChildren()) {

                            if(i++ == pictureDataSnapshot.getChildrenCount() - 1) {

                                String pictureUrl = ds.child("pictureUrl").getValue(String.class);

                                String date = ds.child("date").getValue(String.class);

                                String pictureId = ds.child("pictureId").getValue(String.class);

                                Boolean me = (Boolean)ds.child("me").getValue();
                                Boolean family = (Boolean)ds.child("family").getValue();
                                Boolean friends = (Boolean)ds.child("friends").getValue();
                                Boolean love = (Boolean)ds.child("love").getValue();
                                Boolean pets = (Boolean)ds.child("pets").getValue();
                                Boolean nature = (Boolean)ds.child("nature").getValue();
                                Boolean sport = (Boolean)ds.child("sport").getValue();
                                Boolean persons = (Boolean)ds.child("persons").getValue();
                                Boolean animals = (Boolean)ds.child("animals").getValue();
                                Boolean vehicles = (Boolean)ds.child("vehicles").getValue();
                                Boolean views = (Boolean)ds.child("views").getValue();
                                Boolean food = (Boolean)ds.child("food").getValue();
                                Boolean things = (Boolean)ds.child("things").getValue();
                                Boolean funny = (Boolean)ds.child("funny").getValue();
                                Boolean places = (Boolean)ds.child("places").getValue();
                                Boolean art = (Boolean)ds.child("art").getValue();

                                Picture picture = new Picture(pictureId, pictureUrl, date, me, family, friends, love, pets, nature, sport, persons, animals, vehicles, views, food, things, funny, places, art);

                                datesList.add(date);
                                picturesList.add(picture);
                                usersIdList.add(userDataSnapshot.child("userId").getValue(String.class));


                            }


                        }











                    }




                }

                if(wasCalled) {
                    adapter.notifyDataSetChanged();
                }

                Collections.sort(datesList, new Comparator<String>() {
                    DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    @Override
                    public int compare(String o1, String o2) {
                        try {
                            return f.parse(o1).compareTo(f.parse(o2));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

                for(String date: datesList) {

                            Log.i("date", date);
                            int i = 0;
                            for(Picture picture: picturesList) {

                                if(date.equals(picture.getDate())) {
//                                    Log.i("newPicturesList", date);
                                    newUsersIdList.add(usersIdList.get(i));
                                    newPicturesList.add(picture);
                                    break;
                                }
                                i++;
                            }
                        }

                Collections.reverse(newUsersIdList);




                       usersIdList.clear();
                        picturesList.clear();
                        datesList.clear();


                Collections.reverse(newPicturesList);

                adapter = new PicturesRecyclerViewAdapter(newPicturesList, mOnClickListener, context);
                mRecyclerView.setAdapter(adapter);

                wasCalled = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(eventListener);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.explore_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mRecyclerView = findViewById(R.id.exploreRecyclerView);

        mRecyclerView.setHasFixedSize(true);

        linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

        mOnClickListener = this;
        context = this;


        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(firstVisibleItemPosition);
        firstVisibleItemPosition = 0;


        mAuth = FirebaseAuth.getInstance();

        getDataFromFirebase();


    }
    @Override
    protected void onPause() {
        super.onPause();

        firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }



    @Override
    public void onListItemClick(int clickedItemIndex, Picture picture, int picturesListSize, ArrayList<Picture> picturesList) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        int x = 0;
        for(String userId: newUsersIdList) {
            if(clickedItemIndex == x++) {

                PictureExploreActivity.clickedItemIndex = clickedItemIndex;

                PictureExploreActivity.picturesList.clear();

                for(Picture p : picturesList) {
                    PictureExploreActivity.picturesList.add(p);
                }

                PictureExploreActivity.usersIdList.clear();

                for(String id : newUsersIdList) {
                    PictureExploreActivity.usersIdList.add(id);
                }

                Intent intent = new Intent(ExploreActivity.this, PictureExploreActivity.class);
                startActivity(intent);

//                finish();
                break;

            }
        }


    }





    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.nav_search:
                            startActivity(new Intent(ExploreActivity.this, SearchActivity.class));
                            break;

                        case R.id.nav_add_picture:
                            startActivity(new Intent(ExploreActivity.this, AddPictureActivity.class));
                            break;

                        case R.id.nav_profile:
                            startActivity(new Intent(ExploreActivity.this, ProfileActivity.class));

                            break;
                    }

                    return true;
                }
            };



    private void change() {

        final DatabaseReference databaseReference1;
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child("ho75vUJo0VPup1d4ln0JDu89Cf62").
                child("Pictures");

        ValueEventListener eventListener = new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String pictureId = ds.child("pictureId").getValue(String.class);

                    for(int i = 0; i < Picture.albumsNames.length; i++) {
                        String album = ds.child(Picture.albumsNames[i]).getValue(String.class);


                        if(album.equals("0")) {
                            databaseReference1.child(pictureId).child(Picture.albumsNames[i]).setValue(false);
                        }
                        else {
                            databaseReference1.child(pictureId).child(Picture.albumsNames[i]).setValue(true);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference1.addValueEventListener(eventListener);

    }


}
