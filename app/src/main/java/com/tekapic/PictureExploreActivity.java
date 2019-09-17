package com.tekapic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Picture;
import com.tekapic.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;




public class PictureExploreActivity extends AppCompatActivity {

    private String username;
    private HackyViewPager mViewPager;
    private android.support.v7.app.ActionBar actionBar;
    private DatabaseReference databaseReferenceLikes, databaseReferenceLikedPictures, databaseReferenceUser;
    private boolean liked = false;
    private MenuItem item, itemLikes;
    private long numberOfLikes;
    private FirebaseAuth mAuth;
    private FragmentCollectionAdapter fragmentCollectionAdapter;
    private String reportReason = "";
    private AlertDialog alertDialog;
    private DatabaseReference picDatabaseReference;



    public static Picture picture;
    public static int clickedItemIndex;
    public static ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    public static ArrayList<String> usersIdList=new ArrayList<String>() ;



    private void   showAlbums()  {

        ArrayList<String> albumsList = new ArrayList<String>();

        if(picture.getMe()) {
            albumsList.add(Picture.albumsNamesUpperCase[0]);
        }
        if(picture.getFamily()) {
            albumsList.add(Picture.albumsNamesUpperCase[1]);
        }
        if(picture.getFriends()) {
            albumsList.add(Picture.albumsNamesUpperCase[2]);
        }
        if(picture.getLove()) {
            albumsList.add(Picture.albumsNamesUpperCase[3]);
        }
        if(picture.getPets()) {
            albumsList.add(Picture.albumsNamesUpperCase[4]);
        }
        if(picture.getNature()) {
            albumsList.add(Picture.albumsNamesUpperCase[5]);
        }
        if(picture.getSport()) {
            albumsList.add(Picture.albumsNamesUpperCase[6]);
        }
        if(picture.getPersons()) {
            albumsList.add(Picture.albumsNamesUpperCase[7]);
        }
        if(picture.getAnimals()) {
            albumsList.add(Picture.albumsNamesUpperCase[8]);
        }
        if(picture.getVehicles()) {
            albumsList.add(Picture.albumsNamesUpperCase[9]);
        }
        if(picture.getViews()) {
            albumsList.add(Picture.albumsNamesUpperCase[10]);
        }
        if(picture.getFood()) {
            albumsList.add(Picture.albumsNamesUpperCase[11]);
        }
        if(picture.getThings()) {
            albumsList.add(Picture.albumsNamesUpperCase[12]);
        }
        if(picture.getFunny()) {
            albumsList.add(Picture.albumsNamesUpperCase[13]);
        }
        if(picture.getPlaces()) {
            albumsList.add(Picture.albumsNamesUpperCase[14]);
        }
        if(picture.getArt()) {
            albumsList.add(Picture.albumsNamesUpperCase[15]);
        }


        String albums = "";

        for(String album: albumsList) {
            albums += album + ", ";
        }

        if(albums.endsWith(" "))
        {
            albums = albums.substring(0,albums.length() - 1);
        }


        if(albums.endsWith(","))
        {
            albums = albums.substring(0,albums.length() - 1);
        }


        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Albums");
        builder1.setMessage(albums);

        builder1.setPositiveButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();


    }









    private void makePictureReportToFirebase() {



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Reports");

        String reportId = databaseReference.push().getKey();

        databaseReference.child(reportId).child("reportId").setValue(reportId);

        databaseReference.child(reportId).child("reportReason").setValue(reportReason);
        databaseReference.child(reportId).child("pictureUrl").setValue(picture.getPictureUrl());
        databaseReference.child(reportId).child("userIdWhoGotReported").setValue(usersIdList.get(clickedItemIndex));
        databaseReference.child(reportId).child("pictureId").setValue(picture.getPictureId());
        databaseReference.child(reportId).child("userIdOfReporter").setValue(mAuth.getUid());

        String timeStamp;
        timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        databaseReference.child(reportId).child("date").setValue(timeStamp);




        Toast.makeText(this, "Thank you for your report!", Toast.LENGTH_SHORT).show();

    }

