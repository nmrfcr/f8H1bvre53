package com.tekapic;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tekapic.model.Picture;

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

public class HomeActivity extends AppCompatActivity implements PicturesRecyclerViewAdapter.ListItemClickListener {

    private static final int REQUEST_PHOTO_CAPTURE = 1;
    private static final int REQUEST_PHOTO_PICK = 2;
    private static final String dstDir = Environment.getExternalStorageDirectory() +
            File.separator + "Pictures" + File.separator + "Tekapic";

    private FirebaseAuth mAuth;
    private DatabaseReference mStatusDB;
//    private DatabaseReference mUserDB;
    private RecyclerView mRecyclerView;
    private Uri mPhotoUri;
    private Button button;
    private ImageView imageViewIcon;
    public static boolean isUserhasPics = false;

    boolean[] checkedCategories = new boolean[Picture.numberOfAlbums+1];
    private EditText emailEditText, passwordEditText;
    private ProgressDialog mDialog;
    private String mCurrentPhotoPath;
    private LinearLayoutManager linearLayoutManager;
//    RecyclerView.LayoutManager layoutManager;

    //    static int lastFirstVisiblePosition = 0;
    private int size;
    private int pos;
    private PicturesRecyclerViewAdapter adapter;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private Context context;
    public static int firstVisibleItemPosition = 0;

    private Parcelable mLayoutManagerState;
    private static final String LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE";
    public  static Bundle state;



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

    private void popUpAlertDialogConnectionError() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Error");
        builder1.setMessage("There might be problems with the server or network connection.");
        builder1.setCancelable(false);

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

    public void popUpAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("How would you like to add a picture?");

        builder.setPositiveButton("   Take a picture", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Take a picture
//                Toast.makeText(HomeActivity.this, "Take a picture", Toast.LENGTH_SHORT).show();
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

    private void updateEmail() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Update Email");
        dialog.setCancelable(false);


        dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

//                Toast.makeText(HomeActivity.this, emailEditText.getText().toString() + " " + passwordEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                final String email, password;

                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if(email.isEmpty()) {
                    showAlertDialog("Error", "Email cannot be empty.");
                    return;
                }
                if(password.isEmpty()) {
                    showAlertDialog("Error", "Password cannot be empty.");
                    return;
                }
                if(email.equals(mAuth.getCurrentUser().getEmail())) {
                    showAlertDialog("Error", "Enter new email.");
                    return;
                }

                mDialog.setMessage("Please wait...");
                mDialog.show();
                mDialog.setCancelable(false);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Get auth credentials from the user for re-authentication
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mAuth.getCurrentUser().getEmail(), password); // Current Login Credentials \\
                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDialog.dismiss();

                                if(task.isSuccessful()) {
                                    Log.d("User re-authenticated.", "User re-authenticated.");
                                    //Now change your email address \\
                                    //----------------Code for Changing Email Address----------\\
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    mDialog.setMessage("Please wait...");
                                    mDialog.show();
                                    mDialog.setCancelable(false);

                                    user.updateEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    mDialog.dismiss();

                                                    if (task.isSuccessful()) {
                                                        Log.d("email updated", "User email address updated.");
                                                        showAlertDialog("Attention!", "Your email address has been successfully changed.");

                                                        //here

                                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                        rootRef.child("Users").child(uid).child("email").setValue(email);

                                                    }
                                                    else {
                                                        showAlertDialog("Error", task.getException().getMessage());

                                                    }
                                                }
                                            });
                                    //----------------------------------------------------------\\
                                }
                                else {
                                    showAlertDialog("Error", task.getException().getMessage());
                                }


                            }
                        });




            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

// Add a TextView here for the "Title" label, as noted in the comments
        emailEditText = new EditText(this);
        emailEditText.setHint("Enter new email");
        emailEditText.setText(mAuth.getCurrentUser().getEmail());
        layout.addView(emailEditText); // Notice this is an add method

