package com.tekapic;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.Map;

public class AlbumsActivity extends AppCompatActivity implements AlbumsRecyclerViewAdapter.ListItemClickListener {

    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<Album> albumsList=new ArrayList<Album>() ;
    private AlbumsRecyclerViewAdapter adapter;
    private Map<String, Boolean> albumsMap = new HashMap<>();
    Context context;
    AlbumsRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private  Menu menuItem;
    private android.support.v7.app.ActionBar actionBar;







    private void getUserAlbums() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersdRef = rootRef.child(mAuth.getUid());

        ValueEventListener eventListener = new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                boolean meDoesntExist = true, familyDoesntExist = true, friendsDoesntExist = true;
                boolean loveDoesntExist = true, petsDoesntExist = true, natureDoesntExist = true;
                boolean sportDoesntExist = true, personsDoesntExist = true, animalsDoesntExist = true;
                boolean vehiclesDoesntExist = true, viewsDoesntExist = true, foodDoesntExist = true;
                boolean thingsDoesntExist = true, funnyDoesntExist = true, placesDoesntExist = true;
                boolean artDoesntExist = true;


                for (DataSnapshot ds : dataSnapshot.getChildren()) {

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

                    if(me.equals("1") && meDoesntExist) {
                        albumsMap.put("me", true);
                        meDoesntExist = false;
                    }
                    if(family.equals("1") && familyDoesntExist) {
                        albumsMap.put("family", true);
                        familyDoesntExist = false;
                    }
                    if(friends.equals("1") && friendsDoesntExist) {
                        albumsMap.put("friends", true);
                        friendsDoesntExist = false;
                    }
                    if(love.equals("1") && loveDoesntExist) {
                        albumsMap.put("love", true);
                        loveDoesntExist = false;
                    }
                    if(pets.equals("1") && petsDoesntExist) {
                        albumsMap.put("pets", true);
                        petsDoesntExist = false;
                    }
                    if(nature.equals("1") && natureDoesntExist) {
                        albumsMap.put("nature", true);
                        natureDoesntExist = false;
                    }
                    if(sport.equals("1") && sportDoesntExist) {
                        albumsMap.put("sport", true);
                        sportDoesntExist = false;
                    }
                    if(persons.equals("1") && personsDoesntExist) {
                        albumsMap.put("persons", true);
                        personsDoesntExist = false;
                    }
                    if(animals.equals("1") && animalsDoesntExist) {
                        albumsMap.put("animals", true);
                        animalsDoesntExist = false;
                    }
                    if(vehicles.equals("1") && vehiclesDoesntExist) {
                        albumsMap.put("vehicles", true);
                        vehiclesDoesntExist = false;
                    }
                    if(views.equals("1") && viewsDoesntExist) {
                        albumsMap.put("views", true);
                        viewsDoesntExist = false;
                    }
                    if(food.equals("1") && foodDoesntExist) {
                        albumsMap.put("food", true);
                        foodDoesntExist = false;
                    }
                    if(things.equals("1") && thingsDoesntExist) {
                        albumsMap.put("things", true);
                        thingsDoesntExist = false;
                    }
                    if(funny.equals("1") && funnyDoesntExist) {
                        albumsMap.put("funny", true);
                        funnyDoesntExist = false;
                    }
                    if(places.equals("1") && placesDoesntExist) {
                        albumsMap.put("places", true);
                        placesDoesntExist = false;
                    }
                    if(art.equals("1") && artDoesntExist) {
                        albumsMap.put("art", true);
                        artDoesntExist = false;
                    }
                }

                if(meDoesntExist) {
                    albumsMap.put("me", false);
                }
                if(familyDoesntExist) {
                    albumsMap.put("family", false);
                }
                if(friendsDoesntExist) {
                    albumsMap.put("friends", false);
                }
                if(loveDoesntExist) {
                    albumsMap.put("love", false);
                }
                if(petsDoesntExist) {
                    albumsMap.put("pets", false);
                }
                if(natureDoesntExist) {
                    albumsMap.put("nature", false);
                }
                if(sportDoesntExist) {
                    albumsMap.put("sport", false);
                }
                if(personsDoesntExist) {
                    albumsMap.put("persons", false);
                }
                if(animalsDoesntExist) {
                    albumsMap.put("animals", false);
                }
                if(vehiclesDoesntExist) {
                    albumsMap.put("vehicles", false);
                }
                if(viewsDoesntExist) {
                    albumsMap.put("views", false);
                }
                if(foodDoesntExist) {
                    albumsMap.put("food", false);
                }
                if(thingsDoesntExist) {
                    albumsMap.put("things", false);
                }
                if(funnyDoesntExist) {
                    albumsMap.put("funny", false);
                }
                if(placesDoesntExist) {
                    albumsMap.put("places", false);
                }
                if(artDoesntExist) {
                    albumsMap.put("art", false);
                }




