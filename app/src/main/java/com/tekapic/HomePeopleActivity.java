package com.tekapic;


import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Picture;
import com.tekapic.model.User;
import java.util.ArrayList;
import java.util.Collections;



public class HomePeopleActivity extends AppCompatActivity implements PicturesRecyclerViewAdapter.ListItemClickListener {


    private TextView noPicturesText;
    private boolean isUserhasPics = false;
    private MenuItem item;
    private boolean isInFavorites;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mStatusDB;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private PicturesRecyclerViewAdapter adapter;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private Context context;
    private android.support.v7.app.ActionBar actionBar;
    private boolean isPrivate = true;


    public static int flag;
    public static User user;
    public static int firstVisibleItemPosition = 0;



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    private void albumsIconclicked() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

        if(isPrivate) {
            builder1.setMessage("For viewing " + user.getUsername() + "'s" + " albums, you need to be in " + user.getUsername() + "'s" + " favorites");
        }
        else {
            builder1.setMessage(user.getUsername() + " doesn't have any albums yet.");
        }


        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return false;
        }

        switch (item.getItemId()) {

            case R.id.favorites:
                if(!isInFavorites) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(HomePeopleActivity.this);
                        builder.setTitle("Add " + user.getUsername() + " to favorites?");

                        builder.setMessage("If your account is private, " + user.getUsername() +  " will be able to see your pictures.");

                        builder.setCancelable(false);

                        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveToFavorites();
                                Toast.makeText(getApplicationContext(), user.getUsername() + " added to your favorites", Toast.LENGTH_LONG).show();
                                item.setTitle("Remove from favorites");
                                item.setIcon(R.drawable.ic_star_black_24dp);
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });


                        final AlertDialog dialog = builder.create();
                        dialog.show();



                }
                else {


                    AlertDialog.Builder builder = new AlertDialog.Builder(HomePeopleActivity.this);
                    builder.setTitle("Remove " + user.getUsername() + " from favorites?");

                    builder.setMessage("If your account is private, " +  user.getUsername()  + " will not be able to see your pictures anymore.");

                    builder.setCancelable(false);

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            deleteFromFavorites();
                            Toast.makeText(getApplicationContext(), user.getUsername() + " removed from your favorites", Toast.LENGTH_LONG).show();
                            item.setTitle("Add to favorites");
                            item.setIcon(R.drawable.ic_star_border_black_24dp);

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                    final AlertDialog dialog = builder.create();
                    dialog.show();



                }
                return true;
            case R.id.albumsHomePeople:
                if(isUserhasPics) {
                    startActivity(new Intent(HomePeopleActivity.this, AlbumsPeopleActivity.class));
                }
                else {
                    albumsIconclicked();
                }
                return true;
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(!isNetworkConnected()) {
            popUpAlertDialogConnectionError();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_people_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }


    //****************onCreate()********************//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_people);

        actionBar = getSupportActionBar();


        setTitle(user.getUsername());

        noPicturesText = findViewById(R.id.textHomePeopleNoPics);

        mStatusDB = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId()).child("Pictures");

        isInFavorites = false;
        mAuth = FirebaseAuth.getInstance();
        databaseReference =   FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("Favorites")
                .child(user.getUserId());

        //here******************

        final DatabaseReference databaseReference2 =   FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId());
        final DatabaseReference databaseReference3 = databaseReference2;

        databaseReference2.child("accountPrivacy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue().equals("public")) {
                    isPrivate = false;
                    f();
                }
                else {

                    databaseReference3.child("Favorites").addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                String id = ds.child("userId").getValue(String.class);

                                Log.i("id", ds.child("userId").getValue(String.class));

                                if(id.equals(mAuth.getUid())) {

                                    Log.i("xxx", "yyyyyyyyyyyyyyyyyyyyy");


                                    isPrivate = false;
                                    f();
                                    break;
                                }
                            }
                            if(isPrivate) {

                                noPicturesText.setVisibility(View.VISIBLE);
                                noPicturesText.setText("This Account is Private");


                                mStatusDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        actionBar.setSubtitle("(" + Long.toString(dataSnapshot.getChildrenCount()) +")");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




//*************************************************************


    }

    public void f() {


        checkIfUserHasAnyPictures();

        mRecyclerView = findViewById(R.id.homePeopleRecyclerView);

        mRecyclerView.setHasFixedSize(true);

        linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

        mOnClickListener = this;
        context = this;

        getPictures();


        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(firstVisibleItemPosition);
        firstVisibleItemPosition = 0;

    }




    private void getPictures() {

        checkIfUserHasAnyPictures();


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Users").child(user.getUserId()).child("Pictures");

        ValueEventListener eventListener = new ValueEventListener() {

            boolean wasCalled = false;


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(wasCalled) {
                    picturesList.clear();

                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

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

                if(wasCalled) {
                    adapter.notifyDataSetChanged();
                }

                actionBar.setSubtitle("(" + Integer.toString(picturesList.size()) +")");

                Collections.reverse(picturesList);
                adapter = new PicturesRecyclerViewAdapter(picturesList, mOnClickListener, context);
                mRecyclerView.setAdapter(adapter);

                wasCalled = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usersdRef.addValueEventListener(eventListener);

    }

    @Override
    public void onListItemClick(int clickedItemIndex, Picture picture, int picturesListSize, ArrayList<Picture> picturesList) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        PicturePeopleActivity.picturesList.clear();
        PicturePeopleActivity.clickedItemIndex = clickedItemIndex;

        for(Picture p : picturesList) {
            PicturePeopleActivity.picturesList.add(p);
        }

        PicturePeopleActivity.isPictureFromAlbum = false;
        Intent intent = new Intent(HomePeopleActivity.this, PicturePeopleActivity.class);
        startActivity(intent);

        finish();

    }

    private void checkIfUserHasAnyPictures() {
        mStatusDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    noPicturesText.setVisibility(View.VISIBLE);
                    isUserhasPics = false;
                    mRecyclerView.setVisibility(View.GONE);
                }
                else {
                    noPicturesText.setVisibility(View.GONE);
                    isUserhasPics = true;
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
            try {
                firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

            }catch (Exception e) {

            }

    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        finish();

        switch (flag) {
            case 0:
                startActivity(new Intent(HomePeopleActivity.this, SearchActivity.class));
                break;
            case 1:
                startActivity(new Intent(HomePeopleActivity.this, FavoritesActivity.class));
                break;
            case 2:
                startActivity(new Intent(HomePeopleActivity.this, LikesActivity.class));
                break;
        }
    }

    public void goToProfile(View view) {
        finish();
        Intent intent = new Intent(HomePeopleActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void saveToFavorites() {
        databaseReference.child("userId").setValue(user.getUserId());
        isInFavorites = true;
    }

    private void deleteFromFavorites() {
        databaseReference.removeValue();
        isInFavorites = false;
    }

    private void checkIfInFavorites(MenuItem i) {

            item = i;
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        item.setIcon(R.drawable.ic_star_black_24dp);
                        isInFavorites = true;
                    }
                    else {
                        item.setIcon(R.drawable.ic_star_border_black_24dp);
                        isInFavorites = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.favorites);

        checkIfInFavorites(item);

        return super.onPrepareOptionsMenu(menu);
    }




}