// Add another TextView here for the "Description" label
        passwordEditText = new EditText(this);
        passwordEditText.setHint("Enter your password");
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordEditText); // Another add method

        dialog.setView(layout); // Again this is a set method, not add


        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
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
                    Toast.makeText(getApplicationContext(), "Please add pictures first.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.updateEmailMenu:
                updateEmail();
                return true;
//            case R.id.SortMenu:
//                sort();
//                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();


        if(!isNetworkConnected()) {
            popUpAlertDialogConnectionError();
        }


//        if (state != null) {
//            mLayoutManagerState = state.getParcelable(LAYOUT_MANAGER_STATE);
//            mRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerState);
//        }

//        mRecyclerView.scrollToPosition(lastFirstVisiblePosition);

//        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(lastFirstVisiblePosition,0);
//        (mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);

        if(mAuth.getCurrentUser() == null) {
            goToMainActivity();
        }
        checkIfUserHasAnyPictures();


//
//        if(isUserhasPics) {
////            Toast.makeText(getApplicationContext(), "onResume: " + firstVisibleItemPosition, Toast.LENGTH_SHORT).show();
//            picturesList.clear();
//            getPictures();
//            mRecyclerView.scrollToPosition(firstVisibleItemPosition);
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



//    @Override
//    protected void onStart() {
//
//        super.onStart();
//
//        Query query = mStatusDB;
//
//        final FirebaseRecyclerOptions<Picture> options = new FirebaseRecyclerOptions.Builder<Picture>()
//                .setQuery(query, Picture.class)
//                .build();
//
//        FirebaseRecyclerAdapter firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<Picture, StatusViewHolder>(options) {
//
//                    @NonNull
//                    @Override
//                    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view = LayoutInflater
//                                .from(parent.getContext())
//                                .inflate(R.layout.pictures_row, parent, false);
////                        view.setMinimumHeight();
//                        return new StatusViewHolder(view);
//                    }
//
//
//
//                    @Override
//                    protected void onBindViewHolder(@NonNull final StatusViewHolder holder, final int position, @NonNull final Picture model) {
//
//                        checkIfUserHasAnyPictures();
//
////                        pos = position;
//
//                                try {
//                                    holder.setPicture(getApplicationContext(), model.getPictureUrl());
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                        //listen to image button clicks
//                        holder.imageView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                if(isNetworkConnected() == false) {
//                                    popUpAlertDialogConnectionError();
//                                    return;
//                                }
//
//                                if(model.getPictureUrl().equals("none")) {
//                                    return;
//                                }
//                                //go to PictureActivity
//                                Intent intent = new Intent(HomeActivity.this, PictureActivity.class);
////                                intent.putExtra("MyClass", model);
//                                PictureActivity.picture = model;
//                                PictureActivity.isPictureFromAlbum = false;
//
//                                startActivity(intent);
//                            }
//                        });
//                    }
//
////                    @Override
////                    public int getItemCount() {
////                        return size - 1 - pos; //mNumberOfItems;
////                    }
//
//                };
//
//        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
//        firebaseRecyclerAdapter.startListening();
//
////        mRecyclerView.scrollToPosition(lastFirstVisiblePosition);
//
//    }

//    public static class StatusViewHolder extends RecyclerView.ViewHolder {
//
//        View view;
//        public ImageView imageView;
//
//
//        public StatusViewHolder(View itemView) {
//            super(itemView);
//            this.view = itemView;
//            imageView = view.findViewById(R.id.rowImageView);
//        }
//
//
//        public void setPicture(Context context, String pictureUrl) {
//
//            ImageView imageView = view.findViewById(R.id.rowImageView);
//
//            Glide.with(context)
//                    .load(pictureUrl)
//                    .apply(new RequestOptions().placeholder(R.drawable.b))
//                    .into(imageView);
//
////            Glide.with(context)
////                    .load(pictureUrl)
////                    .into(imageView);
//
//
////            Picasso.with(context).load(pictureUrl).placeholder(R.mipmap.loading_icon).
////                    memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageView);
//
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void dispatchChoosePhotoIntent() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PHOTO_PICK);
        }
        else {
            Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK);
            choosePhotoIntent.setType("image/*");
            startActivityForResult(choosePhotoIntent, REQUEST_PHOTO_PICK);


//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PHOTO_PICK);



//            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            getIntent.setType("image/*");
//
//            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            pickIntent.setType("image/*");
//
//            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

//            startActivityForResult(chooserIntent, REQUEST_PHOTO_PICK);
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

        state = savedInstanceState;

        mDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() == null) {
//            goToLoginActivity();
//            return;
//        }

        button = findViewById(R.id.addNewPicButton);
        imageViewIcon = findViewById(R.id.imageViewHomeIcon);

        mStatusDB = FirebaseDatabase.getInstance().getReference().child(mAuth.getUid());


//        mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");


//        linearLayoutManager = new LinearLayoutManager(this);
//
//
//        mRecyclerView = findViewById(R.id.homeRecyclerView);
//        mRecyclerView.setHasFixedSize(true);
//
//        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
//
//
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//
//
//
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));
//
//        mRecyclerView.setLayoutManager(mGridLayoutManager);

//        //take the latest data to RecyclerView
//        mGridLayoutManager.setReverseLayout(true);
//        mGridLayoutManager.setStackFromEnd(true);
        checkIfUserHasAnyPictures();



        mRecyclerView = findViewById(R.id.homeRecyclerView);


            mRecyclerView.setHasFixedSize(true);

            linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);

            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

            mOnClickListener = this;
            context = this;

            getPictures();

            if(PostActivity.flag) {
                check();
            }

            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(firstVisibleItemPosition);
            firstVisibleItemPosition = 0;











//        RecyclerView.ViewHolder viewHolder  = mRecyclerView.findViewHolderForLayoutPosition(firstVisibleItemPosition);

//        linearLayoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, Offset);



