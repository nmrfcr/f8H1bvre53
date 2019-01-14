package com.tekapic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.tekapic.model.Picture;
import com.tekapic.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class HomePeopleActivity extends AppCompatActivity implements PicturesRecyclerViewAdapter.ListItemClickListener {

    public static boolean flag;
    public static User user;
    private TextView noPicturesText;
    private boolean isUserhasPics = false;
    private MenuItem item;
    private boolean isInFavorites;


    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mStatusDB;
    //    private DatabaseReference mUserDB;
    private RecyclerView mRecyclerView;
    private Uri mPhotoUri;
    private Button button;
    private ImageView imageViewIcon;

    boolean[] checkedCategories = new boolean[Picture.numberOfAlbums+1];
    private EditText emailEditText, passwordEditText;
    private ProgressDialog mDialog;
    private String mCurrentPhotoPath;
    private LinearLayoutManager linearLayoutManager;
//    RecyclerView.LayoutManager layoutManager;

    //    static int lastFirstVisiblePosition = 0;
//    private int size;
    private PicturesRecyclerViewAdapter adapter;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private Context context;
    public static int firstVisibleItemPosition = 0;

    //    private Parcelable mLayoutManagerState;
//    private static final String LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE";
//    private  Menu menuItem;
//    private   boolean flag = false;
    private android.support.v7.app.ActionBar actionBar;


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }



    private void albumsIconclicked() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("This user doesn't have any albums yet.");

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
//                        if(isNetworkConnected() == false) {
//                            popUpAlertDialogConnectionError();
//                        }

                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return false;
        }

        switch (item.getItemId()) {

            case R.id.favorites:
                if(!isInFavorites) {
                    saveToFavorites();
                    Toast.makeText(context, user.getEmail().substring(0, user.getEmail().indexOf("@")) + " added to your favorites", Toast.LENGTH_LONG).show();
                    item.setTitle("Remove from favorites");
                    item.setIcon(R.drawable.ic_star_black_24dp);
                }
                else {
                    deleteFromFavorites();
                    Toast.makeText(context, user.getEmail().substring(0, user.getEmail().indexOf("@")) + " removed from your favorites", Toast.LENGTH_LONG).show();
                    item.setTitle("Add to favorites");
                    item.setIcon(R.drawable.ic_star_border_black_24dp);
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
        checkIfUserHasAnyPictures();
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

        setTitle(user.getEmail().substring(0, user.getEmail().indexOf("@")));

        actionBar = getSupportActionBar();

        noPicturesText = findViewById(R.id.textHomePeopleNoPics);

        mStatusDB = FirebaseDatabase.getInstance().getReference().child(user.getUserId());


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

        isInFavorites = false;
        mAuth = FirebaseAuth.getInstance();
        databaseReference =   FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("Favorites")
                .child(user.getUserId());

    }



    private void getPictures() {

        checkIfUserHasAnyPictures();


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child(user.getUserId());

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

        PicturePeopleActivity.picturesListSize = picturesListSize;
        PicturePeopleActivity.clickedItemIndex = clickedItemIndex;
        PicturePeopleActivity.picturesList = picturesList;


        PicturePeopleActivity.picture = picture;
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

        firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        finish();
        if(flag) {
            Intent intent = new Intent(HomePeopleActivity.this, SearchActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(HomePeopleActivity.this, FavoritesActivity.class);
            startActivity(intent);
        }
    }

    public void goToProfile(View view) {
        finish();
        Intent intent = new Intent(HomePeopleActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void saveToFavorites() {
        databaseReference.child("userId").setValue(user.getUserId());
        databaseReference.child("email").setValue(user.getEmail());

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