                for(int i = 0; i < Picture.numberOfAlbums; i++) {
                    if(albumsMap.get(Picture.albumsNames[i])) {
                        int id = getResources().getIdentifier("com.tekapic:drawable/" + Picture.albumsNames[i], null, null);
                        albumsList.add(new Album(Picture.albumsNames[i], id));
                    }
                }

//                if(flag) {
//                    MenuItem item = menuItem.findItem(R.id.total_pics_home_menu);
//                    item.setTitle("(" + Integer.toString(albumsList.size()) +")");
//                }

                if(albumsList.size() == 0) {
                    goToHomeActivity();
                    return;
                }

                actionBar.setSubtitle("(" + Integer.toString(albumsList.size()) +")");

                adapter = new AlbumsRecyclerViewAdapter(albumsList,mOnClickListener,context);
                mRecyclerView.setAdapter(adapter);

                GridLayoutManager mGridLayoutManager = new GridLayoutManager(AlbumsActivity.this, 3);
                mRecyclerView.setLayoutManager(mGridLayoutManager);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);


    }

    private void goToHomeActivity() {
        finish();
        startActivity(new Intent(AlbumsActivity.this, HomeActivity.class));
    }

    @Override
    public void onBackPressed() {
        goToHomeActivity();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.albums_menu, menu);