//        mRecyclerView.scrollToPosition(6);
//        mRecyclerView.scrollBy(50, 0);
//        mRecyclerView.getLayoutManager().scrollToPositionWithOffset(index, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putParcelable(LAYOUT_MANAGER_STATE, mLayoutManagerState);
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

                                adapter.notifyItemRangeRemoved(0, picturesList.size());
                                adapter.notifyItemRangeInserted(0, picturesList.size() + 1 );

                                picturesList.clear();
                                getPictures();
                            }
                        });
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        });
                        break;
                    }

                }
            }
        });

        t1.start();

    }


    private void getPictures() {

        checkIfUserHasAnyPictures();


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child(mAuth.getUid());

        ValueEventListener eventListener = new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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


                Collections.reverse(picturesList);
                adapter = new PicturesRecyclerViewAdapter(picturesList, mOnClickListener, context);
                mRecyclerView.setAdapter(adapter);

//                mRecyclerView.scrollToPosition(0);


//                GridLayoutManager mGridLayoutManager = new GridLayoutManager(PicturesActivity.this, 3);
//                mRecyclerView.setLayoutManager(mGridLayoutManager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);


    }

    @Override
    public void onListItemClick(int clickedItemIndex, Picture picture, int picturesListSize, ArrayList<Picture> picturesList) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        PictureActivity.picturesListSize = picturesListSize;
        PictureActivity.clickedItemIndex = clickedItemIndex;
        PictureActivity.picturesList = picturesList;


//        Toast.makeText(getApplicationContext(), "Clicked Item Index = " + clickedItemIndex, Toast.LENGTH_SHORT).show();
//        Log.i("pictureUrl", picture.getPictureUrl());

        PictureActivity.picture = picture;
        PictureActivity.isPictureFromAlbum = false;
        Intent intent = new Intent(HomeActivity.this, PictureActivity.class);
        startActivity(intent);

        finish();

    }


    private void checkIfUserHasAnyPictures() {
        mStatusDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    button.setVisibility(View.VISIBLE);
                    imageViewIcon.setVisibility(View.VISIBLE);
                    isUserhasPics = false;
                }
                else {
                    button.setVisibility(View.GONE);
                    imageViewIcon.setVisibility(View.GONE);
                    isUserhasPics = true;
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

    //*************Sorting ***************************////

    public void initialSorting() {
        for(int i = 0; i < Picture.numberOfAlbums+1; i++) {
            checkedCategories[i] = true;
        }
    }

    public void sort() {
        final ArrayList<String> categories = new ArrayList<String>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select categories:");

// add a checkbox list
        builder.setMultiChoiceItems(Picture.albumsNames, checkedCategories, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box
                switch (which) {
                    case 0:
                        if(isChecked) {
                            categories.add("me");
                        }
                        else {
                            categories.remove("me");
                        }
                    case 1:
                        if(isChecked) {
                            categories.add("family");
                        }
                        else {
                            categories.remove("family");
                        }
                    case 2:
                        if(isChecked) {
                            categories.add("friends");
                        }
                        else {
                            categories.remove("friends");
                        }
                    case 3:
                        if(isChecked) {
                            categories.add("love");
                        }
                        else {
                            categories.remove("love");
                        }
                    case 4:
                        if(isChecked) {
                            categories.add("pets");
                        }
                        else {
                            categories.remove("pets");
                        }
                    case 5:
                        if(isChecked) {
                            categories.add("nature");
                        }
                        else {
                            categories.remove("nature");
                        }
                    case 6:
                        if(isChecked) {
                            categories.add("sport");
                        }
                        else {
                            categories.remove("sport");
                        }
                    case 7:
                        if(isChecked) {
                            categories.add("persons");
                        }
                        else {
                            categories.remove("persons");
                        }
                    case 8:
                        if(isChecked) {
                            categories.add("animals");
                        }
                        else {
                            categories.remove("animals");
                        }
                    case 9:
                        if(isChecked) {
                            categories.add("vehicles");
                        }
                        else {
                            categories.remove("vehicles");
                        }

                    case 10:
                        if(isChecked) {
                            categories.add("views");
                        }
                        else {
                            categories.remove("views");
                        }
                    case 11:
                        if(isChecked) {
                            categories.add("food");
                        }
                        else {
                            categories.remove("food");
                        }

                    case 12:
                        if(isChecked) {
                            categories.add("things");
                        }
                        else {
                            categories.remove("things");
                        }
                    case 13:
                        if(isChecked) {
                            categories.add("funny");
                        }
                        else {
                            categories.remove("funny");
                        }
                    case 14:
                        if(isChecked) {
                            categories.add("places");
                        }
                        else {
                            categories.remove("places");
                        }
                    case 15:
                        if(isChecked) {
                            categories.add("art");
                        }
                        else {
                            categories.remove("art");
                        }

                }

            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK

                for(String category: categories) {
                    Log.i("category ", category);
                }
            }
        });
//        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();


    }
    //////////////////***************//////////////////


    @Override
    protected void onPause() {
        super.onPause();

            firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();


//        mLayoutManagerState = mRecyclerView.getLayoutManager().onSaveInstanceState();

//        firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

//        Toast.makeText(getApplicationContext(), "onPause: " + firstVisibleItemPosition, Toast.LENGTH_SHORT).show();
    }

    /****************************************************************************************/






}
