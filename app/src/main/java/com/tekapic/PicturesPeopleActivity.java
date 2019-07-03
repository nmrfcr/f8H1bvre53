package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Picture;
import java.util.ArrayList;
import java.util.Collections;

public class  PicturesPeopleActivity extends AppCompatActivity implements PicturesRecyclerViewAdapter.ListItemClickListener {

    private RecyclerView mRecyclerView;
    private PicturesRecyclerViewAdapter adapter;
    private Context context;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private RecyclerView.LayoutManager layoutManager;
    private android.support.v7.app.ActionBar actionBar;

    public static String wantedAlbum;
    public static int fVisibleItemPosition = 0;
    private BottomNavigationView bottomNavigationView;
    public static int index = 0;
    public static String username = "";


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    private void checkIfAlbumExists() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Users").child(ProfilePeopleActivity.user.getUserId()).child("Pictures");

        ValueEventListener eventListener = new ValueEventListener() {

            boolean wasCalled = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean hasAnyPicture = false;

                if(wasCalled) {
                    picturesList.clear();

                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String albumValue = ds.child(wantedAlbum).getValue(String.class);
                    if(albumValue.equals("1")) {

                        hasAnyPicture = true;
                        break;
                    }
                }

                if(hasAnyPicture == false) {
//                    finish();
//                    startActivity(new Intent(PicturesActivity.this, AlbumsActivity.class));
//                    return;
                    onBackPressed();
                    return;
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usersdRef.addValueEventListener(eventListener);

    }
    private void getPicturesByAlbum() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Users").child(ProfilePeopleActivity.user.getUserId()).child("Pictures");

        ValueEventListener eventListener = new ValueEventListener() {

            boolean wasCalled = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean hasAnyPicture = false;

                if(wasCalled) {
                    picturesList.clear();

                }

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

                if(wasCalled) {
                    adapter.notifyDataSetChanged();
                }

                if(hasAnyPicture == false) {
//                    finish();
//                    startActivity(new Intent(PicturesActivity.this, AlbumsActivity.class));
//                    return;
                    onBackPressed();
                    return;
                }

                actionBar.setSubtitle("(" + Integer.toString(picturesList.size()) +")");
                Collections.reverse(picturesList);
                adapter = new PicturesRecyclerViewAdapter(picturesList,mOnClickListener,context);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_people);

        String album = wantedAlbum.substring(0, 1).toUpperCase() + wantedAlbum.substring(1);
//        setTitle(username + "/" + album);
        setTitle(album);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.pictures_people_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        actionBar = getSupportActionBar();

        context = this;
        mOnClickListener = this;

        mRecyclerView = findViewById(R.id.picturesPeopleRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getApplicationContext(),3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

        getPicturesByAlbum();

        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(fVisibleItemPosition);
        fVisibleItemPosition = 0;
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();



        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(index);
        menuItem.setChecked(true);

        SpannableStringBuilder title = new SpannableStringBuilder(menuItem.getTitle());
        StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        title.setSpan(styleSpan, 0, title.length(), 0);
        menuItem.setTitle((title));


        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        //check if has pics
        checkIfAlbumExists();
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

        PicturePeopleActivity.index = index;

        PicturePeopleActivity.isPictureFromAlbum = true;
        Intent intent = new Intent(PicturesPeopleActivity.this, PicturePeopleActivity.class);
        startActivity(intent);

//        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        fVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.nav_explore:
                            startActivity(new Intent(PicturesPeopleActivity.this, ExploreActivity.class));
                            break;
                        case R.id.nav_search:
                            startActivity(new Intent(PicturesPeopleActivity.this, SearchActivity.class));
                            break;

                        case R.id.nav_add_picture:
                            startActivity(new Intent(PicturesPeopleActivity.this, AddPictureActivity.class));
                            break;

                        case R.id.nav_profile:
                            startActivity(new Intent(PicturesPeopleActivity.this, ProfileActivity.class));
                            break;
                    }

                    return true;
                }
            };

}
