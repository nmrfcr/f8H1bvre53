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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Picture;
import java.util.ArrayList;


public class PicturePeopleActivity extends AppCompatActivity {

    private HackyViewPager mViewPager;
    private android.support.v7.app.ActionBar actionBar;
    private String album;
    private DatabaseReference databaseReferenceLikes, databaseReferenceLikedPictures;
    private boolean liked = false;
    private MenuItem item, itemLikes;
    private long numberOfLikes;
    private FirebaseAuth mAuth;
    private FragmentCollectionAdapter fragmentCollectionAdapter;
    private String reportReason = "";
    private Button button;
    private boolean flag;
    private AlertDialog alertDialog;



    public static Picture picture;
    public static boolean isPictureFromAlbum;
    public static int clickedItemIndex;
    public static ArrayList<Picture> picturesList=new ArrayList<Picture>() ;


    private void   showAlbums()  {

        ArrayList<String> albumsList = new ArrayList<String>();

        if(picture.getMe().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[0]);
        }
        if(picture.getFamily().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[1]);
        }
        if(picture.getFriends().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[2]);
        }
        if(picture.getLove().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[3]);
        }
        if(picture.getPets().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[4]);
        }
        if(picture.getNature().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[5]);
        }
        if(picture.getSport().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[6]);
        }
        if(picture.getPersons().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[7]);
        }
        if(picture.getAnimals().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[8]);
        }
        if(picture.getVehicles().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[9]);
        }
        if(picture.getViews().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[10]);
        }
        if(picture.getFood().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[11]);
        }
        if(picture.getThings().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[12]);
        }
        if(picture.getFunny().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[13]);
        }
        if(picture.getPlaces().equals("1")) {
            albumsList.add(Picture.albumsNamesUpperCase[14]);
        }
        if(picture.getArt().equals("1")) {
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

    private void showStatusReport(String message) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);

        builder1.setPositiveButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }

    private void sendEmail(final String report){


//        BackgroundMail backgroundMail = new BackgroundMail(this);
//        backgroundMail.setGmailUserName("tatyanakon45@gmail.com");
//        backgroundMail.setGmailPassword("Ey5NmHcS1z");
//        backgroundMail.setMailTo("tekapic2018@gmail.com");
//        backgroundMail.setType(BackgroundMail.TYPE_PLAIN);
//        backgroundMail.setFormSubject("Report Abuse");
//        backgroundMail.setFormBody(report);
//        backgroundMail.send();



        BackgroundMail.newBuilder(this)
                .withUsername("tatyanakon45@gmail.com")
                .withPassword("Ey5NmHcS1z")
                .withMailto("tekapic2018@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Report Abuse")
                .withBody(report)

                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        showStatusReport("Your report has been submitted successfully.");
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        showStatusReport("Unfortunately unable to submit your report at this time, please try again");
                    }
                })

                .send();

    }

    private void reportAbuse() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Report Picture");
        builder1.setMessage("Are you sure you want to report this picture?");

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String report;

//                        String reason = "Abusive Content";

                        String userIdOfReporter = mAuth.getUid();

                        String userIdWhoGotReported = HomePeopleActivity.user.getUserId();
                        String pictureIdWhichReported = picture.getPictureId();
                        String picuteUrlWhichReported = picture.getPictureUrl();

                        report = "Report Abuse" + "\n\n";

                        report = report + "Report reason: " + reportReason + "\n\n";

                        report = report + "User Id of reporter: " + userIdOfReporter + "\n\n";

                        report = report + "User Id who got reported: " + userIdWhoGotReported + "\n";
                        report = report + "Picture Id which reported: " + pictureIdWhichReported + "\n";
                        report = report + "Picute url which reported: " + picuteUrlWhichReported + "\n";

                        Log.i("Report Abuse", report);

                        sendEmail(report);

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

//        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//
//                     button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                    if (button != null) {
//                        button.setEnabled(false);
//                    }
//
//            }
//        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return false;
        }
        switch (item.getItemId()) {

            case R.id.showAlbumsPeopleMenu:
                showAlbums();
                return true;

            case R.id.showDatePicturePeopleMenu:
                showDate();
                return true;

            case R.id.reportAbusePicturePeople:
               setPictureReportReason();
                return true;

            case android.R.id.home:
                goBack();
                return true;

            case R.id.likePicturePeopleMenu:
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
                return true;


            case R.id.likesPicturePeopleMenu:

                LikesActivity.flag = 1;
                LikesActivity.userId = HomePeopleActivity.user.getUserId();
                LikesActivity.pictureId = picture.getPictureId();

                startActivity(new Intent(PicturePeopleActivity.this, LikesActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private void updatePicturePosition(int index) {

        setTitle(Integer.toString(index+1) + "/" + Integer.toString(picturesList.size()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_people_menu, menu);

        updatePicturePosition(clickedItemIndex);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_picture_people);

        picture = picturesList.get(clickedItemIndex);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80000000));

        mViewPager = findViewById(R.id.view_pager_people);
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

                updatePicturePosition(position);

                picture = picturesList.get(position);

                clickedItemIndex = position;

                databaseReferenceLikes = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(HomePeopleActivity.user.getUserId()).child("Pictures").child(picture.getPictureId()).child("Likes");

                checkIfILikePicture();
                checkNumberOfLikes();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mAuth = FirebaseAuth.getInstance();

        databaseReferenceLikes = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(HomePeopleActivity.user.getUserId()).child("Pictures").child(picture.getPictureId()).child("Likes");

        databaseReferenceLikedPictures = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getUid()).child("LikedPictures").child(HomePeopleActivity.user.getUserId());






//        databaseReferenceLikedPictures.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()) {
//
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                        String pictureId = ds.child("pictureId").getValue(String.class);
//
//                        Toast.makeText(PicturePeopleActivity.this, pictureId, Toast.LENGTH_LONG).show();
//                    }
//
//                }
//                else {
//
//                    Toast.makeText(PicturePeopleActivity.this, "no data", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        item = menu.findItem(R.id.likePicturePeopleMenu);
        itemLikes = menu.findItem(R.id.likesPicturePeopleMenu);


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

//                actionBar.setSubtitle("Likes: " + numberOfLikes);

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


    private void goBack() {
        finish();
        if(isPictureFromAlbum) {
            startActivity(new Intent(PicturePeopleActivity.this, PicturesPeopleActivity.class));
        }
        else {
            startActivity(new Intent(PicturePeopleActivity.this, HomePeopleActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
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