    private void reportAbuse() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Report Picture");
        builder1.setMessage("Are you sure you want to report this picture?");

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        makePictureReportToFirebase();

                    }
                });
        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();


    }

    private void setPictureReportReason() {

        reportReason = "";

        final String[] reasons =
                {"Sexual content", "Violent or repulsive content", "Hateful or abusive content",
                        "Harmful or dangerous acts", "Spam or misleading"};

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

        builder1.setTitle("Report Picture");

        builder1.setSingleChoiceItems(reasons, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                switch (item) {
                    case 0:
                        reportReason = reasons[0];
                        break;
                    case 1:
                        reportReason = reasons[1];
                        break;
                    case 2:
                        reportReason = reasons[2];
                        break;
                    case 3:
                        reportReason = reasons[3];
                        break;
                    case 4:
                        reportReason = reasons[4];
                        break;
                }


            }
        });

        builder1.setPositiveButton(
                "REPORT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(isNetworkConnected() == false) {
                            popUpAlertDialogConnectionError();
                            return;
                        }

                        reportAbuse();

                    }
                });

        builder1.setNegativeButton(
                "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });


        alertDialog = builder1.create();

        alertDialog.show();

        // Initially disable the button
        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);





    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return false;
        }
        switch (item.getItemId()) {

            case R.id.gotoProfileExploreMenu:

                if(usersIdList.get(clickedItemIndex).equals(mAuth.getUid())) {
                    startActivity(new Intent(PictureExploreActivity.this, ProfileActivity.class));
                    return true;
                }

                User user = new User();
                user.setUserId(usersIdList.get(clickedItemIndex));
                user.setUsername(username);


                ProfilePeopleActivity.user = user;
                ProfilePeopleActivity.index = 0;
                startActivity(new Intent(PictureExploreActivity.this, ProfilePeopleActivity.class));

                return true;

            case R.id.showAlbumsExploreMenu:
                showAlbums();
                return true;

            case R.id.showDatePictureExploreMenu:
                showDate();
                return true;

            case R.id.reportAbusePictureExplore:

                if(mAuth.getUid().equals(usersIdList.get(clickedItemIndex))) {
                    Toast.makeText(this, "You can't report your own picture.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                picDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            setPictureReportReason();
                        }
                        else {
                            Toast.makeText(PictureExploreActivity.this, "Picture was deleted.", Toast.LENGTH_LONG).show();
                            onBackPressed();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                return true;



            case R.id.likePictureExploreMenu:

                picDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            if(liked) {
                                liked = false;
                                item.setIcon(R.drawable.ic_heart);
                                databaseReferenceLikes.child(mAuth.getUid()).removeValue();

                                databaseReferenceLikedPictures.child(picture.getPictureId()).removeValue();

                            }

                            else {
                                liked = true;
                                item.setIcon(R.drawable.ic_like);
                                databaseReferenceLikes.child(mAuth.getUid()).child("userId").setValue(mAuth.getUid());

                                databaseReferenceLikedPictures.child(picture.getPictureId()).child("pictureId").setValue(picture.getPictureId());
                            }
                            checkNumberOfLikes();
                        }
                        else {
                            Toast.makeText(PictureExploreActivity.this, "Picture was deleted.", Toast.LENGTH_LONG).show();
                            onBackPressed();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                return true;

            case R.id.likesPictureExploreMenu:



                LikesActivity.index = 0;
                LikesActivity.userId = usersIdList.get(clickedItemIndex);
                LikesActivity.pictureId = picture.getPictureId();

                startActivity(new Intent(PictureExploreActivity.this, LikesActivity.class));

                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    private void updateTitle() {

        databaseReferenceUser.child(usersIdList.get(clickedItemIndex)).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                username = dataSnapshot.child("username").getValue(String.class);
//                setTitle(Html.fromHtml("<u>" + username + "</u>"));
                setTitle(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_explore_menu, menu);

        updateTitle();

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_picture_explore);

        picture = picturesList.get(clickedItemIndex);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80000000));

        mViewPager = findViewById(R.id.view_pager_explore);
        fragmentCollectionAdapter = new FragmentCollectionAdapter(getSupportFragmentManager(), picturesList);
        mViewPager.setAdapter(fragmentCollectionAdapter);

        mViewPager.setCurrentItem(clickedItemIndex);

        hideSystemUI();
        showSystemUI();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                clickedItemIndex = position;

                picture = picturesList.get(position);

                updateTitle();


                databaseReferenceLikes = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(usersIdList.get(position)).child("Pictures").child(picture.getPictureId()).child("Likes");

                checkIfILikePicture();
                checkNumberOfLikes();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mAuth = FirebaseAuth.getInstance();

        picDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(usersIdList.get(clickedItemIndex)).child("Pictures").child(picture.getPictureId());


        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users");

        databaseReferenceLikes = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(usersIdList.get(clickedItemIndex)).child("Pictures").child(picture.getPictureId()).child("Likes");

        databaseReferenceLikedPictures = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getUid()).child("LikedPictures").child(usersIdList.get(clickedItemIndex));




    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        item = menu.findItem(R.id.likePictureExploreMenu);
        itemLikes = menu.findItem(R.id.likesPictureExploreMenu);


        checkIfILikePicture();
        checkNumberOfLikes();



        return super.onPrepareOptionsMenu(menu);
    }

    private void checkIfILikePicture() {

        databaseReferenceLikes.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    item.setIcon(R.drawable.ic_like);
                    liked = true;
                }
                else {
                    item.setIcon(R.drawable.ic_heart);
                    liked = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkNumberOfLikes() {
        databaseReferenceLikes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberOfLikes = dataSnapshot.getChildrenCount();

                if(numberOfLikes == 0) {
                    itemLikes.setTitle("");
                }
                else {
                    itemLikes.setTitle(Long.toString(numberOfLikes));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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

    private void showDate() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Date Added");
        builder1.setMessage(picture.getDate());

        builder1.setPositiveButton(
                "Close",
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
}