//
//        menuItem = menu;
//
//        ///////////////////////////////
//
//
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//
//        DatabaseReference usersdRef = rootRef.child(mAuth.getUid());
//
//        ValueEventListener eventListener = new ValueEventListener() {
//            @SuppressLint("ResourceType")
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                boolean meDoesntExist = true, familyDoesntExist = true, friendsDoesntExist = true;
//                boolean loveDoesntExist = true, petsDoesntExist = true, natureDoesntExist = true;
//                boolean sportDoesntExist = true, personsDoesntExist = true, animalsDoesntExist = true;
//                boolean vehiclesDoesntExist = true, viewsDoesntExist = true, foodDoesntExist = true;
//                boolean thingsDoesntExist = true, funnyDoesntExist = true, placesDoesntExist = true;
//                boolean artDoesntExist = true;
//                ArrayList<Album> albumsList=new ArrayList<Album>() ;
//
//
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                    String me = ds.child("me").getValue(String.class);
//                    String family = ds.child("family").getValue(String.class);
//                    String friends = ds.child("friends").getValue(String.class);
//                    String love = ds.child("love").getValue(String.class);
//                    String pets = ds.child("pets").getValue(String.class);
//                    String nature = ds.child("nature").getValue(String.class);
//                    String sport = ds.child("sport").getValue(String.class);
//                    String persons = ds.child("persons").getValue(String.class);
//                    String animals = ds.child("animals").getValue(String.class);
//                    String vehicles = ds.child("vehicles").getValue(String.class);
//                    String views = ds.child("views").getValue(String.class);
//                    String food = ds.child("food").getValue(String.class);
//                    String things = ds.child("things").getValue(String.class);
//                    String funny = ds.child("funny").getValue(String.class);
//                    String places = ds.child("places").getValue(String.class);
//                    String art = ds.child("art").getValue(String.class);
//
//                    if(me.equals("1") && meDoesntExist) {
//                        albumsMap.put("me", true);
//                        meDoesntExist = false;
//                    }
//                    if(family.equals("1") && familyDoesntExist) {
//                        albumsMap.put("family", true);
//                        familyDoesntExist = false;
//                    }
//                    if(friends.equals("1") && friendsDoesntExist) {
//                        albumsMap.put("friends", true);
//                        friendsDoesntExist = false;
//                    }
//                    if(love.equals("1") && loveDoesntExist) {
//                        albumsMap.put("love", true);
//                        loveDoesntExist = false;
//                    }
//                    if(pets.equals("1") && petsDoesntExist) {
//                        albumsMap.put("pets", true);
//                        petsDoesntExist = false;
//                    }
//                    if(nature.equals("1") && natureDoesntExist) {
//                        albumsMap.put("nature", true);
//                        natureDoesntExist = false;
//                    }
//                    if(sport.equals("1") && sportDoesntExist) {
//                        albumsMap.put("sport", true);
//                        sportDoesntExist = false;
//                    }
//                    if(persons.equals("1") && personsDoesntExist) {
//                        albumsMap.put("persons", true);
//                        personsDoesntExist = false;
//                    }
//                    if(animals.equals("1") && animalsDoesntExist) {
//                        albumsMap.put("animals", true);
//                        animalsDoesntExist = false;
//                    }
//                    if(vehicles.equals("1") && vehiclesDoesntExist) {
//                        albumsMap.put("vehicles", true);
//                        vehiclesDoesntExist = false;
//                    }
//                    if(views.equals("1") && viewsDoesntExist) {
//                        albumsMap.put("views", true);
//                        viewsDoesntExist = false;
//                    }
//                    if(food.equals("1") && foodDoesntExist) {
//                        albumsMap.put("food", true);
//                        foodDoesntExist = false;
//                    }
//                    if(things.equals("1") && thingsDoesntExist) {
//                        albumsMap.put("things", true);
//                        thingsDoesntExist = false;
//                    }
//                    if(funny.equals("1") && funnyDoesntExist) {
//                        albumsMap.put("funny", true);
//                        funnyDoesntExist = false;
//                    }
//                    if(places.equals("1") && placesDoesntExist) {
//                        albumsMap.put("places", true);
//                        placesDoesntExist = false;
//                    }
//                    if(art.equals("1") && artDoesntExist) {
//                        albumsMap.put("art", true);
//                        artDoesntExist = false;
//                    }
//                }
//
//                if(meDoesntExist) {
//                    albumsMap.put("me", false);
//                }
//                if(familyDoesntExist) {
//                    albumsMap.put("family", false);
//                }
//                if(friendsDoesntExist) {
//                    albumsMap.put("friends", false);
//                }
//                if(loveDoesntExist) {
//                    albumsMap.put("love", false);
//                }
//                if(petsDoesntExist) {
//                    albumsMap.put("pets", false);
//                }
//                if(natureDoesntExist) {
//                    albumsMap.put("nature", false);
//                }
//                if(sportDoesntExist) {
//                    albumsMap.put("sport", false);
//                }
//                if(personsDoesntExist) {
//                    albumsMap.put("persons", false);
//                }
//                if(animalsDoesntExist) {
//                    albumsMap.put("animals", false);
//                }
//                if(vehiclesDoesntExist) {
//                    albumsMap.put("vehicles", false);
//                }
//                if(viewsDoesntExist) {
//                    albumsMap.put("views", false);
//                }
//                if(foodDoesntExist) {
//                    albumsMap.put("food", false);
//                }
//                if(thingsDoesntExist) {
//                    albumsMap.put("things", false);
//                }
//                if(funnyDoesntExist) {
//                    albumsMap.put("funny", false);
//                }
//                if(placesDoesntExist) {
//                    albumsMap.put("places", false);
//                }
//                if(artDoesntExist) {
//                    albumsMap.put("art", false);
//                }
//
//
//
//
//                for(int i = 0; i < Picture.numberOfAlbums; i++) {
//                    if(albumsMap.get(Picture.albumsNames[i])) {
//                        albumsList.add(new Album(Picture.albumsNames[i], 0));
//                    }
//                }
//
//                //here********************************************
//
//                MenuItem item = menuItem.findItem(R.id.total_albums);
//                item.setTitle("(" + Integer.toString(albumsList.size()) +")");
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        usersdRef.addListenerForSingleValueEvent(eventListener);
//
//
//        /////////////////////////////
//
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        actionBar = getSupportActionBar();


        context = this;
        mOnClickListener = this;

        mAuth = FirebaseAuth.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);


        mRecyclerView = findViewById(R.id.albumsRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        getUserAlbums();
//
//        if(albumsList.isEmpty()) {
//            Toast.makeText(this, "isEmpty", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(this, "is not empty", Toast.LENGTH_SHORT).show();
//
//            adapter = new AlbumsRecyclerViewAdapter(albumsList,this,this);
//            mRecyclerView.setAdapter(adapter);
//
//            GridLayoutManager mGridLayoutManager = new GridLayoutManager(AlbumsActivity.this, 3);
//            mRecyclerView.setLayoutManager(mGridLayoutManager);
//        }




        //check////////////////////////


//        int id = getResources().getIdentifier("com.tekapic:drawable/" + "art", null, null);
//        albumsList.add(new Album("art", id));
//
//        adapter = new AlbumsRecyclerViewAdapter(albumsList,this,this);
//        mRecyclerView.setAdapter(adapter);
//
//        GridLayoutManager mg = new GridLayoutManager(AlbumsActivity.this, 3);
//        mRecyclerView.setLayoutManager(mg);

        if(PostActivity.flag) {
            check();
        }


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

                                adapter.notifyItemRangeRemoved(0, albumsList.size());
                                adapter.notifyItemRangeInserted(0, albumsList.size() + 1 );

                                albumsList.clear();

                                getUserAlbums();
                            }
                        });

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
    public void onListItemClick(int clickedItemIndex, String album) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

            PicturesActivity.wantedAlbum = album;
//        Toast.makeText(getApplicationContext(), "clickedItemIndex = " + clickedItemIndex + "  album: " + album, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AlbumsActivity.this, PicturesActivity.class);
//        intent.putExtra("wanted_album", album);
        startActivity(intent);
    }
}
