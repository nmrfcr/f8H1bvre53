package com.tekapic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekapic.model.Picture;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private ImageView imageView;
    private CheckBox me, family, friends, love, pets, nature, sport;
    private CheckBox persons, animals, vehicles, views, food, things, funny;
    private CheckBox places, art;
    private CheckBox[] checkBoxesArray = new CheckBox[Picture.numberOfAlbums];
    private ProgressDialog mDialog;
    private FirebaseUser currentUser;
    private DatabaseReference mUsersDB;
    private Picture picture;




    private void updateAlbums() {

        mDialog.setMessage("Please wait...");
        mDialog.show();
        mDialog.setCancelable(false);

        Map<String, Object> updatePhotoMap = new HashMap<>();

        updatePhotoMap.put("me", picture.getMe());
        updatePhotoMap.put("family", picture.getFamily());
        updatePhotoMap.put("friends", picture.getFriends());
        updatePhotoMap.put("love", picture.getLove());
        updatePhotoMap.put("pets", picture.getPets());
        updatePhotoMap.put("nature", picture.getNature());
        updatePhotoMap.put("sport", picture.getSport());
        updatePhotoMap.put("persons", picture.getPersons());
        updatePhotoMap.put("animals", picture.getAnimals());
        updatePhotoMap.put("vehicles", picture.getVehicles());
        updatePhotoMap.put("views", picture.getViews());
        updatePhotoMap.put("food", picture.getFood());
        updatePhotoMap.put("things", picture.getThings());
        updatePhotoMap.put("funny", picture.getFunny());
        updatePhotoMap.put("places", picture.getPlaces());
        updatePhotoMap.put("art", picture.getArt());

        Log.i("getPictureId", picture.getPictureId());

                mUsersDB.child(picture.getPictureId()).updateChildren(updatePhotoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDialog.dismiss();
                        finish();

                        if(PictureActivity.isPictureFromAlbum) {
                            startActivity(new Intent(EditActivity.this, PicturesActivity.class));
                        }
                        else {
                            startActivity(new Intent(EditActivity.this, HomeActivity.class));
                        }

                        Toast.makeText(EditActivity.this, "Albums updated.", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void initialcheckBoxesArray() {
        checkBoxesArray[0] = me;
        checkBoxesArray[1] = family;
        checkBoxesArray[2] = friends;
        checkBoxesArray[3] = love;
        checkBoxesArray[4] = pets;
        checkBoxesArray[5] = nature;
        checkBoxesArray[6] = sport;
        checkBoxesArray[7] = persons;
        checkBoxesArray[8] = animals;
        checkBoxesArray[9] = vehicles;
        checkBoxesArray[10] = views;
        checkBoxesArray[11] = food;
        checkBoxesArray[12] = things;
        checkBoxesArray[13] = funny;
        checkBoxesArray[14] = places;
        checkBoxesArray[15] = art;
    }

    public void update(View view) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        String albums[] = new String[Picture.numberOfAlbums];
        boolean userDidNotSelectAnyAlbum = true;

        for(int i = 0; i < checkBoxesArray.length; i++) {
            if(checkBoxesArray[i].isChecked()) {
                albums[i] = "1";
            }
            else {
                albums[i] = "0";
            }
        }
        for(int i = 0; i < checkBoxesArray.length; i++) {
            if(checkBoxesArray[i].isChecked()) {
                userDidNotSelectAnyAlbum = false;
                break;
            }
        }
        //If the user didn't check any Album.
        if(userDidNotSelectAnyAlbum) {
            alertDialogAddPicture();
            return;
        }

        picture.setMe(albums[0]);
        picture.setFamily(albums[1]);
        picture.setFriends(albums[2]);
        picture.setLove(albums[3]);
        picture.setPets(albums[4]);
        picture.setNature(albums[5]);
        picture.setSport(albums[6]);
        picture.setPersons(albums[7]);
        picture.setAnimals(albums[8]);
        picture.setVehicles(albums[9]);
        picture.setViews(albums[10]);
        picture.setFood(albums[11]);
        picture.setThings(albums[12]);
        picture.setFunny(albums[13]);
        picture.setPlaces(albums[14]);
        picture.setArt(albums[15]);

        updateAlbums();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);


        me = findViewById(R.id.checkBox_me_edit);
        family = findViewById(R.id.checkBox_family_edit);
        friends = findViewById(R.id.checkBox_friends_edit);
        love = findViewById(R.id.checkBox_love_edit);
        pets = findViewById(R.id.checkBox_pets_edit);
        nature = findViewById(R.id.checkBox_nature_edit);
        sport = findViewById(R.id.checkBox_sport_edit);
        persons = findViewById(R.id.checkBox_persons_edit);
        animals = findViewById(R.id.checkBox_animals_edit);
        vehicles = findViewById(R.id.checkBox_vehicles_edit);
        views = findViewById(R.id.checkBox_views_edit);
        food = findViewById(R.id.checkBox_food_edit);
        things = findViewById(R.id.checkBox_things_edit);
        funny = findViewById(R.id.checkBox_funny_edit);
        places = findViewById(R.id.checkBox_places_edit);
        art = findViewById(R.id.checkBox_art_edit);

        initialcheckBoxesArray();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersDB = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid());

        picture = PictureActivity.picture;

        imageView = findViewById(R.id.imageViewEdit);


        Glide.with(this)
                .load(picture.getPictureUrl())
                .apply(new RequestOptions().placeholder(R.drawable.b))
                .into(imageView);

        getAlbums();

    }

    private void getAlbums() {

        if(picture.getMe().equals("1")) {
            me.setChecked(true);
        }
        if(picture.getFamily().equals("1")) {
            family.setChecked(true);
        }
        if(picture.getFriends().equals("1")) {
            friends.setChecked(true);
        }
        if(picture.getLove().equals("1")) {
            love.setChecked(true);
        }
        if(picture.getPets().equals("1")) {
            pets.setChecked(true);
        }
        if(picture.getNature().equals("1")) {
            nature.setChecked(true);
        }
        if(picture.getSport().equals("1")) {
            sport.setChecked(true);
        }
        if(picture.getPersons().equals("1")) {
            persons.setChecked(true);
        }
        if(picture.getAnimals().equals("1")) {
            animals.setChecked(true);
        }
        if(picture.getVehicles().equals("1")) {
            vehicles.setChecked(true);
        }
        if(picture.getViews().equals("1")) {
            views.setChecked(true);
        }
        if(picture.getFood().equals("1")) {
            food.setChecked(true);
        }
        if(picture.getThings().equals("1")) {
            things.setChecked(true);
        }
        if(picture.getFunny().equals("1")) {
            funny.setChecked(true);
        }
        if(picture.getPlaces().equals("1")) {
            places.setChecked(true);
        }
        if(picture.getArt().equals("1")) {
            art.setChecked(true);
        }

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

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }

    private void alertDialogAddPicture() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Select ✓ at least 1 album.");

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }


}
