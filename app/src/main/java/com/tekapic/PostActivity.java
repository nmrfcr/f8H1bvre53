package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tekapic.model.Picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PostActivity extends AppCompatActivity {


    private ImageView imageView;
    private CheckBox me, family, friends, love, pets, nature, sport;
    private CheckBox persons, animals, vehicles, views, food, things, funny;
    private CheckBox places, art;
    private CheckBox[] checkBoxesArray = new CheckBox[Picture.numberOfAlbums];
    private StorageReference mStorage;
    private FirebaseUser currentUser;
    private DatabaseReference mUsersDB;
    private Picture picture;

    private Uri pictureUri;



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

    public void post(View view) {

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

        finish();
        Toast.makeText(getApplicationContext(), "Uploading..", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(PostActivity.this, HomeActivity.class);
        startActivity(intent);

        String timeStamp;
        timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        picture = new Picture();

        picture.setPictureId("none");
        picture.setDate(timeStamp);

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

        updateUserPhoto(pictureUri);

    }


    private void updateUserPhoto(Uri pictureUri) {

        StorageReference userImageRef = mStorage.child("userImages").child(currentUser.getUid())
                .child(pictureUri.getLastPathSegment());
        userImageRef.putFile(pictureUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                Task<Uri> uploadedImageUri = task.getResult().getMetadata().getReference().getDownloadUrl();

                uploadedImageUri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String pictureId = "none";
                        Uri uploadedImageUri = task.getResult();

                        Map<String, Object> updatePhotoMap = new HashMap<>();

                        updatePhotoMap.put("pictureUrl", uploadedImageUri.toString());
                        updatePhotoMap.put("date", picture.getDate());

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

                        DatabaseReference databaseReference = mUsersDB.push();

                        String arr[] = databaseReference.toString().split("/");
                        for(int i = 0; i < arr.length; i++) {
                            if(1 + i == arr.length) {
                                pictureId = arr[i];
                            }
                        }

                        updatePhotoMap.put("pictureId", pictureId);



                        databaseReference.updateChildren(updatePhotoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //////////////Posting picture completed//////////////////

//                                try {
//                                    mViewPager.getAdapter().notifyDataSetChanged();
//                                    ++PictureActivity.picturesListSize;
//                                }
//                                catch (Exception e) {
//                                    Log.i("Exception", "mViewPager PostActivity");
//                                }

                                Toast.makeText(getApplicationContext(), "Picture has been added.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Intent intent = getIntent();
        if(intent != null) {
            pictureUri = intent.getParcelableExtra("imageUri");
        }


        setContentView(R.layout.activity_post);

        setTitle(R.string.add_new_picture);

        imageView = findViewById(R.id.imageViewPost);

//        Intent intent = getIntent();
//        if(intent != null) {
//            if (Intent.ACTION_SEND.equals(intent.getAction())) {
//
//                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//                    startActivity(new Intent(PostActivity.this, MainActivity.class));
//                    finish();
//                    return;
//                }
//                else {
//                    pictureUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
//                }
//            }
//        }


        Glide.with(this).load(pictureUri).into(imageView);


        me = findViewById(R.id.checkBox_me);
        family = findViewById(R.id.checkBox_family);
        friends = findViewById(R.id.checkBox_friends);
        love = findViewById(R.id.checkBox_love);
        pets = findViewById(R.id.checkBox_pets);
        nature = findViewById(R.id.checkBox_nature);
        sport = findViewById(R.id.checkBox_sport);
        persons = findViewById(R.id.checkBox_persons);
        animals = findViewById(R.id.checkBox_animals);
        vehicles = findViewById(R.id.checkBox_vehicles);
        views = findViewById(R.id.checkBox_views);
        food = findViewById(R.id.checkBox_food);
        things = findViewById(R.id.checkBox_things);
        funny = findViewById(R.id.checkBox_funny);
        places = findViewById(R.id.checkBox_places);
        art = findViewById(R.id.checkBox_art);

        initialcheckBoxesArray();

        mStorage = FirebaseStorage.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Pictures");

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

//    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//        finish();
//        startActivity(new Intent(PostActivity.this, HomeActivity.class));
//    }


}
