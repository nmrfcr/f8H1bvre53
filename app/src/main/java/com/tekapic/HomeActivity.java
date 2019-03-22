package com.tekapic;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements PicturesRecyclerViewAdapter.ListItemClickListener {

    private static final int REQUEST_PHOTO_CAPTURE = 1;
    private static final int REQUEST_PHOTO_PICK = 2;
    private static final String dstDir = Environment.getExternalStorageDirectory() +
            File.separator + "Pictures" + File.separator + "Tekapic";
    private FirebaseAuth mAuth;
    private DatabaseReference mStatusDB;
    private RecyclerView mRecyclerView;
    private Uri mPhotoUri;
    private Button button;
    private ImageView imageViewIcon;
    private EditText emailEditText, passwordEditText;
    private ProgressDialog mDialog;
    private String mCurrentPhotoPath;
    private LinearLayoutManager linearLayoutManager;
    private PicturesRecyclerViewAdapter adapter;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private Context context;
    private android.support.v7.app.ActionBar actionBar;

    public static int firstVisibleItemPosition = 0;
    public static boolean isUserhasPics = false;




    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    private void popUpAlertDialogLogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Log Out of Tekapic?");

        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                mAuth.signOut();
                goToMainActivity();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void buttonClick(View view) {
        popUpAlertDialog();
    }

    private void albumsIconclicked() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Albums");
        builder1.setMessage("Please add pictures first.");

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        popUpAlertDialog();
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

    public void popUpAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("How would you like to add a picture?");

        builder.setPositiveButton("   Take a picture", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Take a picture
                dispatchTakePhotoIntent();
            }
        });

        builder.setNegativeButton("   Choose a picture", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Choose photo
                dispatchChoosePhotoIntent();
            }
        });
        builder.setNeutralButton("   Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();


        // set the buttons to the center of the screen
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        final Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        final Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        LinearLayout.LayoutParams negativeButtonLL = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
        LinearLayout.LayoutParams neutralButtonLL = (LinearLayout.LayoutParams) neutralButton.getLayoutParams();

        positiveButtonLL.gravity = Gravity.CENTER;
        negativeButtonLL.gravity = Gravity.CENTER;
        neutralButtonLL.gravity = Gravity.CENTER;

        positiveButton.setLayoutParams(positiveButtonLL);
        negativeButton.setLayoutParams(negativeButtonLL);
        neutralButton.setLayoutParams(neutralButtonLL);

        positiveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo_camera_black_24dp, 0, 0, 0);
        negativeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_black_24dp, 0, 0, 0);
        neutralButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_black_24dp, 0, 0, 0);
//
//        negativeButtonLL.leftMargin = 45;
        negativeButton.setLayoutParams(negativeButtonLL);
//
//        neutralButtonLL.rightMargin = 100;
        neutralButton.setLayoutParams(neutralButtonLL);

    }


    private void goToMainActivity() {
        finish();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.logoutMenu:
                popUpAlertDialogLogOut();
                return true;
            case R.id.addNewMenu:
                popUpAlertDialog();
                //allert dialog
                return true;
            case R.id.albums:
                if(isUserhasPics) {
                    startActivity(new Intent(HomeActivity.this, AlbumsActivity.class));
                }
                else {
                    albumsIconclicked();
                }
                return true;
            case R.id.editProfileMenu:
                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
                return true;
            case R.id.searchMenu:
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                return true;
            case R.id.exploreMenu:
                Toast.makeText(context, "Explore", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.favoritesMenu:
                startActivity(new Intent(HomeActivity.this, FavoritesActivity.class));
                return true;
            case R.id.accountPrivacyMenu:
                startActivity(new Intent(HomeActivity.this, AccountPrivacyActivity.class));
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

        if(mAuth.getCurrentUser() == null) {
            goToMainActivity();
        }
        checkIfUserHasAnyPictures();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void dispatchChoosePhotoIntent() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PHOTO_PICK);
        }
        else {
            Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK);
            choosePhotoIntent.setType("image/*");
            startActivityForResult(choosePhotoIntent, REQUEST_PHOTO_PICK);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void dispatchTakePhotoIntent() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_PHOTO_CAPTURE);
        }
        else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},  REQUEST_PHOTO_CAPTURE);
        }
        else  {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mPhotoUri = FileProvider.getUriForFile(this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        startActivityForResult(takePictureIntent, REQUEST_PHOTO_CAPTURE);
                    }
                }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PIC_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK) {

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(mCurrentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                PostActivity.pictureUri = contentUri;
                Intent intent = new Intent(this, PostActivity.class);
                startActivity(intent);

                copyFileOrDirectory(mCurrentPhotoPath, dstDir);

        }
        else if(requestCode == REQUEST_PHOTO_PICK && resultCode == RESULT_OK) {
            //success choosing photo
            mPhotoUri = data.getData();

            PostActivity.pictureUri = mPhotoUri;
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHOTO_CAPTURE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    dispatchTakePhotoIntent();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case REQUEST_PHOTO_PICK : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    dispatchChoosePhotoIntent();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }



    //****************onCreate()********************//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        actionBar = getSupportActionBar();


        mDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            goToMainActivity();
            return;
        }

        button = findViewById(R.id.addNewPicButton);
        imageViewIcon = findViewById(R.id.imageViewHomeIcon);

        mStatusDB = FirebaseDatabase.getInstance().getReference().child(mAuth.getUid());

        checkIfUserHasAnyPictures();

        mRecyclerView = findViewById(R.id.homeRecyclerView);

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
        DatabaseReference usersdRef = rootRef.child(mAuth.getUid());

        actionBar.setSubtitle("(" + Integer.toString(picturesList.size()) +")");

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

                    Picture picture = new Picture(pictureId, pictureUrl, date, me, family, friends, love, pets, nature, sport, persons, animals, vehicles, views, food, things, funny, places, art);


                    picturesList.add(picture);

                }

                if(wasCalled) {
                    adapter.notifyDataSetChanged();
                    Log.i("HomeActivity", "notifyDataSetChanged was Called");

                }


                actionBar.setSubtitle("(" + Integer.toString(picturesList.size()) + ")");
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
        PictureActivity.picturesList.clear();
        PictureActivity.clickedItemIndex = clickedItemIndex;

        for(Picture p : picturesList) {
            PictureActivity.picturesList.add(p);
        }

        PictureActivity.isPictureFromAlbum = false;
        Intent intent = new Intent(HomeActivity.this, PictureActivity.class);
        startActivity(intent);

        finish();
    }


    private void checkIfUserHasAnyPictures() {
        mStatusDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    button.setVisibility(View.VISIBLE);
                    imageViewIcon.setVisibility(View.VISIBLE);
                    isUserhasPics = false;
                    mRecyclerView.setVisibility(View.GONE);

                }
                else {
                    button.setVisibility(View.GONE);
                    imageViewIcon.setVisibility(View.GONE);
                    isUserhasPics = true;
                    mRecyclerView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /****************The starting  proccess of the pictures making**************/

    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();

                int filesLength = files.length;

                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);
                }
            } else {
                copyFile(src, dst);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {

        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {

            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }

        }
    }
    /****************The end of the proccess of pictures making**************/

    @Override
    protected void onPause() {
        super.onPause();

        firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }
}
