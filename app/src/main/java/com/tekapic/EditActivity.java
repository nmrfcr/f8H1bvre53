package com.tekapic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tekapic.model.Picture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private CheckBox me, family, friends, love, pets, nature, sport;
    private CheckBox persons, animals, vehicles, views, food, things, funny;
    private CheckBox places, art;
    private CheckBox[] checkBoxesArray = new CheckBox[Picture.numberOfAlbums];

    private ProgressDialog mDialog;
    public  static Uri pictureUri;

    private StorageReference mStorage;
    private FirebaseUser currentUser;
    private DatabaseReference mUsersDB;

    private Picture picture;


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
        //If the user didn't check any album.
        if(userDidNotSelectAnyAlbum) {
            Toast.makeText(this, "Select at least one album.", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp;
        timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(new Date());

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

        mDialog.setMessage("Please wait...");
        mDialog.show();
        mDialog.setCancelable(false);


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
                                Toast.makeText(getApplicationContext(), "Your picture has been posted.", Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(PostActivity.this, HomeActivity.class);
//                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }







//    public void getAlbums() {
//        final ArrayList<String> albumList = new ArrayList<String>();
//        //to fetch all the users of firebase Auth app
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//
//        DatabaseReference usersdRef = rootRef.child(currentUser.getUid());
//
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int i = 0;
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String album = ds.child(Picture.albumsNames[i++]).getValue(String.class);
//                    albumList.add(album);
//                }
//                for(String al: albumList) {
//                    Log.i("Album", al);
//                }
////                ArrayAdapter<String> adapter = new ArrayAdapter(OtherUsersActivity.this, android.R.layout.simple_list_item_1, array);
////
////                mListView.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        usersdRef.addListenerForSingleValueEvent(eventListener);
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mDialog = new ProgressDialog(this);


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

        mStorage = FirebaseStorage.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersDB = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid());

//        picture = (Picture) getIntent().getSerializableExtra("MyClass");
        getAlbums();

    }

    private void getAlbums() {
        picture = PictureActivity.picture;

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







//        Log.i("me", picture.getMe());
//        Log.i("family", picture.getFamily());
//        Log.i("friends", picture.getFriends());
//        Log.i("love", picture.getLove());
//        Log.i("pets", picture.getPets());
//        Log.i("nature", picture.getNature());
//        Log.i("sport", picture.getSport());
//        Log.i("persons", picture.getPersons());
//        Log.i("animals", picture.getAnimals());
//        Log.i("vehicles", picture.getVehicles());
//        Log.i("views", picture.getViews());
//        Log.i("food", picture.getFood());
//        Log.i("things", picture.getThings());
//        Log.i("funny", picture.getFunny());
//        Log.i("places", picture.getPlaces());
//        Log.i("art", picture.getArt());


    }


}
